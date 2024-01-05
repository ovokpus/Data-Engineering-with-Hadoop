# Hive Notes


## Hive console from Ambari

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/hive2console.png)

---


```sql
CREATE VIEW IF NOT EXISTS topMovieIDs AS
SELECT movieID, count(movieID) as ratingCount
FROM ratings
GROUP BY movieID
ORDER BY ratingCount DESC;

SELECT n.title, ratingCount
FROM topMovieIDs t JOIN names n on t.movieID = n.movieID;
```

```sql
DROP VIEW topmovieIDs;
```

### Find the movie with the highest average rating

```sql
CREATE VIEW IF NOT EXISTS averageRatings AS
SELECT movieID, count(movieID) as ratingCount, AVG(rating) as averageRating
FROM ratings
GROUP BY movieID
ORDER BY averageRating DESC;

SELECT n.title as title, averageRating
FROM averageRatings t JOIN names n ON t.movieID = n.movieID
WHERE ratingCount > 10
LIMIT 1;
```

### SET metastore (mysql) access and privileges

```bash
# switch to root
su root

systemctl stop mysqld
systemctl set-environment MYSQLD_OPTS="-–skip-grant-tables –-skip-networking"
systemctl start mysqld
mysql -u root

# Mysql cmd
mysql> FLUSH PRIVILEGES;
mysql> alter user ‘root’@’localhost’ IDENTIFIED BY ‘hadoop’;
mysql> FLUSH PRIVILEGES;
mysql> QUIT;

# CMD
systemctl unset-environment MYSQLD_OPTS
systemctl restart mysqld

# return to user
exit

# Download SQL script
wget http://media.sundog-soft.com/hadoop/movielens.sql

mysql -u root -p

mysql> SET NAMES 'utf8';

mysql> SET CHARACTER utf8;

mysql> USE movielens;

mysql> source movielens.sql;

mysql> show tables;
+---------------------+
| Tables_in_movielens |
+---------------------+
| genres              |
| genres_movies       |
| movies              |
| occupations         |
| ratings             |
| users               |
+---------------------+
6 rows in set (0.00 sec)

mysql> select * from movies limit 10;
+----+------------------------------------------------------+--------------+
| id | title                                                | release_date |
+----+------------------------------------------------------+--------------+
|  1 | Toy Story (1995)                                     | 1995-01-01   |
|  2 | GoldenEye (1995)                                     | 1995-01-01   |
|  3 | Four Rooms (1995)                                    | 1995-01-01   |
|  4 | Get Shorty (1995)                                    | 1995-01-01   |
|  5 | Copycat (1995)                                       | 1995-01-01   |
|  6 | Shanghai Triad (Yao a yao yao dao waipo qiao) (1995) | 1995-01-01   |
|  7 | Twelve Monkeys (1995)                                | 1995-01-01   |
|  8 | Babe (1995)                                          | 1995-01-01   |
|  9 | Dead Man Walking (1995)                              | 1995-01-01   |
| 10 | Richard III (1995)                                   | 1996-01-22   |
+----+------------------------------------------------------+--------------+
10 rows in set (0.00 sec)

mysql> describe ratings;
+----------+-----------+------+-----+-------------------+-----------------------------+
| Field    | Type      | Null | Key | Default           | Extra                       |
+----------+-----------+------+-----+-------------------+-----------------------------+
| id       | int(11)   | NO   | PRI | NULL              |                             |
| user_id  | int(11)   | YES  |     | NULL              |                             |
| movie_id | int(11)   | YES  |     | NULL              |                             |
| rating   | int(11)   | YES  |     | NULL              |                             |
| rated_at | timestamp | NO   |     | CURRENT_TIMESTAMP | on update CURRENT_TIMESTAMP |
+----------+-----------+------+-----+-------------------+-----------------------------+
5 rows in set (0.08 sec)

mysql> SELECT movies.title, COUNT(ratings.movie_id) AS ratingCount
    -> FROM movies
    -> INNER JOIN ratings
    -> ON movies.id = ratings.movie_id
    -> GROUP BY movies.title
    -> ORDER BY ratingCount;
+-----------------------------------------------------------------------------------+-------------+
| title                                                                             | ratingCount |
+-----------------------------------------------------------------------------------+-------------+
| Symphonie pastorale, La (1946)                                                    |           1 |
| Careful (1992)                                                                    |           1 |
| Tainted (1998)                                                                    |           1 |
| Silence of the Palace, The (Saimt el Qusur) (1994)                                |           1 |
| Hedd Wyn (1992)                                                                   |           1 |
| Dadetown (1995)                                                                   |           1 |
| Power 98 (1995)                                                                   |           1 |
| Walk in the Sun, A (1945)                                                         |           1 |
| Contact (1997)                                                                    |         509 |
| Star Wars (1977)                                                                  |         583 |
+-----------------------------------------------------------------------------------+-------------+
1664 rows in set (0.49 sec)

mysql> GRANT ALL PRIVILEGES ON movielens.* to root@localhost IDENTIFIED by 'hadoop';
Query OK, 0 rows affected, 1 warning (0.07 sec)

mysql> exit;

```

# Sqoop import into HDFS

```bash
[root@sandbox-hdp maria_dev] sqoop import --connect jdbc:mysql://localhost/movielens --driver com.mysql.jdbc.Driver --table movies -m 1 --username root --password hadoop
Warning: /usr/hdp/2.6.5.0-292/accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
24/01/04 22:59:25 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6.2.6.5.0-292
24/01/04 22:59:25 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
24/01/04 22:59:25 WARN sqoop.ConnFactory: Parameter --driver is set to an explicit driver however appropriate connection manager is not being set (via --connection-manager). Sqoop is going to fall back to org.apache.sqoop.manager.GenericJdbcManager. Please specify explicitly which connection manager should be used next time.
24/01/04 22:59:25 INFO manager.SqlManager: Using default fetchSize of 1000
24/01/04 22:59:25 INFO tool.CodeGenTool: Beginning code generation
Thu Jan 04 22:59:26 UTC 2024 WARN: Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.
24/01/04 22:59:28 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 22:59:28 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 22:59:28 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/hdp/2.6.5.0-292/hadoop-mapreduce
Note: /tmp/sqoop-root/compile/767ff7ef75bc59abe2853bd292bb4c02/movies.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
24/01/04 23:00:58 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-root/compile/767ff7ef75bc59abe2853bd292bb4c02/movies.jar
24/01/04 23:00:59 INFO mapreduce.ImportJobBase: Beginning import of movies
24/01/04 23:01:02 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 23:01:16 INFO client.RMProxy: Connecting to ResourceManager at sandbox-hdp.hortonworks.com/172.18.0.2:8032
24/01/04 23:01:17 INFO client.AHSProxy: Connecting to Application History server at sandbox-hdp.hortonworks.com/172.18.0.2:10200
Thu Jan 04 23:01:36 UTC 2024 WARN: Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.
24/01/04 23:01:37 INFO db.DBInputFormat: Using read commited transaction isolation
24/01/04 23:01:38 INFO mapreduce.JobSubmitter: number of splits:1
24/01/04 23:01:39 INFO mapreduce.JobSubmitter: Submitting tokens for job: job_1704406060968_0001
24/01/04 23:01:50 INFO impl.YarnClientImpl: Submitted application application_1704406060968_0001
24/01/04 23:01:50 INFO mapreduce.Job: The url to track the job: http://sandbox-hdp.hortonworks.com:8088/proxy/application_1704406060968_0001/
24/01/04 23:01:50 INFO mapreduce.Job: Running job: job_1704406060968_0001
24/01/04 23:04:02 INFO mapreduce.Job: Job job_1704406060968_0001 running in uber mode : false
24/01/04 23:04:02 INFO mapreduce.Job:  map 0% reduce 0%
24/01/04 23:04:22 INFO mapreduce.Job:  map 100% reduce 0%
24/01/04 23:04:23 INFO mapreduce.Job: Job job_1704406060968_0001 completed successfully
24/01/04 23:04:24 INFO mapreduce.Job: Counters: 30
        File System Counters
                FILE: Number of bytes read=0
                FILE: Number of bytes written=171055
                FILE: Number of read operations=0
                FILE: Number of large read operations=0
                FILE: Number of write operations=0
                HDFS: Number of bytes read=87
                HDFS: Number of bytes written=66940
                HDFS: Number of read operations=4
                HDFS: Number of large read operations=0
                HDFS: Number of write operations=2
        Job Counters
                Launched map tasks=1
                Other local map tasks=1
                Total time spent by all maps in occupied slots (ms)=10603
                Total time spent by all reduces in occupied slots (ms)=0
                Total time spent by all map tasks (ms)=10603
                Total vcore-milliseconds taken by all map tasks=10603
                Total megabyte-milliseconds taken by all map tasks=2650750
        Map-Reduce Framework
                Map input records=1682
                Map output records=1682
                Input split bytes=87
                Spilled Records=0
                Failed Shuffles=0
                Merged Map outputs=0
                GC time elapsed (ms)=570
                CPU time spent (ms)=4950
                Physical memory (bytes) snapshot=178757632
                Virtual memory (bytes) snapshot=1948532736
                Total committed heap usage (bytes)=43515904
        File Input Format Counters
                Bytes Read=0
        File Output Format Counters
                Bytes Written=66940
24/01/04 23:04:24 INFO mapreduce.ImportJobBase: Transferred 65.3711 KB in 189.3594 seconds (353.5076 bytes/sec)
24/01/04 23:04:24 INFO mapreduce.ImportJobBase: Retrieved 1682 records.
[root@sandbox-hdp maria_dev]#


```

### Hive import command

```bash
[maria_dev@sandbox-hdp ~]$ sqoop import --connect jdbc:mysql://localhost/movielens --driver com.mysql.jdbc.Driver --table movies -m 1 --username root --password hadoop --hive-import
Warning: /usr/hdp/2.6.5.0-292/accumulo does not exist! Accumulo imports will fail.
Please set $ACCUMULO_HOME to the root of your Accumulo installation.
24/01/04 23:24:50 INFO sqoop.Sqoop: Running Sqoop version: 1.4.6.2.6.5.0-292
24/01/04 23:24:50 WARN tool.BaseSqoopTool: Setting your password on the command-line is insecure. Consider using -P instead.
24/01/04 23:24:50 INFO tool.BaseSqoopTool: Using Hive-specific delimiters for output. You can override
24/01/04 23:24:50 INFO tool.BaseSqoopTool: delimiters with --fields-terminated-by, etc.
24/01/04 23:24:51 WARN sqoop.ConnFactory: Parameter --driver is set to an explicit driver however appropriate connection manager is not being set (via --connection-manager). Sqoop is going to fall back to org.apache.sqoop.manager.GenericJdbcManager. Please specify explicitly which connection manager should be used next time.
24/01/04 23:24:51 INFO manager.SqlManager: Using default fetchSize of 1000
24/01/04 23:24:51 INFO tool.CodeGenTool: Beginning code generation
Thu Jan 04 23:24:51 UTC 2024 WARN: Establishing SSL connection without server's identity verification is not recommended. According to MySQL 5.5.45+, 5.6.26+ and 5.7.6+ requirements SSL connection must be established by default if explicit option isn't set. For compliance with existing applications not using SSL the verifyServerCertificate property is set to 'false'. You need either to explicitly disable SSL by setting useSSL=false, or set useSSL=true and provide truststore for server certificate verification.
24/01/04 23:24:52 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 23:24:52 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 23:24:53 INFO orm.CompilationManager: HADOOP_MAPRED_HOME is /usr/hdp/2.6.5.0-292/hadoop-mapreduce
Note: /tmp/sqoop-maria_dev/compile/7278ca54512db9effc02d6b154570bed/movies.java uses or overrides a deprecated API.
Note: Recompile with -Xlint:deprecation for details.
24/01/04 23:24:57 INFO orm.CompilationManager: Writing jar file: /tmp/sqoop-maria_dev/compile/7278ca54512db9effc02d6b154570bed/movies.jar
24/01/04 23:24:58 INFO mapreduce.ImportJobBase: Beginning import of movies
24/01/04 23:24:58 INFO manager.SqlManager: Executing SQL statement: SELECT t.* FROM movies AS t WHERE 1=0
24/01/04 23:25:00 INFO client.RMProxy: Connecting to ResourceManager at sandbox-hdp.hortonworks.com/172.18.0.2:8032
24/01/04 23:25:00 INFO client.AHSProxy: Connecting to Application History server at sandbox-hdp.hortonworks.com/172.18.0.2:10200
24/01/04 23:25:01 ERROR tool.ImportTool: Encountered IOException running import job: org.apache.hadoop.mapred.FileAlreadyExistsException: Output directory hdfs://sandbox-hdp.hortonworks.com:8020/user/maria_dev/movies already exists
        at org.apache.hadoop.mapreduce.lib.output.FileOutputFormat.checkOutputSpecs(FileOutputFormat.java:146)
        at org.apache.hadoop.mapreduce.JobSubmitter.checkSpecs(JobSubmitter.java:266)
        at org.apache.hadoop.mapreduce.JobSubmitter.submitJobInternal(JobSubmitter.java:139)
        at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1290)
        at org.apache.hadoop.mapreduce.Job$10.run(Job.java:1287)
        at java.security.AccessController.doPrivileged(Native Method)
        at javax.security.auth.Subject.doAs(Subject.java:422)
        at org.apache.hadoop.security.UserGroupInformation.doAs(UserGroupInformation.java:1869)
        at org.apache.hadoop.mapreduce.Job.submit(Job.java:1287)
        at org.apache.hadoop.mapreduce.Job.waitForCompletion(Job.java:1308)
        at org.apache.sqoop.mapreduce.ImportJobBase.doSubmitJob(ImportJobBase.java:200)
        at org.apache.sqoop.mapreduce.ImportJobBase.runJob(ImportJobBase.java:173)
        at org.apache.sqoop.mapreduce.ImportJobBase.runImport(ImportJobBase.java:270)
        at org.apache.sqoop.manager.SqlManager.importTable(SqlManager.java:692)
        at org.apache.sqoop.tool.ImportTool.importTable(ImportTool.java:507)
        at org.apache.sqoop.tool.ImportTool.run(ImportTool.java:615)
        at org.apache.sqoop.Sqoop.run(Sqoop.java:147)
        at org.apache.hadoop.util.ToolRunner.run(ToolRunner.java:76)
        at org.apache.sqoop.Sqoop.runSqoop(Sqoop.java:183)
        at org.apache.sqoop.Sqoop.runTool(Sqoop.java:225)
        at org.apache.sqoop.Sqoop.runTool(Sqoop.java:234)
        at org.apache.sqoop.Sqoop.main(Sqoop.java:243)

[maria_dev@sandbox-hdp ~]$

```

# Export from Hive into mysql

First, create the table in mysql

```bash
sqoop export --connect jdbc:mysql://localhost/movielens -m 1 --driver com.mysql.jdbc.Driver --table exported_movies --export-dir /apps/hive/warehouse/movies --input-fields-terminated-by '\001' --username root --password hadoop
```