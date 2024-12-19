import json
from pyspark.ml.classification import GBTClassifier
from pyspark.sql import SparkSession
from pyspark.ml.feature import StringIndexer, VectorAssembler, OneHotEncoder, StandardScaler, Bucketizer
from pyspark.sql.types import StringType
from pyspark.sql.functions import udf, col

# 初始化 Spark Session
spark = SparkSession.builder.master("local[*]").appName("TrainAndPredictGBT").getOrCreate()

# ----------------- 1. 读取 GBT 模型参数 -----------------
with open("gbdt_model_params.json", "r") as f:
    params = json.load(f)

# ----------------- 2. 加载训练数据和新数据 -----------------
# 训练数据
train_data = spark.read.csv("hdfs:///warehouse/bank_db/dwd/train.csv", inferSchema=True, header=True)

# 新数据
new_data = spark.read.csv("hdfs:///warehouse/bank_db/dwd/newdata.csv", inferSchema=True, header=True)

# 特征列名小写
train_data = train_data.toDF(*[c.lower() for c in train_data.columns])
new_data = new_data.toDF(*[c.lower() for c in new_data.columns])

# ----------------- 3. 数据预处理与特征工程 -----------------
def preprocess_data(df, is_training=False):
    # 新增组合特征
    df = df.withColumn('creditscore_salary', df.creditscore / df.estimatedsalary)
    df = df.withColumn('creditscore_tenure', df.creditscore * df.tenure)
    df = df.withColumn('balance_salary', df.balance / df.estimatedsalary)

    # 年龄分桶
    bucketizer = Bucketizer(splits=[0, 35, 55, 75, 95], inputCol="age", outputCol="age_cat", handleInvalid="keep")
    df = bucketizer.transform(df).withColumn('age_cat', col('age_cat') + 1)
    df = df.withColumn("age_cat", col("age_cat").cast("integer"))

    # UDF 对 tenure 进行分组
    def segment(tenure):
        return "segment_b" if tenure < 5 else "segment_a"

    func_udf = udf(segment, StringType())
    df = df.withColumn('segment', func_udf(col('tenure')))

    # 字符串索引
    indexers = [
        StringIndexer(inputCol="segment", outputCol="segment_label", handleInvalid="keep"),
        StringIndexer(inputCol="gender", outputCol="gender_label", handleInvalid="keep"),
        StringIndexer(inputCol="geography", outputCol="geography_label", handleInvalid="keep")
    ]
    for indexer in indexers:
        df = indexer.fit(df).transform(df)

    # One-Hot 编码
    encoders = [OneHotEncoder(inputCol=col, outputCol=col + "_ohe") for col in ["age_cat", "geography_label"]]
    for encoder in encoders:
        df = encoder.transform(df)

    # 如果是训练数据，确保目标列存在
    if is_training:
        # 假设目标标签列是 "exited"，将其转换为 "label"
        label_indexer = StringIndexer(inputCol="exited", outputCol="label", handleInvalid="keep")
        df = label_indexer.fit(df).transform(df)

    # 组装特征列
    feature_cols = ['creditscore', 'age', 'tenure', 'balance', 'numofproducts', 'hascrcard',
                    'isactivemember', 'estimatedsalary', 'creditscore_salary', 'creditscore_tenure',
                    'balance_salary', 'segment_label', 'gender_label',
                    'age_cat_ohe', 'geography_label_ohe']

    va = VectorAssembler(inputCols=feature_cols, outputCol="features")
    df = va.transform(df)

    # 标准化特征
    scaler = StandardScaler(inputCol="features", outputCol="scaled_features", withMean=False, withStd=True)
    df = scaler.fit(df).transform(df)

    return df

# ----------------- 4. 使用训练的超参数初始化 GBTClassifier 模型 -----------------
gbt = GBTClassifier()

for name, value in params.items():
    gbt.set(gbt.getParam(name), value)


# 预处理训练数据（包含 label 转换）
train_data = preprocess_data(train_data, is_training=True)

# 预处理新数据（不包含 label 转换）
new_data = preprocess_data(new_data, is_training=False)


# ----------------- 预测新数据 -----------------
gbt_model = gbt.fit(train_data)
predictions = gbt_model.transform(new_data)


# 转换预测结果格式
predictions = predictions.withColumn("prediction_str", col("prediction").cast(StringType()))
predictions = predictions.withColumn("probability_str", col("probability").cast(StringType()))

# ----------------- 7. 保存预测结果 -----------------
output_path = "hdfs:///model/predictions.csv"
predictions.select("customerid", "prediction_str", "probability_str").write.csv(output_path, header=True, mode="overwrite")
print(f"预测结果已保存到 {output_path}")

# 关闭 Spark Session
spark.stop()









