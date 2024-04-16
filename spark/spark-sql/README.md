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

## Spark SQL Higher Order Functions

Apache Spark SQL supports several higher-order functions that are designed to work with complex data types like arrays and maps. These functions allow you to apply a function to each element in an array or map, which can be extremely useful in data transformation and analysis. Below, I'll provide a comprehensive list of these higher-order functions, explain each one, and provide code examples.

### 1. **`transform`**

   The `transform` function applies a function to each element in an array and returns a new array containing the results.

   **Syntax:**

   ```sql
   transform(array, x -> expression)
   ```

   **Example:**

   ```sql
   SELECT transform(array(1, 2, 3), x -> x + 1) AS incremented_array;
   ```

   Output: `[2, 3, 4]`

### 2. **`filter`**

   The `filter` function returns an array consisting only of the elements that meet a condition specified by a boolean function.

   **Syntax:**

   ```sql
   filter(array, x -> predicate)
   ```

   **Example:**

   ```sql
   SELECT filter(array(1, 2, 3), x -> x % 2 = 1) AS odd_numbers;
   ```

   Output: `[1, 3]`

### 3. **`exists`**

   The `exists` function returns true if at least one element of the array satisfies the given predicate.

   **Syntax:**

   ```sql
   exists(array, x -> predicate)
   ```

   **Example:**

   ```sql
   SELECT exists(array(1, 2, 3), x -> x = 2) AS contains_two;
   ```

   Output: `true`

### 4. **`forall`**

   The `forall` function returns true if all elements of the array satisfy the specified condition.

   **Syntax:**

   ```sql
   forall(array, x -> predicate)
   ```

   **Example:**

   ```sql
   SELECT forall(array(1, 2, 3), x -> x > 0) AS all_positive;
   ```

   Output: `true`

### 5. **`aggregate`**

   The `aggregate` function reduces the elements of the array into a single value by applying a binary function to an initial state and all elements in the array.

   **Syntax:**

   ```sql
   aggregate(array, initial_value, (accumulator, x) -> new_accumulator)
   ```

   **Example:**

   ```sql
   SELECT aggregate(array(1, 2, 3), 0, (acc, x) -> acc + x) AS sum_array;
   ```

   Output: `6`

### 6. **`zip_with`**

   The `zip_with` function merges two arrays by applying a function to corresponding elements from both arrays.

   **Syntax:**

   ```sql
   zip_with(array1, array2, (x, y) -> expression)
   ```

   **Example:**

   ```sql
   SELECT zip_with(array(1, 2, 3), array(4, 5, 6), (x, y) -> x + y) AS summed_arrays;
   ```

   Output: `[5, 7, 9]`

### 7. **`transform_keys`** and **`transform_values`**

   These functions apply a transformation function to the keys or values of a map, respectively.

   **Syntax for `transform_keys`:**

   ```sql
   transform_keys(map, (k, v) -> new_key_expression)
   ```

   **Syntax for `transform_values`:**

   ```sql
   transform_values(map, (k, v) -> new_value_expression)
   ```

   **Example for `transform_keys`:**

   ```sql
   SELECT transform_keys(map(1, 'a', 2, 'b'), (k, v) -> k + 1) AS key_incremented;
   ```

   **Example for `transform_values`:**

   ```sql
   SELECT transform_values(map(1, 'a', 2, 'b'), (k, v) -> concat(v, 'x')) AS value_transformed;
   ```

Certainly! Here's an explanation of each of the Spark SQL functions you mentioned, along with examples:

### 1. **`get_json_object()`**

   Extracts JSON object from a JSON string based on json path specified.

   **Example:**

   ```sql
   SELECT get_json_object('{"a":{"b":1}}', '$.a.b') AS value;
   ```

   Output: `1`

### 2. **`from_json()`**

   Converts JSON string into a StructType (complex data type) based on the schema specified.

   **Example:**

   ```sql
   SELECT from_json('{"name":"John", "age":30}', 'name STRING, age INT') AS person;
   ```

   Output: `{"name":"John", "age":30}`

### 3. **`to_json()`**

   Converts a StructType or MapType into a JSON string.

   **Example:**

   ```sql
   SELECT to_json(struct(1 AS a, 'black' AS b)) AS json_data;
   ```

   Output: `{"a":1,"b":"black"}`

### 4. **`explode()`**

   Returns a new row for each element in the given array or map.

   **Example:**

   ```sql
   SELECT explode(array(1, 2, 3)) AS value;
   ```

   Output: Rows of values `1`, `2`, `3`

### 5. **`selectExpr()`**

   Allows specifying SQL expressions in a DataFrame API call, useful for performing complex operations in a concise manner.

   **Example:**

   ```scala
   df.selectExpr("count(*) as total", "sum(age) as sum_age")
   ```

### 6. **`array_intersect()`**

   Returns an array of the elements that are in both of the given arrays.

   **Example:**

   ```sql
   SELECT array_intersect(array(1, 2, 3), array(3, 4, 5)) AS common_elements;
   ```

   Output: `[3]`

### 7. **`array_join()`**

   Concatenates the elements of the given array using the delimiter specified, optionally replacing nulls with a value.

   **Example:**

   ```sql
   SELECT array_join(array('John', null, 'Doe'), ', ', 'Unknown') AS name;
   ```

   Output: `"John, Unknown, Doe"`

### 8. **`array_union()`**

   Returns an array containing all the elements of the input arrays.

   **Example:**

   ```sql
   SELECT array_union(array(1, 2, 3), array(3, 4, 5)) AS union_array;
   ```

   Output: `[1, 2, 3, 4, 5]`

### 9. **`filter()`**

   Returns an array of elements that satisfy a condition.

   **Example:**

   ```sql
   SELECT filter(array(1, 2, 3), x -> x % 2 = 1) AS odd_numbers;
   ```

   Output: `[1, 3]`

### 10. **`exists()`**

   Returns true if any element of the array satisfies the given condition.

   **Example:**

   ```sql
   SELECT exists(array(1, 2, 3), x -> x = 2) AS has_two;
   ```

   Output: `true`

### 11. **`reduce()`**

   Reduces the elements of the array to a single value by applying a binary function, starting from an initial value.

   **Example:**

   ```sql
   SELECT reduce(array(1, 2, 3), 0, (acc, x) -> acc + x, acc -> acc) AS sum_values;
   ```

   Output: `6`

### 12. **`trim()`**

   Removes leading and trailing spaces from a string.

   **Example:**

   ```sql
   SELECT trim('   Hello, World!   ') AS trimmed_string;
   ```

   Output: `"Hello, World!"`

### 13. **`split()`**

   Splits a string into an array of substrings based on a delimiter.

   **Example:**

   ```sql
   SELECT split('one,two,three', ',') AS parts;
   ```

   Output: `["one", "two", "three"]`

These functions enable you to manipulate and analyze complex and structured data efficiently within Spark SQL, helping address a wide range of data processing tasks.

### Usage Tips

- These functions are particularly useful when dealing with JSON data or any complex nested data structures.
- They can be used in conjunction with regular SQL functions and can participate in larger SQL queries to perform complex data transformations directly within Spark SQL.

These higher-order functions enhance Spark SQL's capability to handle complex data transformations efficiently, making it a powerful tool for big data processing and analysis.

### More examples

```sql
CREATE OR REPLACE TEMPORARY VIEW movies USING csv OPTIONS (path "/data/BollywoodMovieDetail.csv", header "true", inferSchema "true", mode "FAILFAST");

SELECT title, releaseYear, hitFlop, split(actors,"[|]") AS actors FROM movies;

CREATE TABLE tempTbl1 AS SELECT title, releaseYear, hitFlop, split(actors,"[|]") AS actors FROM movies;

SELECT  title, releaseYear, hitFlop, explode(actors) AS actor FROM tempTbl1 LIMIT 10;

CREATE TABLE tempTbl2 AS SELECT title, releaseYear, hitFlop, trim(actor) AS actor FROM (SELECT  title, releaseYear, hitFlop, explode(actors) AS actor FROM tempTbl1);

select * from tempTbl2 limit 10;

SELECT actor, avg(hitFlop) AS score from tempTbl2 GROUP BY actor ORDER BY score desc LIMIT 5;

select array_distinct(allTitles) FROM (select collect_list(title) AS allTitles from tempTbl2);

select transform(actors, actor -> upper(actor)) from tempTbl1 LIMIT 5;
```

## Spark Joins, Unions, Window Functions

Given your background and interests in data engineering and cloud computing, understanding Spark SQL's window functions will be particularly valuable. These functions enable sophisticated data analysis and are essential for tasks like calculating moving averages, running totals, or ranking without needing to shuffle data back and forth from the server. Here's a detailed overview of the most commonly used window functions in Spark SQL, along with examples to demonstrate their utility in real-world scenarios.

### 1. **`row_number()`**

   Assigns a unique sequential integer to rows within a partition of a result set, starting at 1 for the first row in each partition.

   **Example:**

   ```sql
   SELECT id, data, row_number() OVER (PARTITION BY category ORDER BY data) AS row_num
   FROM dataset;
   ```

### 2. **`rank()`**

   Provides a rank to each row within a partition of a result set, with gaps in the ranking for tied values.

   **Example:**

   ```sql
   SELECT id, data, rank() OVER (PARTITION BY category ORDER BY data) AS rank
   FROM dataset;
   ```

### 3. **`dense_rank()`**

   Similar to `rank()`, but without gaps in the ranking sequence when there are ties.

   **Example:**

   ```sql
   SELECT id, data, dense_rank() OVER (PARTITION BY category ORDER BY data) AS dense_rank
   FROM dataset;
   ```

### 4. **`lead()`**

   Returns the value of an expression evaluated at the following row within the same result set partition.

   **Example:**

   ```sql
   SELECT id, data, lead(data, 1) OVER (PARTITION BY category ORDER BY data) AS next_data
   FROM dataset;
   ```

### 5. **`lag()`**

   Returns the value of an expression evaluated at the preceding row within the same result set partition.

   **Example:**

   ```sql
   SELECT id, data, lag(data, 1) OVER (PARTITION BY category ORDER BY data) AS prev_data
   FROM dataset;
   ```

### 6. **`first_value()`**

   Provides the first value in an ordered set of values.

   **Example:**

   ```sql
   SELECT id, data, first_value(data) OVER (PARTITION BY category ORDER BY data) AS first_val
   FROM dataset;
   ```

### 7. **`last_value()`**

   Provides the last value in an ordered set of values. Care must be taken to ensure the window frame specification covers the rows considered.

   **Example:**

   ```sql
   SELECT id, data, last_value(data) OVER (PARTITION BY category ORDER BY data RANGE BETWEEN CURRENT ROW AND UNBOUNDED FOLLOWING) AS last_val
   FROM dataset;
   ```

### 8. **`nth_value()`**

   Returns the value of an expression evaluated at the nth row of the window frame.

   **Example:**

   ```sql
   SELECT id, data, nth_value(data, 2) OVER (PARTITION BY category ORDER BY data) AS second_val
   FROM dataset;
   ```

### 9. **`sum()`**

   Calculates the sum of an expression over a range of input values.

   **Example:**

   ```sql
   SELECT id, data, sum(data) OVER (PARTITION BY category ORDER BY data) AS running_total
   FROM dataset;
   ```

### 10. **`avg()`**

    Calculates the average of an expression over a range of input values.

    **Example:**
    ```sql
    SELECT id, data, avg(data) OVER (PARTITION BY category ORDER BY data) AS running_avg
    FROM dataset;
    ```

### Applying These in MLOps and Cloud Computing

In your MLOps and cloud computing journey, these window functions can be applied in numerous scenarios:

- **Performance Monitoring**: Calculating running totals or moving averages of system metrics.
- **Customer Behavior Analysis**: Ranking or comparing customer activities over time.
- **Financial Transactions**: Analyzing running totals, growth rates, or periodic assessments in financial data.

Moreover, mastering these functions will enhance your SQL querying capabilities, essential for efficient data manipulation and analysis in big data environments like Spark, and can be particularly useful when working with cloud-based data warehouses or data lakes.

```sql
# Join example
val movies = (spark.read.format("csv")
                         .option("header", "true")
                         .option("samplingRatio", 0.001)
                         .option("inferSchema", "true")
                         .load("/data/BollywoodMovieDetail.csv"))

val actors = (spark.read.format("csv")
                         .option("header", "true")
                         .option("samplingRatio", 0.001)
                         .option("inferSchema", "true")
                         .load("/data/BollywoodActorRanking.csv"))                         

spark.sql("""CREATE OR REPLACE TEMPORARY VIEW movies USING csv OPTIONS (path "/data/BollywoodMovieDetail.csv", header "true", inferSchema "true", mode "FAILFAST")""")

spark.sql("""CREATE TABLE tempTbl1 AS SELECT title, releaseYear, hitFlop, split(actors,"[|]") AS actors FROM movies""")                         

spark.sql("""CREATE TABLE tempTbl2 AS SELECT title, releaseYear, hitFlop, upper(trim(actor)) AS actor FROM (SELECT  title, releaseYear, hitFlop, explode(actors) AS actor FROM tempTbl1)""")

val actorsDF = actors.withColumn("actorName",trim(upper($"actorName")))

val moviesDF = spark.sql("""SELECT * FROM tempTbl2""")

moviesDF.join(actorsDF, $"actor" === $"actorName").show(5,false)

# Union example
val df1 = movies.select("title").where( $"releaseYear" > 2010).limit(2)

val df2 = movies.select("title").where( $"releaseYear" < 2010).limit(2)

df1.union(df2).show(false)

# Window function example
spark.sql("SELECT title, hitFlop, releaseYear, dense_rank() OVER (PARTITION BY releaseYear ORDER BY hitFLop DESC) as rank  FROM MOVIES").show(3)

spark.sql("SELECT title, hitFlop, releaseYear, dense_rank() OVER (PARTITION BY releaseYear ORDER BY hitFLop DESC) as rank  FROM MOVIES WHERE releaseYear=2013").show(3)

spark.sql("SELECT * FROM (SELECT title, hitFlop, releaseYear, dense_rank() OVER (PARTITION BY releaseYear ORDER BY hitFLop DESC) as rank  FROM MOVIES) tmp WHERE rank <=2 ORDER BY releaseYEar").show(5)
```

## Rank and Dense Rank Functions

To explain the differences between the `rank()` and `dense_rank()` functions in Spark SQL, let's use a simple dataset of sales data as an example. The dataset consists of sales transactions with the following fields: `transaction_id`, `employee_id`, and `amount`. We will examine how the sales amounts rank among transactions by each employee.

### Dataset Example

Imagine we have the following dataset:

| transaction_id | employee_id | amount |
|----------------|-------------|--------|
| 1              | 100         | 150    |
| 2              | 100         | 150    |
| 3              | 100         | 200    |
| 4              | 101         | 300    |
| 5              | 101         | 300    |
| 6              | 101         | 500    |

We will use this dataset to compare the `rank()` and `dense_rank()` functions when ranking the sales amounts by each employee.

### Difference in Functions

- **`rank()`** assigns a unique rank to each row within a partition, with a gap in the ranking for tied values.
- **`dense_rank()`** also assigns ranks within a partition but does not leave gaps in the ranking for ties, meaning the ranks are consecutive numbers.

### SQL Code Snippet Using Spark SQL

First, let's define a temporary view using this data (assuming the data is in a DataFrame called `sales_df`):

```python
# Sample data creation and registration as a temporary view
sales_data = [
    (1, 100, 150),
    (2, 100, 150),
    (3, 100, 200),
    (4, 101, 300),
    (5, 101, 300),
    (6, 101, 500)
]

# Assuming the use of PySpark to create a DataFrame and register it
sales_df = spark.createDataFrame(sales_data, ["transaction_id", "employee_id", "amount"])
sales_df.createOrReplaceTempView("sales")
```

Next, the SQL to apply the `rank()` and `dense_rank()` functions:

```sql
SELECT 
    employee_id,
    amount,
    rank() OVER (PARTITION BY employee_id ORDER BY amount) AS rank,
    dense_rank() OVER (PARTITION BY employee_id ORDER BY amount) AS dense_rank
FROM 
    sales
```

### Expected Output and Explanation

This query will generate the following output:

| employee_id | amount | rank | dense_rank |
|-------------|--------|------|------------|
| 100         | 150    | 1    | 1          |
| 100         | 150    | 1    | 1          |
| 100         | 200    | 3    | 2          |
| 101         | 300    | 1    | 1          |
| 101         | 300    | 1    | 1          |
| 101         | 500    | 3    | 2          |

**Explanation:**

- For `employee_id` 100:
  - Both transactions with `amount` 150 share the same rank (1) and dense_rank (1) because they are tied.
  - The next `amount` (200) has a rank of 3 with `rank()` due to the gap created by the tie at rank 1. However, it has a dense_rank of 2 with `dense_rank()` since dense_rank does not create gaps.
- For `employee_id` 101:
  - A similar pattern is observed with amounts 300 and 500.

Using `rank()` and `dense_rank()` allows for nuanced distinctions in ranking data, particularly useful in scenarios like sales performance metrics, leaderboards, and other analytics where relative positions are critical.
