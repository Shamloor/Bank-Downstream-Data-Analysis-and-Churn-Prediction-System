+ 因为分析比较少 , 决定不建立DWS层



+ 客户流失率分析`ads_customer_churn_rate`

```hive
DROP TABLE IF EXISTS bank_db.ads_customer_churn_rate;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ads_customer_churn_rate (
    Geography STRING COMMENT '地理位置',
    AgeGroup STRING COMMENT '年龄段',
    Gender STRING COMMENT '性别',
    ExitedCount BIGINT COMMENT '流失客户数',
    TotalCustomerCount BIGINT COMMENT '总客户数',
    ChurnRate DOUBLE COMMENT '流失率'
)
COMMENT '客户流失率统计表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/warehouse/bank_db/ads/ads_customer_churn_rate'
TBLPROPERTIES ('orc.compress' = 'snappy');
```

+ `ads_customer_churn_rate`数据导入语句

```hive
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
         Gender;
```



+ 客户账户余额分布`ads_customer_balance_distribution`

```hive
DROP TABLE IF EXISTS bank_db.ads_customer_balance_distribution;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ads_customer_balance_distribution (
    BalanceRange STRING COMMENT '余额区间',
    CustomerCount BIGINT COMMENT '客户数量'
)
COMMENT '账户余额分布表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/warehouse/bank_db/ads/ads_customer_balance_distribution'
TBLPROPERTIES ('orc.compress' = 'snappy');
```

+ `ads_customer_balance_distribution`数据导入语句

```hive
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
         END;
```



+ 客户特征分布`ads_customer_feature_distribution`

```hive
DROP TABLE IF EXISTS bank_db.ads_customer_feature_distribution;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ads_customer_feature_distribution (
    Gender STRING COMMENT '性别',
    AgeGroup STRING COMMENT '年龄段',
    CustomerCount BIGINT COMMENT '客户数量',
    ActiveCustomerCount BIGINT COMMENT '活跃客户数',
    InactiveCustomerCount BIGINT COMMENT '不活跃客户数'
)
COMMENT '客户特征分布表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/warehouse/bank_db/ads/ads_customer_feature_distribution'
TBLPROPERTIES ('orc.compress' = 'snappy');
```

+ `ads_customer_feature_distribution`数据导入语句

```hive
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
```



+ 客户价值分层`ads_customer_value_segmentation`

```hive
DROP TABLE IF EXISTS bank_db.ads_customer_value_segmentation;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ads_customer_value_segmentation (
    ValueSegment STRING COMMENT '客户价值分层（高、中、低）',
    CustomerCount BIGINT COMMENT '客户数量',
    AvgBalance DOUBLE COMMENT '平均账户余额',
    AvgCreditScore DOUBLE COMMENT '平均信用评分',
    ActiveCustomerRate DOUBLE COMMENT '活跃客户率',
    ChurnRate DOUBLE COMMENT '流失率'
)
COMMENT '客户价值分层统计表'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/warehouse/bank_db/ads/ads_customer_value_segmentation'
TBLPROPERTIES ('orc.compress' = 'snappy');
```

+ `ads_customer_value_segmentation`数据导入语句

```hive
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
         END;
```

