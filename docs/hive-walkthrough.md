# Hive Walkthrough

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/Apache-Hive-Introduction.jpg)

## Switching to the Hive CLI from within the Cloudera Container

type `hive` on the cloudera user environment to connect to the hive CLI

```bash
[cloudera@quickstart ~]$ hive
2023-09-16 10:43:24,164 WARN  [main] mapreduce.TableMapReduceUtil: The hbase-prefix-tree module jar containing PrefixTreeCodec is not present.  Continuing without it.

Logging initialized using configuration in file:/etc/hive/conf.dist/hive-log4j.properties
WARNING: Hive CLI is deprecated and migration to Beeline is recommended.
hive> show databases;
OK
default
Time taken: 0.666 seconds, Fetched: 1 row(s)
hive> CREATE DATABASE IF NOT EXISTS demo;
OK
Time taken: 0.237 seconds
hive> show databases;
OK
default
demo
Time taken: 0.018 seconds, Fetched: 2 row(s)
hive> use demo;
OK
Time taken: 0.034 seconds
# Get the hive current db name to show in the prompt
hive> set hive.cli.print.current.db = true;
hive (demo)> 
```

Exit the hive cli back to master node, create new directory for input data

```bash
hive (demo)> exit;
WARN: The method class org.apache.commons.logging.impl.SLF4JLogFactory#release() was invoked.
WARN: Please see http://www.slf4j.org/codes.html#release for an explanation.
[cloudera@quickstart ~]$ mkdir input_files
[cloudera@quickstart ~]$ ls
cloudera-manager  cm_api.py  Desktop  Documents  enterprise-deployment.json  express-deployment.json  input_files  kerberos  lib  parcels  workspace
[cloudera@quickstart ~]$ 
```

Put files into Hadoop Master node (container environment)

```bash
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop$ cd data
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ docker ps
CONTAINER ID   IMAGE                         COMMAND                  CREATED          STATUS          PORTS                     NAMES
2c71a9458666   ovokpus/cloudera-quickstart   "/usr/bin/docker-qui…"   37 minutes ago   Up 37 minutes   0.0.0.0:65193->8888/tcp   ecstatic_chatterjee
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ docker cp transactions.txt ecstatic_chatterjee:home/cloudera/input_files
Successfully copied 1.19MB to ecstatic_chatterjee:home/cloudera/input_files
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ 
```

Put files into HDFS from Hadoop master node
```bash
[cloudera@quickstart ~]$ mkdir input_files
[cloudera@quickstart ~]$ ls
cloudera-manager  cm_api.py  Desktop  Documents  enterprise-deployment.json  express-deployment.json  input_files  kerberos  lib  parcels  workspace
[cloudera@quickstart ~]$ ls input_files/
transactions.txt
[cloudera@quickstart ~]$ hadoop fs -mkdir hive_input
[cloudera@quickstart ~]$ cd input_files
[cloudera@quickstart input_files]$ hadoop fs -put transactions.txt hive_input
[cloudera@quickstart input_files]$ 
```

View file browser in Hue (`localhost:8888`)

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/MapReduce-architecture.jpg)

view file contents
```bash
[cloudera@quickstart input_files]$ hadoop fs -cat hive_input/transactions.txt
12-13-2011,4002200,198.41,Outdoor Recreation,Rock Climbing,Westminster,Colorado,credit
05-24-2011,4002383,080.26,Racquet Sports,Badminton,Richmond  ,Virginia,credit
12-24-2011,4000712,103.22,Outdoor Play Equipment,Swing Sets,San Diego,California,credit
03-16-2011,4005784,144.15,Outdoor Recreation,Cycling,Kansas City,Missouri,credit
04-01-2011,4006884,098.16,Outdoor Recreation,Fishing,Houston,Texas,credit
07-29-2011,4008285,034.67,Team Sports,Indoor Volleyball,Nashville  ,Tennessee,cash
01-18-2011,4003225,198.69,Indoor Games,Darts,New York,New York,credit
04-13-2011,4006004,076.02,Team Sports,Soccer,Hampton  ,Virginia,credit
11-16-2011,4006222,053.25,Team Sports,Softball,Saint Paul,Minnesota,credit
01-25-2011,4005513,099.03,Puzzles,Jigsaw Puzzles,Detroit,Michigan,credit
05-12-2011,4008154,092.33,Team Sports,Lacrosse,Newark,New Jersey,credit
10-27-2011,4000332,147.34,Outdoor Recreation,Deck Shuffleboard,Oakland,California,credit
```

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/data-imported-to-hdfs.png)

---

# Create Hive Table

And then get table and schema

```bash
hive (demo)> CREATE TABLE transactions(txn_date string, cust_id bigint, txn_amt double, category string, product string, city string, state string, txn_type string)
           > row format delimited fields terminated by ',';
OK
Time taken: 0.316 seconds
hive (demo)> 
hive (demo)> show tables;
OK
transactions
Time taken: 0.139 seconds, Fetched: 1 row(s)
hive (demo)> describe transactions;
OK
txn_date                string                                      
cust_id                 bigint                                      
txn_amt                 double                                      
category                string                                      
product                 string                                      
city                    string                                      
state                   string                                      
txn_type                string                                      
Time taken: 0.161 seconds, Fetched: 8 row(s)
hive (demo)> desc formatted transactions;
OK
# col_name              data_type               comment             
                 
txn_date                string                                      
cust_id                 bigint                                      
txn_amt                 double                                      
category                string                                      
product                 string                                      
city                    string                                      
state                   string                                      
txn_type                string                                      
                 
# Detailed Table Information             
Database:               demo                     
Owner:                  cloudera                 
CreateTime:             Sat Sep 16 11:27:23 UTC 2023     
LastAccessTime:         UNKNOWN                  
Protect Mode:           None                     
Retention:              0                        
Location:               hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions         
Table Type:             MANAGED_TABLE            
Table Parameters:                
        transient_lastDdlTime   1694863643          
                 
# Storage Information            
SerDe Library:          org.apache.hadoop.hive.serde2.lazy.LazySimpleSerDe       
InputFormat:            org.apache.hadoop.mapred.TextInputFormat         
OutputFormat:           org.apache.hadoop.hive.ql.io.HiveIgnoreKeyTextOutputFormat       
Compressed:             No                       
Num Buckets:            -1                       
Bucket Columns:         []                       
Sort Columns:           []                       
Storage Desc Params:             
        field.delim             ,                   
        serialization.format    ,                   
Time taken: 0.137 seconds, Fetched: 34 row(s)
hive (demo)> 
```


Hive Job Execution - converted into a MapReduce job
```bash
hive (demo)> create table transactions1 row format delimited fields terminated by ',' as select * from transactions;
Query ID = cloudera_20230916111919_2eac86dd-53e9-4c86-ab14-b3bf1450fabe
Total jobs = 3
Launching Job 1 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1694859357759_0001, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1694859357759_0001/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1694859357759_0001
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0
2023-09-16 11:53:22,492 Stage-1 map = 0%,  reduce = 0%
2023-09-16 11:53:27,824 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 1.33 sec
MapReduce Total cumulative CPU time: 1 seconds 330 msec
Ended Job = job_1694859357759_0001
Stage-4 is selected by condition resolver.
Stage-3 is filtered out by condition resolver.
Stage-5 is filtered out by condition resolver.
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/.hive-staging_hive_2023-09-16_11-53-14_461_6143004972243869824-1/-ext-10001
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions1
Table demo.transactions1 stats: [numFiles=1, numRows=0, totalSize=0, rawDataSize=0]
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1   Cumulative CPU: 1.33 sec   HDFS Read: 3497 HDFS Write: 44 SUCCESS
Total MapReduce CPU Time Spent: 1 seconds 330 msec
OK
Time taken: 14.789 seconds'
hive (demo)> 
```

## View Hive query editor and query results in Hue

Hive Query Editor
![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/hive-query-editor.png)

---

Job Tracker
![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/job-browser.png)

---

You can also view a chart showing column distributions from the query results.
![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/hive-query-results-chart.png)

---

### Copy more files

Into master node
```bash
Copy files/folders between a container and the local filesystem
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ docker cp customers_01.csv ecstatic_chatterjee:/home/cloudera/input_files
Successfully copied 21kB to ecstatic_chatterjee:/home/cloudera/input_files
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ docker cp customers_02.csv ecstatic_chatterjee:/home/cloudera/input_files
Successfully copied 364kB to ecstatic_chatterjee:/home/cloudera/input_files
ubuntu@DESKTOP-QRVR3E3:~/Data-Engineering-with-Hadoop/data$ 
```

Into HDFS
```bash
[cloudera@quickstart ~]$ pwd
/home/cloudera
[cloudera@quickstart ~]$ ls
cloudera-manager  cm_api.py  Desktop  Documents  enterprise-deployment.json  express-deployment.json  input_files  kerberos  lib  parcels  workspace
[cloudera@quickstart ~]$ cd input_files
[cloudera@quickstart input_files]$ ls
customers_01.csv  customers_02.csv  transactions.txt
[cloudera@quickstart input_files]$ hadoop fs -put customers_01.csv customer_data
[cloudera@quickstart input_files]$
```

Create an External table in Hive
```bash
hive (demo)> create external table customers(cust_id bigint, fname string, lname string, age int, profession string)
           > row format delimited fields terminated by ','
           > location '/user/cloudera/customer_data';
OK
Time taken: 0.06 seconds
hive (demo)> 

hive (demo)> select * from customers limit 5;
OK
4000001 Kristina        Chung   55      Pilot
4000002 Paige   Chen    74      Teacher
4000003 Sherri  Melton  34      Firefighter
4000004 Gretchen        Hill    66      Computer hardware engineer
4000005 Karen   Puckett 74      Lawyer
Time taken: 0.071 seconds, Fetched: 5 row(s)
hive (demo)> select count(*) from customers;
Query ID = cloudera_20230916111919_2eac86dd-53e9-4c86-ab14-b3bf1450fabe
Total jobs = 1
Launching Job 1 out of 1
Number of reduce tasks determined at compile time: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1694859357759_0004, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1694859357759_0004/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1694859357759_0004
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
2023-09-16 12:33:50,644 Stage-1 map = 0%,  reduce = 0%
2023-09-16 12:33:55,869 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 1.83 sec
2023-09-16 12:34:02,184 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 3.45 sec
MapReduce Total cumulative CPU time: 3 seconds 450 msec
Ended Job = job_1694859357759_0004
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1  Reduce: 1   Cumulative CPU: 3.45 sec   HDFS Read: 25755 HDFS Write: 4 SUCCESS
Total MapReduce CPU Time Spent: 3 seconds 450 msec
OK
500
Time taken: 18.454 seconds, Fetched: 1 row(s)
hive (demo)> 
```

Internal tables are managed by Hive. The underlying data is in the Hive Warehouse location of HDFS. For external tables, the data resides in an external location.

If you drop an internal table, the data is deleted, if you drop an external table, the data files remain in the external location. External tables are more secure in the event of data loss.

---
## Hive Partitioning and Bucketing

Create Partitioned table in Hive
```bash
hive (demo)> create table transactions_part(tan_date string, cust_id bigint, txn_amt double, product string, city string, state string)
           > partitioned by (category string)
           > row format delimited fields terminated by ',';
OK
Time taken: 0.08 seconds
hive (demo)> 
```

Load data into partitioned table

```bash
hive (demo)> load data local inpath '/home/cloudera/input_files/gym_transactions.txt' into table transactions_part
           > partition(category='Gymnastics');
Loading data to table demo.transactions_part partition (category=Gymnastics)
Partition demo.transactions_part{category=Gymnastics} stats: [numFiles=1, numRows=0, totalSize=614, rawDataSize=0]
OK
Time taken: 0.43 seconds
hive (demo)> 

hive (demo)> load data local inpath '/home/cloudera/input_files/puz_transactions.txt' into table transactions_part
           > partition(category='Puzzles');
Loading data to table demo.transactions_part partition (category=Puzzles)
Partition demo.transactions_part{category=Puzzles} stats: [numFiles=1, numRows=0, totalSize=614, rawDataSize=0]
OK
Time taken: 0.372 seconds
hive (demo)> 
hive (demo)> show partitions transactions_part;
OK
category=Gymnastics
Time taken: 0.084 seconds, Fetched: 1 row(s)
hive (demo)> 
```

### Dynamic Partitioning

```bash
hive> set hive.cli.print.current.db = true;
hive (demo)> create table transactions_dynamic_part (txn_date string, cust_id bigint, txn_amt double, product string, city string, state string)
           > partitioned by (category string)
           > row format delimited fields terminated by ',';
OK
Time taken: 0.23 seconds
hive (demo)> 
hive (demo)> set hive.exec.dynamic.partition = true;
hive (demo)> set hive.exec.dynamic.partition.mode = nonstrict;
hive (demo)> show tables;
OK
customers
transactions
transactions1
transactions_dynamic_part
transactions_part
Time taken: 0.126 seconds, Fetched: 5 row(s)
hive (demo)> insert into table transactions_dynamic_part partition(category) select txn_date, cust_id, txn_amt, product, city, state, category from transactions;
Query ID = cloudera_20230916135353_7590669d-15f0-4dd1-8982-05c04e42f259
Total jobs = 3
Launching Job 1 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1694859357759_0006, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1694859357759_0006/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1694859357759_0006
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0
2023-09-16 14:00:12,439 Stage-1 map = 0%,  reduce = 0%
2023-09-16 14:00:18,721 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.22 sec
MapReduce Total cumulative CPU time: 3 seconds 220 msec
Ended Job = job_1694859357759_0006
Stage-4 is selected by condition resolver.
Stage-3 is filtered out by condition resolver.
Stage-5 is filtered out by condition resolver.
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions_dynamic_part/.hive-staging_hive_2023-09-16_14-00-05_259_7886760956649795277-1/-ext-10000
Loading data to table demo.transactions_dynamic_part partition (category=null)
         Time taken for load dynamic partitions : 1336
        Loading partition {category=Dancing}
        Loading partition {category=Jumping}
        Loading partition {category=Winter Sports}
        Loading partition {category=Team Sports}
         Time taken for adding to write entity : 9
Partition demo.transactions_dynamic_part{category=Dancing} stats: [numFiles=1, numRows=116, totalSize=6525, rawDataSize=6409]
Partition demo.transactions_dynamic_part{category=Jumping} stats: [numFiles=1, numRows=626, totalSize=37268, rawDataSize=36642]
Partition demo.transactions_dynamic_part{category=Team Sports} stats: [numFiles=1, numRows=1815, totalSize=99673, rawDataSize=97858]
Partition demo.transactions_dynamic_part{category=Winter Sports} stats: [numFiles=1, numRows=983, totalSize=56055, rawDataSize=55072]
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1   Cumulative CPU: 3.22 sec   HDFS Read: 1194641 HDFS Write: 863781 SUCCESS
Total MapReduce CPU Time Spent: 3 seconds 220 msec
OK'
Time taken: 17.514 seconds
```

Show partitions
```bash
hive (demo)> 
hive (demo)> show partitions transactions_dynamic_part;
OK
category=Air Sports
category=Combat Sports
category=Dancing
category=Exercise & Fitness
category=Games
category=Gymnastics
category=Indoor Games
category=Jumping
category=Outdoor Play Equipment
category=Outdoor Recreation
category=Puzzles
category=Racquet Sports
category=Team Sports
category=Water Sports
category=Winter Sports
Time taken: 0.088 seconds, Fetched: 15 row(s)
hive (demo)> 
```

Check cardinality of columns in data
```bash
[cloudera@quickstart input_files]$ ls
customers_01.csv  customers_02.csv  gym_transactions.txt  puz_transactions.txt  transactions.txt
[cloudera@quickstart input_files]$ head transactions.txt | cut -d ',' -f4 | sort | uniq | wc -l
8
[cloudera@quickstart input_files]$ head transactions.txt | cut -d ',' -f4 | sort | uniq
Exercise & Fitness
Gymnastics
Jumping
Outdoor Play Equipment
Outdoor Recreation
Puzzles
Team Sports
Winter Sports
[cloudera@quickstart input_files]$
```

### Create Bucketed Table
```bash
hive (demo)>  create table transactions_bucket(txn_date string, cust_id bigint, txn_amt double, category string, product string, city string, state string)
           > clustered by (product) into 4 buckets
           > row format delimited fields terminated by ',';
OK
Time taken: 1.017 seconds
hive (demo)> 
```

```sh
hive (demo)> set hive.exec.enforce.bucketing=true;
hive (demo)> insert into table transactions_bucket select txn_date, cust_id, txn_amt, category, product, city, state from transactions;
Query ID = cloudera_20230923124848_8eb3b1f7-c2c0-4e37-bb94-6275a1043196
Total jobs = 3
Launching Job 1 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1695473258878_0001, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1695473258878_0001/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1695473258878_0001
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0
2023-09-23 12:52:05,019 Stage-1 map = 0%,  reduce = 0%
2023-09-23 12:52:11,294 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.04 sec
MapReduce Total cumulative CPU time: 3 seconds 40 msec
Ended Job = job_1695473258878_0001
Stage-4 is selected by condition resolver.
Stage-3 is filtered out by condition resolver.
Stage-5 is filtered out by condition resolver.
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions_bucket/.hive-staging_hive_2023-09-23_12-51-57_179_4492242623984473989-1/-ext-10000
Loading data to table demo.transactions_bucket
Table demo.transactions_bucket stats: [numFiles=1, numRows=15000, totalSize=1080081, rawDataSize=1065081]
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1   Cumulative CPU: 3.04 sec   HDFS Read: 1194330 HDFS Write: 1080169 SUCCESS
Total MapReduce CPU Time Spent: 3 seconds 40 msec
OK
Time taken: 16.475 seconds'
hive (demo)> 

hive (demo)> create table transactions_pb(txn_date string, cust_id bigint, txn_amt double, product string, city string, state string)
           > partitioned by (category string)
           > clustered by (product) into 8 buckets
           > row format delimited fields terminated by ',';
OK
Time taken: 0.178 seconds
hive (demo)> 

hive (demo)> set hive.exec.dynamic.partition=true;
hive (demo)> set hive.exec.dynamic.partition.mode=nonstrict;
hive (demo)> insert into table transactions_pb partition(category) select txn_date, cust_id, txn_amt, product, city, state, category from transactions;
Query ID = cloudera_20230923124848_8eb3b1f7-c2c0-4e37-bb94-6275a1043196
Total jobs = 3
Launching Job 1 out of 3
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1695473258878_0002, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1695473258878_0002/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1695473258878_0002
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0
2023-09-23 13:19:27,704 Stage-1 map = 0%,  reduce = 0%
2023-09-23 13:19:32,957 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.01 sec
MapReduce Total cumulative CPU time: 3 seconds 10 msec
Ended Job = job_1695473258878_0002
Stage-4 is selected by condition resolver.
Stage-3 is filtered out by condition resolver.
Stage-5 is filtered out by condition resolver.
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions_pb/.hive-staging_hive_2023-09-23_13-19-22_018_3026099819814681608-1/-ext-10000
Loading data to table demo.transactions_pb partition (category=null)
         Time taken for load dynamic partitions : 3190
        Loading partition {category=Racquet Sports}
        Loading partition {category=Water Sports}
        Loading partition {category=Jumping}
        Loading partition {category=Winter Sports}
         Time taken for adding to write entity : 4
Partition demo.transactions_pb{category=Jumping} stats: [numFiles=1, numRows=626, totalSize=37268, rawDataSize=36642]
Partition demo.transactions_pb{category=Racquet Sports} stats: [numFiles=1, numRows=494, totalSize=26375, rawDataSize=25881]
Partition demo.transactions_pb{category=Water Sports} stats: [numFiles=1, numRows=1596, totalSize=92459, rawDataSize=90863]
Partition demo.transactions_pb{category=Winter Sports} stats: [numFiles=1, numRows=983, totalSize=56055, rawDataSize=55072]
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1   Cumulative CPU: 3.01 sec   HDFS Read: 1194613 HDFS Write: 863631 SUCCESS
Total MapReduce CPU Time Spent: 3 seconds 10 msec
OK
Time taken: 17.107 seconds'
hive (demo)> 
```

Save query output to HDFS location
```bash
hive (demo)> insert overwrite directory '/user/cloudera/cat_sales' row format delimited fields terminated by '|' lines terminated by '\n' select category, round(sum(txn_amt), 2) as total_amount from transactions group by category;
Query ID = cloudera_20230923124848_8eb3b1f7-c2c0-4e37-bb94-6275a1043196
Total jobs = 1
Launching Job 1 out of 1
Number of reduce tasks not specified. Estimated from input data size: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1695473258878_0006, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1695473258878_0006/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1695473258878_0006
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
2023-09-23 13:48:08,965 Stage-1 map = 0%,  reduce = 0%
2023-09-23 13:48:15,323 Stage-1 map = 100%,  reduce = 0%
2023-09-23 13:48:22,652 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 6.64 sec
MapReduce Total cumulative CPU time: 6 seconds 640 msec
Ended Job = job_1695473258878_0006
Moving data to: /user/cloudera/cat_sales
```


## Hive SerDe

```bash
hive (demo)> create table statement(txn_id int, acc_num bigint, txn_amt double, txn_remarks string, txn_type string)
           > row format delimited fields terminated by ',';
OK
Time taken: 0.065 seconds
hive (demo)> 
hive (demo)> load data local inpath '/home/cloudera/input_files/statement.csv' into table statement;
Loading data to table demo.statement
Table demo.statement stats: [numFiles=1, totalSize=380]
OK
Time taken: 0.337 seconds
hive (demo)> 
hive (demo)> add jar /home/cloudera/opencsv-5.5.2.jar;
Added [/home/cloudera/opencsv-5.5.2.jar] to class path
Added resources: [/home/cloudera/opencsv-5.5.2.jar]
hive (demo)> 
hive (demo)> create table statement_serialized(txn_it int, acc_num bigint, txn_amt double, txn_remarks string, txn_type string)
           > row format serde 'org.apache.hadoop.hive.serde2.OpenCSVSerde';
OK
Time taken: 0.069 seconds
hive (demo)> 
```

Create table with ORC files underneath
```bash
hive (demo)> create table transactions_orc(txn_date string, cust_id bigint, txn_amt double, category string, product string, city string, state string, txn_type string)
           > row format delimited fields terminated by ','
           > stored as ORC;
OK
Time taken: 0.093 seconds
hive (demo)> 
hive (demo)> insert into table transactions_orc select * from transactions;
Query ID = cloudera_20230923124848_8eb3b1f7-c2c0-4e37-bb94-6275a1043196
Total jobs = 1
Launching Job 1 out of 1
Number of reduce tasks is set to 0 since there's no reduce operator
Starting Job = job_1695473258878_0007, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1695473258878_0007/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1695473258878_0007
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 0
2023-09-23 14:46:33,862 Stage-1 map = 0%,  reduce = 0%
2023-09-23 14:46:40,095 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.0 sec
MapReduce Total cumulative CPU time: 3 seconds 0 msec
Ended Job = job_1695473258878_0007
Stage-4 is selected by condition resolver.
Stage-3 is filtered out by condition resolver.
Stage-5 is filtered out by condition resolver.
Moving data to: hdfs://quickstart.cloudera:8020/user/hive/warehouse/demo.db/transactions_orc/.hive-staging_hive_2023-09-23_14-46-28_120_1540461960967366718-1/-ext-10000
Loading data to table demo.transactions_orc
Table demo.transactions_orc stats: [numFiles=1, numRows=15000, totalSize=154994, rawDataSize=8640000]
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1   Cumulative CPU: 3.0 sec   HDFS Read: 1194576 HDFS Write: 155079 SUCCESS
Total MapReduce CPU Time Spent: 3 seconds 0 msec
OK
Time taken: 13.405 seconds'
hive (demo)> 

```

Select from table - MR job
```bash
hive (demo)> select category, sum(txn_amt) as total_sales from transactions group by category;
Query ID = cloudera_20230923124848_8eb3b1f7-c2c0-4e37-bb94-6275a1043196
Total jobs = 1
Launching Job 1 out of 1
Number of reduce tasks not specified. Estimated from input data size: 1
In order to change the average load for a reducer (in bytes):
  set hive.exec.reducers.bytes.per.reducer=<number>
In order to limit the maximum number of reducers:
  set hive.exec.reducers.max=<number>
In order to set a constant number of reducers:
  set mapreduce.job.reduces=<number>
Starting Job = job_1695473258878_0008, Tracking URL = http://quickstart.cloudera:8088/proxy/application_1695473258878_0008/
Kill Command = /usr/lib/hadoop/bin/hadoop job  -kill job_1695473258878_0008
Hadoop job information for Stage-1: number of mappers: 1; number of reducers: 1
2023-09-23 14:50:03,960 Stage-1 map = 0%,  reduce = 0%
2023-09-23 14:50:10,169 Stage-1 map = 100%,  reduce = 0%, Cumulative CPU 3.03 sec
2023-09-23 14:50:16,443 Stage-1 map = 100%,  reduce = 100%, Cumulative CPU 4.59 sec
MapReduce Total cumulative CPU time: 4 seconds 590 msec
Ended Job = job_1695473258878_0008
MapReduce Jobs Launched: 
Stage-Stage-1: Map: 1  Reduce: 1   Cumulative CPU: 4.59 sec   HDFS Read: 1197948 HDFS Write: 443 SUCCESS
Total MapReduce CPU Time Spent: 4 seconds 590 msec
OK
Air Sports      29937.309999999998
Combat Sports   52691.89999999998
Dancing 12070.930000000002
Exercise & Fitness      227900.1199999999
Games   112310.47000000004
Gymnastics      97138.28
Indoor Games    81881.21
Jumping 62393.24000000001
Outdoor Play Equipment  84539.59000000003
Outdoor Recreation      247754.61000000083
Puzzles 19104.16
Racquet Sports  52517.34999999996
Team Sports     185772.86000000002
Water Sports    162132.06000000026
Winter Sports   99570.96999999997
Time taken: 19.279 seconds, Fetched: 15 row(s)
hive (demo)> 
```


