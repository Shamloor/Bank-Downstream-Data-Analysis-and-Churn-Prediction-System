+ 建表`dim_customer_lifecycle_zip`
  + 保存了每条客户记录在每个时间段的详细状态 , 支持查看历史数据
  + 因为上游的修改内容不能确定 , 保留了尽可能多的字段

```hive
DROP TABLE IF EXISTS bank_db.dim_customer_lifecycle_zip;
CREATE TABLE IF NOT EXISTS bank_db.dim_customer_lifecycle_zip (
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
    Exited BOOLEAN COMMENT '是否流失',
    effective_date STRING COMMENT '记录生效日期，格式为YYYY-MM-DD',
    expiration_date STRING COMMENT '记录失效日期，格式为YYYY-MM-DD',
    is_current BOOLEAN COMMENT '是否为当前有效记录'
)
COMMENT 'DIM层客户生命周期拉链表，记录客户的历史变更状态'
CLUSTERED BY (CustomerId) INTO 16 BUCKETS -- 使用 CustomerId 进行分桶，分为16桶
STORED AS ORC
LOCATION '/warehouse/bank_db/dim/dim_customer_lifecycle_zip'
TBLPROPERTIES (
    'orc.compress' = 'snappy', -- 使用 Snappy 压缩
    'transactional' = 'true'   -- 开启事务支持
);
```

+ `dim_customer_lifecycle_zip`插入数据
  + `ods_customer_data_full`表中的`CustomerId`这一列可能存在重复的数据 , 这种情况下只保留重复的最后一个记录 . 
  + 去重后从`ods_customer_data_full`表中插入数据 , `effective_date`为`1970-01-01` , `expiration_date`为`9999-12-31` , 标记为当前有效记录
  + 处理`ods_modify_logs_inc`表中`Action`为`INSERT`的操作 ,  插入一条新数据 , `effective_date`取自`ods_modify_logs_inc`表的`TimeStamp`这列 , 注意这个列的格式为`xxxx-xx-xx xx:xx:xx` , 需要只取日期忽略后面的`xx:xx:xx , expiration_date`为`9999-12-31` , 标记为当前有效记录
  + 处理`ods_modify_logs_inc`表中`Action`为`UPDATE`的操作 , 插入一条新数据 , `effective_date`取自`ods_modify_logs_inc`表的`TimeStamp`这列 , 注意这个列的格式为`xxxx-xx-xx xx:xx:xx` , 需要只取日期忽略后面的`xx:xx:xx` , `expiration_date`为`9999-12-31` , 标记为当前有效记录 . 与`INSERT`操作不同的是 , `UPDATE`操作需要更新旧的`CustomerId`(对应`ods_customer_data_full`表的`RowId`)的`expiration_date` , 其值为新纪录的`effective_date` , 然后将旧记录的`is_current`标记为false . 
  + 处理`ods_modify_logs_inc`表中`Action`为`DELETE`的操作 , 插入一条新数据 , `effective_date`和旧纪录一致 , 而`expiration_date`为`ods_modify_logs_inc`表的`TimeStamp`这列 , 注意这个列的格式为`xxxx-xx-xx xx:xx:xx` , 需要只取日期忽略后面的`xx:xx:xx` , 标记为当前有效记录 , 然后将旧记录的`is_current`标记为false . 
  + 因为可能涉及多次更新 / 删除 , 关于"旧的" "旧纪录"是如何判断的应该设定为"相同`CustomerId` +` is_current`为True" , 这样就只会更改一条记录 . 

```hive
-- 处理事务表
SET hive.support.concurrency=true;
SET hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager;

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
    SUBSTR(`TimeStamp`, 1, 10) AS effective_date,
    '9999-12-31' AS expiration_date,
    TRUE AS is_current
FROM bank_db.ods_modify_logs_inc
WHERE Action = 'INSERT';

-- 更新旧记录的 expiration_date 和 is_current 标记
MERGE INTO bank_db.dim_customer_lifecycle_zip target
USING (
    SELECT
        RowId AS CustomerId,
        SUBSTR(`TimeStamp`, 1, 10) AS effective_date
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
    SUBSTR(`TimeStamp`, 1, 10) AS effective_date,
    '9999-12-31' AS expiration_date,
    TRUE AS is_current
FROM bank_db.ods_modify_logs_inc
WHERE Action = 'UPDATE';

-- 更新旧记录的 is_current 标记
MERGE INTO bank_db.dim_customer_lifecycle_zip target
USING (
    SELECT
        RowId AS CustomerId,
        SUBSTR(`TimeStamp`, 1, 10) AS effective_date
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
    SUBSTR(`TimeStamp`, 1, 10) AS expiration_date,
    TRUE AS is_current
FROM bank_db.dim_customer_lifecycle_zip old
JOIN bank_db.ods_modify_logs_inc logs
ON old.CustomerId = logs.RowId
WHERE logs.Action = 'DELETE'
  AND old.is_current = TRUE;

```

+ 事务表的查看方式

```hive
-- 处理事务表
SET hive.support.concurrency=true;
SET hive.txn.manager=org.apache.hadoop.hive.ql.lockmgr.DbTxnManager;

SELECT * FROM dim_customer_lifecycle_zip;
```

