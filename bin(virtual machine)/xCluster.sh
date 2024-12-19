#!/bin/bash
if [ $# -eq 0 ]; then
	echo "Usage $(basename $0) start|stop"
	exit 0
fi
case $1 in

    "start"){
	echo " -------- 启动 zookeeper集群 ------"
	ssh niit01 zk.sh start
        echo " -------- 启动 hadoop集群 -------"
        ssh niit01 "/opt/onlineedu/hadoop/sbin/start-dfs.sh"
        ssh niit03 "source /etc/profile;/opt/onlineedu/hadoop/sbin/start-yarn.sh"
        ssh niit01 "/opt/onlineedu/hadoop/sbin/mr-jobhistory-daemon.sh start historyserver"
        echo " -------- 启动 hive ---------"
        ssh niit01 hv.sh start
	echo " -------- 启动 kafka集群 ------"
	ssh niit01 kf.sh start
	sleep 2
	echo " -------- 启动 maxwell -------"
	ssh niit01 mxw.sh start
	echo " -------- 启动 flume 脚本 --------"
	fl_hdfs.sh start
	fl_rabbitmq.sh start
	#f2.sh start
	#f3.sh start
    };;
    "stop"){
	echo " ------- 关闭 flume 脚本 --------"
	fl_hdfs.sh stop
	fl_rabbitmq.sh stop
	#f2.sh stop
	#f3.sh stop
	echo " ------- 停止 maxwell -------" 
	ssh niit01 mxw.sh stop
	echo " -------- 停止 kafka集群 ------"
	ssh niit01 kf.sh stop
        echo " -------- 停止 hive --------"
        ssh niit01 hv.sh stop
        echo " -------- 停止 hadoop集群 -------"
        ssh niit01 "/opt/onlineedu/hadoop/sbin/mr-jobhistory-daemon.sh stop historyserver"
        ssh niit03 "/opt/onlineedu/hadoop/sbin/stop-yarn.sh"
        ssh niit01 "/opt/onlineedu/hadoop/sbin/stop-dfs.sh"
	echo " -------- 停止 zookeeper集群 ------"
	ssh niit01 zk.sh stop
    };;
esac
