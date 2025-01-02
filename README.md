# 项目流程文档

### 数据类型

本项目使用两种类型的数据：

1. **客户信息**：来源于机器学习网站下载的官方数据集。
2. **模拟修改日志**：通过Python脚本生成的随机数据集。

### 数据导入MySQL

将上述两种数据导入MySQL数据库：

- **客户信息**：使用 `LOAD DATA INFILE` 命令高效导入。
- **模拟修改日志**：通过Python脚本直接将生成的日志数据写入MySQL。

### 数据传输

#### 客户信息传输

由于客户信息表数据静态且不变，采用一次性传输方式：

- 使用 **Sqoop** 将数据从MySQL传输到 **HDFS**。

#### 模拟修改日志传输

模拟修改日志的数据传输流程如下：

1. **数据变更监控**：
   - 使用 **MaxWell** 监听MySQL的binlog。
   - 数据变更时，MaxWell将变更数据传输到 **Kafka** 进行暂存。
2. **数据消费**：
   - 配置两个不同的 **Customer Group** 消费同一个 **Kafka Topic**，分别对应两个不同的 **Flume Agent**。
   - Flume Agent 1
     - 将数据传输至 **HDFS Sink**，用于构建数据仓库。
   - Flume Agent 2
     - 将数据传输至 **RabbitMQ Sink**，用于实时日志展示。
     - 注意：默认的Flume Jar包不包含RabbitMQ Sink，因此需自定义构建Jar包。参考项目：[flume-ng-rabbitmq](https://github.com/jcustenborder/flume-ng-rabbitmq/tree/master)。
     - 将构建好的Jar包添加到 `/lib` 目录，并适当修改配置以启用RabbitMQ Sink。
3. **自定义拦截器**：
   - 在 Flume Agent 的 Source 中自定义拦截器，实现以下功能：
     - 移除除 `"data"` 外的JSON对象。
     - 去除转义字符，避免HIVE导入后 `SELECT` 操作时报错。
   - 同样需打包并放入 `/lib` 目录以供使用。

### 构建数据仓库

使用 **Hive** 构建数据仓库，包含以下四个层级：

1. ODS层（Operational Data Store）
   - 存储客户信息全量表和模拟修改日志增量表。
2. DWD层（Data Warehouse Detail）
   - 对ODS层数据进行整合，将变更日志的增量数据合并到客户信息表，同时进行去重处理。
3. DIM层（Dimension）
   - 作为事务表，保存每条客户记录在各时间段的详细状态，支持历史数据查询。
4. ADS层（Application Data Store）
   - 直接来源于DWD层，包含四个展示表：
     - 客户流失率统计表
     - 账户余额分布表
     - 客户特征分布表
     - 客户价值分层表

为便于展示，使用 **Sqoop** 将ADS层表数据传输至MySQL数据库，以便Java应用程序更方便地访问。

### Spark训练模型与预测流失率

#### 模型训练

使用 **PySpark** 构建并训练一个梯度提升树分类器（GBTClassifier）：

1. **数据加载与预处理**：
   - 从 **HDFS** 加载银行客户数据。
   - 进行特征工程，包括类别变量索引和独热编码。
   - 标准化特征向量，并将数据集分割为训练集和测试集。
2. **模型训练与评估**：
   - 训练GBTClassifier模型并保存模型参数。
   - 评估模型性能（准确率、精确率、召回率、F1分数、ROC AUC）并将评估结果保存至HDFS。
   - 生成并保存ROC曲线及前十个重要特征的可视化图表。

#### 流失率预测

使用训练好的GBT模型进行流失率预测：

1. **模型加载与数据预处理**：
   - 从HDFS加载已保存的GBT模型参数。
   - 读取训练数据和新数据，进行特征工程（特征组合、分桶、索引、独热编码）。
2. **预测与保存结果**：
   - 使用加载的模型参数初始化并训练GBT分类器。
   - 对新数据进行预测，将预测结果转换为字符串格式后保存至HDFS中的 `predictions.csv` 文件。

### 前后端演示项目

采用 **SpringBoot 3** 和 **Vue 3** 构建前后端分离的演示项目，集成多种技术栈并使用 **JWT** 进行身份验证。参考项目：[SpringBoot-Vue-Template-Jwt](https://github.com/itbaima-study/SpringBoot-Vue-Template-Jwt/tree/main)。

#### 前端实现

- **Axios**：前端通过Axios向后端API发送请求。
- 功能模块
  1. 实时数据展示
     - 监听RabbitMQ队列，获取更新数据并保存至数组，前端通过Axios轮询逐条展示。
  2. 分析数据展示
     - 使用 **ECharts** 进行数据可视化展示。
  3. 训练模型
     - 提供界面触发模型训练脚本。
  4. 预测流失率
     - 提供界面触发流失率预测脚本。

#### 后端实现

- Controller层
  - 配置不同的Controller处理验证、文件上传、MySQL操作、SSH命令执行及数据更新获取等功能。
- Service层
  - Controller层调用Service层的具体功能函数。

### 自动化运行

提供按钮操作以自动运行后端脚本，对应脚本位于 `bin` 目录下：

- `everyday_update`：每日数据更新脚本。
- `model_training.sh`：模型训练脚本。
- `prediction.sh`：流失率预测脚本。

通过点击相应按钮即可执行对应的后端自动化任务，简化操作流程，提高工作效率。

### 结语

本项目通过整合多种技术栈，实现了从数据导入、传输、仓库构建到模型训练与预测的完整流程，并通过前后端分离的演示项目展示了数据的实时与分析展示功能。自动化运行脚本的配置进一步提升了项目的可操作性和维护性。


