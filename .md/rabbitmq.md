# RabitMQ



rabit是基于erlang语言开发的

## docker

```
docker run \
-e RABBITMQ_DEFAULT_USER=root \
-e RABBITMQ_DEFAULT_PASS=123 \
-v mq-plugins:/plugins \
--name mq \
--hostname mq \
-p 15672:15672 \
-p 5672:5672 \
-d \
rabbitmq:3.8-management
```

整体架构：

1、publisher：消息发布者
2、consumer：消息消费者
3、queue：队列
4、exchange：交换机，负责路由信息

5、virtual-host：虚拟机，起到数据隔离的作用

## 配置

```
spring:
	rabbitmq:
    	host: 172.23.88.252
    	port: 5672
    	virtual-host: me
    	username: cej
    	password: 123
    	listener:
    		simple:
    			prefetch: 1 #一个队列多个消费者,默认不管消费能力怎样,都是一人一个。配置这个改成能者多劳

```

## 监听代码

```

@Component
public class MyListener  {

    @RabbitListener(queues = "cej-me")    //队列名
    public void listener1(String msg){
        System.out.println("listener1:"+msg);
    }

    @RabbitListener(queues = "fanout.cej-me") //交换机.队列名
    public void listener2(String msg){
        System.out.println("listener2:"+msg);
    }
}
```



## 交换机类型:

1、fanout-广播：会将接收到的消息分散给每一个绑定的queue
2、direct-定向：根据规则路由到指定queue
每个queue与exchange都有一个bindingkey
生产者发送routeKey，exchange根据bingdingkey指定队列。
3、topic，与direct类似。bingdingkey具有通配符功能 gils.* file.# 或  *.boys、#.boys
4、header，基于消息内容的headers属性进行匹配
	

可以在java项目中，声明式定义交换机和队列的关系

## 基于注解
监听 + 创建

```
@RabbitListener(bindings = @QueueBinding(
            value = @Queue(name="q1", durable = "true"),
            exchange = @Exchange(name="directEx",type= ExchangeTypes.DIRECT),
            key= {"red","blue"}
    ))
public void listener3(String msg){
    System.out.println("listener3:"+msg);
}
```

消息转化器：
默认是jdk序列化，可以设置一个messageConverter

```
@Bean
public MessageConverter messageConverter(){
	return new Jackson2JsonMessageConverter(); //变成json串
}
```

## 生产者重连

客户端连接MQ失败的情况下，重试机制。这是一种阻塞式的重试(springamqp的实现)，看业务需求，是否使用

```
spring:
	rabbitmq:
		connection-timeout: 1s #   设置连接超时时间
		template:
			retry:
				enabled: true #开启超时重试机制
				initial-interval: 1000ms #失败后的初始等待时间
				multiplier: 1 #失败后下次的等待时长倍数(按这个配置,第一次失败等待1s则第二次等待2s)
				max-attempts: 3 #最大重试次数
```

## 生产者可靠性

开启确认机制后，mq会在接受到信息后返回确认信息给生产者。
提供了两种确认机制：publisher confirm 和 publisher return
1、mq收到消息，但是路由失败。
2、临时消息和持久化消息接收成功，并入队，返回ACK
3、其他情况返回NACK，告知失败

```
spring:
	rabbitmq:
		publisher-confirm-type: correlated #开启publisher confirm机制，并设置了类型
		publisher-returns: true #开启publisher return机制
```

这里publisher-confirm-type有三种类型：
-none：关闭
-simple：同步阻塞等待MQ回执信息
-correlated：异步
returnCallBack代码实现

```
@Configuration
public class RabbitReturnBackConfig implements ApplicationContextAware{
	@Overrride
	public void setApplicationContext(ApplicationContext applicationContext){
		RabbitTemplate template = applicationContext.getBean(RabbitTemplate.class);
		template.setReturnCallBack((message,replyCode,relyText,exchange,routingKey)->{
			//应答码，原因，交换机，路由键，消息
		});
	}
}
```

confirmCallBack代码实现：

```
CorrelationData cd = new CorrelationData(UUID);
cd.getFuture.addCallBack(new ListenableFutureCallback<CorrelationData.Confirm>(){
	@Override
	public void onFailure(Throwable ex){
		//
	}
	@Override
	public void onSuccess(CorrelationData.confirm result){
		if(reusut.isAck){
			//收到ack
		}else{
			//收到nack
		}
	}
})
template.convertAndSend("direct","red","hello",cd); //添加回调
```

## 消息可靠性

-一旦mq宕机，内存信息丢失
-内存空间有限，有可能导致消息积压，引发阻塞


两种手段：

### 1、数据持久化  。

3.12版本之前

-交换机持久化-类型选择durable
-队列持久化-类型选择durable
使用spring代码定义交换机和队列时，默认就是持久化的

```
Message msg = MessageBuilder
	.withBody("hello".getBytes)
	.setDeliveryMode(MessageDeliveryMode.PERSISTENT).build(); //发送持久化类型消息，
```

### 2、Lazy Queue

3.12之后默认方式

惰性队列，接收信息后直接存入磁盘而非内存（内存只保留最近的消息，默认2048条）
支持数百万消息

## 消费者可靠性

当消费者处理消息后，应该向rabbitMq发送一个回执.
-ack,成功处理消息，从队列删除消息
-nack，处理失败，再次投递
-reject，处理失败并拒绝，从队列删除消息

springAMQP已经实现消息确认功能
-none，消息投递之后直接返回ack
-manual，手动模式
-auto，业务正常执行返回ack，出现异常，返回nack，校验异常返回reject

```
spring:
	rabbitmq:
		listener:
			simple:
				acknowledge-mode: auto
```

### 失败本地重试机制

当业务异常，消息会不断requeue，再次发送给消费者，带来不必要的压力。
可以利用spring提供的retry机制，本地重试，而不是无限制的reqeue。

```
spring:
	rabbitmq:
		listener:
			simple:
				retry:
					enabled: true #开启消费者失败重试
					initial-interval: 1000ms  #初始的失败等待时长
					multiplier: 1
					max-attempts: 3
					stateless: true  #true无状态，false有状态
					
```

失败消息处理策略
开启重试机制后，次数耗尽一九失败，则需要MessageRecoverer接口处理，
-RejectAndRequeueRecoverer：直接reject，丢弃消息。默认方式
-ImmediateRequeueMessageRecoverer:：返回nack，重新入队
-RepublishMessageRecoverer：将消息投递到指定交换机

```

//定义失败交换机、队列
@Configuration
@ConditionalOnProperty(prefix="spring.rabbitmq.listener.simple.retry",name="enabled",havingValue="true")
public class ErrorConfiguration{
	@Bean
	public DirectExchange errorExchange(){
		return new DirectExchange("error.direct")
	}
	@Bean
	public Queue errorQueue(){
		return new Queue("error.queue");
	}
	@Bean
	public Binding errorBinding(Queue errorQueue, DirectExchange errorExchange){
		return BingdingBuilder.bind(errorQueue).to(errorExchange).with("key");
	}
	@Bean
	public MessageRecoverer messageRecoverer(RabbitTemplate template){
		return new RepublishMessageRecover(template,"error.direct","key")
	}
}
```

## 业务幂等性

指一个业务执行一次和执行多次的影响是一致的

## 备份交换机

可以创建备份交换机

## 消息超时
可以针对队列 设置 消息的超时时间

## 死信
一个消息没有被处理，则变成死信
	-返回nack，但是没有重新返回队列
	-队列消息达到上限，先进先出，最开始的消息会丢失
	-设置了超时时间的消息未被消费
### 1、处理方法
	-直接丢弃
	-写入数据库后续处理
	-扔进死信队列，监听并处理。 配置死信交换机后，就会扔进去
### 2、创建死信交换机和队列
### 3、创建正常交换机和队列
	参数
		-x-dead-letter-exchange   	---指定死信交换机
		-x-dead-letter-routing-key  ---指定队列的路由键
		-x-max-length   			---最大长度
		-x-message-ttl   			---超时时间
## 延迟队列
	案例：火车票下单后，半小时内过期
	方案：1、借助过期时间+死信队列，消费者去绑定死信队列
		 2、rabbitmq安装插件
			怎么安装： 
				下载官方插件后，放到插件目录就行。docker需要挂载下目录
				执行linux命令：rabbitmq-plugins enable rabbitmq_delayed_message_exchange
			安装延迟队列插件后，选择队列类型为 x-delayed-message,并且需要添加argument， 例： x-delayed-type = direct 
			会改造交换机，让消息延迟发送给队列
## rabbitmq 高可用机制
	-普通集群模式
		单节点之间复制元数据，不复制队列内信息
		该模式节省空间，但不符合高可用，一旦某节点宕机就丢失消息
	-镜像集群模式
		单节点之间复制所有
		该模式性能差一些，但是符合高可用
## 如果有百万消息堆积在队列中，怎么办
	原因： 生产和消费的速度不一样，导致失衡
	解决方法：
		-1、提前预防
			-流量预估
			-做好压测
			-做好预案
		-2、应急处理
			-消费端: 
				-临时增加消费者实例
				-临时增加消费者把消息写入数据库，后续处理
			-生产端
				-适当的限流
		-3、事后优化
			-优化业务处理问题，提高业务处理效率，减少io，减少数据库操作、减少网络连接
## 如何解决消息堆积导致消息过期
	解决方法：
		-1、让消息不要被堆积
		-2、增加消息过期时间
		-3、设置死信队列
		-4、编写临时程序补发消息
	





























