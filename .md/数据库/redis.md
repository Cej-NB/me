1、基本类型
	string
	hash
	list
	set
	sortedset
2、特殊类型
	geo
	bitmap
	hyperlog

## 1、指令
### 1、通用命令
-keys [pattern] : 匹配key的值。    不建议在生产模式上使用，因为redis是单线程，会阻塞其他请求
-del [key] ：删除指定key值
-exists [key]：判断是否存在该key
-expire [key]: 设置有效期，单位秒
-ttl [key]：查看剩余有效期。    -2表示不存在，-1表示永久存在
### 2、String
基于SDS(动态字符串)实现，其基本编码方式RAW，最大上限，一个字段512Mb。存在三种编码
-raw编码，*ptr指向SDS
-embstr编码，如果存储的SDS长度小于44字节，则会采用embstr编码，此时RedisObject和SDS是一段连续的内存空间。申请内存时只需申请一次。
-int编码，如果是数字字符串，并且大小在Long_MAX下，采用int编码。直接将数据存放到*ptr上，刚好8字节，不再需要SDS

1、set
2、get
3、mset [key][value][key][value]批量添加键值对
4、mget [key][key][key]批量获取键值对
5、incr 让一整型自增
6、incrby [key][增量]
7、decr [key]自减
8、incrbyfloat [key][增量]
9、setnx [key][value]
10、setex [key]      添加同时设置有效期

### 3、hash
value值是个无序字典，可以存有结构的对象
1、h(m 批量)set [key] [field] [value]
2、h(m 批量)get [key] [field]
3、hgetall   返回所有键值对
4、hkeys
5、hvals 
### 4、list
和linklist类似，链表，支持正反向检索。元素可以重复、有序、插入删除快、查询速度一般。

3.2之后，默认使用quickList实现
1、lpush [key] [element]    向左侧插入一个元素
2、lpop [key]				移除左侧第一个元素
3、rpush [key] [element]
4、rpop [key]
5、lrange [key] [start] [end]   获取范围内元素
6、blpop和brpop 和没b的区别，有b的会阻塞，直到有元素可以移除
如何用list模拟栈、队列、阻塞队列

### 5、set
底层是一个hash表，与hashmap类似，只不过value都为null
-无序、元素不可重复、查找快、支持交集、并集、差集

-因为每次操作需要判断元素是否存在，所以要一个查询效率高的，用hash表，可以做hash运算快速定位。
-默认采用HT编码(也就是dict)
-当存储的是整数，且小于set-max-intset-enties时，采用intset编码(一种排序数组，使用二分查找快速查询)

1、sadd [key] [member] ....
2、srem [key] [menber] ....
3、scard [key] 返回个数
4、sismember [key] [member] 判断是否存在set
5、smembers 获取set中所有元素
6、sinter key1 key2 查询两个的交集
7、sdiff key1 key2 查询差集
8、sunion key1 key2 查询并集

### 6、sortedset
可排序set集合，底层实现是一个skipList和hash表
-可排序、元素不重复、查询速度快
-可以根据score值排序
-键值存储，键必须唯一

这种保存两种结构的，很耗内存。
所以在满足以下条件
-元素个数小于zset_max_zipList_entries,默认值128
-每个元素都小于zset_maxzipList_value,默认64
采用zipList来节省空间

1、zadd [key] [score] [member] 
2、zrem [key] [member]
3、zscore [key] [member]   查询元素score
4、zrank [key] [member]  获取元素排名
5、zcard [key]   获取元素数
6、zcount [key] [min] [max]   查询score指定范围内的元素个数
默认升序，降序则在z后面添加rev

## 2、java连接
### 1、jedis
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
</dependency>

jedis本身是线程不安全的，推荐使用JedisPool连接池。

### 2、SpringDataRedis
提供redisTemplate同一api操作redis
支持redis发布订阅、哨兵、集群
支持各种对象序列化和反序列化
支持基于reidis的JDKCollection

默认使用lecture实现
|api|返回类型||
|---|---|---|
|redisTemplate.opsForValue()|valueOperation|针对string|
|...|...|...|...|



## 3、redis持久化
### RDB
redis database backup file   (redis数据备份文件、快照)
-save。 redis为单线程，这个命令会阻塞，不推荐使用
-bgsave。 使用额外进程单独进行保存。这个进程是主进程fork出来的，采用copy-on-write
-服务停止执行，配置中设置执行条件
在redis.conf中设置，定时rdb
~~~
#900内，如果至少1个key被修改，则执行bgsave
save 900 1
#是否压缩
rdbcompression yes
#RDB文件名称
dbfilename dump.rdb
#保存路径
dir ./
~~~


### AOF
append only file
记录命令日志文件，默认关闭
aof会比rdf大得多
bgrewriteaof命令重写aof文件。
~~~
#开关
appendonly yes
#名称
appendfilename "appendfile.aof"
#设置频率
appendfsync always  -立即
appendfsync everysec -先放入aof缓冲区，每隔一秒刷	到aof文件，默认方案
appendfsync no -先放入aof缓冲区,由操作系统决定时机刷入文件
~~~
### 主从复制
搭建主从架构
redis一般读多写少，所以选择一主多从的模式

replicaof [host] [port]    -从节点设置命令，临时的。 永久的可以在redis.conf中配置

主从数据同步
主从第一次同步为全量同步
1.0 slave 执行replicaof
1.1 slave 请求数据同步
1.2 master 判断是否第一次数据同步 ，判断replication id 一直不一直
1.3 master 第一次，返回数据版本号   replicationid 和offset
1.4 slave 保存版本信息

2.1 master 执行bgsave
2.2 master 发送rdb文件
2.3 slave 清空本地文件，加载rdb文件
2.4 master 记录传输rdb期间的所有命令，reol_baklog

3.1 master 发送repl_baklog
3.2 slave 执行接受到的命令

slave 做同步时，会告诉master 两个字段：replicationid(数据集id)和offset(偏移量)，master才知道同步哪些数据
repli_log大小有上限，当slave宕机过久，repli_log超过大小，就只能全量同步

## redis.conf

~~~conf
#是否以守护线程开启redis
daemonize no
#持久化 aof
appendonly no
#如果bind ip、未配置密码，满足这两条件，yes会让远程连接redis无法操作。
protected-mode no
#数据库数量 默认16
databases 16
~~~

## 4、redis 缓存
缓存更新策略
1、内存淘汰
不用自己维护，当内存不足时，自动淘汰一部分数据
2、超时剔除
给内存TTL时间，到期自动清除。
3、主动更新
更新数据库时，更新缓存，一致性好，但维护高

根据业务场景选择更新策略

1、低一致性需求，使用redis内置内存淘汰机制
2、高一致性需求，主动更新，并以超时机制兜底
	读操作：缓存命中直接返回，未命中查询数据库，并写入数据库，设定时间
	写操作：先写数据库，再删除缓存，要确保数据库和缓存的数据一致性

### redis 缓存穿透
请求的数据在缓存和数据库都不存在，则必须查完缓存还得查数据库，给数据库带来压力

1、缓存空对象
请求会在缓存建立一个空对象(设置TTL)。
实现简单，维护方便，但是内存消耗，会造成短期数据不一致
2、布隆过滤
客户端和redis之间添加一个布隆过滤
内存占用少，实现复杂，存在误判可能

增加id复杂度
做好数据的基础格式校验
加强用户权限校验
做好热点参数的限流

### redis 缓存雪崩
同一时段，大量的缓存key未命中或redis挂了，导致大量请求达到数据库

给不同的key设置不同的TTL
利用redis集群提高服务的可用性
给缓存业务添加降级限流策略
给业务添加多级缓存

### redis 缓存击穿
热点key问题，一个被高并发访问并且缓存重建业务复杂的key突然失效了，无数的请求会直达数据库

1、互斥锁
线程一 > 未命中 > 获取互斥锁 > 查询数据库建立缓存 > 释放锁
线程二 > 未命中 > 获取互斥锁失败 > 休眠一会重试 > 缓存命中

内存消耗小，保证一致性，实现简单。有可能会死锁，线程需等待，性能收影响
2、逻辑过期
不设置TTL，但是会加入一个时间字段，通过代码逻辑上判断该key是否过期

线程一 > 查询发现逻辑时间已过期 > 获取互斥锁 > 开启新线程去重建缓存，刷新逻辑时间 > 返回旧数据
线程二 > 查询发现逻辑时间已过期 > 获取互斥锁失败 > 返回旧数据

线程无需等待，性能好。不保证一致性，额外内存消耗，实现复杂

### 秒杀
全局唯一id：
在分布式环境下生成唯一id的工具
特性：
1、唯一性
2、高可用
3、高性能
4、递增性
5、安全性
基于redis生成全局唯一id的方案。
64b，1b为符号位，31b为时间戳，32b为序列号
redis对自增的值有上限2的26次方

乐观锁：不加锁，通过版本信息判断是否操作成功。
cas法： 查询num，update ... set num = num -1 where num = {查询num}，看的懂吗，我觉得你看得懂
添加版本号： 查询版本号，update 。。。 where 版本号 = {查询版本号}

## 5、分布式锁
1、多进程可见
2、互斥
3、高可用
4、高性能
5、安全性
三种实现方式：

||mysql|redis|zookeeper|
|互斥|利用数据库本身互斥锁|利用setnx这种互斥命令|利用节点的唯一性和有序性实现互斥|
|高可用|好|好|好|
|高性能|一般|好|一般|
|安全性|非常好|利用锁超时机制|非常好|

### 1、添加简单锁

~~~
# NX表示互斥、EX设置时间
set locak [] NX EX 10
~~~
见SimpleRedisLock类
2、误删锁
t1获取锁 -> t1阻塞 -> t1锁过期 -> t2获取锁 -> t1结束阻塞 -> t1把t2获取的锁释放了
方案：释放锁的时候判断一下是不是自己线程获取的锁，注意，判断和释放锁的操作得是原子性的，避免其他原因造成的阻塞影响

3、问题
-这种锁不可重入，可能会发生死锁现象
-无法自旋
-主从同步存在延迟，会导致问题

### 2、redission

一个redis分布式实现

```
<dependency>
	<groupId>org.redisson</groupId>
	<artifactid>redisson</artifactid>
</dependency>
@Configuration
public class RedisConfig{
	@Bean
	public RedissonClient redissonClient(){
		Config config = new Config();
		config.s;
		...
		return Redisson.create(config);
	}
}
```




## 6、redis 哨兵 sentinel
### 1、描述

1、监控，不断检查master和slave状态。
2、自动故障恢复，master故障，则将一个slave升级为master，故障master恢复后，也以新master为主
3、心跳机制，每隔一秒，每个实例发送ping命令
-主观下线，实例节点未在某响应时间内回应，则下线
-客观下线，超过数量的sentinel都认为该实例主观下线，则客观下线。
4、选举新master
-判断slave与master断开时间长短，超过down-after-milliseconds*10则会排除该节点
-判断slave-priority,越小优先级越大，0则不参与选举
-slave-priority一样，则判断offset，越大优先级越高
-最后判断运行id，越小优先级月高
5、故障转移
sentinel向选中的slave执行slaveof no one(别跪了)
向其他slave执行slaveof 新ip 新port

### 2、搭建哨兵

sentinel.conf

~~~
port 27001
sentinel annouce-ip 192.168.1.1
sentinel monitor [集群名称] 192.168.1.1 7001 2    -2为quorum
sentinel down-after-milliseconds [集群名称] 5000   -slave和master断开超时时间
sentinel failover-timeout [集群名称] 60000   -故障超时时间
dir "/mydata/sentinel/s1"    -工作目录
~~~
redis-sentinel /mydata/sentinel/s1/sentinel.conf   启动sentinel
7、redisTemplate的哨兵
1、配置信息
spring:
	redis:
		sentinel:
			master: [集群名称]
			node: #指定集群信息
				-192.168.1.1:27001
				-192.168.1.2:27002
配置读写分离

~~~
@Bean
public LettuceClientConfigurationBuilderCustomizer configurationBuilderCustomizer(){
	return configBuilder -> configBuilder.readFrom(ReadFrom.REPLICA_PREFERRD)
}
~~~
MASTER: 从主节点读
MASTER_PREFERRD: 优先主节点读
REPLICA: 从slave节点读

### 3、Raft




## 7、redis 分片集群

### 1、搭建

redis.conf

```
port 6379
cluster-enabled yes
cluster-config-file /mydata/redis/6379/redis.conf
#节点心跳失败的超时时间
cluster-node-timeout 5000
#持久化存放地址
dir /mydata/redis/6379
#绑定地址
bind 0.0.0.0
#后台运行
daemonize yes
#注册的实例ip
replica-announce-ip 192.168.1.1
#保护模式
protected-mode no
#数据库数量
databeses 16
#日志
logfile /mydata/redis/6379/run.log

```

启动所有redis服务后，启动集群管理
redis-cli --cluster create --cluster-replicas 1 ip1:port1 ip2:port2 ip3:port3 ip4:port4
查看集群状态
redis-cli -p 7001 cluster nodes

### 2、散列插槽

-redis会把master映射到0~16383共16384个插槽上。
-拿key的值计算，利用CRC16算法算出一个hash值，并对16384取模，得到slot值
-如果key的值中存在{}，取{}中的值计算。这样可以将同一类的key存在同一个插槽

### 3、集群伸缩

1、添加一个节点到集群
-redis-cli --cluster add-node 192.168.1.1:7004(新节点) 192.168.1.1:7001(集群中已存在的节点)
-redis-cli --cluster  reshard  192.168.1.1:7001  添加好后，需要分配插槽给新节点
		移动多少个插槽？-> 接受节点ID？ -> 提供插槽ID？ 
2、删除节点

### 4、故障转移

<img src="D:\work\zi\img\故障转移.png" alt="25" style="zoom:40%;" />

利用cluster failover命令，强制把执行该命令的slave变成master
三种模式：1、图上顺序，默认 2、force，忽略offset 3、忽略一切，直接成为master

### 5、访问集群

```
spring:
	redis:
		cluster:
			nodes:
				192.168.1.1:7001
				192.168.1.1:7002
				192.168.1.1:7003
				192.168.1.1:7004
```



## 8、redis 多级缓存

### 1、JVM进程缓存

caffeine：
可以设置缓存上限、设置时间

## 9、redis消息队列

redis提供三种不同的方式实现消息队列。
-list结构，基于list结构模拟消息队列
-PubSub，基本的点对点消息模型
-stream，比较完善的消息队列

### -list结构

redis的list数据结构是双向链表
使用BLPOP、BRPOP模拟队列。
优点：
1、利用redis存储，不受限于jvm内存上限
2、基于redis做持久化，数据安全性有保证
3、可以满足消息有序性
缺点：
1、无法避免消息丢失
2、只支持单消费者

### -PubSub

publish-subscribe(发布-订阅)
命令：
publish [队列名] 消息发布信息
subscribe [队列名] 订阅某队列，一个队列可以被多个消费者订阅
优点：
1、支持多生产、多消费
缺点：
1、无法持久化
2、无法避免消息丢失
3、消息堆积会有上限

### -Stream

redis5.0引入的一种新的数据类型

![](D:\work\zi\img\xadd.png)

![](D:\work\zi\img\XRead.png)

## 10、数据结构

### 1、动态字符串SDS

redis是用C语言编写的，但是redis没有直接使用C语言编写。因为1、获取字符串长度需要计算2、非二级制安全3、不可修改
redis构建了一种新的字符串结构，简单动态字符串。

```
//redis定义了多种结构体
struct __attribute__ ((__packed__)) sdshdr8{
	uint8_t len; //已保存的字符串节数,不包含结束标识
	uint8_t alloc; //申请的总字节,不包含结束标识
	unsigned char flags; //不同SDS的头类型，用来控制头大小
	char buf[];
}
```

动态意味着可以扩容，新字符串小于1M，则每次长度翻倍，大于1M，每次加1M

### 2、intSet

整数集合，具备长度可变、有序

```
typedef struct intset{
	uint32_t encoding; //编码方式,支持16位、32位、64位
	uint32_t length;	//元素个数
	int8_t contents;	//整数数组8字节指针
}
```

1、redis会保证intset中的元素唯一、有序
2、具备类型升级机制，可以接省空间
3、底层使用二分查找来做查询

### 3、Dict

这玩意和HashMap类似
Dict由三个部分组成1、Dict(字典)2、DictHashTable(哈希表)3、DictEntry(哈希节点)

```
typedef struct dictht{
	dictEntry **table; //指向entry的指针
	unsigned long size; //哈希表大小
	unsigned long sizemask; //哈希表大小-1
	unsigned long used; //entry个数
}
```

```
typedef struct dictEntry{
	void *key; //键
	union{
		void *val; 
		uint64_t u64;
		int64_t s64;
		double d;
	} v;  //值
	struct dictEntry *next; //指向下一个元素
}
```

扩容：当元素太多，会发生过多的hash冲突，导致链表过长，查询效率变低
dict每次新增键值都会检查负载因子
1、loaderFactor > 1,且没有bgsave、bgrewriteaof等进程
2、loaderFactor > 5

### 4、zipList (压缩链表)

zipList是一种特殊的双端链表、由一系列特殊编码的连续内存块组成。

-可以看成连续内存的双端链表，但不是真的链表
-每个节点之间不是用指针连接，通过记录上一节点长度来寻址，这种内存占用低。一个指针一般占用8个字节
-链表过长，影响查询效率
-增加数据有可能会导致发生连续更新问题

|         |                                                              | 字节 |
| ------- | ------------------------------------------------------------ | ---- |
| zlbytes | 总字节数                                                     | 4    |
| zltail  | 尾偏移量，尾节点到起始地址之间的字数。通过这个可以确定表尾节点的地址 |      |
| zllen   | 整个entry节点数                                              |      |
| entry   |                                                              |      |
| ...     |                                                              |      |
| entry   |                                                              |      |
| zlend   | 结束标识（0xff）                                             |      |

#### 结构

struct entry{
	previous_entry_length;//前一个节点长度
	encoding;// 编码，记录content类型及长度
	contents; //保存内容
}
通过算出整个entry字节长度，算出下个节点位置
注意：zipList所有存储长度的都用小端字节序(低位字节在前、高位字节在后)

#### encoding编码：

00、01、10 代表记录的是字符串。

| 编码                                             | 编码长度 | 字符串大小                                |
| ------------------------------------------------ | -------- | ----------------------------------------- |
| 00pppppp                                         | 1bytes   | 63（2^6-1）                               |
| 01pppppp\|pppppppp                               | 2bytes   | 16383((2^6)(2^8)-1)                       |
| 10pppppp\|rrrrrrrr\|eeeeeeee\|yyyyyyyy\|pppppppp | 5 bytes  | 4294967295((2^6)*(2^8)*(2^8)(2^8)(2^8)-1) |

11代表记录的整数

| 编码     | 整数类型       |
| -------- | -------------- |
| 11000000 | int16_t        |
| 11010000 | int32_t        |
| 11100000 | int64_t        |
| 11110000 | 24位有符号整数 |
| 11111110 | 8位有符号数    |

#### 连锁更新问题

-当前一个字节小于254，previous_entry_length用一个字节
-大于256时，用5个字节，第一个字节为0xfe，后四个字节为长度
那么，这就有个问题。当一大串entry大小为253，当插入一个254字节的entry，则下一个字节的previous_entry_length变成5个字节，整体的entry就变成257个字节，那么下下个字节也变大。会导致**连锁更新**

### 5、quickList

zipList节省内存，但是如果内存过大，申请内存的效率就低，因为是申请连续的内存。所以必须限制zipList长度。
quitList是一个双端链表，每个节点都是一个zipList。控制zipList长度。
redis提供了一个配置项限制zipList大小，list-max-ziplist-size。

### 6、skipList(跳表)

-本质是双向链表，元素按升序排序，每个节点包含score和ele值
-节点按score排序，score一样按ele字典排序
-一个节点允许多个指针，指针跨度不同，层数是1~32，层级越大，跨度越大
-增删改查效率和红黑数基本一致，实现却更简单

### 7、RedisObject

redis中任意类型的键值都会被封装成redisObject，也叫redis对象

```
typedef struct RedisObject{
	unsigned type:4;  //对象类型 string:0、list:1、set:2、zset:3、hash:4
	unsigned encoding:4;  //底层编码方式
	unsigned lru:LRU_BITS; //标识对象最后一次被访问的时间，24bit
	int refcount; //对象引用计数器
	void *ptr; //指针，指向存放数据地址
} rb;
```

## 11、redis网络模型

### 1、用户空间和内核空间

为了避免用户应用冲突导致内核崩溃，用户应用和内核是分开的
进程的寻址空间分两部分：内核空间、用户空间
-用户空间只能执行Ring3等级的命令，不能直接调用系统资源。
-内核空间执行Ring0等级命令，调用一切系统资源

Linux为了提高I/O效率，用户、内核空间都加入了缓存。
-写数据，用户缓存区 ->内核缓存区 -> 设备
-读数据，设备 ->内核缓存区 ->用户缓存区 

### 2、阻塞I/O

用户进程在内核获取数据时、内核缓存到用户缓存，两个阶段都处于阻塞状态。

### 3、非阻塞I/O

用户进程访问内核，内核未获取数据前，会返回失败给用户进程。用户进程不会进入阻塞状态，而是不断的访问内核，直到获取数据

### 4、I/O多路复用

