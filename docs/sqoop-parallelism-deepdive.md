# Sqoop - Parallelism, Incremental imports and custom queries

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/sqoop.png)

Parallelism in Sqoop is primarily handled through the use of multiple mappers. Each mapper in Sqoop is responsible for transferring a portion of the data, allowing for concurrent data transfer and thus speeding up the overall process. Here's how parallelism is managed and can be controlled in Sqoop:

### 1. **Number of Mappers**:

By default, Sqoop uses 4 mappers to transfer data. However, you can specify a different number using the `--num-mappers` or `-m` option.

Example:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--table mytable \
--num-mappers 8 \
--target-dir /user/hive/warehouse/mytable_data
```

In this example, Sqoop will use 8 mappers to import data from `mytable`.

### 2. **Splitting Data**:

For Sqoop to effectively use multiple mappers, it needs to split the data in the source table into chunks. Sqoop does this based on the primary key of the table or another column you specify.

- **Primary Key Splitting**: By default, Sqoop will try to determine the minimum and maximum values of the primary key and split the range into roughly equal chunks for each mapper.

- **Custom Column Splitting**: If a table doesn't have a primary key or if you want to use a different column for splitting, you can use the `--split-by` option.

Example:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--table mytable \
--split-by column_name \
--num-mappers 8 \
--target-dir /user/hive/warehouse/mytable_data
```

### 3. **Handling Non-Splittable Queries**:

Some queries or data sources might not be easily splittable. In such cases, you can force Sqoop to use only one mapper with the `--num-mappers 1` option. This is common when using custom queries with joins or when importing from a database view.

### 4. **Boundary Values**:

For more control over the splitting process, you can specify the minimum and maximum values for the splitting column using the `--boundary-query` option.

Example:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--table mytable \
--split-by column_name \
--boundary-query "SELECT MIN(column_name), MAX(column_name) FROM mytable" \
--num-mappers 8 \
--target-dir /user/hive/warehouse/mytable_data
```

### 5. **Considerations**:

- **Database Load**: Increasing the number of mappers can put more load on the source database. It's essential to find a balance that maximizes data transfer speed without overloading the database.

- **Data Skew**: If data is not uniformly distributed across the splitting column, some mappers might get more data than others, leading to inefficiencies. In such cases, consider choosing a different splitting column or adjusting boundary values.

Sqoop's parallelism is achieved through the use of multiple mappers, and various options allow you to control and optimize this parallelism based on your specific data and infrastructure.


## When and how to use different kinds of Sqoop imports

Let's delve into when and how to use parallel imports, incremental imports, and custom queries with Sqoop, along with example commands for each:

### 1. Parallel Import:

**When to use**: 
- When dealing with large datasets to speed up the transfer process.
- When you have a distributed Hadoop cluster to utilize the resources of all nodes.
- When you have time constraints, such as nightly batch jobs.

**Example Command**:
Importing a table with 4 parallel tasks:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--username myuser \
--password mypass \
--table mytable \
--num-mappers 4 \
--target-dir /user/hive/warehouse/mytable_data
```

### 2. Incremental Imports:

**When to use**:

- **Append Mode**: 
  - For tables where records are only added and never updated, like log tables or transaction tables.
  
**Example Command**:

Importing only new rows based on the `id` column:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--username myuser \
--password mypass \
--table mytable \
--incremental append \
--check-column id \
--last-value 1000 \
--target-dir /user/hive/warehouse/mytable_incremental_data
```

- **Last Modified Mode**: 
  - For tables where records can be updated, like user profiles or inventory tables.

**Example Command**:

Importing rows that have been added or updated since the last import based on a `last_modified` timestamp column:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--username myuser \
--password mypass \
--table mytable \
--incremental lastmodified \
--check-column last_modified \
--last-value '2023-01-01 00:00:00' \
--target-dir /user/hive/warehouse/mytable_incremental_data
```

### 3. Custom Queries:

**When to use**:
- When you need to perform transformations on the data before importing.
- When you want to import the result of a join operation between tables.
- When you want to import a subset of data based on certain criteria.
- When you want to avoid full table scans on the source database.

**Example Command**:

Importing data based on a custom SQL query:

```bash
sqoop import \
--connect jdbc:mysql://mydbserver.example.com/mydb \
--username myuser \
--password mypass \
--query 'SELECT a.*, b.* FROM a JOIN b ON a.id=b.id WHERE $CONDITIONS' \
--split-by a.id \
--target-dir /user/hive/warehouse/joined_data
```

Note: The `$CONDITIONS` placeholder is required in the custom query, and Sqoop will replace it with appropriate conditions to support parallel imports.

By understanding the specific requirements of your data transfer task and the nature of your source data, you can effectively choose between parallel imports, incremental imports, and custom queries with Sqoop.