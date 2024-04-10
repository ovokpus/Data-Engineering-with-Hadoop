package com.jmb;

import com.jmb.udfs.DateParserUDF;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.*;

public class UDFs {

    private static final Logger LOGGER = LoggerFactory.getLogger(UDFs.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/Covid19_Historic_Records.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        UDFs app = new UDFs();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("UDFs")
                .master("local").getOrCreate();

        //Ingest data from CSV files into a DataFrames
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES);

        //Display five first rows and inspect schema
        df.show(5);

        //Register a date parser UDF
        session.udf().register("dateParser", new DateParserUDF(), DataTypes.DateType);

        //call the UDF function
        Dataset<Row> dfDatesParsed = df.withColumn("EventDateParsed", callUDF("dateParser", col("EventDate"), lit("YYYY/MM/DD HH:mm:ss")))
                                        .drop("EventDate");

        //Print results
        dfDatesParsed.show(5);

    }

}
