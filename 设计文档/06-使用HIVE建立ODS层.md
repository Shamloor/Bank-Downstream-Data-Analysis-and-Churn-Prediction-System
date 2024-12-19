#### 流程

+ 建表`ods_customer_data_full`

```hive
DROP TABLE IF EXISTS bank_db.ods_customer_data_full;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ods_customer_data_full (
    Id BIGINT COMMENT '唯一标识',                     -- 主键
    CustomerId BIGINT COMMENT '客户唯一标识',          -- 用户标识
    Surname STRING COMMENT '客户姓氏',                -- 客户姓
    CreditScore INT COMMENT '信用评分',               -- 客户的信用分
    Geography STRING COMMENT '地理位置',              -- 客户所在地区
    Gender STRING COMMENT '性别',                     -- 客户性别
    Age INT COMMENT '年龄',                           -- 客户年龄
    Tenure INT COMMENT '客户关系年限',                -- 客户与银行关系的年限
    Balance DOUBLE COMMENT '账户余额',                -- 客户的账户余额
    NumOfProducts INT COMMENT '使用的银行产品数',     -- 客户使用的银行产品数量
    HasCrCard BOOLEAN COMMENT '是否拥有信用卡',       -- 是否持有信用卡
    IsActiveMember BOOLEAN COMMENT '是否为活跃客户',  -- 是否为活跃成员
    EstimatedSalary DOUBLE COMMENT '估计薪资',        -- 客户的估计薪资
    Exited BOOLEAN COMMENT '是否流失'                 -- 客户是否已流失
)
COMMENT 'ODS层客户信息表，支持全量更新'
ROW FORMAT DELIMITED FIELDS TERMINATED BY '\t'
NULL DEFINED AS ''
LOCATION '/warehouse/bank_db/ods/ods_customer_data_full';  -- 数据存储路径
```

+ 导入`ods_customer_data_full`数据

```hive
LOAD DATA INPATH "/origin_data/bank/db/customer_data"
INTO TABLE bank_db.ods_customer_data_full;
```

+ 建表`ods_modify_logs_inc`
  + 使用结构体处理嵌套JSON文本

```hive
DROP TABLE IF EXISTS bank_db.ods_modify_logs_inc;
CREATE EXTERNAL TABLE IF NOT EXISTS bank_db.ods_modify_logs_inc (
    LogID INT COMMENT '日志记录的唯一标识符',                      -- 唯一标识符
    `TimeStamp` STRING COMMENT '修改时间戳，格式：YYYY-MM-DD HH:mm:ss', -- 修改时间
    Action STRING COMMENT '操作类型 (INSERT, UPDATE, DELETE)',       -- 操作类型
    RowId INT COMMENT '被修改的记录ID',                              -- 关联的记录 CostumerID
    ModifiedColumn STRING COMMENT '被修改的列名称',                  -- 被修改的字段
    OldValue STRING COMMENT '修改前的值',                            -- 修改前的值
    NewValue STRUCT<
    Surname: STRING,
    Gender: STRING,
    IsActiveMember: BOOLEAN,
    Tenure: INT,
    CreditScore: STRING,
    NumOfProducts: INT,
    CustomerId: BIGINT,
    HasCrCard: BOOLEAN,
    Exited: BOOLEAN,
    EstimatedSalary: STRING,
    Id: BIGINT,
    Balance: STRING,
    Age: INT,
    Geography: STRING
	> COMMENT '嵌套JSON结构，用于存储修改后的值'
)
COMMENT 'ODS层修改日志表，用于存储增量日志数据'
PARTITIONED BY (dt STRING COMMENT '分区字段，格式为YYYY-MM-DD')        -- 按天分区
ROW FORMAT SERDE 'org.apache.hive.hcatalog.data.JsonSerDe'
LOCATION '/warehouse/bank_db/ods/ods_modify_logs_inc';                 -- 数据存储路径
```

+ 导入`ods_modify_logs_inc`数据
  + xxxx-xx-xx需改为对应日期

```hive
LOAD DATA INPATH "/origin_data/bank/db/modify_logs/xxxx-xx-xx"
INTO TABLE bank_db.ods_modify_logs_inc PARTITION (dt = 'xxxx-xx-xx');
```

