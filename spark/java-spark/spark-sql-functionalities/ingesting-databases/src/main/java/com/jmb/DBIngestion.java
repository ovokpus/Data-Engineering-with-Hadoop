package com.jmb;

import com.jmb.datasource.DerbyDBManager;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;


public class DBIngestion {

    public static final String EMBEDDED_DRIVER_STRING = "org.apache.derby.jdbc.EmbeddedDriver";


    private static final Logger LOGGER = LoggerFactory.getLogger(DBIngestion.class);

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        DBIngestion app = new DBIngestion();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {

        DerbyDBManager dbManager = new DerbyDBManager();

        //Start the embedded Derby DB (in memory) - Runs also INSERT queries
        dbManager.startDB();

        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkDBIngestion")
                .master("local").getOrCreate();

        //Create Properties with DB connection information
        Properties props = new Properties();
        props.put("user", "app");
        props.put("password", "derby");

        //Use Properties declared and read from the DB Table into a DataFrame
        Dataset<Row> studentsTable = session.read().jdbc("jdbc:derby:firstdb", "student", props);

        studentsTable.show();
        studentsTable.printSchema();

        //Retrieved filtered information by using a QUERY
        String query = "SELECT * FROM STUDENT WHERE AGE > 25";

        Dataset<Row> studentsFiltered = session.read()
                .jdbc("jdbc:derby:firstdb", "(" + query + ") student_alias", props);

        studentsFiltered.show();

        //Stop the embedded Derby DB (in memory) - Runs also DELETE TABLE query
        dbManager.stopDB();

    }

}
