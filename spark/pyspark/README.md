


## Sructured Streaming
---

### 1. Importing Libraries

```python
from pyspark.sql import SparkSession
import pyspark.sql.functions as func
```

- **SparkSession:** This is the entry point for using DataFrame and SQL functionality in PySpark.
- **pyspark.sql.functions:** Imported as `func`, this module provides many useful functions for transforming and analyzing data (e.g., regex extraction, windowing, ordering).

---

### 2. Creating the Spark Session

```python
spark = SparkSession.builder.appName("StructuredStreaming").getOrCreate()
```

- **SparkSession Builder:** Initializes a new SparkSession with the application name "StructuredStreaming". The comment mentions that additional configuration might be necessary on Windows systems.

---

### 3. Reading the Streaming Data

```python
accessLines = spark.readStream.text("s3a://spark-course-ovo/logs/")
```

- **Streaming Input:** This line sets up a streaming DataFrame that monitors the specified S3 bucket (`s3a://spark-course-ovo/logs/`) for new text files.
- **accessLines:** Each row in this DataFrame represents a line from one of the log files.

---

### 4. Defining Regular Expression Patterns

```python
contentSizeExp = r'\s(\d+)$'
statusExp = r'\s(\d{3})\s'
generalExp = r'\"(\S+)\s(\S+)\s*(\S*)\"'
timeExp = r'\[(\d{2}/\w{3}/\d{4}:\d{2}:\d{2}:\d{2} -\d{4})]'
hostExp = r'(^\S+\.[\S+\.]+\S+)\s'
```

- **Purpose:** These regex patterns are used to extract specific components from each log line:
  - **hostExp:** Extracts the host information at the beginning of the line.
  - **timeExp:** Captures the timestamp enclosed in square brackets.
  - **generalExp:** Used to extract the HTTP method, endpoint, and protocol from the quoted part of the log.
  - **statusExp:** Extracts the three-digit HTTP status code.
  - **contentSizeExp:** Extracts the content size (numeric value) at the end of the line.

---

### 5. Parsing the Log Lines into a DataFrame

```python
logsDF = accessLines.select(
    func.regexp_extract('value', hostExp, 1).alias('host'),
    func.regexp_extract('value', timeExp, 1).alias('timestamp'),
    func.regexp_extract('value', generalExp, 1).alias('method'),
    func.regexp_extract('value', generalExp, 2).alias('endpoint'),
    func.regexp_extract('value', generalExp, 3).alias('protocol'),
    func.regexp_extract('value', statusExp, 1).cast('integer').alias('status'),
    func.regexp_extract('value', contentSizeExp, 1).cast('integer').alias('content_size')
)
```

- **regexp_extract:** Applies each regular expression to the 'value' column (each log line) to extract parts of the log.
- **Alias:** The extracted data is given meaningful column names (e.g., 'host', 'timestamp', 'method', etc.).
- **Casting:** The status code and content size are cast to integers for further numerical analysis.

---

### 6. Adding a Timestamp Column for Event-Time

```python
logsDF2 = logsDF.withColumn("eventTime", func.current_timestamp())
```

- **Current Timestamp:** Adds a new column named `eventTime` that contains the current processing time. This is useful for time-based aggregations in streaming.

---

### 7. Aggregating Data with Windowing

```python
endpointCounts = logsDF2.groupBy(
    func.window(func.col("eventTime"), "30 seconds", "10 seconds"),
    func.col("endpoint")
).count()
```

- **Windowing:** Groups the data into time windows of 30 seconds that slide every 10 seconds.
- **Grouping by Endpoint:** The data is also grouped by the `endpoint` field.
- **Count:** For each combination of time window and endpoint, the code calculates the count of occurrences.

---

### 8. Sorting the Aggregated Results

```python
sortedEndpointCounts = endpointCounts.orderBy(func.col("count").desc())
```

- **Ordering:** The resulting aggregated DataFrame is sorted in descending order by the count of endpoints. This makes it easier to see which endpoints are most frequently accessed.

---

### 9. Writing the Stream Output

```python
query = sortedEndpointCounts.writeStream.outputMode("complete").format("console") \
      .queryName("counts").start()
```

- **writeStream:** Sets up the streaming query to output the aggregated results.
- **Output Mode ("complete"):** Every time the stream is updated, the complete aggregated result is written to the console.
- **Console Format:** Outputs are printed to the console.
- **Query Name:** The query is named "counts" for identification purposes.
- **start():** Starts the streaming process.

---

### 10. Awaiting Termination and Stopping the Session

```python
query.awaitTermination()
spark.stop()
```

- **awaitTermination():** This call blocks the execution and waits until the streaming query is terminated manually or an error occurs.
- **spark.stop():** Cleans up and stops the SparkSession once the streaming is done.

---

### Summary

Overall, this code demonstrates how to build a PySpark Structured Streaming application that:
- Reads streaming log data from an S3 bucket.
- Parses each log line to extract structured information using regular expressions.
- Adds a processing timestamp for event-time based windowing.
- Groups the data by time windows and endpoint to count access frequencies.
- Sorts and writes the results to the console in real time.

This is a common pattern for monitoring and analyzing log data in real time using Spark's powerful streaming capabilities.


Below is a block‐by‐block explanation of the code for streaming:

---

### 1. File Header and Metadata
```python
# -*- coding: utf-8 -*-
"""
Created on Wed Dec 18 09:15:05 2019

@author: Frank
"""
```
**Explanation:**  
This section is a header that:
- Specifies the file encoding as UTF-8.
- Contains a docstring with metadata about when the file was created and who authored it. This information is useful for maintenance and documentation.

---

### 2. Importing Required Modules
```python
from pyspark import SparkContext
from pyspark.streaming import StreamingContext
from pyspark.sql import Row, SparkSession

from pyspark.sql.functions import regexp_extract
import os
```
**Explanation:**  
Here, the code imports the necessary libraries:
- **SparkContext & StreamingContext:** Although not used later directly in the code, these are part of the core Spark and Spark Streaming APIs.
- **Row and SparkSession:** Used for creating structured data (DataFrames) and managing Spark SQL operations.
- **regexp_extract:** A function to extract substrings from a column based on a regular expression; critical for parsing log data.
- **os:** The standard Python module for interacting with the operating system (e.g., checking and creating directories).

---

### 3. Creating a SparkSession
```python
spark = SparkSession.builder.appName("StructuredStreaming").getOrCreate()
```
**Explanation:**  
This line initializes a **SparkSession**, which is the entry point for using DataFrame and SQL functionality in Spark. The application is named "StructuredStreaming", and if an existing session is running, it will be reused. The comment indicates that additional configuration might be necessary for Windows environments.

---

### 4. Ensuring the Logs Directory Exists
```python
if not os.path.exists("logs"):
    os.makedirs("logs")
```
**Explanation:**  
Before starting the streaming process, the code checks whether a directory named "logs" exists:
- If it doesn’t exist, it creates the directory using `os.makedirs()`.  
This is important to avoid errors when the streaming process attempts to read files from a directory that might not exist.

---

### 5. Reading the Log Files as a Streaming DataFrame
```python
accessLines = spark.readStream.text("file:///home/hadoop/logs")
```
**Explanation:**  
This block sets up a streaming DataFrame:
- **`readStream.text(...)`:** Tells Spark to monitor the specified directory for new text files (in this case, the logs directory).  
- **File URI:** The path is specified as a file URI (`file:///home/hadoop/logs`), meaning Spark will continuously read new log data from this directory as it appears.

---

### 6. Parsing the Log Data Using Regular Expressions
```python
contentSizeExp = r'\s(\d+)$'
statusExp = r'\s(\d{3})\s'
generalExp = r'\"(\S+)\s(\S+)\s*(\S*)\"'
timeExp = r'\[(\d{2}/\w{3}/\d{4}:\d{2}:\d{2}:\d{2} -\d{4})]'
hostExp = r'(^\S+\.[\S+\.]+\S+)\s'

logsDF = accessLines.select(
    regexp_extract('value', hostExp, 1).alias('host'),
    regexp_extract('value', timeExp, 1).alias('timestamp'),
    regexp_extract('value', generalExp, 1).alias('method'),
    regexp_extract('value', generalExp, 2).alias('endpoint'),
    regexp_extract('value', generalExp, 3).alias('protocol'),
    regexp_extract('value', statusExp, 1).cast('integer').alias('status'),
    regexp_extract('value', contentSizeExp, 1).cast('integer').alias('content_size')
)
```
**Explanation:**  
This is the heart of the log processing:
- **Regular Expression Definitions:**  
  - `contentSizeExp`: Extracts the content size (a number at the end of the line).  
  - `statusExp`: Captures a three-digit HTTP status code surrounded by spaces.  
  - `generalExp`: Extracts three parts from a quoted string—typically the HTTP method, endpoint, and protocol.  
  - `timeExp`: Extracts the timestamp from within square brackets.  
  - `hostExp`: Extracts the hostname (assuming a typical domain format at the start of the log line).

- **Building the DataFrame:**  
  - The `select()` method uses `regexp_extract` to apply each regex on the 'value' column (which contains the raw log line).  
  - Each extraction is given an alias (e.g., 'host', 'timestamp', 'method', etc.), and numeric fields are cast to integers for proper numeric operations.

---

### 7. Aggregating Data by HTTP Status Code
```python
statusCountsDF = logsDF.groupBy(logsDF.status).count()
```
**Explanation:**  
This block groups the parsed log data by the HTTP status code:
- **`groupBy(logsDF.status)`:** Aggregates rows based on the status column.
- **`.count()`:** Counts the number of occurrences for each status code.
This produces a new DataFrame (`statusCountsDF`) with each status code and its corresponding access count.

---

### 8. Starting the Streaming Query to Output Results
```python
query = ( statusCountsDF.writeStream
          .outputMode("complete")
          .format("console")
          .queryName("counts")
          .start() )
```
**Explanation:**  
This segment initiates a streaming query that continuously outputs the aggregation results:
- **`writeStream`:** Indicates that the DataFrame is to be written out as a streaming sink.
- **`outputMode("complete")`:** In complete mode, Spark outputs the full aggregated result each time it updates.
- **`format("console")`:** Specifies that the results are printed to the console.
- **`queryName("counts")`:** Gives a name to the query (useful for management and debugging).
- **`start()`:** Launches the streaming query.

---

### 9. Running the Streaming Query Indefinitely
```python
query.awaitTermination()
```
**Explanation:**  
This call makes the application wait indefinitely:
- **`awaitTermination()`:** Causes the program to continue running until the streaming query is manually stopped or an error occurs. This keeps the streaming application active to process incoming log data.

---

### 10. Cleanly Shutting Down the Spark Session
```python
spark.stop()
```
**Explanation:**  
Once the streaming process is terminated, this command:
- **`spark.stop()`:** Gracefully shuts down the Spark session, ensuring that all resources are properly released.

---

Overall, this code is a PySpark structured streaming application that monitors a log directory, processes each log entry using regular expressions to extract useful fields, aggregates the data by HTTP status code, and continuously outputs the aggregated counts to the console.