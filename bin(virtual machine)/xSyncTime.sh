#!/bin/bash

# 检查参数是否为空
if [ -z "$1" ]; then
        echo "Usage:  `basename $0` yyyy-MM-dd HH:mm:ss"
  exit 1
fi

# 使用date命令将时间字符串转换为日期和时间
# 如果转换失败，则说明时间字符串不合法
if ! date -d "$*" >/dev/null 2>&1; then
        echo "Wrong argument for $*"
        echo "Usage:  `basename $0` yyyy-MM-dd HH:mm:ss"
  exit 1
fi

echo ">>>>>>>>>>>> SYNC TIME START >>>>>>>>>>>>"
sum=-1

while [ $sum -ne 0 ]; do
  echo set time for niit01 to $1 '>>>'
  ssh niit01 "(sudo timedatectl set-ntp false && sudo date -s \"$*\" && sudo timedatectl set-ntp true)"
  ok1=$?
  echo sync time from niie02 to niit01 '>>>'
  ssh niit02 "(sudo timedatectl set-ntp false && sudo timedatectl set-ntp true)"
  ok2=$?
  echo sync time from niit03 to niit01 '>>>'
  ssh niit03 "(sudo timedatectl set-ntp false && sudo timedatectl set-ntp true)"
  ok3=$?
  sum=`expr $ok1 + $ok2 + $ok3`

  if [ $sum -eq 0 ]; then
    echo "<<<<<<<<<<<<< SYNC TIME END <<<<<<<<<<<<<"
    sleep 5
    xRun.sh date
    sudo hwclock -w
  else
    echo "sync time failed, will try 10 senconds later"
    sleep 10
  fi
done
