package com.jmb;

import com.jmb.mapper.DamageEstMapper;
import com.jmb.model.DamageEstimator;
import com.jmb.model.DamageEstimatorSerializable;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Serialization {

    private static final Logger LOGGER = LoggerFactory.getLogger(Serialization.class);

    private static final String RESOURCE_CSV = "src/main/resources/spark-data/earthquakes.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        Serialization app = new Serialization();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {

        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkSerialization")
                .master("local").getOrCreate();

        //Load Earhquakes CSV file into a DataFrame
        Dataset<Row> df = session.read().format("csv")
                .option("header", "true")
                .load(RESOURCE_CSV);

        //Print loaded DataFrame of Earthquakes information.
        df.show();

        /** UNCOMMENT THIS COMMENT SECTION TO SEE EXCEPTION

        //Apply Damage Estimation Mapper - Transform into a new DataFrame
        Dataset<Row> damagePerCityDf = df.map(new DamageEstMapper(new DamageEstimator()),
                RowEncoder.apply(DamageEstMapper.getSchema()));

        //Trigger transformation with action
        damagePerCityDf.count();

         **/

        /*Pass a serializable object to the mapper to have the code work
        Apply Damage Estimation Mapper - Transform into a new DataFrame */
        Dataset<Row> damagePerCityDf = df.map(new DamageEstMapper(new DamageEstimatorSerializable()),
                RowEncoder.apply(DamageEstMapper.getSchema()));

        //Trigger transformation with action
        damagePerCityDf.count();

        //Print results
        damagePerCityDf.show();
    }

}
