package com.jmb;

import com.jmb.datasource.DerbyDBManager;
import com.jmb.mapper.NationalityMapper;
import com.jmb.persistence.SQLDatabasePersister;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DBIngestion {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(DBIngestion.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        DBIngestion app = new DBIngestion();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {

        SparkConf appConfig = new SparkConf().set("spark.testing.memory", "900000000");

        DerbyDBManager dbManager = new DerbyDBManager();

        //Start the embedded Derby DB (in memory) - Runs also INSERT queries
        dbManager.startDB();

        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkDBIngestion")
                .config(appConfig)
                .master("local").getOrCreate();

        //Create Properties with DB connection information
        Properties props = new Properties();
        props.put("user", "app");
        props.put("password", "derby");

        //Use Properties declared and read from the DB Table into a DataFrame
        Dataset<Row> studentsTable = session.read().jdbc("jdbc:derby:firstdb", "student", props);

        studentsTable.show();
        studentsTable.printSchema();

        //Create Map of Nationalities
        Map<String, String> nationalities = new HashMap<>();
        nationalities.put("Brown", "USA");
        nationalities.put("Curie", "UK");
        nationalities.put("Truman", "SCOTLAND");
        nationalities.put("Ross", "USA");
        nationalities.put("Spencer", "UK");
        nationalities.put("Birch", "UK");

        //Drop unwanted columns
        Dataset<Row> studentsFiltered = studentsTable.drop("ID", "AGE");

        //Apply Nationality Mapper
        Dataset<Row> studentsNationalities = studentsFiltered.map(new NationalityMapper(nationalities), RowEncoder.apply(NationalityMapper.getSchema()));

        //Print resulting transformed Df
        studentsNationalities.show(2);

        //Save to DB Table
        studentsNationalities.write()
                             .mode(SaveMode.Append)
                             .jdbc("jdbc:derby:firstdb", "STUDENTS_ORIGIN", props);

        //Persist manually through the appropriate class
        Dataset<Row> persistedDf = studentsNationalities.mapPartitions(new SQLDatabasePersister(dbManager),RowEncoder.apply(NationalityMapper.getSchema()));

        //Trigger above transformation
        persistedDf.count();

        //Save to a file
        studentsNationalities.write().format("csv").save("nationalities.csv");

        dbManager.printStudentNationalitiesTable();

        //Stop the embedded Derby DB (in memory) - Runs also DELETE TABLE query
        dbManager.stopDB();

    }

}
