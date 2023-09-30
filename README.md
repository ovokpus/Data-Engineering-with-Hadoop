# Data-Engineering-with-Hadoop
Exploring Data Engineering capabilities within the Hadoop Ecosystem

---

# Overview of the Hadoop Ecosystem

Let's dive deeper into each of the core components and some of the auxiliary tools in the Hadoop ecosystem.

### Core Components

#### Hadoop Distributed File System (HDFS)

HDFS is the storage layer of Hadoop, designed to store vast amounts of data across multiple nodes in a distributed fashion. It is optimized for high-throughput and fault-tolerance, with data automatically replicated across different nodes. HDFS is particularly well-suited for storing large files and is optimized for sequential read operations, making it ideal for batch processing tasks.

- **NameNode**: This is the master server that manages the metadata and namespace of the HDFS. It keeps track of the structure of the file system tree and the metadata for all the files and directories.
- **DataNode**: These are the worker nodes responsible for storing the actual data. They serve read and write requests from clients and perform block creation, deletion, and replication upon instruction from the NameNode.
- **Secondary NameNode**: Contrary to its name, it's not a backup for the NameNode. It's responsible for merging the fsimage and the edits log files periodically to prevent the edits log file from becoming too large.

#### MapReduce

MapReduce is the data processing layer of Hadoop. It allows for the distributed processing of large data sets across a Hadoop cluster. The MapReduce programming model involves two primary tasks: Map and Reduce. The Map task takes a set of data and converts it into key-value pairs. The Reduce task then takes these key-value pairs and performs a reduction operation to generate the output. MapReduce is highly scalable and can handle tasks ranging from sorting and filtering to more complex analytical queries.

- **JobTracker**: This is the master daemon that manages all jobs and resources in a MapReduce cluster. It assigns tasks to different TaskTracker nodes in the cluster, trying to keep the work as close to the data as possible.
- **TaskTracker**: These are the slave nodes that execute tasks (Map, Reduce, and Shuffle operations) as directed by the JobTracker. Each TaskTracker is configured with a set of slots that indicate the number of tasks it can accept.

#### YARN (Yet Another Resource Negotiator)

YARN serves as the resource management layer for Hadoop, decoupling the resource management capabilities from the MapReduce programming model. This allows for multiple data processing engines like Spark and Tez to run on a single Hadoop cluster. YARN improves the resource utilization and scheduling capabilities of Hadoop, making it more flexible and capable of handling a broader range of use-cases beyond just MapReduce.

- **ResourceManager (RM)**: This is the ultimate authority for resource allocation. It has two main components: Scheduler and ApplicationManager. The Scheduler is responsible for allocating resources based on the requirement, and the ApplicationManager is responsible for accepting job submissions.
- **NodeManager (NM)**: These are per-machine slave daemons responsible for monitoring resource usage on each machine and reporting back to the ResourceManager.
- **ApplicationMaster (AM)**: An instance of this runs for each application. It negotiates resources from the ResourceManager and works with the NodeManager(s) to execute and monitor tasks.

### Hadoop Common

Hadoop Common acts as the foundation for the various modules and tools in the Hadoop ecosystem. It provides essential libraries, APIs, and utilities that are used by other Hadoop modules. Hadoop Common also includes the client-side libraries that applications need to interact with HDFS, as well as the necessary JAR files and scripts required to start Hadoop. It essentially serves as the glue that holds the different components of Hadoop together.

These core components collectively make Hadoop a powerful and scalable platform for distributed computing and big data analytics.

- **Common Utilities**: This provides the Java libraries, utilities, and APIs, including the client-side libraries that are needed for basic Hadoop functions. It also contains the necessary JAR files and scripts required to start Hadoop.

### Auxiliary Tools

- **Hive**: Provides a SQL-like interface to query data stored in HDFS. It translates SQL queries into MapReduce jobs.
- **Pig**: A high-level scripting language that allows for complex data transformations and translates these into MapReduce jobs.
- **HBase**: A NoSQL database that runs on top of HDFS. It is designed for wide-table storage and is horizontally scalable.
- **ZooKeeper**: Provides distributed synchronization, naming registry, and configuration services. It's often used in ensemble mode for high availability.
- **Oozie**: A workflow scheduler system to manage Hadoop jobs. It supports scheduling Hadoop MapReduce jobs, Pig jobs, Hive jobs, etc.
- **Mahout**: Provides machine learning libraries designed to be scalable and work with Hadoop.
- **Flume**: Used for aggregating and moving large amounts of log data from various sources to HDFS.
- **Sqoop**: A tool to transfer data between HDFS and relational databases like MySQL, Oracle, etc.

Each of these components and tools has its own set of configurations, optimizations, and best practices, making Hadoop a very flexible and powerful system for handling big data.


---

## Local Development Setup
### Start container

```bash
docker run -m 8g --hostname=quickstart.cloudera --privileged=true -t -i -v /home/ubuntu/Data-Engineering-with-Hadoop/cdh_files:/src --publish-all=true -p 8888:8888 ovokpus/cloudera-quickstart /usr/bin/docker-quickstart
```

inside the container switch user from root to cloudera
Notice file environments
```bash
[root@quickstart cloudera]# su - cloudera
[cloudera@quickstart ~]$ pwd
/home/cloudera
[cloudera@quickstart ~]$ ls
cloudera-manager  cm_api.py  Desktop  Documents  enterprise-deployment.json  express-deployment.json  kerberos  lib  parcels  workspace
[cloudera@quickstart ~]$ 
```

Move to folder where source files are to be copied from:

```bash
cd data
docker cp transactions.txt distracted_einstein:/home/cloudera
```

HDFS commands compared to Linux commands

| Linux Command    | HDFS Command           | HDFS Long-Form Command   | Description                           |
|-------------------|------------------------|--------------------------|---------------------------------------|
| `ls`                | `hdfs dfs -ls`           | `hadoop fs -ls`           | List files and directories            |
| `cd`                | N/A                    | N/A                     | Change directory (not applicable)     |
| `cat`               | `hdfs dfs -cat`          | `hadoop fs -cat`          | View the contents of a file           |
| `mkdir`             | `hdfs dfs -mkdir`        | `hadoop fs -mkdir`        | Create a new directory                |
| `cp`                | `hdfs dfs -copyToLocal`  | `hadoop fs -copyToLocal`  | Copy a file from HDFS to the local file system |
| `mv`                | `hdfs dfs -mv`           | `hadoop fs -mv`           | Move a file or directory               |
| `rm`                | `hdfs dfs -rm`           | `hadoop fs -rm`           | Remove a file or directory            |
| `touch`             | N/A                    | N/A                     | Create a new empty file (not applicable) |
| `du`                | `hdfs dfs -du`           | `hadoop fs -du`           | Show disk usage of files and directories in HDFS |
| `pwd`               | N/A                    | N/A                     | Display the current working directory (not applicable) |
| `ps`                | N/A                    | N/A                     | List running processes (not applicable) |
| `kill`              | N/A                    | N/A                     | Kill a running process (not applicable) |
| `cp local_file HDFS_destination` | `hdfs dfs -copyFromLocal local_file HDFS_destination` | `hadoop fs -copyFromLocal local_file HDFS_destination` | Copy a file from the local file system to HDFS |
| `cp HDFS_source local_destination` | `hdfs dfs -copyToLocal HDFS_source local_destination` | `hadoop fs -copyToLocal HDFS_source local_destination` | Copy a file from HDFS to the local file system |
