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

