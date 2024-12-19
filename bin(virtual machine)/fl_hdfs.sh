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
	ssh niit03 "tail -F /opt/onlineedu/flume/log1.txt"
};;
esac
