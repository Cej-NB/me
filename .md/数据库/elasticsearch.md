## elasticSearch 
	结合 kinabna、logstash、Beats 也就是 elastic stack。 广泛应用在日志数据分析、实时监控等邻域
	elasticSearch, 存储、计算、搜索数据
	logstash、Beats，数据抓取
	kinaba，数据可视化
	
	底层基于 apache lucene。 lucene是一个Java语言的搜索引擎类库。


​	
### 倒排索引
	-文档 document。
	
	-词条 term。
### 索引
	索引index：相同类型的文档集合。   类似table
	文档document：一条条数据。       类似row
	映射mapping：索引中文档的约束。   类似schema
	字段field： json文档中的字段。    类似column
	DSL：                            类似SQL
### docker
	docker run -d \
		--name es \
		-e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
		-e "discovery.type=single.node" \
		-v es-data:/usr/share/elasticsearch/data \
		-v es-plugins:/usr/share/elasticsearch/plugins \
		--privileged \
		--network es-net \
		-p 9200:9200 \
		-p 9300:9300 \
		elasticsearch:7.12.1
	
	9200是http请求之间的端口号
	9300是es容器之间互联的端口
	
	docker run -d \
		--name kibana \
		-e ELASTICSEARCH_HOSTS=http://es:9200 \
		--network es-net \
		-p 56501:5601 \
		kibana:7.12.1
	
	kibana需要和es在同一个网络
### 直接安装

	-需要创建用户运行，不允许用root用户
~~~
useradd user-es
chown user-es:user-es -R /usr/local/elasticsearch-7.13.2
~~~
    -非root用户被允许使用文件数量限制，需要设置成65536
        vim /etc/security/limits.conf
        添加 user-es soft nofile 65536
            user-es hard nofile 65536
            ulimit -Hn    该命令查看当前用户的值
    -修改
    	vim /etc/sysctl.conf
    	添加 vm.max_map_count=262144
    
    es8 默认使用https访问。
    修改 elasticsearch.yml
    	xpack.security.enabled: false
~~~
修改密码
/usr/local/elasticsearch-8.16.1/bin/elasticsearch-reset-password -u elastic -i
~~~
	ik 分词器分两种模式： ik_smart、ik_max_word

## 索引库操作
mapping常见属性：
type: 数据类型
index: 是否索引
analyzer: 分词器
properties: 子字段
~~~
创建、删除、修改、查询
PUT /cej
{
  "mappings": {
    "properties": {
      "name":{
        "type": "text",
        "analyzer": "ik_smart"
      },
      "age":{
        "type": "integer",
        "index": false
      }
    }
  }
}
~~~

## 文档操作
新增、删除、全量修改、局部修改
~~~
PUT /cej/_doc/文档id
{
	"name": "陈呢饥饿",
	"age": 27
}
~~~

## restClient

## DSL语法

-查询

~~~
GET /nc/_search
{
    "query":{
        "查询类型": {     --match、 match_all、multi_match、term、range
            "查询条件": ""   
        }
    }
}
例:
{
    "query": {
        "multi_match":{
            "query": "尼玛",
            "fields":["sName","pos","pid"]
        }
    }
}
~~~
-精确查询
    ·term:根据词条精确查询
    ·range:根据词条的范围查询
-相关性算法
-function score query
    ·过滤条件
    ·算分函数
