# Spark SQL

---

## Simple Query Example

When we execute Spark SQL queries in the spark-shell, we don’t need to explicitly create a SparkSession object. We would if we were writing a Spark application that was to be run using spark-submit. The SparkSession object is provided implicitly by the shell. Read the listing below, which is similar to what we have done in the previous lessons, except for creating the temporary view, which we discuss later.

```scala
scala> val movies = spark.read.format("csv")
                       .option("header", "true")
                       .option("samplingRatio", 0.001)
                       .option("inferSchema", "true")
                       .load("/data/BollywoodMovieDetail.csv")
movies: org.apache.spark.sql.DataFrame = [imdbId: string, title: string ... 8 more fields]

scala> movies.createOrReplaceTempView("tempView")

scala> spark.sql("SELECT title FROM tempView WHERE releaseYear > 2010 ORDER BY title desc")
            .show(3)
+--------------------+
|               title|
+--------------------+
|            Zokkomon|
|   Zindagi Tere Naam|
|Zindagi Na Milegi...|
+--------------------+
only showing top 3 rows
```

We can also express the same query using DataFrames API as follows:

```scala
movies.where(＄"releaseYear" > 2010)
      .sort(desc("title"))
```

---

## Complex Query Example

Let’s try a slightly more complex query. Imagine that we want to list all the movies released post 2010 and at the same time label each movie below average, average, and above average if the hitFlop column is less than 5, equal to 5, and higher than 5, respectively. The query and its output are presented below:

```scala
scala> val df = spark.sql("""SELECT title, CASE 
                WHEN hitFlop < 5 THEN 'below average' 
                WHEN hitFlop = 5 THEN 'average' 
                WHEN hitFlop > 5 THEN 'above average' END AS MovieRating 
                FROM tempView WHERE releaseYear > 2010 ORDER BY title desc""")
df: org.apache.spark.sql.DataFrame = [title: string, MovieRating: string]

scala> df.show(10)
+--------------------+-------------+
|               title|  MovieRating|
+--------------------+-------------+
|            Zokkomon|below average|
|   Zindagi Tere Naam|below average|
|Zindagi Na Milegi...|above average|
|       Zindagi 50 50|below average|
|                 Zid|below average|
|            Zed Plus|below average|
|             Zanjeer|below average|
|         Youngistaan|below average|
|   Yeh Saali Zindagi|below average|
| Yeh Jo Mohabbat Hai|below average|
+--------------------+-------------+
only showing top 10 rows
```

The equivalent query expressed using DataFrame APIs is as follows:

```scala
movies.where(＄"releaseYear" > 2010)
      .withColumn("MovieRating", 
            when(＄"hitFlop" === 5,"average" )
            .when(＄"hitFlop">5,"above average")
            .otherwise("below average"))
      .select(＄"title", ＄"MovieRating")
      .sort(desc("title"))
      .show(10)
```

---

## Views and Tables

```scala
// Creating database and list exsiting tables
spark.sql("CREATE DATABASE spark_course")

spark.sql("USE spark_course")

spark.sql("SHOW TABLES").show(5, false)

// Read-in data
val movies = (spark.read.format("csv")
                .option("header", "true")
                .option("samplingRatio", 0.001)
                .option("inferSchema", "true")
                .load("/data/BollywoodMovieDetail.csv"))

spark.sql("CREATE TABLE movieShortDetail(imdbID String, title String)")

spark.sql("SHOW TABLES").show(5, false)

// Creating table using DataFrames API.
movies.write.saveAsTable("movieShortDetailUsingDataFrame")


// Creating table from data read-in from a file
spark.sql("CREATE TABLE movieShortDetailUnmanaged (imdbID STRING, title STRING) USING csv OPTIONS (PATH '/data/BollywoodMovieDetail.csv')")

// Using DataFrames API we can create a table from the data file as follows
movies.write.option("path","/data/shortMovieDetail.csv").saveAsTable("movieShortDetailUsingDataFrameUnmanaged")

// Listing tables using catalog
spark.catalog.listTables().show(5, false)

// Listing columns of a table
spark.catalog.listColumns("movieshortdetail").show(10, false)

// Listing databases
spark.catalog.listDatabases().show(10, false)

// Creating views
movies.write.saveAsTable("movies")

spark.sql("CREATE OR REPLACE TEMP VIEW high_rated_movies AS SELECT title FROM movies WHERE hitFlop > 7")

spark.catalog.listTables().show(3)

spark.catalog.listTables().show(5,false)

// Creating a global view
spark.sql("CREATE OR REPLACE GLOBAL TEMP VIEW high_rated_movies_global AS SELECT title FROM movies WHERE hitFlop > 7")

spark.sql("SELECT * FROM global_temp.high_rated_movies_global").show(3, false)

// Catalog examples
spark.catalog.listDatabases().show(3)

spark.catalog.listTables().show(3)

spark.catalog.listColumns("movies").show(3)
```

---

## Spark SQL Data Sources

```scala
// Reading data into DataFrames. To paste a multiline command into the spark
// shell, follow the below steps:
// 1. Enter paste-mode using :paste
// 2. Copy the below command, paste it in the console and hit enter
// 3. Enter Ctrl + D to return to spark-shell
//
val movies = spark.read.format("csv")
                    .option("header", "true")
                    .option("samplingRatio", 0.001)
                    .option("inferSchema", "true")
                    .load("/data/BollywoodMovieDetail.csv")

movies.write.saveAsTable("movieData")

val movieTitles = spark.sql("SELECT title FROM movieData")

movieTitles.show(3, false)

// Parquet
(movies.write.format("parquet")
                   .mode("overwrite")
                   .option("compression","snappy")
                   .save("/data/moviesParquet")) # output file path

// If you choose a different output file path, please use that in the below statement
val filePath="/data/moviesParquet"

val dataFrame = (spark.read.format("parquet")
                          .load(filePath))

dataFrame.show(3)


spark.sql("""CREATE OR REPLACE TEMPORARY VIEW movies_view USING parquet OPTIONS ( path "/data/moviesParquet")""")

spark.sql("SELECT title FROM movies_view").show(3)

// JSON
(movies.write.format("JSON")
                   .mode("overwrite")
                   .save("/data/moviesJSON")) # output file path

// If you choose a different output file path, please use that in the below statement
val filePath = "/data/moviesJSON"

val dataFrame = (spark.read.format("JSON")
                          .load(filePath))

dataFrame.show(3)

spark.sql("""CREATE OR REPLACE TEMPORARY VIEW movies_view_json USING json OPTIONS(path "/data/moviesJSON")""")

spark.sql("SELECT * FROM movies_view_json").show(3)
```

---

## Spark User Defined Functions

```scala
// Read-in data
val movies = (spark.read.format("csv")
                       .option("header", "true")
                       .option("samplingRatio", 0.001)
                       .option("inferSchema", "true")
                       .load("/data/BollywoodMovieDetail.csv"))

movies.write.saveAsTable("movies")

// Spark UDFs
val twoDigitYear = (year : Int) => {  ((year/10)%10).toString +  (year%10).toString }

spark.udf.register("shortYear", twoDigitYear)

spark.sql("SELECT title, shortYear(releaseYear) FROM movies").show(3)

// Spark's built-in functions
spark.sql("SELECT upper(title) FROM movies").show(3)

spark.sql("SELECT random()").show(1)

spark.sql("SELECT now()").show(1, false)

spark.sql("SELECT current_timestamp()").show(1, false)
```

---
