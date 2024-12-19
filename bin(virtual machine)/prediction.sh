#!/bin/bash

# 指定目录
directory="/home/niit/upload"

# 查找目录中的 CSV 文件
csv_file=$(find "$directory" -maxdepth 1 -type f -name "*.csv")

# 如果有 CSV 文件
if [ -n "$csv_file" ]; then
    mv "$csv_file" "$directory/newdata.csv"
    echo "CSV 文件重命名为 newdata.csv"
else
    echo "没有找到 CSV 文件，停止执行脚本"
    exit 1
fi

# 检查 HDFS 中是否有对应的文件并删除
/opt/onlineedu/hadoop/bin/hdfs dfs -test -e /warehouse/bank_db/dwd/newdata.csv
if [ $? -eq 0 ]; then
    /opt/onlineedu/hadoop/bin/hdfs dfs -rm /warehouse/bank_db/dwd/newdata.csv
fi

# 上传文件
/opt/onlineedu/hadoop/bin/hdfs dfs -put /home/niit/upload/newdata.csv /warehouse/bank_db/dwd/newdata.csv

# 提交 Spark 预测任务
/opt/onlineedu/spark/bin/spark-submit \
    --master spark://niit01:7077 \
    --deploy-mode client \
    /home/niit/bin/prediction.py > /home/niit/bin/prediction.log 2>&1

# 检查 HDFS 中的预测结果是否存在
/opt/onlineedu/hadoop/bin/hdfs dfs -test -e /model/predictions.csv
if [ $? -eq 0 ]; then
    # 将 HDFS 中的预测结果合并并保存到本地指定目录
    /opt/onlineedu/hadoop/bin/hdfs dfs -getmerge hdfs:///model/predictions.csv /home/niit/bin/predictions.csv
    echo "预测结果已保存到 /home/niit/bin/predictions.csv"
else
    echo "HDFS 中没有找到预测结果文件！"
    exit 1
fi

