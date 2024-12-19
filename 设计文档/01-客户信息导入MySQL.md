#### 流程

+ 准备源文件train.csv (下载地址 : https://www.kaggle.com/competitions/playground-series-s4e1/data)
+ 删除源文件的id列
+ 创建MySQL数据库`bank_db`和客户信息表`customer_data`

```mysql
CREATE DATABASE IF NOT EXISTS bank_db;

USE bank_db;

DROP TABLE IF EXISTS customer_data;
CREATE TABLE customer_data (
    id INT AUTO_INCREMENT NOT NULL,          -- 主键
    CustomerId  BIGINT NOT NULL,             -- 客户ID（大整数）
    Surname VARCHAR(100),                    -- 客户姓氏
    CreditScore INT,                         -- 信用评分
    Geography VARCHAR(50),                   -- 所属国家或地区
    Gender VARCHAR(10),                      -- 性别
    Age INT,                                 -- 年龄
    Tenure INT,                              -- 贷款期限或账户年限
    Balance DECIMAL(15, 2),                  -- 账户余额（带小数）
    NumOfProducts INT,                       -- 产品数量
    HasCrCard TINYINT(1),                    -- 是否拥有信用卡（布尔值）
    IsActiveMember TINYINT(1),               -- 是否为活跃会员（布尔值）
    EstimatedSalary DECIMAL(15, 2),          -- 预估工资（带小数）
    Exited TINYINT(1),                       -- 是否离开（布尔值）
    PRIMARY KEY (id)                         -- 设置主键
);
```

+ 因为MySQL系统变量`secure_file_priv`的影响 , 需要将文件转移到MySQL可信任的文件夹`/var/lib/mysql-files/`

```bash
sudo mv ~/train.csv /var/lib/mysql-files/
```

+ 导入csv文件数据 , 需要忽略第一行

```mysql
LOAD DATA INFILE '/var/lib/mysql-files/train.csv'
INTO TABLE customer_data
FIELDS TERMINATED BY ',' ENCLOSED BY '"'
LINES TERMINATED BY '\n'
IGNORE 1 ROWS
(CustomerId, Surname, CreditScore, Geography, Gender, Age, Tenure, Balance, NumOfProducts, HasCrCard, IsActiveMember, EstimatedSalary, Exited);
```



