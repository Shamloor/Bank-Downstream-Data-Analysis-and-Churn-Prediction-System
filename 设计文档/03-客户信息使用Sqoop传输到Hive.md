#### 流程

+ 安装sqoop1.4.7
  + 配置环境变量
  + 添加连接MySQL的jar包 `mysql-connector-java-5.1.27-bin.jar`

+ 清理根目录 (若重复上传则必须执行这一步)

```bash
hdfs dfs -rm -r /origin_data/bank/db/customer_data
```

+ 使用sqoop上传`customer_data`表

```bash
sqoop import \
--connect jdbc:mysql://niit01:3306/bank_db \
--username root \
--password 000000 \
--table customer_data \
--target-dir /origin_data/bank/db/customer_data \
--null-string '' \
--null-non-string '' \
--fields-terminated-by '\t' \
--lines-terminated-by '\n' \
--num-mappers 4
```

