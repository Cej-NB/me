开源的分布式数据库生态项目

-shardingsphere-jdbc    轻量级java框架
-shardingsphere-proxy   数据库代理
-shardingsphere-sidecar kubernetes云原生数据库代理

## 1、高性能数据库集群架构模式
-读写分离：读操作和写操作会被路由到不同的主机上。
CAP(brewer's theorem) 布鲁尔定理： 在分布式系统中，只能保证一致性、可用性、分区容错性三种的两个，另外一个必须牺牲掉
C：一致性(consistency),对于客户端，读写操作保证能够返回最新的写操作结果
A:可用性(availability),非故障节点在合理的时间内返回合理的响应
P:分区容忍性(partition tolerance),在出现网络分区后，系统能够继续“履行职责”
理论上不存在CA

-数据分片:
垂直分片和水平分片

## 2、mysql主从复制

1、

3、创建master
~~~
#创建mysql实例
docker run -d -p 3306:3306 -v /mydata/mysql/master/conf:/etc/mysql/conf.d -e MYSQL_ROOT_PASSWORD=root --name mysql8-master mysql:8.0.34
#进入实例
docker exec -it mysql8-master env LANG=C.UTF-8  /bin/bash

~~~

4、主句中创建slave用户
~~~
--创建用户
create user 'cejcej'@'%';
--设置密码
alter user 'cejcej'@'%' identified with mysql_native_password by '123';
--授予复制权限
grant replication slave on *.* to 'cejcej'@'%';
--刷新权限
flush privileges；
~~~

5、主机中查询master状态
show master status；
记录File和Position的值
(binlog.000003 1461)

6、binlog_do_db、binlog_ignore_db
优先级 binlog_do_db > binlog_ignore_db

### 2、创建slave
1、docker run --name mysql8-slave1 -p 3307:3306 \
	-v /mydata/mysql/slave1/conf:/etc/mysql/conf.d \
	-e MYSQL_ROOT_PASSWORD=123 \
	-d mysql:8.0.34
2、创建配置文件
~~~conf
vim /mydata/mysql/slave1/conf/my.cnf
[mysqld]
server-id=2
#中继文件名
relay-log=relay-bin
~~~
3、在从机上配置主从关系
在从机mysql>
change master to master_host='192.168.1.1'
master_user='cejcej',master_password='123',
master_port=3306,
master_log_file='binlog.000001',master_log_pos=1357;
4、启动主从同步
start slave；
--查看状态
show slave status\G	
两个参数：slave_io_running：yes
		slave_sql_running：yes
都是yes表示成功了。
5、停止和重置
```sql
-- 在从机上执行。功能说明：停止I/O 线程和SQL线程的操作。
stop slave; 
-- 在从机上执行。功能说明：用于删除SLAVE数据库的relaylog日志文件，并重新启用新的relaylog文件。
reset slave;
-- 在主机上执行。功能说明：删除所有的binglog日志文件，并将日志索引文件清空，重新开始所有新的日志文件。
-- 用于第一次进行搭建主从库时，进行主库binlog初始化工作；
reset master;
```
6、问题
启动docker容器后提示 `WARNING: IPv4 forwarding is disabled. Networking will not work.`
```shell
#修改配置文件：
vim /usr/lib/sysctl.d/00-system.conf
#追加
net.ipv4.ip_forward=1
#接着重启网络
systemctl restart network
```

## 3、shardingsphere
事务测试：为了保证主从库间的事务一致性，避免跨服务的分布式事务，主从模型中，事务中的读写均用主库
* 不添加@Transactional：insert对主库操作，select对从库操作
* 添加@Transactional：则insert和select均对主库操作
 
负载均衡测试：
	提供了三种算法
		-轮询：ROUND_ROBIN
		-随机访问：RANDOM
		-权重访问：WEITHG
## 4、垂直分片




## 5、水平分片
不使用自增id，需要在业务代码中生成id

配置看项目配置application-hori.yml


雪花算法： 
64bit：1b - 符号位    基本为0，id都是选择整数
		41b -时间戳     能用到2086年
		12b -工作进程    1024个进程
		