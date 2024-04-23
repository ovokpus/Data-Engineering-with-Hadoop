package com.jmb;

import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FilesFormats {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilesFormats.class);
    private static final String CSV_PATH_RESOURCES = "src/main/resources/spark-data/complex.csv";
    private static final String JSON_PATH_RESOURCES = "src/main/resources/spark-data/format_json.json";
    
    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        FilesFormats app = new FilesFormats();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {

        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkFileFormats")
                .master("local").getOrCreate();

        //Ingest data from a complex CSV file into a DataFrame
        Dataset<Row> csvDf = session.read()
                .format("csv")
                .option("header", "true")
                .option("inferSchema", true)
                .option("multiline", true)
                .option("sep", "|")
                .option("quote", "\"")
                .load(CSV_PATH_RESOURCES);

        //Print csv files load results
        csvDf.show();

        //Print the schema
        csvDf.printSchema();

        /** READ JSON FILE **/
        Dataset<Row> jsonDf = session.read()
                .format("json")
                //uncomment to read traditional JSON files, with objects spanning into multiple lines
                .option("multiline", true)
                .load(JSON_PATH_RESOURCES);

        //Show and print schema
        jsonDf.show();
        jsonDf.printSchema();

                /** READ XML FILE **/
        Dataset<Row> xmlDf = session.read().format("xml")
                .option("rowTag", "widget")
                .load(XML_PATH_RESOURCES);

        //Show and print schema
        xmlDf.show();
        xmlDf.printSchema();

        /** READ TXT FILE **/
        Dataset<Row> textDf = session.read().format("text")
                .load(TXT_PATH_RESOURCES);

        //Show and print schema
        textDf.show();


    }

}
