package com.jmb;

import com.jmb.filter.HerbsFilter;
import com.jmb.mapper.EmptyToUnderscoreMapper;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;


public class BasicTransformations {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTransformations.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    public static final String PATH_RESOURCES = "src/main/resources/spark-data/generic-food.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        BasicTransformations app = new BasicTransformations();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("BasicTransformations")
                .master("local").getOrCreate();

        //Ingest data from CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .load(PATH_RESOURCES);

        //Show first 5 records of the Raw ingested DataSet
        df.show(5);

        //Apply a Map Transformation
        Dataset<Row> transformedDf = applyMapTransformation(df);

        //Also print first five rows
        transformedDf.show(5);

        //Filter food and keep ones belonging to the Herbs subgroup
        Dataset<Row> filteredDf = applyFilerFunction(transformedDf);

        //Print some rows to see filter was applied correctly
        filteredDf.show(4);

        //Apply an in-line predicate to filter column's values
        Dataset<Row> inlinefilteredDf = transformedDf.filter(transformedDf.col("GROUP").equalTo("Vegetables"));
        inlinefilteredDf.show(5);

        //Count the total filtered rows for veggies group
        LOGGER.info("Total vegetables in DataFrame: " + inlinefilteredDf.count());

        //Take the first 4 veggies from the Filtered DataFrame
        Row[] result = (Row[]) inlinefilteredDf.take(4);
        //And print the Resulting array of Row
        LOGGER.info("=============== Printing Rows =============== ");
        Arrays.asList(result).stream().forEach(row -> LOGGER.info("Row: " + row.mkString(",")));
        LOGGER.info("=============== End Printing Rows =============== ");

        //Collect all the rows
        Row[] collected = (Row[]) inlinefilteredDf.collect();
        //Display the size of the Java list with the collected rows
        LOGGER.info("Total Collected Rows for DataFrame: " + collected.length);

    }

    private Dataset<Row> applyMapTransformation(Dataset<Row> inputDf) {
        return inputDf.map(new EmptyToUnderscoreMapper("GROUP"), RowEncoder.apply(inputDf.schema()));
    }

    private Dataset<Row> applyFilerFunction(Dataset<Row> inputDf) {
        return inputDf.filter(new HerbsFilter());
    }
}
