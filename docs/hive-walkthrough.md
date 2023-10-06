# Hive Walkthrough

![image](hive-introduction)

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