#### 流程

+ 检查Kafka Topic里是否有数据 , 只检查offset , 不消费记录

```bash
kafka-run-class.sh kafka.tools.GetOffsetShell \
--broker-list niit01:9092,niit02:9092,niit03:9092 \
--topic modify_logs --time -1
```

+ 配置flume agent文件
  + 从kafka接收数据 , source为`kafkaSource`
  + 使用拦截器
  + 为追求实时 , channel为`memory channel`
  + 数据上传至hdfs , sink为`hdfs sink` , 按照拦截器所得时间放在相关日期的子文件夹
  + 为了之后方便演示 , 滚动策略设置为更小

```properties
a1.sources = r1
a1.channels = c1
a1.sinks = k1

# Kafka Source 配置
a1.sources.r1.type = org.apache.flume.source.kafka.KafkaSource
a1.sources.r1.batchSize = 500
a1.sources.r1.batchDurationMillis = 2000
a1.sources.r1.kafka.bootstrap.servers = niit01:9092,niit02:9092,niit03:9092
a1.sources.r1.kafka.topics = modify_logs
a1.sources.r1.kafka.auto.offset.reset = latest
a1.sources.r1.kafka.consumerThreads = 3
a1.sources.r1.kafka.consumer.group.id = flume-hdfs-group

# Interceptor 配置
a1.sources.r1.interceptors = i1
a1.sources.r1.interceptors.i1.type = com.example.flume.DataCleanInterceptor$Builder

# Memory Channel 配置
# Channel c1 供 HDFS Sink 使用
a1.channels.c1.type = memory
a1.channels.c1.capacity = 50000
a1.channels.c1.transactionCapacity = 5000

# HDFS Sink 配置
a1.sinks.k1.type = hdfs
a1.sinks.k1.hdfs.path = /origin_data/bank/db/modify_logs/%Y-%m-%d/
a1.sinks.k1.hdfs.filePrefix = log
a1.sinks.k1.hdfs.round = false
a1.sinks.k1.hdfs.rollInterval = 60     
a1.sinks.k1.hdfs.rollCount = 20      
a1.sinks.k1.hdfs.rollSize = 20480   
a1.sinks.k1.hdfs.fileType = CompressedStream
a1.sinks.k1.hdfs.codeC = gzip

# 绑定 Source 和 Channels
a1.sources.r1.channels = c1

# 绑定 Sinks 和 Channels
a1.sinks.k1.channel = c1
```

+ 新建Maven项目 , 设置拦截器
  + 去除"data"以外的json对象
  + 去除转义字符 , 防止HIVE导入后`SELECT`时报错
  + 打包放在`/opt/onlineedu/flume/lib/`中 , 名称为`flume-interceptor-1.0-SNAPSHOT-jar-with-dependencies.jar`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.niit</groupId>
    <artifactId>flume-interceptor</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    	<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>
    <dependencies>
        <dependency>
            <groupId>org.apache.flume</groupId>
            <artifactId>flume-ng-core</artifactId>
            <version>1.9.0</version>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>fastjson</artifactId>
            <version>1.2.62</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.3.2</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            <plugin>
                <artifactId>maven-assembly-plugin</artifactId>
                <configuration>
                    <descriptorRefs>
                        <descriptorRef>jar-with-dependencies</descriptorRef>
                    </descriptorRefs>
                </configuration>
                <executions>
                    <execution>
                        <id>make-assembly</id>
                        <phase>package</phase>
                        <goals>
                            <goal>single</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

```java
package com.example.flume;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.flume.Event;
import org.apache.flume.Context;
import org.apache.flume.interceptor.Interceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class DataCleanInterceptor implements Interceptor {

    @Override
    public void initialize() {
        // 初始化操作
    }

    @Override
    public Event intercept(Event event) {
        // 获取事件 body 并解析 JSON
        String body = new String(event.getBody(), StandardCharsets.UTF_8);
        JSONObject jsonObject = JSON.parseObject(body);

        // 保留 "data" 部分
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            // 如果 "NewValue" 存在且为转义的 JSON 字符串，去除转义符
            if (data.containsKey("NewValue")) {
                String newValue = data.getString("NewValue");
                JSONObject parsedNewValue = JSON.parseObject(newValue); // 去除转义符
                data.put("NewValue", parsedNewValue);
            }

            // 获取 TimeStamp 并设置动态路径
            String timeStamp = data.getString("TimeStamp");
            if (timeStamp != null) {
                String dateFolder = timeStamp.split(" ")[0]; // 提取日期部分
                Map<String, String> headers = event.getHeaders();
                headers.put("hdfs.filePrefix", dateFolder);
            }

            // 设置新 body
            String cleanedData = data.toJSONString();
            event.setBody(cleanedData.getBytes(StandardCharsets.UTF_8));
        }

        return event;
    }

    @Override
    public List<Event> intercept(List<Event> events) {
        for (Event event : events) {
            intercept(event);
        }
        return events;
    }

    @Override
    public void close() {
        // 关闭操作
    }

    public static class Builder implements Interceptor.Builder {
        @Override
        public Interceptor build() {
            return new DataCleanInterceptor();
        }

        @Override
        public void configure(Context context) {
            // 配置读取（如有必要）
        }
    }
}

```

+ 准备flume启停脚本`fl.sh`

```bash
#! /bin/bash

case $1 in
"start"){
	echo " -------- 启动 $i flume -------"
	ssh niit03 "nohup /opt/onlineedu/flume/bin/flume-ng agent --conf-file /opt/onlineedu/flume/job/kafka-flume-hdfs.conf --name a1 -Dflume.root.logger=DEBUG,LOGFILE >/opt/onlineedu/flume/log1.txt 2>&1  &"
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

+ 启动Flume

```bash
fl.sh start
```

+ 检查Flume运行状态 , 系统正常运行 , 日志上传到HDFS

```bash
fl.sh status
```
