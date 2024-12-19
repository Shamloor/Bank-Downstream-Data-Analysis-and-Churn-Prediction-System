from pyspark.ml.classification import GBTClassifier
from pyspark.ml.evaluation import BinaryClassificationEvaluator
from pyspark.ml.evaluation import MulticlassClassificationEvaluator
from pyspark.ml.feature import StringIndexer, VectorAssembler, OneHotEncoder, StandardScaler
from pyspark.sql import SparkSession
from pyspark.ml.feature import Bucketizer
from pyspark.sql import Row
from pyspark.sql.functions import col
from pyspark.sql.functions import udf
from pyspark.sql.types import ArrayType, DoubleType, StringType
import pyspark.sql.functions as F
import subprocess
import matplotlib.pyplot as plt
import numpy as np
from sklearn.metrics import roc_curve, auc
import joblib
import json

# create a spark session
spark = SparkSession.builder.master("local[*]").getOrCreate()

# Read train.csv dataset from hdfs
spark_df = spark.read.csv("hdfs:///warehouse/bank_db/dwd/train.csv", inferSchema=True, header=True)

spark_df.printSchema()

# change feature names to lower case
spark_df = spark_df.toDF(*[c.lower() for c in spark_df.columns])

# drop irrelevent features
spark_df = spark_df.drop('id', "customerid", "surname")

# Feature engineering
spark_df = spark_df.withColumn('creditscore_salary', spark_df.creditscore / spark_df.estimatedsalary)
spark_df = spark_df.withColumn('creditscore_tenure', spark_df.creditscore * spark_df.tenure)
spark_df = spark_df.withColumn('balance_salary', spark_df.balance / spark_df.estimatedsalary)

# spark_df.select("age").summary("count", "min", "25%", "50%","75%", "max").show()
bucketizer = Bucketizer(splits=[0, 35, 55, 75, 95], inputCol="age", outputCol="age_cat")
spark_df = bucketizer.setHandleInvalid("keep").transform(spark_df)
spark_df = spark_df.withColumn('age_cat', spark_df.age_cat + 1)

spark_df = spark_df.withColumn("age_cat", spark_df["age_cat"].cast("integer"))

def segment(tenure):
    if tenure < 5:
        return "segment_b"
    else:
        return "segment_a"

func_udf = udf(segment, StringType())
spark_df = spark_df.withColumn('segment', func_udf(spark_df['tenure']))

indexer = StringIndexer(inputCol="segment", outputCol="segment_label")
# indexer.fit(spark_df).transform(spark_df).show(5)
temp_sdf = indexer.fit(spark_df).transform(spark_df)
spark_df = temp_sdf.withColumn("segment_label", temp_sdf["segment_label"].cast("integer"))
spark_df = spark_df.drop('segment')

indexer = StringIndexer(inputCol="gender", outputCol="gender_label")
# indexer.fit(spark_df).transform(spark_df).show(5)
temp_sdf = indexer.fit(spark_df).transform(spark_df)
spark_df = temp_sdf.withColumn("gender_label", temp_sdf["gender_label"].cast("integer"))
spark_df = spark_df.drop('gender')

indexer = StringIndexer(inputCol="geography", outputCol="geography_label")
# indexer.fit(spark_df).transform(spark_df).show(5)
temp_sdf = indexer.fit(spark_df).transform(spark_df)
spark_df = temp_sdf.withColumn("geography_label", temp_sdf["geography_label"].cast("integer"))
spark_df = spark_df.drop('geography')

# One-Hot Encoding
encoders = [OneHotEncoder(inputCol=col, outputCol=col + "_ohe") for col in ["age_cat", "geography_label"]]
for encoder in encoders:
    spark_df = encoder.transform(spark_df)

# Generate feature names after One Hot Encoding
age_cat_ohe_count = len(spark_df.select("age_cat_ohe").first()["age_cat_ohe"])
geography_label_ohe_count = len(spark_df.select("geography_label_ohe").first()["geography_label_ohe"])

age_cat_ohe_names = [f"age_cat_ohe_{i}" for i in range(age_cat_ohe_count)]
geography_label_ohe_names = [f"geography_label_ohe_{i}" for i in range(geography_label_ohe_count)]

stringIndexer = StringIndexer(inputCol='exited', outputCol='label')

temp_sdf = stringIndexer.fit(spark_df).transform(spark_df)

spark_df = temp_sdf.withColumn("label", temp_sdf["label"].cast("integer"))

# User defined function to convert Vector into array
def vector_to_array(vector):
    return vector.toArray().tolist()

vector_to_array_udf = udf(vector_to_array, ArrayType(DoubleType()))

# Converting Vector types to arrays
spark_df = spark_df.withColumn("age_cat_ohe_array", vector_to_array_udf(col("age_cat_ohe")))
spark_df = spark_df.withColumn("geography_label_ohe_array", vector_to_array_udf(col("geography_label_ohe")))

# Expanding the array into multiple individual columns
for i in range(age_cat_ohe_count):
    spark_df = spark_df.withColumn(f"age_cat_ohe_{i}", col("age_cat_ohe_array")[i])
    
for i in range(geography_label_ohe_count):
    spark_df = spark_df.withColumn(f"geography_label_ohe_{i}", col("geography_label_ohe_array")[i])

# drop orininal vector columns
spark_df = spark_df.drop("age_cat_ohe", "geography_label_ohe", "age_cat_ohe_array", "geography_label_ohe_array")

# get feature columns after OHE
all_feature_names = [
    'creditscore', 'age', 'tenure', 'balance', 'numofproducts', 'hascrcard',
    'isactivemember', 'estimatedsalary', 'creditscore_salary', 'creditscore_tenure',
    'balance_salary', 'segment_label', 'gender_label'
] + [f"age_cat_ohe_{i}" for i in range(age_cat_ohe_count)] + [f"geography_label_ohe_{i}" for i in range(geography_label_ohe_count)]

# transform dataframe into vector
va = VectorAssembler(inputCols=all_feature_names, outputCol="features")
va_df = va.transform(spark_df)

final_df = va_df.select("features", "label")

# standard scale features vector 
scaler = StandardScaler(inputCol="features", outputCol="scaled_features")
final_df = scaler.fit(final_df).transform(final_df)

# split dataset into train_df and test_df
train_df, test_df = final_df.randomSplit([0.8, 0.2], seed=17)

# print("Training Dataset Count: " + str(train_df.count()))
# print("Test Dataset Count: " + str(test_df.count()))

# train a GBTClassifier using train_df and test using test_df
gbm = GBTClassifier(maxIter=100, featuresCol="scaled_features", labelCol="label")
gbm_model = gbm.fit(train_df)
y_pred = gbm_model.transform(test_df)

# save model to hdfs
# 提取模型参数
model_params = gbm_model.extractParamMap()
params_json = {param.name: value for param, value in model_params.items()}

# 保存参数到 JSON 文件
with open("gbdt_model_params.json", "w") as f:
    json.dump(params_json, f)
print("模型参数已保存到 gbdt_model_params.json")

# Model evaluation
evaluator = BinaryClassificationEvaluator(labelCol="label", rawPredictionCol="prediction", metricName='areaUnderROC')
evaluatorMulti = MulticlassClassificationEvaluator(labelCol="label", predictionCol="prediction")

acc = evaluatorMulti.evaluate(y_pred, {evaluatorMulti.metricName: "accuracy"})
precision = evaluatorMulti.evaluate(y_pred, {evaluatorMulti.metricName: "weightedPrecision"})
recall = evaluatorMulti.evaluate(y_pred, {evaluatorMulti.metricName: "weightedRecall"})
f1 = evaluatorMulti.evaluate(y_pred, {evaluatorMulti.metricName: "f1"})

# print("accuracy: %f, precision: %f, recall: %f, f1: %f" % (acc, precision, recall, f1))

# create dataframe of evaluation results
results = spark.createDataFrame([
    Row(accuracy=acc, precision=precision, recall=recall, f1=f1)
])

# evaluation results saved to CSV file on Linux
eva_path = "hdfs:///model/evaluation_results.csv"
results.write.csv(eva_path, header=True, mode="overwrite")

# print(f"Evaluation results saved to HDFS at {eva_path}")

# ROC cureve and AUC
evaluator = BinaryClassificationEvaluator(labelCol="label")
roc_auc = evaluator.evaluate(y_pred, {evaluator.metricName: "areaUnderROC"})

y_true = y_pred.select("label").collect()
y_scores = y_pred.select("probability").collect()

y_true = [row.label for row in y_true]
y_scores = [row.probability[1] for row in y_scores]

fpr, tpr, _ = roc_curve(y_true, y_scores)
roc_auc = auc(fpr, tpr)

plt.figure()
plt.plot(fpr, tpr, color='darkorange', lw=2, label=f'ROC curve (area = {roc_auc:.2f})')
plt.plot([0, 1], [0, 1], color='navy', lw=2, linestyle='--')
plt.xlim([0.0, 1.0])
plt.ylim([0.0, 1.05])
plt.xlabel('False Positive Rate')
plt.ylabel('True Positive Rate')
plt.title('Receiver Operating Characteristic')
plt.legend(loc="lower right")
plt.savefig("roc_curve.png", transparent=True, dpi=300)


# feature importances
importances = gbm_model.featureImportances.toArray()

# get top 10 feature importances
indices = np.argsort(importances)[-10:][::-1]
top_importances = importances[indices]
top_feature_names = np.array(all_feature_names)[indices]

plt.figure(figsize=(10, 6))
plt.title("Feature Importances (TOP 10)", fontsize=16)
plt.bar(range(len(top_importances)), top_importances, align="center")
plt.xticks(range(len(top_feature_names)), top_feature_names, rotation=60, fontsize=12)
plt.tight_layout()
plt.savefig("feature_importances.png", dpi=300, transparent=True)

# upload figure to hdfs
# local_path = "feature_importances.png"
# hdfs_path = "hdfs:///model/plots/feature_importances.png"
# subprocess.run(["hdfs", "dfs", "-put", local_path, hdfs_path], check=True)

# stop SparkSession
spark.stop()
