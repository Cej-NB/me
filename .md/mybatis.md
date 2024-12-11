## 同类型比较
hibernate:
    -操作简单，开发效率高
    -基于全框架的全映射,大量pojo进行部分映射比较困难
    -大量反射影响服务器性能

mybatis:
    -轻量级、性能出色
    -开发效率略低于hibernate
## mybatis 获取参数的两种方式
    -${}  字符串拼接
    -#{}  占位符赋值
## insert 自增主键设置
    <insert id="insert" useGenerateKeys="true" keyProperty="id">
## 缓存机制
    一级缓存
        -在同一个SqlSession中存在的缓存，底层是一个HashMap
        -做增删改、提交或关闭，则会清空缓存
        -默认开启，不能关关闭。
                但是配置 mybatis: configuration: local-cache-scope: statement 。相当于关闭，默认session
        -不同SqlSession数据是隔离的
    二级缓存
        -SqlSession提交或关闭后，会把数据写入二级缓存，也是HashMap
        -增删改，也会清空缓存
        -再次查询相同数据的时候，就不用查询数据库了
        -多个SqlSession可以共用一个二级缓存
        -mapper.xml的namespace相同，那么共用二级缓存
        -使用二级缓存的pojo，需要实现Serializable
## 
