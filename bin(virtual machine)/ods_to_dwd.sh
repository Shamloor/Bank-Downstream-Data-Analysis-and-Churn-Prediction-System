#!/bin/bash

# 脚本名称: ods_to_dwd.sh
# 说明: 执行 Hive SQL，将 ODS 数据转换并加载到 DWD 层

# 获取当前日期，格式为 yyyy-MM-dd
today=$(date +%F)

# 定义 Hive SQL 脚本内容
hive_sql="
SET hive.strict.checks.cartesian.product=false;
SET hive.mapred.mode=nonstrict;

-- Step 1: 去重 ods_customer_data_full，保留每个 CustomerId 的最新记录
WITH ods_deduplicated AS (
    SELECT
        CustomerId,
        Surname,
        CAST(CreditScore AS INT) AS CreditScore,
        Geography,
        Gender,
        CAST(Age AS INT) AS Age,
        CAST(Tenure AS INT) AS Tenure,
        CAST(Balance AS DOUBLE) AS Balance,
        CAST(NumOfProducts AS INT) AS NumOfProducts,
        CAST(HasCrCard AS BOOLEAN) AS HasCrCard,
        CAST(IsActiveMember AS BOOLEAN) AS IsActiveMember,
        CAST(EstimatedSalary AS DOUBLE) AS EstimatedSalary,
        CAST(Exited AS BOOLEAN) AS Exited
    FROM (
        SELECT *,
               ROW_NUMBER() OVER (PARTITION BY CustomerId ORDER BY Id DESC) AS rn
        FROM bank_db.ods_customer_data_full
    ) t
    WHERE t.rn = 1
),

-- Step 2: 处理增量日志，生成需要插入的数据
logs_processed AS (
    SELECT
        NewValue.CustomerId AS CustomerId,
        NewValue.Surname AS Surname,
        CAST(NewValue.CreditScore AS INT) AS CreditScore,
        NewValue.Geography AS Geography,
        NewValue.Gender AS Gender,
        CAST(NewValue.Age AS INT) AS Age,
        CAST(NewValue.Tenure AS INT) AS Tenure,
        CAST(NewValue.Balance AS DOUBLE) AS Balance,
        CAST(NewValue.NumOfProducts AS INT) AS NumOfProducts,
        CAST(NewValue.HasCrCard AS BOOLEAN) AS HasCrCard,
        CAST(NewValue.IsActiveMember AS BOOLEAN) AS IsActiveMember,
        CAST(NewValue.EstimatedSalary AS DOUBLE) AS EstimatedSalary,
        CAST(NewValue.Exited AS BOOLEAN) AS Exited
    FROM bank_db.ods_modify_logs_inc
    WHERE Action IN ('INSERT', 'UPDATE')
),

-- Step 3: 生成最终需要插入的数据，处理 DELETE
final_data AS (
    SELECT *
    FROM ods_deduplicated o
    WHERE o.CustomerId NOT IN (
        SELECT RowId
        FROM bank_db.ods_modify_logs_inc
        WHERE Action = 'DELETE'
    )
    UNION ALL
    SELECT *
    FROM logs_processed
)

-- Step 4: 插入到 dwd_customer_data_full 表
INSERT OVERWRITE TABLE bank_db.dwd_customer_data_full
SELECT *
FROM final_data;"

# 执行 Hive SQL
/opt/onlineedu/hive/bin/hive -e "$hive_sql"

if [ $? -eq 0 ]; then
    echo "ODS 数据成功转换并加载到 DWD 层，日期: $today"
else
    echo "错误: 数据加载失败，请检查 Hive 日志。"
    exit 1
fi

