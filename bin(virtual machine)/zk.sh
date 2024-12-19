#!/bin/bash
if [ $# -eq 0 ];then
    echo "Usage: $(basename $0) start|stop|status (zookeeper)"
    exit 0
fi
case $1 in
"start"){
    echo ---------- zookeeper 集群 启动 ------------
    xRun.sh "/opt/onlineedu/zookeeper/bin/zkServer.sh start"
};;
"stop"){
    echo ---------- zookeeper 集群 停止 ------------
    xRun.sh "/opt/onlineedu/zookeeper/bin/zkServer.sh stop"
};;
"status"){
    echo ---------- zookeeper 集群 状态 ------------
    xRun.sh "/opt/onlineedu/zookeeper/bin/zkServer.sh status"
};;
*){
    echo "Usage: $(basename $0) start|stop|status"
};;
esac
