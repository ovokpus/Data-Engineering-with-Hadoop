package com.jmb;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.concat;
import static org.apache.spark.sql.functions.lit;

public class DataFrameSchema {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFrameSchema.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/classic_books.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        DataFrameSchema dataFrameSchema = new DataFrameSchema();
        dataFrameSchema.run();
    }

    private void run() {

       //Create the Spark session
        SparkSession session = SparkSession.builder()
                .appName("DataFrameSchemaApp")
                .master("local").getOrCreate();

       //Ingest data from the CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .load(PATH_RESOURCES);

       //Show the first five rows
        df.show(5);

       //Print the schema of the DataFrame
        df.printSchema();

       //Drop columns not prefixed by "bibliography" and print the new schema
        Dataset<Row> dfFewerColumns = dropColumns(df);
        dfFewerColumns.printSchema();

       //Rename the columns, removing the bibliography.prefix
        Dataset<Row> renamedDf = renameColumns(dfFewerColumns);
        renamedDf.printSchema();

       //Create a DataFrame for the "Subjects" table in the DB
        Dataset<Row> subjectsDf = createSubjectsDf(renamedDf);
        subjectsDf.show(5);

       //Remove duplicates from the Subjects DataFrame
        Dataset<Row> subjectsUnique = subjectsDf.distinct();
        subjectsUnique.show(5);

       //Adjust the renamed DataFrame to the books table
        Dataset<Row> booksDf = createBooksDf(renamedDf);

        LOGGER.info("Schema of Books DF as JSON: " + booksDf.schema().prettyJson());
    }

   //To match the Subjects Table, drop all the columns except the Subject one
    private Dataset<Row> createSubjectsDf(Dataset<Row> renamedDf) {
        return renamedDf.withColumn("SubjectID", concat(renamedDf.col("subjects"), lit("_S")))
                .drop("classificacions", "languages", "title", "type", "author_birth", "author_death",
                "author_name", "publication_day", "publication_full", "publication_month", "publication_month_name",
                "publication_month_name", "publication_year");
    }

    private Dataset<Row> createBooksDf(Dataset<Row> renamedDf) {
        return renamedDf
                .withColumn("BookID",
                        concat(renamedDf.col("title"), lit("_"), renamedDf.col("author_name")))
                .withColumn("SubjectID", concat(renamedDf.col("subjects"), lit("_S")))
                .drop("subjects");
    }

    private Dataset<Row> renameColumns(Dataset<Row> dfFewerColumns) {
        return dfFewerColumns
                .withColumnRenamed("bibliography.congress classifications", "classificacions")
                .withColumnRenamed("bibliography.languages", "languages")
                .withColumnRenamed("bibliography.subjects", "subjects")
                .withColumnRenamed("bibliography.title", "title")
                .withColumnRenamed("bibliography.type", "type")
                .withColumnRenamed("bibliography.author.birth", "author_birth")
                .withColumnRenamed("bibliography.author.death", "author_death")
                .withColumnRenamed("bibliography.author.name", "author_name")
                .withColumnRenamed("bibliography.publication.day", "publication_day")
                .withColumnRenamed("bibliography.publication.full", "publication_full")
                .withColumnRenamed("bibliography.publication.month", "publication_month")
                .withColumnRenamed("bibliography.publication.month name", "publication_month_name")
                .withColumnRenamed("bibliography.publication.year", "publication_year");
    }

    private Dataset<Row> dropColumns(Dataset<Row> df) {
        return df.drop("metadata.downloads", "metadata.id", "metadata.rank", "metadata.url",
                "metrics.difficulty.automated readability index", "_c18", "_c19");
    }
}
