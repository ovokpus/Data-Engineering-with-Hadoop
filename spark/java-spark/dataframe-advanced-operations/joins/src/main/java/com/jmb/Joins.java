package com.jmb;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import java.util.Arrays;


public class Joins {

    private static final Logger LOGGER = LoggerFactory.getLogger(Joins.class);
    private static final String SPARK_FILES_FORMAT = "csv";
    private static final String PATH_RESOURCES_DF1 = "src/main/resources/spark-data/employees.csv";
    private static final String PATH_RESOURCES_DF2 = "src/main/resources/spark-data/departments.csv";

    public static void main(String[] args) throws Exception {

        LOGGER.info("Application starting up");
        Joins app = new Joins();
        app.init();
        LOGGER.info("Application gracefully exiting...");
    }

    private void init() throws Exception {



        //Create the Spark Session
        SparkSession session = SparkSession.builder()
                .appName("Joins")
                .master("local").getOrCreate();

                //Ingest data from CSV file into a DataFrame
        Dataset<Row> employeesDf = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES_DF1);

        //Show first 5 records of the Raw ingested DataSet
        employeesDf.show();

        //Load another file into a different DataFrame.
        Dataset<Row> deptsDf = session.read()
                .format(SPARK_FILES_FORMAT)
                .option("header", "true")
                .option("inferSchema", "true")
                .load(PATH_RESOURCES_DF2);

        //Inspect the second data frame
        deptsDf.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of INNER JOIN ... ==========");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        /** Perform an INNER JOIN - Retrieve the departments names where the employees belong to */
        Dataset<Row> joinedDfs =
                employeesDf.join(deptsDf, employeesDf.col("emp_dept_id").equalTo(deptsDf.col("dept_id")), "inner");

        //Drop unwanted columns for this query
        Dataset<Row> simpleJoinedDf = joinedDfs.drop("manager_emp_id", "start_year", "gender", "salary");

        //Show the joined DataFrames
        simpleJoinedDf.show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of LEFT JOIN ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        /** LEFT (OUTER) JOIN - List employees and the departments they belong to*/
        Dataset<Row> employeesDepts =
                employeesDf.join(deptsDf, employeesDf.col("emp_dept_id").equalTo(deptsDf.col("dept_id")), "leftouter");

        employeesDepts.drop("start_year","gender", "salary").show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of RIGHT JOIN ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        /** RIGHT (OUTER) JOIN - List departments and employees they belong to, if any*/
        Dataset<Row> deptsEmployees =
                employeesDf.join(deptsDf, employeesDf.col("emp_dept_id").equalTo(deptsDf.col("dept_id")), "rightouter");

        deptsEmployees.drop("start_year","gender", "salary").show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of FULL OUTER JOIN ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        /** FULL (OUTER) JOIN - List employees and the departments they belong to*/
        Dataset<Row> empsDeptsFull =
                employeesDf.join(deptsDf, employeesDf.col("emp_dept_id").equalTo(deptsDf.col("dept_id")), "fullouter");

        empsDeptsFull.drop("start_year","gender", "salary").show();

        LOGGER.info("======== Input any non blank key and tap Enter to see results of SELF (INNER) JOIN ... ==========");
        scan = new Scanner(System.in);
        s = scan.next();

        /** SELF (INNER) JOIN - List the employees next to their managers - Uses SparkSQL Component */

        //Create global view to use in SQL like syntax
        employeesDf.createOrReplaceTempView("employees");

        //Do the inner join
        Dataset<Row> empAndManagers =
                session.sql("SELECT E1.emp_id, E1.name, E1.manager_emp_id as manager_id, " +
                        "E2.emp_id as manager_emp_id, E2.name as manager_name " +
                        "FROM employees AS E1, employees AS E2 " +
                        "WHERE E1.manager_emp_id = E2.emp_id ");

        //List above DataFrame records
        empAndManagers.show();
    }

}
