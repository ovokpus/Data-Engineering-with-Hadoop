package com.jmb;

import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

import static org.apache.spark.sql.functions.lit;


public class UnionAndDistinct {

    private static final Logger LOGGER = LoggerFactory.getLogger(UnionAndDistinct.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES_DF1 = "src/main/resources/spark-data/us_salesmen.csv";
    private static final String PATH_RESOURCES_DF2 = "src/main/resources/spark-data/uk_salesmen.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        UnionAndDistinct app = new UnionAndDistinct();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("UnionAndDistinct")
                .master("local").getOrCreate();

        //Ingest data from CSV files into a DataFrames
        Dataset<Row> dfSalesUs = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES_DF1);

        Dataset<Row> dfSalesUk = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES_DF2);

        //Print the 5 first records to inspect both the schema and data of the loaded DataFrames
        LOGGER.info("US Salesmen DataFrame Ingested: ");
        dfSalesUs.show();
        LOGGER.info("UK Salesmen DataFrame Ingested: ");
        dfSalesUk.show();

        //Inspect both dfs schemas to make sure the structure matches thus allowing a Union Transformation
        dfSalesUs.printSchema();
        dfSalesUk.printSchema();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of Union ... ==========");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        //Perform a Union of the DataFrames ingested
        Dataset<Row> allEmployees = dfSalesUs.union(dfSalesUk);

        //Print resulting merged DataFrame
        allEmployees.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see DataFrame Cols Re arranged ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Rearrange columns order.
        Dataset<Row> dfUkSalesReOrg = dfSalesUk.drop("department").withColumn("department", lit("TBD"));

        dfUkSalesReOrg.show(2);

        LOGGER.info("======== Input any non blank key and tap Enter to see UnionByName applied ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

       //Now attempt a unionByName - union based on matching columns' Names, regardless of their schemas
        Dataset<Row> allEmployeesUnionByName = dfSalesUs.unionByName(dfUkSalesReOrg);

        // Print results
        allEmployeesUnionByName.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see DropDuplicates applied ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Drop duplicates based on employee name and department as identity criteria
        Dataset<Row> uniqueAllEmployees = allEmployees.dropDuplicates("employee_name", "department");

        //Print results
        uniqueAllEmployees.show();

    }

}
