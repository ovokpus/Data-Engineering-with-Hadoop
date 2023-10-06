# Sqoop Walkthrough

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/sqoop-import-architecture.png)

---

## Run basic sql queries using sqoop

List databases in mysql
```bash
[cloudera@quickstart ~]$ sqoop list-databases --connect jdbc:mysql://quickstart.cloudera:3306 --username cloudera --password cloudera
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/23 15:10:31 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/23 15:10:31 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
23/09/23 15:10:31 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
information_schema
cm
firehose
hue
metastore
mysql
nav
navms
oozie
retail_db
rman
sentry
[cloudera@quickstart ~]$ 
```

List tables in `retail_db` database
```bash
[cloudera@quickstart ~]$ sqoop list-tables --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password cloudera
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/23 15:11:44 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/23 15:11:44 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
23/09/23 15:11:44 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
categories
customers
departments
order_items
orders
products
[cloudera@quickstart ~]$ 
```

Run select queries using sqoop. Note that the query execution takes place inside of mysql, and not in HDFS
```bash
[cloudera@quickstart ~]$ sqoop eval --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password cloudera --query 'select * from products limit 5'
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/23 15:14:44 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/23 15:14:44 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
23/09/23 15:14:44 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
-----------------------------------------------------------------------------------------------------------------
| product_id  | product_category_id | product_name         | product_description  | product_price | product_image        | 
-----------------------------------------------------------------------------------------------------------------
| 1           | 2           | Quest Q64 10 FT. x 10 FT. Slant Leg Instant U |                      | 59.98        | http://images.acmesports.sports/Quest+Q64+10+FT.+x+10+FT.+Slant+Leg+Instant+Up+Canopy | 
| 2           | 2           | Under Armour Men's Highlight MC Football Clea |                      | 129.99       | http://images.acmesports.sports/Under+Armour+Men%27s+Highlight+MC+Football+Cleat | 
| 3           | 2           | Under Armour Men's Renegade D Mid Football Cl |                      | 89.99        | http://images.acmesports.sports/Under+Armour+Men%27s+Renegade+D+Mid+Football+Cleat | 
| 4           | 2           | Under Armour Men's Renegade D Mid Football Cl |                      | 89.99        | http://images.acmesports.sports/Under+Armour+Men%27s+Renegade+D+Mid+Football+Cleat | 
| 5           | 2           | Riddell Youth Revolution Speed Custom Footbal |                      | 199.99       | http://images.acmesports.sports/Riddell+Youth+Revolution+Speed+Custom+Football+Helmet | 
-----------------------------------------------------------------------------------------------------------------'
[cloudera@quickstart ~]$ 
[cloudera@quickstart ~]$ sqoop eval --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password cloudera --query 'select count(*) from products'
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/23 15:15:31 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/23 15:15:31 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
23/09/23 15:15:31 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
------------------------
| count(*)             | 
------------------------
| 1345                 | 
------------------------
[cloudera@quickstart ~]$ 
```

---


### Import RDMBS table with Sqoop

Create password file for sqoop as a best practice to not expose password value in terminal outputs

```bash
[cloudera@quickstart ~]$ echo -n "cloudera" > sqoop_pwd.txt
[cloudera@quickstart ~]$ hadoop fs -put sqoop_pwd.txt
```

Import table

```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table categories --target-dir /user/cloudera/sqoop-import/categories
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/30 11:12:35 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/30 11:12:36 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
23/09/30 11:12:36 INFO tool.CodeGenTool: Beginning code generation
23/09/30 11:12:37 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM `categories` AS t LIMIT 1
23/09/30 11:12:37 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM `categories` AS t LIMIT 1
23/09/30 11:12:37 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/lib/hadoop-mapreduce
Note: /tmp/sqoop-cloudera/compile/74d44dd8a5a77fb205c67fd5b8bbf93f/categories.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
23/09/30 11:12:38 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-cloudera/compile/74d44dd8a5a77fb205c67fd5b8bbf93f/categories.jar
23/09/30 11:12:38 WARN manager.MySQLManager: It looks like you are importing from mysql.
23/09/30 11:12:38 WARN manager.MySQLManager: This transfer can be faster! Use the --direct
23/09/30 11:12:38 WARN manager.MySQLManager: option to exercise a MySQL-specific fast path.
23/09/30 11:12:38 INFO manager.MySQLManager: Setting zero DATETIME behavior to convertToNull (mysql)
23/09/30 11:12:38 INFO mapreduce.ImportJobBase: Beginning import of categories
23/09/30 11:12:38 INFO Configuration.deprecation: mapred.job.tracker is deprecated. Instead, use mapreduce.jobtracker.address
23/09/30 11:12:38 INFO Configuration.deprecation: mapred.jar is deprecated. Instead, use mapreduce.job.jar
23/09/30 11:12:38 INFO Configuration.deprecation: mapred.map.tasks is deprecated. Instead, use mapreduce.job.maps
23/09/30 11:12:38 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
23/09/30 11:12:40 INFO db.DBInputFormat: Using read commited transaction isolation
23/09/30 11:12:40 INFO db.DataDrivenDBInputFormat: BoundingValsQuery: SELECT MIN(`category_id`), MAX(`category_id`) FROM `categories`
23/09/30 11:12:40 INFO db.IntegerSplitter: Split size: 14; Num splits: 4 from: 1 to: 58
23/09/30 11:12:40 INFO mapreduce.JobSubmitter: number of splits:4
23/09/30 11:12:40 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1696070190844_0001
23/09/30 11:12:41 INFO impl.YarnClientImpl: Submitted application application_1696070190844_0001
23/09/30 11:12:41 INFO mapreduce.Job: The url to track the job: http://quickstart.cloudera:8088/proxy/application_1696070190844_0001/
23/09/30 11:12:41 INFO mapreduce.Job: Running job: job_1696070190844_0001
23/09/30 11:12:49 INFO mapreduce.Job: Job job_1696070190844_0001 running in uber mode : false
23/09/30 11:12:49 INFO mapreduce.Job:  map 0% reduce 0%
23/09/30 11:12:55 INFO mapreduce.Job:  map 25% reduce 0%
23/09/30 11:12:56 INFO mapreduce.Job:  map 50% reduce 0%
23/09/30 11:12:57 INFO mapreduce.Job:  map 75% reduce 0%
23/09/30 11:12:58 INFO mapreduce.Job:  map 100% reduce 0%
23/09/30 11:12:58 INFO mapreduce.Job: Job job_1696070190844_0001 completed successfully
23/09/30 11:12:58 INFO mapreduce.Job: Counters: 30
        File System Counters
                FILE: Number of bytes read=0
                FILE: Number of bytes written=555504
                FILE: Number of read operations=0
                FILE: Number of large read operations=0
                FILE: Number of write operations=0
                HDFS: Number of bytes read=472
                HDFS: Number of bytes written=1029
                HDFS: Number of read operations=16
                HDFS: Number of large read operations=0
                HDFS: Number of write operations=8
        Job Counters 
                Launched map tasks=4
                Other local map tasks=4
                Total time spent by all maps in occupied slots (ms)=16247
                Total time spent by all reduces in occupied slots (ms)=0
                Total time spent by all map tasks (ms)=16247
                Total vcore-seconds taken by all map tasks=16247
                Total megabyte-seconds taken by all map tasks=16636928
        Map-Reduce Framework
                Map input records=58
                Map output records=58
                Input split bytes=472
                Spilled Records=0
                Failed Shuffles=0
                Merged Map outputs=0
                GC time elapsed (ms)=86
                CPU time spent (ms)=4110
                Physical memory (bytes) snapshot=813330432
                Virtual memory (bytes) snapshot=5526700032
                Total committed heap usage (bytes)=1068498944
        File Input Format Counters 
                Bytes Read=0
        File Output Format Counters 
                Bytes Written=1029
23/09/30 11:12:58 INFO mapreduce.ImportJobBase: Transferred 1.0049 KB in 19.9307 seconds (51.6289 bytes/sec)
23/09/30 11:12:58 INFO mapreduce.ImportJobBase: Retrieved 58 records.
[cloudera@quickstart ~]$ 
```
Underlying datafiles imported into sqoop
![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/underlying-sqoop-datafiles.png)

---

## Handling Parallelism in Sqoop

```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table categories --target-dir /user/cloudera/sqoop-import/categories1 --fields-terminated-by '|' --num-mappers 2
```

---

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/pipe-delimited-sqoop-files.png)

---

Import table without primary key
```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table emp --target-dir /user/cloudera/sqoop-import/emp --split-by emp_id

```

---

Custom Query import
```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --query 'select order_item_id, order_item_order_id, order_item_product_id, order_item_subtotal from order_items where order_item_quantity>1 and $CONDITIONS' --target-dir /user/cloudera/sqoop-import/order_items_multiple --split-by order_item_id

```

---

Incremental Sqoop import - append
```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table orders --target-dir /user/cloudera/sqoop-import/orders --incremental append --check-column order_id --last-value 0

# It imports records after the last value set in the command. Those are the incremental records
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table orders --target-dir /user/cloudera/sqoop-import/orders --incremental append --check-column order_id --last-value 68883
```
---

```bash
[cloudera@quickstart ~]$ sqoop import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table orders --target-dir /user/cloudera/sqoop-import/orders_lm --incremental lastmodified --check-column order_date --last-value '2023-09-30 13:10:41.0' --merge-key order_id
```

Only update jobs in sqoop have reduce. The rest are just map jobs. Use `--merge-key` for updates instead of `--append` in order to avoid duplicates

## Creating a Sqoop job

```bash
[cloudera@quickstart ~]$ sqoop job --create import_orders -- import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table orders --target-dir /user/cloudera/sqoop-import/orders_inc_job --incremental append --check-column order_id --last-value 0
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/30 14:07:35 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
[cloudera@quickstart ~]$ 

# list job
[cloudera@quickstart ~]$ sqoop job --list
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/30 14:08:12 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
Available jobs:
  import_orders

# show job
[cloudera@quickstart ~]$ sqoop job --show import_orders
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/30 14:12:37 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
Job: import_orders
Tool: import
Options:
----------------------------
verbose = false
incremental.last.value = 68885
db.connect.string = jdbc:mysql://quickstart.cloudera:3306/retail_db
codegen.output.delimiters.escape = 0
codegen.output.delimiters.enclose.required = false
codegen.input.delimiters.field = 0
split.limit = null
hbase.create.table = false
hdfs.append.dir = true
db.table = orders
codegen.input.delimiters.escape = 0
accumulo.create.table = false
import.fetch.size = null
codegen.input.delimiters.enclose.required = false
db.username = cloudera
reset.onemapper = false
codegen.output.delimiters.record = 10
import.max.inline.lob.size = 16777216
hbase.bulk.load.enabled = false
hcatalog.create.table = false
db.clear.staging.table = false
incremental.col = order_id
codegen.input.delimiters.record = 0
db.password.file = /user/cloudera/sqoop_pwd.txt
enable.compression = false
hive.overwrite.table = false
hive.import = false
codegen.input.delimiters.enclose = 0
accumulo.batch.size = 10240000
hive.drop.delims = false
customtool.options.jsonmap = {}
codegen.output.delimiters.enclose = 0
hdfs.delete-target.dir = false
codegen.output.dir = .
codegen.auto.compile.dir = true
relaxed.isolation = false
mapreduce.num.mappers = 4
accumulo.max.latency = 5000
import.direct.split.size = 0
codegen.output.delimiters.field = 44
export.new.update = UpdateOnly
incremental.mode = AppendRows
hdfs.file.format = TextFile
codegen.compile.dir = /tmp/sqoop-cloudera/compile/50add4e5c41c534f98d02e7dffa97e41
direct.import = false
hdfs.target.dir = /user/cloudera/sqoop-import/orders_inc_job
hive.fail.table.exists = false
db.batch = false
[cloudera@quickstart ~]$ 

# Execute job
[cloudera@quickstart ~]$ sqoop job --exec import_orders
Warning: /usr/lib/sqoop/../accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
23/09/30 14:09:15 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6-cdh5.7.0
23/09/30 14:09:17 INFO manager.MySQLManager: Preparing to use a MySQL streaming resultset.
23/09/30 14:09:17 INFO tool.CodeGenTool: Beginning code generation
23/09/30 14:09:18 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM `orders` AS t LIMIT 1
23/09/30 14:09:18 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM `orders` AS t LIMIT 1
23/09/30 14:09:18 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/lib/hadoop-mapreduce
Note: /tmp/sqoop-cloudera/compile/d9e5a5c711d756cab9f9936fbcee9c80/orders.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
23/09/30 14:09:19 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-cloudera/compile/d9e5a5c711d756cab9f9936fbcee9c80/orders.jar
23/09/30 14:09:19 INFO tool.ImportTool: Maximal id query for free form incremental import: SELECT MAX(`order_id`) FROM `orders`
23/09/30 14:09:19 INFO tool.ImportTool: Incremental import based on column `order_id`
23/09/30 14:09:19 INFO tool.ImportTool: Lower bound value: 0
23/09/30 14:09:19 INFO tool.ImportTool: Upper bound value: 68885
23/09/30 14:09:19 WARN manager.MySQLManager: It looks like you are importing from mysql.
23/09/30 14:09:19 WARN manager.MySQLManager: This transfer can be faster! Use the --direct
23/09/30 14:09:19 WARN manager.MySQLManager: option to exercise a MySQL-specific fast path.
23/09/30 14:09:19 INFO manager.MySQLManager: Setting zero DATETIME behavior to convertToNull (mysql)
23/09/30 14:09:19 INFO mapreduce.ImportJobBase: Beginning import of orders
23/09/30 14:09:19 INFO Configuration.deprecation: mapred.job.tracker is deprecated. Instead, use mapreduce.jobtracker.address
23/09/30 14:09:19 INFO Configuration.deprecation: mapred.jar is deprecated. Instead, use mapreduce.job.jar
23/09/30 14:09:19 INFO Configuration.deprecation: mapred.map.tasks is deprecated. Instead, use mapreduce.job.maps
23/09/30 14:09:19 INFO client.RMProxy: Connecting to ResourceManager at /0.0.0.0:8032
23/09/30 14:09:21 INFO db.DBInputFormat: Using read commited transaction isolation
23/09/30 14:09:21 INFO db.DataDrivenDBInputFormat: BoundingValsQuery: SELECT MIN(`order_id`), MAX(`order_id`) FROM `orders` WHERE ( `order_id` > 0 AND `order_id` <= 68885 )
23/09/30 14:09:21 INFO db.IntegerSplitter: Split size: 17221; Num splits: 4 from: 1 to: 68885
23/09/30 14:09:21 INFO mapreduce.JobSubmitter: number of splits:4
23/09/30 14:09:21 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1696074608505_0018
23/09/30 14:09:21 INFO impl.YarnClientImpl: Submitted application application_1696074608505_0018
23/09/30 14:09:21 INFO mapreduce.Job: The url to track the job: http://quickstart.cloudera:8088/proxy/application_1696074608505_0018/
23/09/30 14:09:21 INFO mapreduce.Job: Running job: job_1696074608505_0018
23/09/30 14:09:29 INFO mapreduce.Job: Job job_1696074608505_0018 running in uber mode : false
23/09/30 14:09:29 INFO mapreduce.Job:  map 0% reduce 0%
23/09/30 14:09:39 INFO mapreduce.Job:  map 50% reduce 0%
23/09/30 14:09:40 INFO mapreduce.Job:  map 75% reduce 0%
23/09/30 14:09:41 INFO mapreduce.Job:  map 100% reduce 0%
23/09/30 14:09:41 INFO mapreduce.Job: Job job_1696074608505_0018 completed successfully
23/09/30 14:09:41 INFO mapreduce.Job: Counters: 30
        File System Counters
                FILE: Number of bytes read=0
                FILE: Number of bytes written=559104
                FILE: Number of read operations=0
                FILE: Number of large read operations=0
                FILE: Number of write operations=0
                HDFS: Number of bytes read=469
                HDFS: Number of bytes written=3000022
                HDFS: Number of read operations=16
                HDFS: Number of large read operations=0
                HDFS: Number of write operations=8
        Job Counters 
                Launched map tasks=4
                Other local map tasks=4
                Total time spent by all maps in occupied slots (ms)=29457
                Total time spent by all reduces in occupied slots (ms)=0
                Total time spent by all map tasks (ms)=29457
                Total vcore-seconds taken by all map tasks=29457
                Total megabyte-seconds taken by all map tasks=30163968
        Map-Reduce Framework
                Map input records=68885
                Map output records=68885
                Input split bytes=469
                Spilled Records=0
                Failed Shuffles=0
                Merged Map outputs=0
                GC time elapsed (ms)=368
                CPU time spent (ms)=18080
                Physical memory (bytes) snapshot=986648576
                Virtual memory (bytes) snapshot=5566504960
                Total committed heap usage (bytes)=1266679808
        File Input Format Counters 
                Bytes Read=0
        File Output Format Counters 
                Bytes Written=3000022
23/09/30 14:09:41 INFO mapreduce.ImportJobBase: Transferred 2.861 MB in 21.6533 seconds (135.3007 KB/sec)
23/09/30 14:09:41 INFO mapreduce.ImportJobBase: Retrieved 68885 records.
23/09/30 14:09:41 INFO util.AppendUtils: Creating missing output directory - orders_inc_job
23/09/30 14:09:41 INFO tool.ImportTool: Saving incremental import state to the metastore
23/09/30 14:09:41 INFO tool.ImportTool: Updated data for job: import_orders
[cloudera@quickstart ~]$ 
```

---

## Sqoop Import to Hive

```bash

[cloudera@quickstart ~]$ sqoop job --create import_orders_hive_create -- import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.tx
t --table orders --target-dir /user/cloudera/sqoop-import/orders_hive --incremental append --check-column order_id --last-value 0 --hive-import --hive-table retail.orders --create-hive-table
```

---
Note this job creation command included the `--create hive table` flag. You'll need another job for subsequent runs without that flag so as to avoid the error that the table already exists.

```bash
[cloudera@quickstart ~]$ sqoop job --create import_orders_hive -- import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --tab
le orders --target-dir /user/cloudera/sqoop-import/orders_hive --incremental append --check-column order_id --last-value 68883 --hive-import --hive-table retail.orders
```

---

There is an option to truncate the table before running a job
```bash
[cloudera@quickstart ~]$ sqoop job --create import_orders_hive -- import --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table orders --target-dir /user/cloudera/sqoop-import/orders_hive --incremental append --check-column order_id --last-value 68883 --hive-import --hive-table retail.orders --hive-overwrite
```

## Import all tables in a database
```bash
[cloudera@quickstart ~]$ sqoop import-all-tables --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --warehouse-dir /user/clouder
a/sqoop-import-all-tables
```

Other Scenarios of sqoop import all tables
```bash
# Excluding tables without primary key
[cloudera@quickstart ~]$ sqoop import-all-tables --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --warehouse-dir /user/cloudera/sqoop-import-all-tables-01 --exclude-tables dept,emp

# Using autoreset to one mapper
[cloudera@quickstart ~]$ sqoop import-all-tables --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --warehouse-dir /user/cloudera/sqoop-import-all-tables-02 --autoreset-to-one-mapper
```

---

## Sqoop Export tables from Hive/HDFS into Relational Database

```bash
# Move data from HDFS back to relational database
[cloudera@quickstart ~]$ sqoop export --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table emp --export-dir /user/cloudera/employee

# Export Hive table
[cloudera@quickstart ~]$ sqoop export --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table ord --hcatalog-database retail --hcatalog-table orders

# Hive export using a staging table
[cloudera@quickstart ~]$ sqoop export --connect jdbc:mysql://quickstart.cloudera:3306/retail_db --username cloudera --password-file /user/cloudera/sqoop_pwd.txt --table ord --hcatalog-database retail --hcatalog-table orders --staging-table ord_staging --clear-staging-table
```

---

