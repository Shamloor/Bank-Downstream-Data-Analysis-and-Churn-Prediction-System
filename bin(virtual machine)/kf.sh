#!/bin/bash
case $1 in
"start"){
    for i in niit01 niit02 niit03
    do
        echo " -------- 启动 $i Kafka -------"
        ssh $i "/opt/onlineedu/kafka/bin/kafka-server-start.sh -daemon /opt/onlineedu/kafka/config/server.properties"
    done
};;

"stop"){
    for i in niit01 niit02 niit03
    do
        echo " -------- 停止 $i Kafka -------"
        ssh $i "/opt/onlineedu/kafka/bin/kafka-server-stop.sh stop"
    done
};;
esac
