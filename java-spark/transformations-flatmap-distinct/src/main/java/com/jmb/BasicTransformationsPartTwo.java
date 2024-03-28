package com.jmb;

import com.jmb.mapper.StudentGradesFlatMapper;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;

import static org.apache.spark.sql.functions.*;


public class BasicTransformationsPartTwo {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTransformationsPartTwo.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES = "src/main/resources/spark-data/students_subjects.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        BasicTransformationsPartTwo app = new BasicTransformationsPartTwo();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {
        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("BasicTransformationsPartTwo")
                .master("local").getOrCreate();

        //Ingest data from CSV file into a DataFrame
        Dataset<Row> df = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .load(PATH_RESOURCES);

        //Show first 5 records of the Raw ingested DataSet
        df.show(5);

                //Split the second column to get an array of individual grades
        LOGGER.info("======== Input any non blank key and tap Enter to see results of GRADE column split... ==========");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        Dataset<Row> studentsDf = df.select(df.col("Student_Name"), split(df.col("Grades"), " "));        
        studentsDf.show();


        //Rename second column to something more meaningful
        LOGGER.info("======== Input any non blank key and tap Enter to see results renaming column 'split' ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        Dataset<Row> renamedStudentsDf = studentsDf.withColumnRenamed("split(Grades,  , -1)", "Grades");
        renamedStudentsDf.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of FlatMap applied' ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        Dataset<Row> flattenedDf = renamedStudentsDf
                .flatMap(new StudentGradesFlatMapper(), RowEncoder.apply(getStudentsFlattenedSchema()));
        flattenedDf.show();

        LOGGER.info("========= Input any non blank key and tap Enter to see results of Distinct ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        //Retrieve only distinct records, that is, delete duplicates
        Dataset<Row> distinctStudents = flattenedDf.distinct();
        distinctStudents.show();
    }

    public static StructType getStudentsFlattenedSchema() {
        StructField[] fields = new StructField[]{
                new StructField("Name", DataTypes.StringType, true, null),
                new StructField("Grade 1", DataTypes.StringType, true, null),
                new StructField("Grade 2", DataTypes.StringType, true, null),
                new StructField("Grade 3", DataTypes.StringType, true, null),
                new StructField("Grade 4", DataTypes.StringType, true, null),
                new StructField("Grade 5", DataTypes.StringType, true, null),
                new StructField("Grade 6", DataTypes.StringType, true, null)
        };
        return new StructType(fields);
    }
}
