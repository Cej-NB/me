


1、删除原yum源，rm -rf /etc/yum.repos.d/*
2、下载ali源，wget -O /etc/yum.repos.d/epel.repo http://mirrors.aliyun.com/repo/Centos-7.repo
3、清除缓存，yum clean all
4、新建缓存，yum makecache




### 1、apt-get
~~~
# 查看 apt-get 数据源
cat /etc/apt/sources.list

~~~

### 2、sed
~~~
#删除文本内容
sed '4d' 文件名  ---删除第几行
~~~


1、yum list installed | grep -i java ---查看yum安装的java相关软件
