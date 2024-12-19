#!/bin/bash

# 脚本名称: ods_to_dim.sh
# 说明: 执行 Hive SQL，将 ODS 数据转换并加载到 DIM 层

# 获取当前日期，格式为 yyyy-MM-dd
today=$(date +%F)

# 定义 Hive SQL 脚本内容
hive_sql="
SET hive.support.concurrency=true;
SET hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager;
SET hive.strict.checks.cartesian.product=false;
SET hive.mapred.mode=nonstrict;

-- 处理 ods_customer_data_full 表中重复数据，只保留最后一条记录
WITH deduplicated_data AS (
    SELECT *
    FROM (
        SELECT *,
               ROW_NUMBER() OVER (PARTITION BY CustomerId ORDER BY Id DESC) AS rn
        FROM bank_db.ods_customer_data_full
    ) t
    WHERE rn = 1
)
INSERT INTO bank_db.dim_customer_lifecycle_zip
SELECT
    CustomerId,
    Surname,
    CreditScore,
    Geography,
    Gender,
    Age,
    Tenure,
    Balance,
    NumOfProducts,
    HasCrCard,
    IsActiveMember,
    EstimatedSalary,
    Exited,
    '1970-01-01' AS effective_date,
    '9999-12-31' AS expiration_date,
    TRUE AS is_current
FROM deduplicated_data;

-- 处理 ods_modify_logs_inc 表中 INSERT 操作
INSERT INTO bank_db.dim_customer_lifecycle_zip
SELECT
    NewValue.CustomerId,
    NewValue.Surname,
    NewValue.CreditScore,
    NewValue.Geography,
    NewValue.Gender,
    NewValue.Age,
    NewValue.Tenure,
    NewValue.Balance,
    NewValue.NumOfProducts,
    NewValue.HasCrCard,
    NewValue.IsActiveMember,
    NewValue.EstimatedSalary,
    NewValue.Exited,
    SUBSTR(\`TimeStamp\`, 1, 10) AS effective_date,
    '9999-12-31' AS expiration_date,
    TRUE AS is_current
FROM bank_db.ods_modify_logs_inc
WHERE Action = 'INSERT';

-- 更新旧记录的 expiration_date 和 is_current 标记
MERGE INTO bank_db.dim_customer_lifecycle_zip target
USING (
    SELECT
        RowId AS CustomerId,
        SUBSTR(\`TimeStamp\`, 1, 10) AS effective_date
    FROM bank_db.ods_modify_logs_inc
    WHERE Action = 'UPDATE'
) source
ON target.CustomerId = source.CustomerId
   AND target.is_current = TRUE
WHEN MATCHED THEN
UPDATE SET
    expiration_date = source.effective_date,
    is_current = FALSE;

-- 插入新记录
INSERT INTO bank_db.dim_customer_lifecycle_zip
SELECT
    NewValue.CustomerId,
    NewValue.Surname,
    NewValue.CreditScore,
    NewValue.Geography,
    NewValue.Gender,
    NewValue.Age,
    NewValue.Tenure,
    NewValue.Balance,
    NewValue.NumOfProducts,
    NewValue.HasCrCard,
    NewValue.IsActiveMember,
    NewValue.EstimatedSalary,
    NewValue.Exited,
    SUBSTR(\`TimeStamp\`, 1, 10) AS effective_date,
    '9999-12-31' AS expiration_date,
    TRUE AS is_current
FROM bank_db.ods_modify_logs_inc
WHERE Action = 'UPDATE';

-- 更新旧记录的 is_current 标记
MERGE INTO bank_db.dim_customer_lifecycle_zip target
USING (
    SELECT
        RowId AS CustomerId,
        SUBSTR(\`TimeStamp\`, 1, 10) AS effective_date
    FROM bank_db.ods_modify_logs_inc
    WHERE Action = 'DELETE'
) source
ON target.CustomerId = source.CustomerId
   AND target.is_current = TRUE
WHEN MATCHED THEN
UPDATE SET
    expiration_date = source.effective_date,
    is_current = FALSE;

-- 插入标记为删除的记录
INSERT INTO bank_db.dim_customer_lifecycle_zip
SELECT
    CustomerId,
    Surname,
    CreditScore,
    Geography,
    Gender,
    Age,
    Tenure,
    Balance,
    NumOfProducts,
    HasCrCard,
    IsActiveMember,
    EstimatedSalary,
    Exited,
    effective_date, -- 保留原记录的 effective_date
    SUBSTR(\`TimeStamp\`, 1, 10) AS expiration_date,
    TRUE AS is_current
FROM bank_db.dim_customer_lifecycle_zip old
JOIN bank_db.ods_modify_logs_inc logs
ON old.CustomerId = logs.RowId
WHERE logs.Action = 'DELETE'
  AND old.is_current = TRUE;"

# 执行 Hive SQL
/opt/onlineedu/hive/bin/hive -e "$hive_sql"

if [ $? -eq 0 ]; then
    echo "ODS 数据成功转换并加载到 DIM 层，日期: $today"
else
    echo "错误: 数据加载失败，请检查 Hive 日志。"
    exit 1
fi

