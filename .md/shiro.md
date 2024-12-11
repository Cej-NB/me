# shiro

-authentication，登录认证
-authorization，权限验证
-session management，会话管理
-cryptography，密码管理

组件：
-Subject：交互主体
-Realm：相当于DataSource，从数据库获取权限数据、包括认证授权校验等功能
-SessionDao：对session会话操作的一套接口，将session存到redis或数据库
-CacheManage：缓存管理，将用户权限缓存，提高性能