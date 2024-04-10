package com.jmb;

import com.jmb.dto.PingAnomaly;
import com.jmb.mapper.TransmitterCompanyMapper;
import com.jmb.mapper.TransmitterSpeedMapper;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.api.java.function.ForeachFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.sql.*;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.LongAccumulator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

public class AccumulatorsBroadcasting {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccumulatorsBroadcasting.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/network_transit.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        AccumulatorsBroadcasting app = new AccumulatorsBroadcasting();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("AccumAndBroadcasting")
                .master("local").getOrCreate();

        //Ingest data from CSV files into a DataFrames
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES);

        //Display five first rows and inspect schema
        df.printSchema();

        //Analyze the information with the Mapper and produce the new DataFrame
        TransmitterSpeedMapper transmitterSpeedMapper = new TransmitterSpeedMapper();
        Dataset<Row> results = df.map(transmitterSpeedMapper,
                RowEncoder.apply(transmitterSpeedMapper.structType()));

        //Register and use and accumulator
        LongAccumulator longAccumulator = session.sparkContext().longAccumulator("ErrorsCounter");
        df.foreach((ForeachFunction<Row>) row -> {
            if(row.anyNull()){
                longAccumulator.add(1L);
            }
        });

        //Print the errors accumulator
        LOGGER.info("============ Amount of rows with errors: ============= " +  longAccumulator.value());
        //Show processed results
        results.show();
        LOGGER.info("====================================================== " +  longAccumulator.value());

        //Declare and use a CollectionAccumulator
        CollectionAccumulator<PingAnomaly> pingAccum = session.sparkContext().collectionAccumulator();

        int sourceColId = 0;
        int destColId = 1;
        int pingColId = 2;

        df.foreach((ForeachFunction<Row>) row -> {
            if(!row.anyNull() && row.getInt(pingColId) == 0){
                pingAccum.add(new PingAnomaly(row.getString(sourceColId), row.getString(destColId)));
            }
        });

        //Print anomalous rows
        LOGGER.info("============ Amount of rows with anomalies: ============= ");
        pingAccum.value()
                .forEach(pingAnomaly -> LOGGER.info("Source - dest " + pingAnomaly.getSource() + "-" + pingAnomaly.getDestination()));
        LOGGER.info("========================================================= ");


        //Filter transmitters with high ping as depicted by the category OVLD
        Dataset<Row> slowTransmitters = results.filter(
                (FilterFunction<Row>) row -> row.getAs("Category").equals("OVLD")
        );

        //Declare the lookup table as Broadcast Variable
        Map<String, String> lookupTable = new HashMap<>();
        lookupTable.put("ans","American Networking");
        lookupTable.put("xty","Expert Transmitters");
        lookupTable.put("xfh","XFH");

        Broadcast<Map<String, String>> bcVar = session.sparkContext().broadcast(lookupTable,  scala.reflect.ClassTag$.MODULE$.apply(Map.class));

        //Apply a map transformation using the broadcast variable
        TransmitterCompanyMapper companyMapper = new TransmitterCompanyMapper(bcVar);
        Dataset<Row> manufacturers = slowTransmitters.map(companyMapper, RowEncoder.apply(companyMapper.structType()));

        //Display results for manufacturers
        LOGGER.info("============ Result for Manufacturers: ================== ");
        manufacturers.show();
        LOGGER.info("========================================================= ");

        bcVar.destroy();
    }

}
