
## 1、

### 1、IOC
inversion of controller 
控制反转，强调的是创建Bean的权力反转给第三方






### 2、DI
dependency injection 
依赖注入，强调的是第三方的关系，关系由第三方去负责设置





### 3、AOP
aspect oriented programming
面向切面编程，功能的横向切取，主要实现方式proxy

配置目标对象 -> 配置通知对象 -> 配置aop

实现思想：   动态代理技术

aspect 五种通知类型

| 通知类型        |                        |
| --------------- | ---------------------- |
| before          | 目标方法之前执行       |
| after-returning | 执行之后执行           |
| around          | 环绕方法执行           |
| after-throwing  | 抛异常执行             |
| after           | 都会执行，不管是否异常 |

方法被调用，可以传一些参数

| 参数类型            |                            |
| ------------------- | -------------------------- |
| JoinPoint           | 连接点对象                 |
| ProceedingJoinPoint | 用于环绕通知，执行目标方法 |
| Throwable           | 异常对象                   |

配置方式：
aspect 相比更为灵活

1、aspect 方式
可以再配置中任意组装通知类型

2、advise 方式
需要通知类实现Advise接口的各种子接口

```
<aop:config>
	<aop:advisor advice-ref="" point-ref=""></aop:advisor>
</aop:config>
```

aop 两种动态代理方式

1、cglib
不需要接口，目标类不能使用final修饰。生成的代理对象是目标对象的子类

2、jdk动态代理
需要存在接口

#### 1、注解方式

```
@component
@Aspect
public class MyAdvise{

	@PointCut("execution(void com.spring.*.*(**))")
	public void pointCut(){}
	
	@Before("execution(void com.spring.*.*(**))")
	public void before(JoinPoint joinPoint){}
}

<aop:aspectj-autoproxy expose-proxy="true"/> --开启aop注解监听 
替代注解@EnableAspectJAutoProxy
```




## 2、Beanfactory和ApplicationContext
Beanfactory 是早期接口，成为bean工厂。
ApplicationContext为后期更高级的接口，称为spring容器。 

BeanFactory更偏向底层，ApplicationContext底层api大多是对BeanFactory的封装。
BeanFactory是延时加载，在调用getBean()的时候获取Bean，ApplicationContext在初始化的时候就创建Bean。

BeanFactory默认实现DefaultListableBeanFactory，ApplicationContext默认的工厂实现类。

基础Spring环境下，ApplicationContext有几个常用的实现类
|实现类|功能|
|----|----|
|ClassPathXmlApplicationContext|加载类路径|
|FileSystemXmlApplicationContext|加载磁盘路径|
|AnnotationConfigApplicationContext|加载注解配置类|
|AnnotationConfigWebApplicationContext|web环境，加载注解配置类|

### SpringBean配置
|xml配置方式|功能描述|
|---|---|
|<bean id="" class="">|Bean的id和全限定名|
|<bean name="">|设置别名，通过别名也能获取到实例|
|<bean scope="singleton">|作用范围，BeanFactory作为容器时取值singleton(单例)、prototype(每次调用getBean()都新建Bean,且不存进singletonObject里面)、request、session|
|<bean lazy-init="true">|实例化时机，是否延迟加载|
|<bean init-method="">|bean实例化后自动调用的初始化方法|
|<bean destroy-method="">|bean实例销毁前调用的方法|
|<bean autowire="byType">|自动注入模式，byType、byName|
|<bean factory-bean="" factory-method="">|指定哪个工厂的Bean方法|

### SpringBean实例化方式
1、构造方法实例化: 底层对构造方法进行bean的实例化
~~~
<bean id="userService" class="com.spring.service.impl.UserServiceImpl">
        <constructor-arg name="name" value="cj"/>
        <property name="userMapper" ref="userMapper"/>
    </bean>
~~~

2、静态工厂方法实例化: 底层通过自定义工厂对bean进行实例化
作用：在实例化容器对象前，可以做一些业务代码处理

会将factory-method指定的方法返回对象，注入容器
~~~
<bean id="simpleUserService" class="com.spring.factory.MySimpleFactory" factory-method="getUserService"/>
~~~
3、实例工厂：先实例化一个工厂对象注入容器，通过工厂方法返回一个对象，并注入
~~~
<bean id="simpleFactory" class="com.spring.factory.MySimpleFactory"/>
<bean id="spUserService" factory-bean="simpleFactory" factory-method="getMouseService"/>
~~~
4、FactoryBean接口
实现FactoryBean接口的工厂类，返回的实例对象会延迟实例化，并放入factoryBeanObjectCache属性中
### bean的注入方式
|注入方式|配置方式|
|---|---|
|set方法|<property name="" ref="">|
|构造方法|<constructor-arg name="" ref="">|

注入类型：
1、基本属性用value
2、引用对象用ref
3、数组类型用子标签
	例：<List><value></value></List>
		<map><entry key="" value-ref=""></entry></map>
		<props><prop key="">value</prop></props>
#### 自动装配
会去容器中找对应的名称或类型进行注入
autowire=""
byName： 根据setXxx()找对应的
byType： 根据类型找对应的，找到多个报错	
### 运行环境
System.setProperty("spring.profiles.active","dev");
bean设置运行环境
~~~
<bean profile="dev">
~~~
### BeanDefinition
Spring初始化时会将<Bean>标签 定义为一个 BeanDefinition 对象，存到beanDefinitionMap中。
Spring对beanDefinitionMap遍历，通过反射实例对象。
创建好的对象存进SingleOnObjects中。

## 3、Spring 后处理器
spring允许手动介入bean实例化过程
### Bean工厂后处理器     BeanFactoryPostProcessor
-实现该接口的Bean注入后，BeanDefinitionMap填充完毕后执行。
-可以判断业务后，动态的注入BeanDefinition。


### Bean后处理器     BeanPostProcessor
单例实例化后，填充到singleonObject之前
执行顺序:
BeanPostProcessor.postProcessBeforeInitialization()
InitializingBean.afterPropertiesSet()
对象初始化
BeanPostProcessor.postProcessAfterInitialization()

## 4、SpringBean 生命周期
初始化阶段:
1、Bean实例的属性填充
循环依赖问题：
三级缓存：Spring提供了三级缓存存储完整的Bean和半成品实例
在DafaultListableBeanFactory的爹的爹的爹，DefaultSingletonbeanRegistry类中，提供了三个Map
		1、最终存储成品的map-singletonObjects，一级缓存
		2、早期Bean单例池，earlySingletonObjects，二级缓存，对象已经被引用
		3、单例Bean的工厂池，singletonFactories，三级缓存，对象未被引用
2、Aware接口属性注入
3、BeanPostProcessor的before()方法回调
4、InitializingBean接口的初始化方法回调
5、自定义初始化方法回调
6、BeanPostProcessor的after()方法回调

## 5、Aware接口

## 6、整合mybatis
-配置 SqlSessionFactoryBean 和 MapperScannerConfigurer

-MapperFactoryBean, Mapper的factoryBean,调用getObeject()获取Mapper

## 7、自定义命名空间

(有空再研究)

## 8、基于注解

@Priamary  提高bean使用的优先级
@Profile("test")  指定bean的使用环境

## 9、基于AOP的事务

### 1、编辑式事务控制

三个主要类

|                                            |                                                          |
| ------------------------------------------ | -------------------------------------------------------- |
| PlatformTransationManager   平台事务管理器 | 一个接口，定义了事务提交、回滚，不同框架具有不同实现方案 |
| TransationDefinition   事务定义            | 封装事务的隔离级别、传播行为、过期信息等                 |
| TransationStatus   事务状态                | 存储当前事务状态                                         |



### 2、声明式事务控制

1、配置事务管理器

```
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
</bean>
```

2、配置事务通知

```
<tx:advice id="txAdvice" transaction-manager="transactionManager">
	<tx:attributes>
		<tx:method name="" isolation="" timeout="" read-only="" propagation="" ></tx:method>
	</tx:attributes>
</tx:advice>
<!-- isolation: 事务隔离级别
	propagation：事务的传播行为，解决事务嵌套问题-->
```

required：A调B，B必须有事务，B加入A的事务，如果A没有，B就自己创建
required_new:  无论A怎样，B都自己创建新的事务
supports：B是否有事务随A
not_supports: B无事务，A有则挂起
never: B无事务，A有则异常
mandatory：B加入A，A无事务则抛异常
nested: 

@EnableTransationManagement   开启声明式事务管理
@Transational  开启事务

## 10、javaWeb

javaWeb三大组件：
servlet: 服务器
filter: 过滤器
listener: 监听器	

服务器启动的时候listener去生成ApplicationContext，并保存到全局ServletContext中

```
xml方式配置监听器
<context-param>
    <param-name>contextConfigLocation</param-name>
    <param-value>classpath:spring.xml</param-value>
</context-param>
<listener>
    <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

## 11、springmvc

1、handleMapping先去容器中取自定义的类型，没有则取DispatcherServlet.properties默认的三种类型

2、文件上传需要手动开启文件上传参数解析器,id必须是multipartResolver，导入commons-fileupload的包

```
<bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartFile"/>
```

3、@RequestHeader("")  获取请求头某个值
	@CookieValue("") 获取cookie中某个值
	@RequestAttribute("") 获取request中某个值

4、静态资源的访问
-第一种，激活tomcat 的DefaultServlet。
-第二种，配置

```
<!--配置了这个后,会注册一个SimpleUrlHandleMapping，导致默认的三个HandleMapping失效，只能自己手动注册一个HandleMapping-->
<mvc:default-servlet-handler/>
<!-- 配置静态资源，直接映射到对应的文件夹，DispatcherServlet 不处理 -->
<mvc:resources mapping="/static/**" location="/static/"/>
```

5、4里的问题spring提供了mvc注解驱动

```
<mvc:annotation-driven/>   里面默认注册了HandleMapping和HandleAdapt,还做了一些其他的处理器和转换器
@EnableWebMvc   内部Import({DelegatingWebMvcConfiguration.class})
```

6、springmvc拦截器
filter由javaweb提供的，请求在进入servlet之前，先进入filter。
interceptor由spring提供，对进入dispatcherServlet之后的请求，再进入interceptor

```
<mvc:interceptors>
	<mvc:interceptor>
		<mvc:mapping path="/**"/>
		<bean id="">
	</mvc:interceptor>
</mvc:interceptors>
配置一个WebMvcConfig impl WebMvcConfigurer
public void addInterceptors(InterceptorRegistry registry) {
        //添加拦截器,拦截时的redisTemplate从这边注入
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns("/user/**").order(1);
}
```

preHandle()执行顺序取决于配置顺序,postHandle()和afterComplecation()则相反
