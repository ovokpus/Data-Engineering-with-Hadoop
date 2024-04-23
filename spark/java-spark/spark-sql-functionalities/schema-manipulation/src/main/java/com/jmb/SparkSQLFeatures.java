package com.jmb;

import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.catalyst.expressions.GenericRowWithSchema;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SparkSQLFeatures {

    private static final Logger LOGGER = LoggerFactory.getLogger(SparkSQLFeatures.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/themepark_atts.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        SparkSQLFeatures app = new SparkSQLFeatures();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("SparkSQLFeatures")
                .master("local").getOrCreate();

        //Ingest data from CSV files into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES);

        Row firstRow = df.first();
        LOGGER.info("Row Schema: " + firstRow.schema());

       //Print a row's content in JSON format
        LOGGER.info("Row contents: " + firstRow.prettyJson());

       //Define a schema for the new row returned
        StructType schemaTags = defineTagsSchema();

        MapFunction<Row, Row> tagsMapFunction = row -> {
            int index = 0;
            Object[] rowReturned = new Object[2];
            rowReturned[index++] = row.getString(1);
            String[] tags = row.getString(4).split("_");
            rowReturned[index] = Arrays.toString(tags);
            return new GenericRowWithSchema(rowReturned, schemaTags);
        };

       //Use a MapFunction, inline function, to transform the DataFrame into a new one
        Dataset<Row> dfTags1 = df.map(tagsMapFunction, RowEncoder.apply(schemaTags));

        dfTags1.show();

        MapFunction<Row, TaggedRide> tagsMapFunctionPojos = row -> {
            int index = 0;
            TaggedRide tagRide = new TaggedRide();
            tagRide.setName(row.getString(1));
            tagRide.setTags(row.getString(4).split("_"));
            return tagRide;
        };

       //Now convert to a schema that is based on a POJO, for the tags to be processed in our Java scope.
        Dataset<TaggedRide> tagsPojosDf = df.map(tagsMapFunctionPojos, Encoders.bean(TaggedRide.class));

       //Print the schema of the Dataset
        tagsPojosDf.printSchema();

        //Print results
        tagsPojosDf.show();
    }

    private static StructType defineTagsSchema() {
        int fieldsIndex = 0;
        StructField[] fields = new StructField[2];
        fields[fieldsIndex++] = new StructField("name", DataTypes.StringType, true, null);
        fields[fieldsIndex] = new StructField("tags", DataTypes.StringType, true, null);
        return new StructType(fields);
    }

}
