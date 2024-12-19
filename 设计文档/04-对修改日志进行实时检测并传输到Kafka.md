#### 流程

+ 配置Maxwell的`config.properties`文件

```properties
log_level=info

#Maxwell数据发送目的地，可选配置有stdout|file|kafka|kinesis|pubsub|sqs|rabbitmq|redis
producer=kafka
#目标Kafka集群地址
kafka.bootstrap.servers=niit01:9092,niit02:9092,niit03:9092
#目标Kafka topic，可静态配置，例如:maxwell，也可动态配置，例如：%{database}_%{table}
kafka_topic=modify_logs

# 过滤仅监听 bank_db.modify_logs 表
include_dbs=bank_db
include_tables=modify_logs

#MySQL相关配置
host=niit01
user=maxwell
password=maxwell
jdbc_options=useSSL=false&serverTimezone=Asia/Shanghai

kafka.compression.type=snappy
kafka.retries=0
kafka.acks=1
```

+ 修改配置文件`/etc/my.cnf` , 这样才能将需要监听的数据库加入监听范围

```properties
binlog-do-db=bank_db
```

+ 重启mysql

```bash
sudo systemctl restart mysqld.service
```

+ 清空mysql数据库中maxwell的状态 , 以让新的maxwell设置文件生效

```mysql
DROP DATABASE maxwell;
```

+ 在kafka中创建`modify_logs`的TOPIC

```bash
kafka-topics.sh --create \
--bootstrap-server niit01:9092,niit02:9092,niit03:9092 \
--replication-factor 1 \
--partitions 3 \
--topic modify_logs
```

+ 检测是否有消息传入 , 若kafka能接收到新的信息则说明传输成功

```bash
kafka-console-consumer.sh \
--bootstrap-server niit01:9092,niit02:9092,niit03:9092 \
--topic modify_logs \
--from-beginning
```

+ 编写Maxwell脚本

```bash
#!/bin/bash
MAXWELL_HOME=/opt/onlineedu/maxwell

status_maxwell(){
  result=$(ps -ef | grep com.zendesk.maxwell.Maxwell | grep -v grep | wc -l)
  if [[ $result -gt 0 ]]; then
    echo "Maxwell 正在运行（进程数：$result）"
    return 0
  else
    echo "Maxwell 未运行"
    return 1
  fi
}

start_maxwell(){
  status_maxwell
  if [[ $? -ne 0 ]]; then
    echo "启动Maxwell"
    nohup $MAXWELL_HOME/bin/maxwell --config $MAXWELL_HOME/config.properties > $MAXWELL_HOME/maxwell.log 2>&1 &
  else
    echo "Maxwell 已在运行"
  fi
}

stop_maxwell(){
  status_maxwell
  if [[ $? -eq 0 ]]; then
    echo "停止Maxwell"
    # 优雅停止
    ps -ef | grep com.zendesk.maxwell.Maxwell | grep -v grep | awk '{print $2}' | xargs -r kill
    sleep 3
    # 强制停止未退出的进程
    ps -ef | grep com.zendesk.maxwell.Maxwell | grep -v grep | awk '{print $2}' | xargs -r kill -9
    echo "Maxwell 已停止"
  else
    echo "Maxwell 未在运行"
  fi
}

case $1 in
  start )
    start_maxwell
  ;;
  stop )
    stop_maxwell
  ;;
  restart )
    stop_maxwell
    start_maxwell
  ;;
  status )
    status_maxwell
  ;;
esac
```

+ 启动Maxwell

```bash
mxw.sh start
```

+ 查看Maxwell进程状态

```bash
mxw.sh status
```

+ 新开一个session , 查看Maxwell启动状态

```bash
tail -F /opt/onlineedu/maxwell/logs/MaxwellDaemon.out
```

