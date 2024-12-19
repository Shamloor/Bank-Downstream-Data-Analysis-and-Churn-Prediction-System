#!/bin/bash

# 启动 Spark 集群
/opt/onlineedu/spark/sbin/start-all.sh

# 提交 Spark 训练任务，重定向输出
/opt/onlineedu/spark/bin/spark-submit \
    --master spark://niit01:7077 \
    --deploy-mode client \
    /home/niit/bin/model.py > /home/niit/bin/model_training.log 2>&1

