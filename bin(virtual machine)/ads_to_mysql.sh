#!/bin/bash

# 脚本名称: ads_to_mysql.sh
# 说明: 清空 MySQL 目标表，导出 ADS 数据到 MySQL，并删除 Sqoop 生成的 .java 文件

# MySQL 配置
mysql_host="niit01"
mysql_db="bank_db"
mysql_user="root"
mysql_pass="000000"

# Sqoop 配置
sqoop_connect="jdbc:mysql://$mysql_host:3306/$mysql_db"

# 表名列表
tables=(
    "ads_customer_churn_rate"
    "ads_customer_balance_distribution"
    "ads_customer_feature_distribution"
    "ads_customer_value_segmentation"
)

# HDFS 目录列表
export_dirs=(
    "/warehouse/bank_db/ads/ads_customer_churn_rate"
    "/warehouse/bank_db/ads/ads_customer_balance_distribution"
    "/warehouse/bank_db/ads/ads_customer_feature_distribution"
    "/warehouse/bank_db/ads/ads_customer_value_segmentation"
)

# 清空目标表并执行 Sqoop 导出
for i in "${!tables[@]}"; do
    table=${tables[$i]}
    export_dir=${export_dirs[$i]}

    echo "清空 MySQL 表: $table"
    mysql -h $mysql_host -u $mysql_user -p$mysql_pass -e "TRUNCATE TABLE $mysql_db.$table;"

    if [ $? -eq 0 ]; then
        echo "执行 Sqoop 导出: $table"
        /opt/onlineedu/sqoop/bin/sqoop export \
            --connect $sqoop_connect \
            --username $mysql_user \
            --password $mysql_pass \
            --table $table \
            --export-dir $export_dir \
            --input-fields-terminated-by '\t'
        
        if [ $? -eq 0 ]; then
            echo "Sqoop 导出成功: $table"
        else
            echo "错误: Sqoop 导出失败: $table"
            exit 1
        fi
    else
        echo "错误: 清空 MySQL 表失败: $table"
        exit 1
    fi

    # 删除当前目录中 Sqoop 生成的 .java 文件
    echo "删除 Sqoop 生成的 .java 文件"
    find . -name "*.java" -type f -delete

done

echo "ADS 数据成功导出到 MySQL，所有操作完成。"
