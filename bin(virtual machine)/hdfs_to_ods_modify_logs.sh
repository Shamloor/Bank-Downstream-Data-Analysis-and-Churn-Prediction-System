#!/bin/bash

# 获取当前日期，格式为 yyyy-MM-dd
today=$(date +%F)

# 可接受用户指定日期作为参数，如果没有，使用当前日期
date_suffix=${1:-$today}

export HADOOP_HOME=/opt/onlineedu/hadoop
export PATH=$HADOOP_HOME/bin:$PATH

# 定义HDFS数据目录和表名
data_dir="/origin_data/bank/db/modify_logs/$date_suffix"
hive_table="bank_db.ods_modify_logs_inc"

# 检查HDFS目录是否存在
/opt/onlineedu/hadoop/bin/hadoop fs -test -d "$data_dir"
if [ $? -ne 0 ]; then
    echo "HDFS目录 $data_dir 无数据。"
    exit 1
fi

# 查找符合条件的HDFS文件（排除 .tmp 文件）
files=$(/opt/onlineedu/hadoop/bin/hadoop fs -ls "$data_dir" | grep -v ".tmp" | awk '{print $8}')

if [ -z "$files" ]; then
    echo "没有找到有效的文件用于加载：$data_dir。"
    exit 0
fi

# 构建单一的 Hive SQL 语句
hive_sql=""
for file in $files; do
    hive_sql+="LOAD DATA INPATH '$file' INTO TABLE $hive_table PARTITION (dt = '$date_suffix'); "
done

# 执行所有 LOAD DATA 命令
if [ -n "$hive_sql" ]; then
    echo "执行批量加载命令..."
    hive -e "$hive_sql"
    if [ $? -eq 0 ]; then
        echo "所有文件加载成功，日期: $date_suffix"
    else
        echo "文件加载失败，请检查日志。"
    fi
fi

