package com.jmb;

import com.jmb.mapper.AirportsMapGrouper;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;


public class DataGrouping {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataGrouping.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/airlines.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        DataGrouping app = new DataGrouping();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("DataGrouping")
                .master("local").getOrCreate();

        //Ingest data from CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES);

        //Show first 5 records of the Raw ingested DataSet
        df.show(5);

        //Group by Airport code and month, sum with aggregate function
        Dataset<Row> groupedAirportsDf = df.groupBy(df.col("`Airport.Code`"), df.col("`Time.Month`"))
                .agg(functions.sum("`Statistics.Flights.Total`"));

        LOGGER.info("======== Input any non blank key and tap Enter to see results GroupBy: Group by Airport code and sum total flights ==========");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        //Print the grouped partitions of the DataFrame
        groupedAirportsDf.show(5);

        //Filter out all flights except for the year 2005
        Dataset<Row> filteredDf = df.filter(df.col("`Time.Label`").contains("2005"));

        LOGGER.info("======== Input any non blank key and tap Enter to see Filtered DataFrame: Filter 2005 Flights ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Print some rows to see filter applied
        filteredDf.show(10);


        int airportCodeRowIndex = 0;
        KeyValueGroupedDataset keyValueGroupedDataset =
                filteredDf.groupByKey((MapFunction<Row, String>) row -> row.getString(airportCodeRowIndex), Encoders.STRING());

        Dataset<Row> summarisedResults = keyValueGroupedDataset.mapGroups(new AirportsMapGrouper(),
                RowEncoder.apply(new StructType(AirportsMapGrouper.defineRowSchema())));


        LOGGER.info("======== Input any non blank key and tap Enter to see GroubByKey results: Group by airport and show percentages of flights pero month of a year (2005) ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Show the results
        summarisedResults.show(10);

    }

}
