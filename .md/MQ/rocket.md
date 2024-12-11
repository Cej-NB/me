1、mq用途
	1-限流削峰
	2-异步解耦
	3-数据收集
2、常见MQ
    activeMQ、kafka、rocketMQ
3、MQ常见协议 
    1- JMS(java messaging service)
	2- STOMP(streaming text orientated message protocol)  一种简单文本协议  。 ActiveMq 实现
	3- AMQP(advanced message queuing protocol)   一种提供统一消息服务的应用层标准。 rabbitMQ 实现
	4- MQTT(message queuing telemetry transport) 
4、发展历程 
    2007年 阿里开始五彩石项目，notify 作为项目中 交易核心消息流转系统
	2010年 B2B 使用activeMQ 作为阿里消息内核。
	2011年 kafka开源。阿里开发了MetaMQ
	2012年 MetaMQ v3.0。在这基础上进一步做抽象，形成rocketMQ,并开源
	2015年 阿里在rocketMQ基础上 专门推出一款针对阿里云上用户的消息系统，aliwareMQ 
	2016年 阿里捐赠rocketMQ给apache 基金会
5、消息与主题 
    -topic表示一类消息的集合。每条消息只能属于一个topic
	-producer能生产多种topic
	-concumer只能消费一种类型的消息
6、队列 
    -一个topic中可以存放多个queue，queue中存放消息(queue类似于分区)
	   -多个分区可以让多个cosumer同时消费
7、消息标识 
	-producer 发送消息的时候会有messageId
	-broker 接收到消息也会生成一个offsetMessageId(brokerId+物理分区的offset)
	-key 用户指定的业务Id
8、生产者与生产者组 
9、消费者与消费者组 
10、nameserver整体功能介绍 
	-早期版本metaMq，也是使用zookeeper。从rocketMq开始使用自己的nameserver。
    -nameserver是一个broker、topic的注册中心，支持broker的动态注册和发现
	-broker管理。接受保存broker集群的注册信息，提供心跳机制检测broker是否还活着
	-路由信息管理。
11、nameserver的路由注册
	-nameserver集群之间不通信，broker启动的时候会轮询nameserver列表，与nameserver建立长连接，发起注册请求。
		nameserver内部维护一个broker列表，动态存储broker信息
	-这种方式区别与微服务的注册发现，不利于nameserver扩容。
	-broker每30秒发送一个心跳包给nameserver。
12、nameserver的路由剔除 
    -nameserver 10秒扫描broker列表，查看最新时间戳，是否超过120，超过剔除
	-运维停用broker，需要先禁用读写权限，等没有流量再停用
13、nameserver的路由发现
	-采用pull模型，拉取模型，客户端主动拉取路由，默认30秒。   实时性较差
	-push模型，推送模型。需要维护一个长连接，占用资源，但实时性较好
	-长轮询模型
14、客户端对nameserver的选择策略 
	-客户端的配置需要写上集群地址。连接时，客户端会取个随机数，取摸，得到索引。若连接失败，采用round-robin策略
15、broker功能介绍 
	-broker充当消息中转角色
		-remoting module 整个broker实体
			-client manager 客户端管理器
			-store service 存储服务。提供方便简单的api接口，处理消息存储到物理硬盘和消息查询功能
			-HA service 高可用服务。提供master broker和slave broker之间的数据同步功能。
			-index service 索引服务。根据特定的message key ，对消息进行索引服务，同时提根据message key对消息进行快速查询的功能。
	broker节点集群是一个主备集群。master负责读写请求，slave负责读请求。master和slave之间通过相同的brokerName，不同的brokerid来确定。
	brokerid为0是master，非0是slave。之间建立长连接，定时注册topic信息到所有nameserver。
16、rocket工作流程  
	1、启动nameserver，启动后监听端口，等待broker、producer、consumer连接
	2、启动broker，broker会与所有nameserver建立长连接，30秒发送心跳包
	3、收发消息前，创建topic，创建topic需要指定建立在哪些broker上，
	4、producer发送消息，启动时和nameserver集群其中一台建立长连接，并从nameserver获取路由信息，即当前topic消息的queue和broker地址的映射关系。
	5、consumer与nameserver建立长连接，获取topic路由信息，根据算法获取queue，再和broker建立长连接。同样30秒获取路由信息。
		不同于producer，consumer还要向broker发送心跳，确保broker存活状态。
17、topic的创建模式 
	-集群模式。该模式下创建的topic在该集群中，所有broker的queue数量是相同的
	-broker模式。可以不同
	自动创建时，默认采用broker模式。会为每个broker默认创建4个queue。
18、读写队列问题 17.19 3.13.23