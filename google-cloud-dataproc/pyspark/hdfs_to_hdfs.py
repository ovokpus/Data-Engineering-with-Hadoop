from pyspark.sql import SparkSession

spark = SparkSession.builder \
    .appName('spark_hdfs_to_hdfs') \
    .getOrCreate()

sc = spark.sparkContext
sc.setLogLevel("WARN")

MASTER_NODE_INSTANCE_NAME = "backend-cluster-m"
log_files_rdd = sc.textFile('hdfs://{}/data/log_examples/*'.format(MASTER_NODE_INSTANCE_NAME))

split_rdd = log_files_rdd.map(lambda x: x.split(" "))
selected_col_rdd =  split_rdd.map(lambda x: (x[0], x[3], x[5], x[6]))

columns = ["ip", "date", "method", "url"]
logs_df = selected_col_rdd.toDF(columns)
logs_df.createOrReplaceGlobalTempView('logs_df')

sql = f"""
    SELECT
        url,
        count(*) as count
    FROM logs_df
    WHERE url LIKE '%/article%'
    GROUP BY url        
    """

article_count_df = spark.sql()
print(" ### Get only articles and blog records ### ")
article_count_df.show(5)

article_count_df.write.save('hdfs://{}/data/article_count_df'.format(MASTER_NODE_INSTANCE_NAME), format='csv', mode='overwrite')
