package com.jmb;

import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Sorting {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sorting.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES_DF = "src/main/resources/spark-data/attractions_ireland.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        Sorting app = new Sorting();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SortAndOrderBy")
                .master("local").getOrCreate();

                //Ingest data from CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES_DF);

        //Print the 5 first records to inspect both the schema and data of the loaded DataFrame
        df.show(5);

        //Sort by Region
        Dataset<Row> dfRegionSorted = df.sort(df.col("AddressRegion").asc_nulls_last());

        //Drop a few columns and display
        dfRegionSorted.drop("Url","Telephone","Longitude","Latitude", "Tags").show(8);

        //Sort by Name, then Region
        Dataset<Row> dfNameRegion = df.sort("Name", "AddressRegion");

        //Display Results drop some columns before
        dfNameRegion.drop("Url","Telephone","Longitude","Latitude", "Tags").show(7);

        //Use the orderBy df function, an alias for sort
        Dataset<Row> dfAddressLocalitySorted = df.orderBy(df.col("AddressLocality").desc());

        //Display results
        dfAddressLocalitySorted.drop("Url","Telephone","Longitude","Latitude", "Tags").show(7);
    }

}
