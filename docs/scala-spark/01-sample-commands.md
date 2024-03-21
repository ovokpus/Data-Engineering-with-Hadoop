# Apache Spark Sample commands from EducativeIO

```scala
// Start-up the spark-shell
spark-shell

// Print the context object
spark.sparkContext

// Read the CSV file containing car records
val carsRDD = spark.sparkContext.textFile("file:///DataJek/cars.txt")

// Print the number of records read-in
carsRDD.count()
 
// Print the first record 
carsRDD.first()
 
 // Print all the records
carsRDD.foreach(System.out.println(_))

// Print the number of partitions
carsRDD.getNumPartitions

// Experiment with the map function
val concatRDD = carsRDD.map( (line:String) =>  line + " " + line)
concatRDD.first()

// Attempting to use the map function to split
val temp = carsRDD.map(line => line.split(","))
temp.collect()

// Word count problem
val temp1 = carsRDD.flatMap(line => line.split(","))
temp1.collect()
val temp2 = temp1.map(word => (word, 1))
temp2.collect()
val temp3 = temp2.reduceByKey( (v1, v2) => v1 + v2)
temp3.collect()
val temp4 = temp3.reduce( (t1, t2) => ("totalCars", t1._2 + t2._2) )
temp4.collect()
```

```sh
# Start the Hadoop Cluster
/DataJek/startHadoop.sh

# Verify Hadoop daemons are up
jps

# Upload data file to HDFS
hdfs dfs -copyFromLocal /DataJek/cars.data /

# Run spark-submit. Note that the command we execute has additional configuration
# settings because of the limited resources that the terminal runs with. NOTE THAT 
# YOU MAY SEE THE JOB FAIL, IN CASE, THE EDUCATIVE PLATFORM RUNS WITH A VM WITH 
# HIGHLY CONSTRAINED RESOURCES. 
spark-submit --conf "spark.executor.instances=1" --conf "spark.executor.memory=500mb"  --master yarn --deploy-mode client --class io.datajek.spark.CarCounterProgram /DataJek/Spark-1.0-SNAPSHOT.jar

# Verify the output directory was created
hdfs dfs -ls /output

# Print results on console. The results should include the car makes
# and their consolidated counts.
hdfs dfs -text /output/part-00000

# Delete the output directory to re-run the application in local mode
hdfs dfs -rm -r /output

# Re-run in local mode
spark-submit --master local --deploy-mode client --class io.datajek.spark.CarCounterProgram /DataJek/Spark-1.0-SNAPSHOT.jar

# Observe the output
hdfs dfs -text /output/part-00000
```

```scala
    // Start the Spark shell
    spark-shell

    // Create a dataframe consisting of ints from 0 to 9
    val dataFrame1 = spark.range(10).toDF()
    val dataFrame2 = spark.range(10).toDF()

    // Examine the number of partitions
    dataFrame1.rdd.getNumPartitions

    // Repartition the data
    val transformation1 = dataFrame1.repartition(20)
    val transformation2 = dataFrame2.repartition(20)

    // Create multiples of 3 from dataFrame1
    val mulsOf3 = transformation1.selectExpr("id * 3 as id")

    // Perform an inner join between the two dataframes.
    // transformation1.collect() = [0, 1, 2, 3, 4, 5. 6, 7, 8, 9]
    // mulsOf3.collect() = [0, 3, 6, 9, 12, 15, 18, 21, 24, 27]
    val join = mulsOf3.join(transformation2, "id")

    // Sum the intersection of the two dataFrames now
    // join.collect() = [0, 3, 6]
    val sum = join.selectExpr("sum(id)")

    // Perform an action to get the final result
    val result = sum.collect()

    // Examine the physical plan generated
    sum.explain()
```

## Sequence file

```scala
    # Start Hadoop
    ./DataJek/startHadoop.sh

    # Upload a sequence file
    hdfs dfs -copyFromLocal DataJek/fancyCars.seq /

    # Attempt to read the file, you'll get an error
    hdfs dfs -text /fancyCars.seq

    # Set the class path to include the Car class 
    export HADOOP_CLASSPATH="/DataJek/SequenceFiles-1.0-SNAPSHOT.jar"

    # Rerun the text command and the contents of the sequence file are printed
    hdfs dfs -text /fancyCars.seq
```

## Avro

```scala
    # cd into the DataJek directory
    cd /DataJek

    # execute the following command to print the emebedded schema on console
    java -jar avro-tools-1.9.1.jar getschema fancyCars.avro

    # Change directory to DataJek
    cd DataJek/

    # Compile the schema to Java class
    java -jar avro-tools-1.9.1.jar compile schema car.avsc .

    # Examine the auto-generated class
    cat io/datajek/Car.java
```

### Avro IDL

```scala
    # Change directory
    cd /DataJek

    # Compile IDL file to protocol file
    java -jar avro-tools-1.9.1.jar idl car.avdl car.avpr

    # Compile protocol file to Java class
    java -jar avro-tools-1.9.1.jar compile protocol car.avpr .

    # View the generated files
    ls /Datajek/io/datajek/

    # Compile IDL file to schema file
    java -jar avro-tools-1.9.1.jar idl2schemata car.avdl

    # View the generated schema
    cat Car.avsc
```

---

### Parquet

```scala
    # Change directory
    cd DataJek

    # Inspect the contents of the parquet file.
    java  -jar ./parquet-tools-1.6.0rc7.jar cat fancyCarsInAvroParquet.parquet

    # Retrieve schema.
    java  -jar ./parquet-tools-1.6.0rc7.jar schema fancyCarsInAvroParquet.parquet

    # Retrieve metadata.
    java  -jar ./parquet-tools-1.6.0rc7.jar meta fancyCarsInAvroParquet.parquet

    # Debug parquet file. Prints Definition and Repitition levels.
    java  -jar ./parquet-tools-1.6.0rc7.jar dump fancyCarsInAvroParquet.parquet
```

---

```scala
    # Start spark-shell. There's also an equivalent shell for python but for this
    # exercise we'll work with the Scala shell. This is Spark running in local mode.
    # All the Spark operations run locally in a single JVM.
    spark-shell

    # Check the version of Spark. Note the variable spark represents the SparkSession.
    spark.version

    # Read a CSV file of movies. Here we are creating a DataFrame from the input
    # file. The DataFrame is a Spark high level structured API that we'll cover 
    # in detail later. 
    val movies = spark.read.text("/data/BollywoodMovieDetail.csv")

    # Count the number of lines in the file.
    movies.count()

    # Now print the first five rows of the read-in file. The second parameter if
    # set to true will print truncated lines.
    movies.show(5, false)

```

---

## Spark Datasets

```scala
    # Create case class to reprsent a row from the data file
    import org.apache.spark.sql.Encoders

    case class BMovieDetail(imdbID: String,
    title: String,
    releaseYear: Int,
    releaseDate: String,
    genre: String,
    writers: String,
    actors: String,
    directors: String,
    sequel: String,
    rating: Int)

    val caseClassSchema = Encoders.product[BMovieDetail].schema

    val movieDataset = (spark.read.option("header",true)
                        .schema(caseClassSchema)
                        .csv("/data/BollywoodMovieDetail.csv")
                        .as[BMovieDetail])

    movieDataset

    movieDataset.show(3)

    # Actions and transformations on Datasets

    movieDataset.filter(row => {row.releaseYear > 2010})

    val tempDataset = movieDataset.filter(row => {row.releaseYear > 2010})

    tempDataset.show(3)

    case class MovieShortDetail (
    imdbID: String,
    title: String)

    val tempDataset = movieDataset.filter(row => {row.releaseYear > 2010})
                                        .map(row => (row.imdbID, row.title))
                                        .toDF("imdbID", "title")
                                        .as[MovieShortDetail]

    println(tempDataset.first())

    val tempDataset2 = movieDataset.select($"imdbID", $"title")
                                        .where("releaseYear > 2010")
                                        .as[MovieShortDetail]

    println(tempDataset2.first())
```

---

### Spark Datasets with Scala Case Class and Java Bean Class

```scala
    # Generating data using SparkSession
    case class MovieDetailShort(imdbID: String, rating: Int)

    val rnd = new scala.util.Random(9)

    val data = for(i <- 0 to 100) yield (MovieDetailShort("movie-"+i, rnd.nextInt(10)))

    val datasetMovies = spark.createDataset(data)

    datasetMovies.show(3)

    # Using filter
    (datasetMovies.filter(mov => mov.rating > 5)
                .show(3))

    def highRatedMovies(mov : MovieDetailShort) = mov.rating > 5

    (datasetMovies.filter(highRatedMovies(_))
                .show(3))

    #Using map
    case class MovieGrade(imdbID: String, grade: String)

    def movieGrade(mov: MovieDetailShort):MovieGrade = { 
        val grade = if (mov.rating == 5) "B" 
                    else if (mov.rating < 5) "C" 
                    else "A"; 
        MovieGrade(mov.imdbID, grade) }

    datasetMovies.map(movieGrade).show(3)    
```

## DataFrames in Spark

```scala
    # Calculating average rating usig RDDs
    (sc.parallelize(Seq(("Gone with the Wind", 6), ("Gone with the Wind", 8),
        ("Gone with the Wind", 8)))
        .map(v => (v._1, (v._2, 1)))
        .reduceByKey((k, v) => (k._1 + v._1, k._2 + v._2))
        .map(x => (x._1, x._2._1 / (x._2._2 * 1.0)))
        .collect())

    # Calculating average rating usig DataFrames
    val dataDF = (spark.createDataFrame(Seq(("Gone with the Wind", 6),
        ("Gone with the Wind", 8), ("Gone with the Wind", 8)))
        .toDF("movie", "rating"))

    val avgDF = (dataDF.groupBy("movie").agg(avg("rating")))
    avgDF.show()

    # More examples
    val movies = (spark.read.format("csv")
        .option("header","true")
        .option("inferSchema","true")
        .load("/data/BollywoodMovieDetail.csv"))

    movies.schema

    # Explicitly passing-in the schema
    import org.apache.spark.sql.types._

    val customSchema = (StructType(Array(StructField("imdbId",StringType,true), 
    StructField("title",StringType,true), 
    StructField("releaseYear",IntegerType,true), 
    StructField("releaseDate",StringType,true), 
    StructField("genre",StringType,true), 
    StructField("writers",StringType,true), 
    StructField("actors",StringType,true), 
    StructField("directors",StringType,true), 
    StructField("sequel",IntegerType,true), 
    StructField("hitFlop",IntegerType,true))))

    val movies = (spark.read.format("csv")
                .option("header","true")
                .option("inferSchema","false")
                .schema(customSchema)load("/data/BollywoodMovieDetail.csv"))
    movies.schema

    # Using sampling ratio to determine schema
    val movies = (spark.read.format("csv")
    .option("header","true")
    .option("inferSchema","true")
    .option("samplingRatio", 0.01)
    .load("/data/BollywoodMovieDetail.csv"))

    movies.schema

    # Specifying schema using DDL
    val ddl = "imdbId STRING, title STRING, releaseYear STRING, releaseDate STRING, genre STRING, writers STRING, actors STRING, directors STRING, sequel INT, hitFlop INT"

    val movies = (spark.read.format("csv")
                .option("header","true")
                .option("inferSchema","false")
                .schema(ddl).load("/data/BollywoodMovieDetail.csv"))

    # Specifyig DATE types in schema
    val ddl = "imdbId STRING, title STRING, releaseYear DATE, releaseDate DATE, genre STRING, writers STRING, actors STRING, directors STRING, sequel INT, hitFlop INT"

    val movies = (spark.read.format("csv")
                .option("header","true")
                .option("inferSchema","false")
                .schema(ddl).load("/data/BollywoodMovieDetail.csv"))
    movies.show(1, false)

    # Writing out DataFrames
    (movies.write.format("parquet")
        .save("/data/moviesFile"))

    # quit the shell using Ctrl + C 
    ls /data/moviesFile
```

### DataFrame Column Operations

```scala
    # Reading-in movies
    val movies = (spark.read.format("csv")
    .option("header","true")
    .option("inferSchema","true")
    .load("/data/BollywoodMovieDetail.csv"))

    # Listing all columns
    movies.columns

    # Accessing columns
    var ratingCol = movies.col("hitFlop")

    movies.select(ratingCol).show(5)

    movies.select("hitFlop").show(5)

    movies.select(expr("hitFlop")).show(5)

    # Manipulating columns
    (movies.select(expr("hitFlop > 5"))
        .show(3))

    (movies.select(movies.col("hitFlop") > 5)
        .show(3))

    (movies.withColumn("Good Movies to Watch", expr("hitFlop > 5"))
        .show(3))

    (movies.withColumn("Good Movies to Watch", expr("hitFlop > 5"))
        .select("Good Movies to Watch")
        .show(3))

    # Sorting columns
    var ratingCol = movies.col("hitFlop")

    (movies.sort(ratingCol.desc)
        .show(3))

    (movies.sort($"hitFlop".desc)
        .show(3))
```

---

### DataFrame Row Operations

```scala
    # Creating Row
    import org.apache.spark.sql.Row

    val row = Row("Upcoming New Movie", 2021, "Comedy")

    row(0)

    row(1)

    row(2)

    val rows = (Seq(("Tom Cruise Movie", 2021, "Comedy"), 
                ("Rajinikanth Movie", 2021, "Action")))

    val newMovies = rows.toDF("Movie Name", "Release Year", "Genre")

    newMovies.show()

    # Projections and filters
    val movies = (spark.read.format("csv")
                .option("header","true")
                .option("inferSchema","true")
                .load("/data/BollywoodMovieDetail.csv"))

    (movies.select("title")
        .where(col("hitFlop") > 8)
        .show())

    (movies.select("title")
        .where(col("hitFlop") > 8)
        .count())

    (movies.select("title")
                .where(col("hitFlop") > 8)
                .show())

    (movies.select("title")
                .where(col("hitFlop") > 8)
                .count())

    (movies.select("title")
                .filter($"genre".contains("Romance"))
                .count())

    (movies.select("title")
                .filter($"genre".contains("Romance"))
                .where($"releaseYear" > 2010)
                .count())

    (movies.select("releaseYear")
                .distinct()
                .sort($"releaseYear".desc)
                .show())             
```

---

### More DataFrame Operations

```scala
    # Read in the movies data
    val movies = (spark.read.format("csv")
                .option("header","true")
                .option("inferSchema","true")
                .load("/data/BollywoodMovieDetail.csv"))

    # Changing column names
    val moviesNewColDF = movies.withColumnRenamed("hitFlop","Rating")

    moviesNewColDF.printSchema

    # Changing column types
    val newDF = movies.withColumn("launchDate", to_date($"releaseDate", "d MMM yyyy"))
                    .drop("releaseDate")

    newDF.printSchema

    (newDF.select("releaseDate","launchDate")
        .where($"launchDate".isNull)
        .show(5,false))

    (newDF.select("releaseDate","launchDate")
        .where($"launchDate".isNull)
        .count())

    (newDF.select(year($"launchDate"))
        .distinct()
        .orderBy(year($"launchDate"))
        .show())

    # Aggregations
    (movies.select("releaseYear")
        .groupBy("releaseYear")
        .count()
        .orderBy("releaseYear")
        .show)

    (movies.select(max($"hitFlop"))
        .show)

    (movies.select(min($"hitFlop"))
        .show)

    (movies.select(sum($"hitFlop"))
        .show)

    (movies.select(avg($"hitFlop"))
        .show)

    (movies.select("releaseYear","hitFlop")
        .groupBy("releaseYear")
        .avg("hitFlop")
        .orderBy("releaseYear")
        .show)
```

---

