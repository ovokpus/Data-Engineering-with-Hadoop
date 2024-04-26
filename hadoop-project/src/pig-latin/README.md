# Pig in the Hadoop Ecosystem

**Pig** is a high-level platform for creating programs that run on Apache Hadoop. The language for this platform is called **Pig Latin**. Pig is designed to handle all kinds of data, transforming it into a format that's suitable for analysis. It abstracts the complexity of Java-based MapReduce functions, allowing for query execution over large data sets without deep knowledge of the MapReduce paradigm.

## Key Components of Pig

1. **Pig Latin**: This is the scripting language used with Pig. It's a data flow language, which means that it specifies how data should flow through various transformations and storage phases. The language is simple and supports operations like join, filter, sort, etc.
2. **Execution Environment**: Pig scripts can run in either local mode (directly on your machine, suitable for development and testing) or on a Hadoop cluster (where it converts scripts into MapReduce jobs).
3. **Extensibility**: Users can extend Pig with custom functions written in Java, Python, etc., for more complex data manipulations that aren't supported natively in Pig Latin.

Pig simplifies the task of writing, maintaining, and optimizing data processing pipelines, integrating well with the Hadoop ecosystem to leverage distributed processing and storage capabilities.

## Pig Functions and Keywords
Certainly! Here's an explanation of some common Pig Latin keywords along with examples for each to illustrate their usage:

### 1. LOAD

**Purpose**: Loads data from the file system into a Pig relation.
**Example**:

```pig
A = LOAD 'data.csv' USING PigStorage(',') AS (id:int, name:chararray, age:int);
```

This command loads data from a CSV file named `data.csv`, where fields are separated by commas. It explicitly defines the schema of the data with fields named `id`, `name`, and `age`.

### 2. STORE

**Purpose**: Saves a Pig relation to the file system.
**Example**:

```pig
STORE A INTO 'output' USING PigStorage(',');
```

This saves the relation `A` to a file or directory named `output` in the file system, using a comma as the field delimiter.

### 3. DUMP

**Purpose**: Outputs the contents of a Pig relation to the console.
**Example**:

```pig
DUMP A;
```

This command prints the content of relation `A` to the console, allowing you to view the data.

### 4. FILTER

**Purpose**: Filters records in a relation based on a condition.
**Example**:

```pig
B = FILTER A BY age > 30;
```

This creates a new relation `B` containing only the records from relation `A` where the `age` field is greater than 30.

### 5. FOREACH ... GENERATE

**Purpose**: Transforms each record in a relation, potentially altering the number and type of fields.
**Example**:

```pig
C = FOREACH A GENERATE name, age * 2 AS doubleAge;
```

This transforms relation `A`, producing a new relation `C` that includes only the `name` and `age` fields, where the `age` is doubled.

### 6. GROUP

**Purpose**: Groups the data in one or more relations by one or more fields.
**Example**:

```pig
D = GROUP A BY age;
```

This groups the records in `A` by the `age` field. Each group will contain records having the same age.

### 7. JOIN

**Purpose**: Joins two or more relations by one or more common fields.
**Example**:

```pig
E = JOIN A BY id, B BY id;
```

This joins relations `A` and `B` based on the `id` field. Records with the same `id` in both relations are combined into new records in relation `E`.

### 8. ORDER

**Purpose**: Sorts the records in a relation by one or more fields.
**Example**:

```pig
F = ORDER A BY age DESC;
```

This sorts the records in relation `A` in descending order based on the `age` field.

### 9. DISTINCT

**Purpose**: Removes duplicate records from a relation.
**Example**:

```pig
G = DISTINCT A;
```

This creates a new relation `G` where all duplicate records from `A` are removed.

### 10. SPLIT

**Purpose**: Splits a relation into two or more relations based on a condition.
**Example**:

```pig
SPLIT A INTO older IF age > 30, younger IF age <= 30;
```

This splits relation `A` into two new relations: `older` containing records where `age` is greater than 30, and `younger` for records where `age` is 30 or younger.

Each of these operations in Pig Latin helps in manipulating large datasets efficiently, utilizing Hadoop's power to handle and process big data.

## Pig Latin Example Explanation

The provided script is written in Pig Latin, which is used to perform data manipulations on large datasets within the Apache Hadoop ecosystem. Here’s a breakdown of each line and its purpose:

1. **Load Ratings Data**:

   ```pig
   ratings = LOAD '/user/maria_dev/ml-100k/u.data' AS (userID:int, movieID:int, rating:int, ratingTime:int);
   ```

   This line loads the dataset from a specified path in the Hadoop file system. The dataset contains user ratings for movies, with each field in the data corresponding to user ID, movie ID, the rating given, and the time of the rating, defined with respective data types.

2. **Load Metadata**:

   ```pig
   metadata = LOAD '/user/maria_dev/ml-100k/u.item' USING PigStorage('|')
       AS (movieID:int, movieTitle:chararray, releaseDate:chararray, videoRelease:chararray, imdbLink:chararray);
   ```

   This loads another dataset containing metadata about the movies. The data is delimited by vertical bars (`|`). Each record includes the movie ID, title, release date, video release date, and a link to its IMDb page.

3. **Extract Movie Names**:

   ```pig
   nameLookup = FOREACH metadata GENERATE movieID, movieTitle;
   ```

   This line processes the metadata to create a new dataset (`nameLookup`) that includes only the movie ID and movie title, useful for later joining with ratings data.

4. **Group Ratings by Movie ID**:

   ```pig
   groupedRatings = GROUP ratings by movieID;
   ```

   Groups the ratings data by the movie ID. This is essential for calculating aggregated statistics like average rating per movie.

5. **Calculate Average Ratings and Count**:

   ```pig
   averageRatings = FOREACH groupedRatings GENERATE group AS movieID, AVG(ratings.rating) AS avgRating,
       COUNT(ratings.rating) AS numRatings;
   ```

   Calculates the average rating and the number of ratings for each movie. This step is crucial for filtering and analysis based on these metrics.

6. **Filter Out Bad Movies**:

   ```pig
   badMovies = FILTER averageRatings BY avgRating < 2.0;
   ```

   Filters the movies to find those with an average rating below 2.0, categorizing them as "bad" movies.

7. **Join Bad Movies with Their Titles**:

   ```pig
   namedBadMovies = JOIN badMovies BY movieID, nameLookup BY movieID;
   ```

   Joins the `badMovies` dataset with the `nameLookup` dataset to associate the bad movie IDs with their titles.

8. **Select Final Results**:

   ```pig
   finalResults = FOREACH namedBadMovies GENERATE nameLookup::movieTitle AS movieName,
       badMovies::avgRating as avgRating, badMovies::numRatings as numRatings;
   ```

   Generates the final results, selecting the movie title, average rating, and number of ratings for each bad movie from the join.

9. **Sort Results**:

   ```pig
   finalResultsSorted = ORDER finalResults BY numRatings DESC;
   ```

   Orders the final results by the number of ratings in descending order, which helps in understanding which bad movies had the most viewer engagement.

10. **Output the Results**:

   ```pig
   DUMP finalResultsSorted;
   ```

   Outputs the sorted list of bad movies, showing their titles, average ratings, and the number of ratings.

This script is an example of how Pig Latin is used to perform complex data transformations and analyses with relative ease, using a sequence of operations that include loading data, joining datasets, and applying aggregations and filters.
