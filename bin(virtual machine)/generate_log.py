# -*- coding: utf-8 -*-
import random
import pymysql
from faker import Faker
from decimal import Decimal
from datetime import datetime, timedelta
import json
import sys
import time

# 初始化 Fake 实例
fake = Faker()

# 配置 MySQL 数据库连接
db_config = {
    "host": "localhost",
    "user": "root",
    "password": "123456",
    "database": "bank_db",
}

# 定义随机生成修改日志的函数
def generate_modify_logs(num_logs=10, target_date=None):
    logs = []
    try:
        # 连接 MySQL 数据库
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor(pymysql.cursors.DictCursor)

        # 查询客户信息表中的所有记录
        cursor.execute("SELECT * FROM customer_data")
        customers = cursor.fetchall()

        for _ in range(num_logs):
            action = random.choice(['UPDATE', 'DELETE', 'INSERT'])

            # 如果指定了日期，则生成当天的随机时间
            if target_date:
                random_seconds = random.randint(0, 86399)  # 一天的秒数
                timestamp = datetime.strptime(target_date, "%Y-%m-%d") + timedelta(seconds=random_seconds)
            else:
                # 生成过去 10 秒内的随机时间戳
                random_seconds = random.randint(1, 10)
                timestamp = datetime.now() - timedelta(seconds=random_seconds)

            if action == 'UPDATE':
                customer = random.choice(customers)  # 随机选择一个客户记录
                column_to_modify = random.choice([
                    'CreditScore', 'Balance', 'Age', 'Geography', 'EstimatedSalary'
                ])
                old_value = customer[column_to_modify]

                # 根据字段类型生成新值
                if column_to_modify in ['CreditScore', 'Age']:
                    new_value = old_value + random.randint(-50, 50) if old_value else random.randint(0, 1000)
                elif column_to_modify == 'Balance':
                    new_value = round((old_value + Decimal(random.uniform(-500, 500))), 2) if old_value else 0.0
                elif column_to_modify == 'Geography':
                    new_value = fake.country()
                elif column_to_modify == 'EstimatedSalary':
                    new_value = round((old_value + Decimal(random.uniform(-1000, 1000))), 2) if old_value else 0.0
                else:
                    new_value = None

                # 创建完整的 JSON 数据
                updated_customer = customer.copy()
                updated_customer[column_to_modify] = new_value
                new_value_json = json.dumps(updated_customer, ensure_ascii=False, default=str)

                logs.append({
                    'TimeStamp': timestamp,
                    'Action': action,
                    'RowId': customer['CustomerId'],  # 修改为 CustomerId
                    'ModifiedColumn': column_to_modify,
                    'OldValue': old_value,
                    'NewValue': new_value_json
                })

            elif action == 'DELETE':
                customer = random.choice(customers)  # 随机选择一个客户记录
                logs.append({
                    'TimeStamp': timestamp,
                    'Action': action,
                    'RowId': customer['CustomerId'],  # 修改为 CustomerId
                    'ModifiedColumn': None,
                    'OldValue': None,
                    'NewValue': None
                })

            elif action == 'INSERT':
                # 生成 8 位数的唯一 CustomerId
                new_customer_id = random.randint(10000000, 99999999)

                # 模拟生成一个新的客户记录
                new_customer = {
                    'CustomerId': new_customer_id,
                    'Surname': fake.last_name(),
                    'CreditScore': random.randint(300, 850),
                    'Geography': fake.country(),
                    'Gender': random.choice(['Male', 'Female']),
                    'Age': random.randint(18, 90),
                    'Tenure': random.randint(0, 10),
                    'Balance': round(random.uniform(0, 1000000), 2),
                    'NumOfProducts': random.randint(1, 4),
                    'HasCrCard': random.randint(0, 1),
                    'IsActiveMember': random.randint(0, 1),
                    'EstimatedSalary': round(random.uniform(10000, 200000), 2),
                    'Exited': random.randint(0, 1)
                }
                
                # 将字典转换为 JSON 字符串
                new_customer_json = json.dumps(new_customer, ensure_ascii=False)  # 确保支持非 ASCII 字符

                logs.append({
                    'TimeStamp': timestamp,
                    'Action': action,
                    'RowId': None,  # 插入操作没有关联现有的 RowId
                    'ModifiedColumn': None,
                    'OldValue': None,
                    'NewValue': new_customer_json  # 存储为 JSON 格式字符串
                })

    except pymysql.MySQLError as e:
        print("MySQL 错误: {}".format(e))
    finally:
        if conn:
            conn.close()

    # 如果指定了目标日期，按时间戳排序
    if target_date:
        logs = sorted(logs, key=lambda x: x['TimeStamp'])

    return logs

# 将生成的日志插入到修改日志表中
def insert_logs_to_db(log):
    try:
        # 连接 MySQL 数据库
        conn = pymysql.connect(**db_config)
        cursor = conn.cursor()

        # 插入修改日志数据
        insert_query = """
            INSERT INTO modify_logs (TimeStamp, Action, RowId, ModifiedColumn, OldValue, NewValue)
            VALUES (%s, %s, %s, %s, %s, %s)
        """
        cursor.execute(insert_query, (
            log['TimeStamp'], log['Action'],
            log['RowId'], log['ModifiedColumn'], log['OldValue'], log['NewValue']
        ))

        conn.commit()
        print("成功插入一条修改日志！")
    except pymysql.MySQLError as e:
        print("MySQL 错误: {}".format(e))
    finally:
        if conn:
            conn.close()

# 主程序
if __name__ == "__main__":
    if len(sys.argv) == 2:
        # 获取命令行参数
        arg = sys.argv[1]
        if arg == "item_by_item":
            print("开始逐条插入日志，每隔 3 秒插入一条。按 Ctrl+C 中断。")
            try:
                while True:
                    log = generate_modify_logs(num_logs=1)[0]  # 生成一条日志
                    insert_logs_to_db(log)
                    time.sleep(3)
            except KeyboardInterrupt:
                print("操作中断。")
                sys.exit(0)
        else:
            try:
                datetime.strptime(arg, "%Y-%m-%d")  # 验证日期格式
                logs = generate_modify_logs(num_logs=20, target_date=arg)
                if logs:
                    for log in logs:
                        insert_logs_to_db(log)
                    print("成功插入 {} 条日志！".format(len(logs)))
            except ValueError:
                print("参数错误：请输入 'item_by_item' 或有效的日期（yyyy-mm-dd）。")
                sys.exit(1)
    else:
        # 无参数时生成实时日志
        logs = generate_modify_logs(num_logs=20)
        if logs:
            for log in logs:
                insert_logs_to_db(log)
            print("成功插入 {} 条日志！".format(len(logs)))
