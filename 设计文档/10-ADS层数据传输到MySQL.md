+ 在MySQL的`bank_db`数据库中建立对应ADS表

```mysql
USE bank_db;
```

```mysql
CREATE TABLE ads_customer_churn_rate (
    Geography VARCHAR(100),
    AgeGroup VARCHAR(50),
    Gender VARCHAR(10),
    ExitedCount INT,
    TotalCustomerCount INT,
    ChurnRate DECIMAL(5, 2)
);
```

```mysql
CREATE TABLE ads_customer_balance_distribution (
    BalanceRange VARCHAR(50),
    CustomerCount INT
);
```

```mysql
CREATE TABLE ads_customer_feature_distribution (
    Gender VARCHAR(10),
    AgeGroup VARCHAR(50),
    CustomerCount INT,
    ActiveCustomerCount INT,
    InactiveCustomerCount INT
);
```

```mysql
CREATE TABLE ads_customer_value_segmentation (
    ValueSegment VARCHAR(50),
    CustomerCount INT,
    AvgBalance DECIMAL(10, 2),
    AvgCreditScore DECIMAL(5, 2),
    ActiveCustomerRate DECIMAL(5, 2),
    ChurnRate DECIMAL(5, 2)
);
```



+ 使用sqoop导出数据

```bash
sqoop export \
--connect jdbc:mysql://niit01:3306/bank_db \
--username root \
--password 000000 \
--table ads_customer_churn_rate \
--export-dir /warehouse/bank_db/ads/ads_customer_churn_rate \
--input-fields-terminated-by '\t'
```

```bash
sqoop export \
--connect jdbc:mysql://niit01:3306/bank_db \
--username root \
--password 000000 \
--table ads_customer_balance_distribution \
--export-dir /warehouse/bank_db/ads/ads_customer_balance_distribution \
--input-fields-terminated-by '\t'
```

```bash
sqoop export \
--connect jdbc:mysql://niit01:3306/bank_db \
--username root \
--password 000000 \
--table ads_customer_feature_distribution \
--export-dir /warehouse/bank_db/ads/ads_customer_feature_distribution \
--input-fields-terminated-by '\t'
```

```bash
sqoop export \
--connect jdbc:mysql://niit01:3306/bank_db \
--username root \
--password 000000 \
--table ads_customer_value_segmentation \
--export-dir /warehouse/bank_db/ads/ads_customer_value_segmentation \
--input-fields-terminated-by '\t'
```

