

卸载docker
yum remove -y docker docker-client docker-client-latest docker-common docker-latest docker-latest-logrotate docker-logrotate docker-engine
安装yum-utils
yum install -y yum-utils
配置docker yum源
yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
安装docker
yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
配置docker镜像源
[容器镜像服务 (aliyun.com)](https://cr.console.aliyun.com/cn-hangzhou/instances/mirrors)

启动docker
systemtrl start docker
查看版本
docker -v
查看镜像
docker images
获取镜像
docker pull mysql:5.7.10
开机自动启动
systemctl enable docker

docker save [image]  --保存成.tar文件
docker load -i [文件名]   --加载文件成镜像

docker exec -it nginx bash
docker volume ls  --查看文件挂载
docker volume inspect [挂载名] --查看信息
docker volume rm [挂载名] --删除挂载数据卷

dockerfile

```
#指定基础镜像
from ubuntu:16.04
#配置环境变量，JDK安装目录
env JAVA_DIR=/usr/local
#拷贝jdk和java项目的包
copy ./jdk8.tar.gz $JAVA_DIR/
copy ./docker-demo.jar /tmp/app.jar
#安装JDK
RUN cd $JAVA_DIR && tar -xf ./jdk8.tar.gz && mv ./jdk1.8.0_144 ./java8
#配置环境变量
env JAVA_HOME=$JAVA_DIR/java8
env PATH=$PATH:$JAVA_HOME/bin
#入口，java启动命令
ENTRYPOINT ["java","-jar","/app.jar"]
```



```
#基础镜像
from openjdk:11.0-jre-buster
#拷贝jar包
copy ./docker-demo.jar /tmp/app.jar
#设置时区
env TZ=Asia/Shanghai
run ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
#入口，java启动命令
ENTRYPOINT ["java","-jar","/app.jar"]
```

自定义镜像

docker build -t myImage:1.0 /etc/dockerfile   --dockefile文件地址

容器网络互联
docker network create
docker network ls
docker network rm
docker network prune  --清除未使用的网络
docker network connect --指定容器加入某网络
docker network disconnect
docker network inspect  --查看网络详细信息 



部署应用项目
项目打包上传 ->
	->构建dockerfile文件
		->构建镜像：docker build -t [镜像名] [dockerfile文件位置]
			->运行镜像



docker一件部署：dockerCompose



















## redis

1、docker pull redis
2、docker run --name redis -p 6379:6379 --privileged=true  \
		-v /mydata/redis/master/redis.conf:/etc/redis/redis.conf \
		-v /mydata/redis/master/data:/data \
		-d redis:6.0.8 redis-server /etc/redis/redis.conf

mysql

docker run --name mysql -p 3306:3306  \
		-e MYSQL_ROOT_PASSWORD=123 \
		-d mysql:5.7.10