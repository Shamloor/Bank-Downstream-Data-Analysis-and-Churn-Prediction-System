+ Step1 在三台节点上安装python3.7 在三台节点上执行同样操作
  + 更新系统的包信息和工具：
    sudo dnf update -y
  + 安装编译 Python 所需的开发工具和库：
    sudo dnf groupinstall -y "Development Tools"
    sudo dnf install -y gcc gcc-c++ zlib-devel bzip2 bzip2-devel readline-devel sqlite sqlite-devel \
    openssl-devel tk-devel libffi-devel xz-devel wget
  + 运行以下命令从 Python 官方网站下载 Python 3.7 的源码：
    wget https://www.python.org/ftp/python/3.7.17/Python-3.7.17.tgz
  + 解压下载的 tar 包：
    tar -xvzf Python-3.7.17.tgz
    cd Python-3.7.17
  + 运行以下命令配置和编译 Python：
    ./configure --enable-optimizations
    make -j$(nproc)    # 使用所有 CPU 内核加快编译
  + 运行以下命令安装 Python 3.7：
    sudo make altinstall
  + 安装依赖库
    pip3.7 install pyspark numpy matplotlib scikit-learn -i  https://mirrors.aliyun.com/pypi/simple/
  + 修改`spark/conf`下的`spark-env.sh`，添加：
    export PYSPARK_PYTHON=/usr/local/bin/python3.7
    export PYSPARK_DRIVER_PYTHON=/usr/local/bin/python3.7

+ Step2 处理spark

  + 下载缺失的 Parquet JAR 文件
    wget https://repo1.maven.org/maven2/org/apache/parquet/parquet-common/1.10.1/parquet-common-1.10.1.jar
    wget https://repo1.maven.org/maven2/org/apache/parquet/parquet-column/1.10.1/parquet-column-1.10.1.jar
    wget https://repo1.maven.org/maven2/org/apache/parquet/parquet-encoding/1.10.1/parquet-encoding-1.10.1.jar
    wget https://repo1.maven.org/maven2/org/apache/parquet/parquet-hadoop/1.10.1/parquet-hadoop-1.10.1.jar
    wget https://repo1.maven.org/maven2/org/apache/parquet/parquet-format/2.4.0/parquet-format-2.4.0.jar

  + 将 JAR 文件移动到 $SPARK_HOME/jars/：
    mv parquet-*.jar $SPARK_HOME/jars/

  + 编辑hadoop的core-site.xml
    sudo vi $HADOOP_HOME/etc/hadoop/core-site.xml

  + 删除下面最后两行lzo，注意snappy后面的逗号也要删除
    ```<property>
        <name>io.compression.codecs</name>
        <value>
            org.apache.hadoop.io.compress.GzipCodec,
            org.apache.hadoop.io.compress.DefaultCodec,
            org.apache.hadoop.io.compress.BZip2Codec,
            org.apache.hadoop.io.compress.SnappyCodec,
            com.hadoop.compression.lzo.LzoCodec,
            com.hadoop.compression.lzo.LzopCodec
        </value>
    </property>```
    ```

    变成
    ```<property>
        <name>io.compression.codecs</name>
        <value>
            org.apache.hadoop.io.compress.GzipCodec,
            org.apache.hadoop.io.compress.DefaultCodec,
            org.apache.hadoop.io.compress.BZip2Codec,
            org.apache.hadoop.io.compress.SnappyCodec
        </value>
    </property>

+ <!-- 从这里开始只在niit01操作 -->
  <!-- ****************************** -->
  Step3 上传数据到hdfs, train.csv和newdata.csv先上传到当前目录/home/niit

  hdfs dfs -put train.csv /warehouse/bank_db/dwd/train.csv
  hdfs dfs -put newdata.csv /warehouse/bank_db/dwd/newdata.csv

+ Step4 上传python程序model.py, prediction.py至当前目录/home/niit

+ Step5 模型训练
  + 开启hadoop集群
    xCluster.sh start
  + 开启spark集群
    $SPARK_HOME/sbin/start-all.sh
  + 在spark集群提交训练任务（三节点standalone模式）
    spark-submit \
        --master spark://niit01:7077 \
        --deploy-mode client \
        bin/model.py
  + 结果：
  + 模型参数会保存到本地当前目录gbdt_model_params.json
  + 模型评估保存到hdfs:///model/evaluation_results.csv
    保存到本地，运行：
    hdfs dfs -getmerge hdfs:///model/evaluation_results.csv evaluation_results.csv
  + ROC图片会保存到本地roc_curve.png
    特征重要性会保存到本地feature_importances.png

+ Step6 模型预测
  + 在spark集群提交训练任务（三节点standalone模式）
    spark-submit \
        --master spark://niit01:7077 \
        --deploy-mode client \
        bin/prediction.py
  + 结果：
  + 模型预测结果保存到hdfs:///model/predictions.csv
    保存到本地，运行：
    hdfs dfs -getmerge hdfs:///model/predictions.csv predictions.csv