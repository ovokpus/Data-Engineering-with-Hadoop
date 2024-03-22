package com.jmb;

import com.jmb.mappers.CarMapper;
import com.jmb.model.Car;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DatasetPOJOs {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetPOJOs.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    public static final String PATH_RESOURCES = "src/main/resources/spark-data/cars.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        DatasetPOJOs app = new DatasetPOJOs();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("DatasetPOJOs")
                .master("local").getOrCreate();

        //Ingest data from CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .load(PATH_RESOURCES);

        //Show first 5 records of the Raw ingested dataset
        df.show(5);

        //Also print its schema
        df.printSchema();

        //Map to a Dataset of Car objects by using a Mapper (MapFunction) class
        Dataset<Car> carDataset = df.map(new CarMapper(), Encoders.bean(Car.class));

        //Print the dataset schema and show the first 5 rows
        carDataset.printSchema();
        carDataset.show(5);

    }
}
