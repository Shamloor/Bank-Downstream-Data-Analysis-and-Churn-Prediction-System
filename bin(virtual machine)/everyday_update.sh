#!/bin/bash

# 获取日期参数，默认为昨天
if [ -z "$1" ]; then
    update_day=$(date -d "yesterday" +%F)
elif [ "$1" == "today" ]; then
    update_day=$(date +%F)
    redirect_output=true
else
    update_day=$1
fi

echo "更新日期为: $update_day"

# 定义日志文件路径
log_file="/home/niit/bin/new_everyday_update.log"

# 根据是否需要重定向输出执行不同的操作
if [ "$redirect_output" = true ]; then
    exec > "$log_file" 2>&1
fi

# 执行各个脚本并传递 update_day 参数
/home/niit/bin/hdfs_to_ods_modify_logs.sh "$update_day"
if [ $? -ne 0 ]; then echo "执行 hdfs_to_ods_modify_logs.sh 失败"; exit 1; fi

/home/niit/bin/ods_to_dwd.sh "$update_day"
if [ $? -ne 0 ]; then echo "执行 ods_to_dwd.sh 失败"; exit 1; fi

/home/niit/bin/ods_to_dim.sh "$update_day"
if [ $? -ne 0 ]; then echo "执行 ods_to_dim.sh 失败"; exit 1; fi

/home/niit/bin/dwd_to_ads.sh "$update_day"
if [ $? -ne 0 ]; then echo "执行 dwd_to_ads.sh 失败"; exit 1; fi

# 执行最后的脚本（不需要参数）
/home/niit/bin/ads_to_mysql.sh
if [ $? -ne 0 ]; then echo "执行 ads_to_mysql.sh 失败"; exit 1; fi

echo "自动化分析完成，日期: $update_day"

