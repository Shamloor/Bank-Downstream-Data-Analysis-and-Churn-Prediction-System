#!/bin/bash

# 脚本名称: dwd_to_ads.sh
# 说明: 执行 Hive SQL，将 DWD 数据转换并加载到 ADS 层

# 获取当前日期，格式为 yyyy-MM-dd
today=$(date +%F)

# 定义 Hive SQL 脚本内容
hive_sql="
SET hive.strict.checks.cartesian.product=false;
SET hive.mapred.mode=nonstrict;

-- 添加流失率表
INSERT OVERWRITE TABLE bank_db.ads_customer_churn_rate
SELECT
    Geography,
    CASE
        WHEN Age BETWEEN 18 AND 30 THEN '18-30'
        WHEN Age BETWEEN 31 AND 45 THEN '31-45'
        WHEN Age BETWEEN 46 AND 60 THEN '46-60'
        ELSE '60+'
    END AS AgeGroup,
    Gender,
    COUNT(CASE WHEN Exited = TRUE THEN 1 ELSE NULL END) AS ExitedCount,
    COUNT(*) AS TotalCustomerCount,
    ROUND(COUNT(CASE WHEN Exited = TRUE THEN 1 ELSE NULL END) * 100.0 / COUNT(*), 2) AS ChurnRate
FROM bank_db.dwd_customer_data_full
GROUP BY Geography,
         CASE
             WHEN Age BETWEEN 18 AND 30 THEN '18-30'
             WHEN Age BETWEEN 31 AND 45 THEN '31-45'
             WHEN Age BETWEEN 46 AND 60 THEN '46-60'
             ELSE '60+'
         END,
         Gender
HAVING ROUND(COUNT(CASE WHEN Exited = TRUE THEN 1 ELSE NULL END) * 100.0 / COUNT(*), 2) >= 3;

-- 添加余额分布表
INSERT OVERWRITE TABLE bank_db.ads_customer_balance_distribution
SELECT
    CASE
        WHEN Balance < 10000 THEN '<10k'
        WHEN Balance BETWEEN 10000 AND 50000 THEN '10k-50k'
        WHEN Balance BETWEEN 50001 AND 100000 THEN '50k-100k'
        ELSE '>100k'
    END AS BalanceRange,
    COUNT(*) AS CustomerCount
FROM bank_db.dwd_customer_data_full
WHERE Balance IS NOT NULL
GROUP BY CASE
             WHEN Balance < 10000 THEN '<10k'
             WHEN Balance BETWEEN 10000 AND 50000 THEN '10k-50k'
             WHEN Balance BETWEEN 50001 AND 100000 THEN '50k-100k'
             ELSE '>100k'
         END
HAVING COUNT(*) >= 100;

-- 添加用户特征分布表
INSERT OVERWRITE TABLE bank_db.ads_customer_feature_distribution
SELECT
    Gender,
    CASE
        WHEN Age BETWEEN 18 AND 30 THEN '18-30'
        WHEN Age BETWEEN 31 AND 45 THEN '31-45'
        WHEN Age BETWEEN 46 AND 60 THEN '46-60'
        ELSE '60+'
    END AS AgeGroup,
    COUNT(*) AS CustomerCount,
    COUNT(CASE WHEN IsActiveMember = TRUE THEN 1 ELSE NULL END) AS ActiveCustomerCount,
    COUNT(CASE WHEN IsActiveMember = FALSE THEN 1 ELSE NULL END) AS InactiveCustomerCount
FROM bank_db.dwd_customer_data_full
GROUP BY Gender,
         CASE
             WHEN Age BETWEEN 18 AND 30 THEN '18-30'
             WHEN Age BETWEEN 31 AND 45 THEN '31-45'
             WHEN Age BETWEEN 46 AND 60 THEN '46-60'
             ELSE '60+'
         END;

-- 添加用户价值分级表
INSERT OVERWRITE TABLE bank_db.ads_customer_value_segmentation
SELECT 
    CASE 
        WHEN Balance >= 100000 THEN 'High'
        WHEN Balance BETWEEN 50000 AND 99999 THEN 'Medium'
        ELSE 'Low'
    END AS ValueSegment,
    COUNT(*) AS CustomerCount,
    ROUND(AVG(Balance), 2) AS AvgBalance,
    ROUND(AVG(CreditScore), 2) AS AvgCreditScore,
    ROUND(SUM(CASE WHEN IsActiveMember = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS ActiveCustomerRate,
    ROUND(SUM(CASE WHEN Exited = TRUE THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 2) AS ChurnRate
FROM bank_db.dwd_customer_data_full
GROUP BY CASE 
             WHEN Balance >= 100000 THEN 'High'
             WHEN Balance BETWEEN 50000 AND 99999 THEN 'Medium'
             ELSE 'Low'
         END;"

# 执行 Hive SQL
/opt/onlineedu/hive/bin/hive -e "$hive_sql"

if [ $? -eq 0 ]; then
    echo "DWD 数据成功转换并加载到 ADS 层，日期: $today"
else
    echo "错误: 数据加载失败，请检查 Hive 日志。"
    exit 1
fi

