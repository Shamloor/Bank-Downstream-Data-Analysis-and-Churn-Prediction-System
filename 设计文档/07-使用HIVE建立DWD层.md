+ 建表`dwd_customer_data_full`
  + 将 `ods_customer_data_full` 表中的全量数据与 `ods_modify_logs_inc` 表中的增量数据变更日志合并
  + 根据日志中的 `Action` 字段执行对应的插入 , 更新或删除操作 , 生成最终状态的用户数据
  + 去除Id这一列

```hive
DROP TABLE IF EXISTS bank_db.dwd_customer_data_full;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.dwd_customer_data_full (
    CustomerId BIGINT COMMENT '客户唯一标识',
    Surname STRING COMMENT '客户姓氏',
    CreditScore INT COMMENT '信用评分',
    Geography STRING COMMENT '地理位置',
    Gender STRING COMMENT '性别',
    Age INT COMMENT '年龄',
    Tenure INT COMMENT '客户关系年限',
    Balance DOUBLE COMMENT '账户余额',
    NumOfProducts INT COMMENT '使用的银行产品数',
    HasCrCard BOOLEAN COMMENT '是否拥有信用卡',
    IsActiveMember BOOLEAN COMMENT '是否为活跃客户',
    EstimatedSalary DOUBLE COMMENT '估计薪资',
    Exited BOOLEAN COMMENT '是否流失'
)
COMMENT 'DWD层客户宽表，存储客户最新状态'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
LOCATION '/warehouse/bank_db/dwd/dwd_customer_data_full'
TBLPROPERTIES ('orc.compress' = 'snappy');
```

+ `dwd_customer_data_full`插入数据
  + `ods_customer_data_full`表的`CustomerID`可能重复 , 这种情况只保留最后一个重复的记录
  + 去重后对于`ods_customer_data_full`的现有数据 , 如果`ods_modify_logs_inc`表的`Action`为`UPDATE` , 则插入`ods_modify_logs_inc`的`NewValue` , 如果`ods_modify_logs_inc`表的`Action`为`DELETE` , 则不插入数据 , 两种情况都不符合则插入`ods_customer_data_full`的原有数据
  + 对于`Action`为`INSERT` , 则根据`ods_modify_logs_inc`的`NewValue`插入新数据

```hive
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
FROM final_data;
```

