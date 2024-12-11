# mysql
## 1、索引
### 1、千万级数据表如何使用索引快速查找

​	索引的数据结构:  

| 二叉树   如果插入的数据是一个自增序列，很容易导致索引形成一个链表。并不会提升查询性能。 |
| ------------------------------------------------------------ |
| 红黑树  (也是一种二叉树[二叉平衡树]，每次插入如果两边不平衡，例左边1个节点，右边3个节点，则右边会形成 子-父-子 的新结构)。当数据量很大，树的高度会很高，不可控 |
| hash表                                                       |
| b-tree  (多叉树，每个节点多分配了空间，可以指向多个子节点)， 减少了树的高度<br/>5、b+tree  (只在叶子节点存储data，非叶子节点不存data，只存索引) |

-show global status like 'innodb_page_size' 

查询默认节点大小 16.384kb
​	-默认索引地址大小6B
​						

-将非叶子节点加载到内存(无数据)，定位数据地址后做一次磁盘I/O。所以非常快
​						

-b+tree叶子节点之间从左往右、从小到大，相互指向。这样支持范围查找

-show global status like 'Com_______'    可以查看执行语句次数

#### 2、mysql表存储

​	-innodb。 .frm(表结构文件) .ibd(数据和索引文件)
​	-myisam。 .frm(表结构文件) .myd(数据文件) .myi(索引文件)
​	

	如果需要支持事务和行级锁等特性，可以选择InnoDB;
	如果需要进行全文搜索或者处理OLAP应用，可以选择MyISAM;
	如果需要处理临时表或缓存等频繁读写的应用，可以选择Memory引擎;
	如果需要进行分布式存储和处理，可以选择NDB引擎;

### 3、innodb

​	和myisam引擎的区别: myisam索引叶子节点存储数据的物理地址，所以索引.myi和数据.myd可以分开为两个文件。

	聚集索引：-Innodb通过主键聚集数据，如果没有定义主键，innodb会选择非空的唯一索引代替。
			 -如果没有这样的索引，MySQL自动为InnoDB表生成一个隐含字段作为主键，这个字段长度为6个字节，类型为长整形
			 -非单调的主键会造成在插入新记录时数据文件为了维持B+Tree的特性而频繁的分裂调整，十分低效，而使用自增字段作为主键则是一个很好的选择
				推荐用整型自增主键,整型比较大小快，且占用存储小
	优点: 1、数据访问更快 
		  2、聚簇索引对于主键的排序查找和范围查找速度非常快
	缺点: 1、插入速度严重依赖于插入顺序。 因此，对于InnoDB表，我们一般都会定义一个自增的ID列为主键
		  2、更新主键的代价很高。因此，对于InnoDB表，我们一般定义主键为不可更新
		  3、二级索引访问需要两次索引查找，第一次找到主键值，第二次根据主键值找到行数据
	
	辅助索引：聚簇索引之上创建的索引称之为辅助索引，引用主键作为data域，叶子节点除了包含键值外，还包含了相应行数据的聚簇索引键
	(覆盖索引: 指通过一次索引查询就能拿到数据，不用去另一个磁盘文件再次查询)
### 4、hash索引 (基本不用，都选b+tree)

​	对索引的key进行hash计算，取模后定位出一维数组的存储位置，相同的用链表存储。
​	-仅能满足“=”，“IN”,不支持范围查询
​	-hash冲突问题
​	

### 6、MVCC多版本并发机制

multi-version concurrency control

MVCC主要是为了提高并发的读写性能，不用加锁就能让多个事务并发读写



### 7、mysql表关联常见两种算法

​	超过三张表禁止关联

	-nested-loop join 嵌套循环连接
		select * from t1 inner t2 on t1.b = t2.b
		1、把t2的所有数据放入join_buffer中
		2、遍历t1数据，与join_buffer做对比
		3、返回满足条件的数据
	整个过程内存中判断了t1的行数*t2的行数
	如果t2是个大表，join_buffer放不下，则进行分段(又join_buffer_size参数决定，默认256K)


​	-block nested-loop join 
### 8、buffer pool 和 redo log buffer 和binlog

​	innodb 特有机制。 
​	
​	增删改查都是直接操作bufferpool,一般设置为内存60%。
​	
​    更新数据会写入undo日志便于回滚。
​    如果出现断电、宕机等情况，buffer pool 中的数据未刷到磁盘文件中，会导致数据丢失，所以提供了**redo log buffer**机制。

```
show variables like 'innodb_flush_log_at_trx_commit';
set global innodb_flush_log_at_trx_commit=1
```

=1 这个策略表示，每次操作会直接进行数据刷盘。
=0 这个策略表示，每隔一秒才会进行系统内存放入和刷盘操作。

binlog提供历史查询、数据库备份和恢复、主从复制等功能。写入redo log buffer的同时进行binlog日志的数据刷盘。

### 9、连接池

两个重要参数

<!--最大连接数-->

```
show variables like 'max_connections';

set global max_connections=；
```

<!--单次最大数据报文-->

```
show variables like 'max_allowed_packet';
set global max_allowed_packet=;
```
### 10、慢查询
-mysql 把所有执行语句超过指定参数(long_query_time 单位秒，默认10秒)记录在慢查询日志。
-慢查询默认没有开启，在配置信息/etc/my.cnf 配置 
	开启慢查询 show_query_log=1
	设置慢查询时间 long_query_time=2(秒)
-慢查询日志文件/var/lib/mysql/localhost-slow.log

```
show variables like 'slow_query_log'    --查看是否开启
```
### 11、profiles
-show profiles 能够在做SQL优化的时候帮助我们了解时间都去哪了。通过having_profiling参数，能够看到mysql当前版本是否支持
	select @@have_profiling;
-默认是关闭的，通过(select @@profiling;)查看，0代表关闭。开启(set profiling = 1;)
-show profiles; 查看当前session中语句执行的时间情况
-show profile (cpu) for query query_id;  查看语句查询各阶段用时

### 12、explain

id: 序列值，在多表查询中，值越大越先执行
select-type：类型
table：表
partitions: 分区
type：连接类型
possible_keys: 可能用到的索引
key：实际用到的索引
key_len: 索引的长度
row：扫描的记录数

### 13、sql提示

例: select username from user ***use index(indexName)*** where id = 1; 
	**u	se** - 建议使用
	**ignore** - 不使用
	**force** - 必须使用

### 14、覆盖索引
尽量使用覆盖索引(查询使用了索引，并且需要返回的列，在该索引中已经全部能够找到)， 减少select * （极易出现回表查询）
| extra                 |                                  |
| --------------------- | -------------------------------- |
| using index condition | 使用了索引，但是需要回表查询数据 |
| using where;using in  | 不需要回表                       |

回表查询，查询字段非索引字段，则通过辅助索引查询到的数据，又要回到主键(聚集)索引找数据
### 15、前缀索引
当索引是字符串类型时，有时候会是很长一段字符串，浪费大量IO。 可以只将字符串一部分前缀，建立索引。
~~~创建语法
create index idx_name on table_name(column(n));  --n代表了截取字符串前几位
~~~
### 16、单列索引和联合索引选择问题

单列索引容易回表查询
在业务场景中，如果存在多个查询条件，考虑针对查询字段建立联合索引。
## 2、SQL优化
### 1、insert 优化
-建议批量插入，避免频繁建立sqlsession。一次插入建议不超过1000条。
-手动提交事务，避免频繁开启终止事务。
-主键顺序插入，效率高于乱序插入。
1、大批量插入数据
	一次性大批量插入数据，使用insert性能较低。可以使用**load**指令
~~~使用load
#客户端连接服务器，加上--local-infile
mysql --local-infile -u root -p pwd
#设置全局参数为1后，开启本地加载文件导入数据的开关
set global local-infile = 1;
#执行load指令
load data local infile '/root/sql.log' into table `user` fields terminated by ',' lines terminated by'\n'
~~~
### 2、order by 优化
1、using filesort 通过索引查询出数据，在缓冲区再进行排序。
2、using index 通过索引查询出的数据就已经是排好序的。
3、backward index scan 反向扫描索引
~~~
create index idx_ac01_phone_email on ac01(phone asc,email desc)  --创建索引可以指定排序，根据具体查询排序建立索引
~~~
4、如果避免不了大数据，适当增加缓冲区sort_buffer_size（默认256k）大小

### 3、limit 优化
可以通过覆盖索引，order by 索引字段
### 4、count 优化
count(id) < count(字段) < count(1) ≈ count(*)
### 5、update 优化
update 如果不是根据索引进行更新，会进行表锁
## 3、视图
### 1、基本语法
~~~
create or replace view v_ac01_1 as select id,aac003 from ac01 where id = 1; -创建
show create view v_ac01_1; -查看创建视图语句
alter view v_ac01_1 as select id,aac003,phone from ac01;     -修改视图
drop view if exists v_ac01_1;    -删除视图
~~~
### 2、检查选项
~~~
with cascaded check option   -对视图进行新增、修改、删除。会检查是否符合视图的定义。多个视图堆叠，往下的都会检查
with local check option -区别在于，只会检查当前视图
~~~
### 3、视图更新
想要update视图，需要保证视图数据与基础表存在一对一的关系。例使用聚合函数的视图
### 4、视图的作用
操作简单、数据安全、数据独立
## 4、存储过程、触发器、存储函数
## 5、锁
### 1、全局锁
对整个数据库实例加锁
使用场景，对全局进行备份
~~~
flush tables with read lock;  -加上全局锁
unlock tables； -解锁

mysqldump (--single transation  使用该参数不用加全局锁，底层用的快照机制) -uroot -p123 demo > demo.sql   ; --备份数据库
~~~
### 2、表级锁
1、表锁
	-表共享读锁。锁了所有session的写操作。
~~~
lock tables ac01 read;  --读锁
unlock tables;  --解锁
~~~
	-表独占写锁。锁了其他session的读写操作。
~~~
lock tables ac01 write;  --写锁
~~~
2、元数据锁(meta data lock,MDL)
表存在增删改查的事务活动时，上锁，禁止表结构变更。
同理表结构变更时，不允许增删改查。
~~~
select * from performance_schema.metadata_locks --查看当前元数据锁
~~~
3、意向锁
存在行锁事务活动时，会给全表加一个意向锁，用来判断该表是否存在行锁
	-意向共享锁。select...lock in share mode 添加。  与表锁共享锁兼容，表锁排他锁互斥。
	-意向排他锁。一些普通语句 添加。与表锁都互斥。意向锁之间不会互斥。
~~~
select * from performance_schema.data_locks --查看当前意向锁
~~~
### 3、行级锁
innodb的行级锁是基于索引项来加锁的。
1、行锁
2、间隙锁
锁定索引叶子节点之间的间隙，防止在叶子之间插入数据，防止幻读。（update.......where id = 7,其实id为7的行不存在，那么就会用间隙锁锁住这个间隙，
避免其他会话往里面插数据）。 间隙锁是共享锁
3、临键锁（非唯一索引）
同时锁住叶子节点和节点前面的间隙。（和间隙锁区别在，id为7的存在，会在7和左边一个叶子节点之间，右边叶子节点和7之间，加锁，避免其他会话插入id为7的值）
## 6、innodb
###、1逻辑存储结构
tablespace(表空间)-segment(段)-extant(区)-page(页)-row(行){trx_id:每行记录改动时的事务id，roll_pointer:指针，指向undo日志中的旧数据}
### 2、内存架构
	-Buffer poor,缓冲区是主内存的一块区域，增删改查都是操作的buffer poor里的数据，然后通过一定频率刷新到磁盘。
		以page为单位。三种页状态：1、free page(未被使用) 2、clean page(已被使用，但未被修改) 3、dirty page（被使用被修改，与磁盘数据不一致）
	-change buffer，针对非唯一二级索引，一些数据page不存在buffer poor中，那么这些数据的修改会先存到changebuffer中，等待数据加载早buffer poor
		中后，合并到buffer poor，再刷到磁盘。意义： 一般二级索引读写相对随机，耗费IO，这样可以减少磁盘IO。
	-adaptive hash index:自适应hash索引。如果mysql认为查询用hash会更快，则会建立hash索引。参数-innodb_adaptive_hash_index
	-log buffer,日志缓冲区，包括redolog，undolog，默认大小16Mb。
		参数-innodb_log_buffer_size:缓冲区大小；innodb_flush_log_at_trx_commit:日志刷新到磁盘时机;
### 3、磁盘结构
-system tablespace: 系统表空间，参数-innodb_data_file_path(系统表空间路劲)
-file-Per-Table-Tablespace: 每个表的文件表空间，包含单个表数据和索引。参数-innodb_file_per_table
-general tablespace: 通用表空间，自己创建，建表时可以指定表空间
~~~
create tablespace tb_cej add datafile 'cej.ibd' engine = innodb; -创建表空间
create table ac01()engine=innodb tablespace cej;
~~~
-undo tablespace: 撤销表空间。实例启动时自动创建两个默认的undo表空间，初始大小16M，用于存储undolog日志。
-temporary tablespace: 临时表空间。
-doublewrite buffer files: 双写缓冲区，innodb从bufferpoor数据页刷到磁盘前，先写入缓冲区文件中，便于系统异常时恢复数据。
-redolog: 重做日志，用来实现事务的持久性。保证出现异常时，数据的恢复。
### 4、后台线程
-master thread：核心后台线程，负责调度其他线程。脏页的刷新，合并插入缓存，undo页的回收。
-IO thread: 负责AIO的请求回调

| 线程类型             | 默认个数 | 职责                         |
| -------------------- | -------- | ---------------------------- |
| read thread          | 4        | 负责读操作                   |
| write thread         | 4        | 负责写操作                   |
| log thread           | 1        | 负责将日志缓冲区刷新到磁盘   |
| insert buffer thread | 1        | 负责将写缓冲区内容刷新到磁盘 |

~~~
show engine innodb status；  -查看innodb引擎信息
~~~
-Purge thread：回收事务已经提交了的undolog
-page cleaner thread：协助master thread 刷新脏页数据到磁盘，减轻master thread压力，减少阻塞。
### 5、事务原理
事务是一组操作的集合，
特性ACID。
A:atomicity,原子性。不可分割一部分，要么同时成功，要么同时失败。
C:consistency,一致性。所有数据都保持一致状态。
I:isolation,隔离性。数据库系统提供的隔离性，保证事务在不受外部并发操作影响的独立环境下运行。
D:durability，持久性。事务一旦提交或回滚，他对数据的改变是永久的。

| 事务隔离级别               | 脏读   | 不可重复读 | 幻读         |
| -------------------------- | ------ | ---------- | ------------ |
| 未提交读(read uncommitted) | 可能   | 可能       | 可能         |
| 已提交读(read committed)   | 不可能 | 可能       | 可能         |
| 可重复读(repeatable read)  | 不可能 | 不可能     | InnoDB不可能 |
| 串行化(serializable)       | 不可能 | 不可能     | 不可能       |

## 7、mvcc

当前读、快照读

### 1、隐藏字段
-DB_TRX_ID: 最近修改的事务id
-DB_ROLL_PTR: 回滚指针，指向上个版本
-DB_ROW_ID: 默认创建的唯一字段。

~~~
ibd2sdi ac01.ibd -查看表空间文件结构(某些版本没有该指令)
~~~
### 2、undo log
insert 产生的undolog日志会在事务提交时，立即被删除
update、delete 产生的不会被立即删除。这些日志不仅在回滚时需要，在快照读时也需要。

事务对数据进行修改时，会形成undolog版本链。通过DB_ROLL_PTR字段形成链表

### 3、readview
readview读视图包含四个核心字段
| 字段               | 含义   |
| -------------------------- | ------ |
| m_ids | 当前活跃的事务id集合   | 
|min_trx_id|最小活跃事务ID|
|max_trx_id|预分配事务ID，当前最大事务ID+1|
|creator_trx_id|readview创建者的事务ID|
## 8、系统数据库
| 数据库               | 含义   |
| -------------------------- | ------ |
|mysql | 存储mysql服务器正常运行所需要的各种信息(时区、主从、用户、权限等)   | 
|infomation_schema|提供了访问数据库元数据的各种表和视图，包含数据库、表、字段类型及访问权限等|
|performance_schema|为mysql数据库运行时状态提供了一个底层监控的功能，主要用于收集数据库服务器性能参数|
|sys|包含了一系列方便DBA和开发人员利用performance_schema性能数据库进行性能调优和诊断的视图|
## 9、常用工具
mysql -h(地址) -p(端口) -u(用户名) -p(密码) (数据库) -e(执行语句) "select * from tb_";  
mysqladmin 这个指令一般在脚本中使用，用来创建、删除数据库等。
mysqlbinlog 用来查看mysql二进制日志文件的
mysqlshow 客户端对象查找工具，查看、统计数据库表信息

mysqldump 客户端工具用来备份数据库或在不同数据库之间数据迁移
		-phuP 连接信息
		-n 不包含数据库创建的语句
		-t 不包含数据库表的创建语句
		-d 不包含数据
		-T 自动生成两个文件。.sql表结构文件，.txt数据文件
		例: mysqldump -uroot -p123 demo > demo.sql
		
mysqlimport 这个指令专门用来导入mysqldump -T 导出的数据
source 这么指令用来导入.sql文件。该命令在 mysql> 下使用
## 10、日志
mysql配置文件路劲  /etc/mysql/my.cnf
### 1、错误日志
该日志是默认开启的，默认位置/var/log/mysqld.log
~~~
show variables like '%log_error%' -查看位置
~~~
### 2、二进制日志
binlog 记录了所有DDl、DML日志，不记录select、show这样的语句
~~~
show variables like '%log_bin%' -查看相关信息
show variables like '%binlog_format%' -
show variables like '%binlog_expire_logs_seconds%' -设置logs文件过期，会自动删除
~~~
|日志格式|含义|
| ----|----|
|statemenet|基于sql语句的日志，对数据进行修改的sql都记录|
|row|基于行的日志，记录每一行数据的变更(默认)|
|mixed|混合两种模式，默认采用statement，某种情况自动切换|

作用:	1、灾难时的数据恢复
		2、mysql的主从复制
查看二进制日志文件：mysqlbinlog binlog.0000001
日志删除：
|指令|含义|
|-----|-----|
|reset master|删除全部日志，重置日志编号|
|purge master logs to 'binlog.*****'|删除编号之前的所有日志|
|purge master logs before 'yyyy-mm-dd hh24:mi:ss'|删除某一时间之前的所有日志|

### 3、查询日志
查询日志记录了所有操作日志，会很大，默认关闭
~~~
show variables like '%general_log%' -日志开关，默认关闭
~~~

### 4、慢查询日志
~~~
#开关
slow_query_log=1
#时间
long_query_time=2
#记录执行较慢的管理语句
log_slow_admin_statements=1
#记录执行较慢的未使用索引的语句
log_queries_not_using_indexes=1
~~~

记录时间超过long_query_time的查询语句。默认未开启，默认10秒，精度可以到微秒

## 11、主从复制
把主库的DDL和DML的二进制日志文件同步到从库中，并执行，从而保持数据的同步。
1、主库出现问题，可以切到从库提供服务
2、读写分离，降低主库压力
3、可以备份从库，避免影响主库服务
原理：基于保存DDL和DML日志的二进制日志binlog。slave存在一个IOthread从主库获取binlog，生成relay_log中继日志。
还存在SQLThread读取relay_log并执行。

### 搭建
1、修改配置信息/etc/mysql/my.cnf
~~~
#服务id，保证唯一
server-id=1
#是否只读，1代表只读
read-only=0
#不需要同步的数据库
binlog-ignore-db=mysql
#指定同步的数据
binlog_do_db=user
~~~
2、主库配置
~~~
#创建用户，可以从任意主机连接mysql服务
create user 'cej'@'%' identified with mysql_native_password by '123';
#分配主从复制权限
grant replication slave on *.* to 'cej'@'%'
~~~

## 12、分库分表

水平分表、垂直分配

### Mycat
mycat伪装了mysql协议，使用跟mysql方法一样

shardingsphere更广泛

