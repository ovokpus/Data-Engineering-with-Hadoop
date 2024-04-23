package com.jmb;

import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

public class SparkSQLIntroduction {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkSQLIntroduction.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/themepark_atts.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        SparkSQLIntroduction app = new SparkSQLIntroduction();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkSQLIntroduction")
                .master("local").getOrCreate();

        //Ingest data from CSV files into a DataFrames
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES);

        //Display five first rows and inspect schema
        df.show();

        //Create a temporary view
        df.createOrReplaceTempView("rides");

        LOGGER.info("======== Input any non blank key and tap Enter to see results of SELECT QUERY... ==========");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        //Select rides for 18 year old people or older
        Dataset<Row> eighteenOldRides = session.sql(
                "SELECT att_id,name,age_required FROM rides WHERE age_required >= 18"
        );

        //Display results
        eighteenOldRides.show();

        //Group by minimum age required per ride
        Dataset<Row> ridesGrouping = session.sql(
                "SELECT age_required, count(1) AS amount_rides FROM rides GROUP BY age_required "
        );

        LOGGER.info("======== Input any non blank key and tap Enter to see results of GROUPBY QUERY... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Display above results
        ridesGrouping.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see Execution Plan... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Display the execution plan prior to running the query
        LOGGER.info("QUERY EXECUTION PLAN: " + ridesGrouping.queryExecution().simpleString());
    }

}
