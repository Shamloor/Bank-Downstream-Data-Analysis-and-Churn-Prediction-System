+ 进入RabbitMQ管理控制台`http://niit01:15672/`

  + 在RabbitMQ的exchanges里创建新的交换机(用于管理消息的路由规则) 名称为`db_update_exchange`

  + 在RabbitMQ的queues里创建新的队列(队列是消息的最终存储地，你需要创建一个队列来接收数据库更新事件) 名称为`db_update_queue`

  + 将交换机绑定到队列

+ maxwell将变化日志发送到Kafka , kafka传入Flume , 使用不同的kafka消费者组 , 使用两个flume agents同时向hdfs和rabbitmq发送信息

  + 添加jar包 , 参考这个2013年的项目https://github.com/jcustenborder/flume-ng-rabbitmq 在IDEA中编译 , 注意要将java版本换为1.8 , 将jar包放在Flume的`lib`目录下
  + 创建新的flume agent

  ```properties
  a1.sources = r1
  a1.channels = c1
  a1.sinks = rabbitmq-sink
  
  # Kafka Source 配置
  a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
  a1.sources.r1.batchSize = 500
  a1.sources.r1.batchDurationMillis = 2000
  a1.sources.r1.kafka.bootstrap.servers = niit01:9092,niit02:9092,niit03:9092
  a1.sources.r1.kafka.topics = modify_logs
  a1.sources.r1.kafka.auto.offset.reset = latest
  a1.sources.r1.kafka.consumerThreads = 3
  a1.sources.r1.kafka.consumer.group.id = flume-rabbitmq-group
  
  # Interceptor 配置
  a1.sources.r1.interceptors = i1
  a1.sources.r1.interceptors.i1.type = com.example.flume.DataCleanInterceptor$Builder
  
  # Memory Channel 配置
  a1.channels.c1.type = memory
  a1.channels.c1.capacity = 50000
  a1.channels.c1.transactionCapacity = 5000
  
  # RabbitMQ Sink 配置
  a1.sinks.rabbitmq-sink.type = org.apache.flume.sink.rabbitmq.RabbitMQSink
  a1.sinks.rabbitmq-sink.hostname = niit01
  a1.sinks.rabbitmq-sink.username = admin
  a1.sinks.rabbitmq-sink.password = admin
  a1.sinks.rabbitmq-sink.port = 5672
  a1.sinks.rabbitmq-sink.virtualhost = /
  a1.sinks.rabbitmq-sink.queuename = db_update_queue
  
  # 绑定 Source 和 Channels
  a1.sources.r1.channels = c1
  
  # 绑定 Sinks 和 Channels
  a1.sinks.rabbitmq-sink.channel = c1
  ```
  
+ 创建新的flume执行脚本`fl_rabbitmq.sh`

```bash
#! /bin/bash

case $1 in
"start"){
	echo " -------- 启动 $i flume -------"
	ssh niit03 "nohup /opt/onlineedu/flume/bin/flume-ng agent --conf-file /opt/onlineedu/flume/job/kafka-flume-rabbitmq.conf --name a1 -Dflume.root.logger=DEBUG,LOGFILE >/opt/onlineedu/flume/log2.txt 2>&1  &"
};;
"stop"){
	echo " -------- 停止 $i flume -------"
	ssh niit03 "ps -ef | grep kafka-flume-hdfs | grep -v grep | awk '{print \$2}' | xargs -n1 kill -9 "
};;
"status"){
	echo " -------- flume状态 ------"
	ssh niit03 "tail -F /opt/onlineedu/flume/log.txt"
};;
esac

```

+ 启动脚本`fl_rabbitmq.sh`

+ springboot后端添加`listener/MySQLUpdateListener`监听对应队列 , 当队列有新元素时会调用service层

+ service层`service/UpdateService`将监听获取到的数据保存到一个列表中 , 并写好了返回最新一条消息的函数

+ 前端通过axios轮询`controller/UpdateController` , 获取最新一条信息