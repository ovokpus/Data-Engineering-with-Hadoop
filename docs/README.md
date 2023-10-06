# Overview of the Hadoop Ecosystem

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/hadoop.png)

Let's dive deeper into each of the core components and some of the auxiliary tools in the Hadoop ecosystem.

### Core Components

#### Hadoop Distributed File System (HDFS)

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/Basic-Hadoop-Architecture.jpg)

HDFS is the storage layer of Hadoop, designed to store vast amounts of data across multiple nodes in a distributed fashion. It is optimized for high-throughput and fault-tolerance, with data automatically replicated across different nodes. HDFS is particularly well-suited for storing large files and is optimized for sequential read operations, making it ideal for batch processing tasks.

- **NameNode**: This is the master server that manages the metadata and namespace of the HDFS. It keeps track of the structure of the file system tree and the metadata for all the files and directories.
- **DataNode**: These are the worker nodes responsible for storing the actual data. They serve read and write requests from clients and perform block creation, deletion, and replication upon instruction from the NameNode.
- **Secondary NameNode**: Contrary to its name, it's not a backup for the NameNode. It's responsible for merging the fsimage and the edits log files periodically to prevent the edits log file from becoming too large.

---

#### MapReduce

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/MapReduce-architecture.jpg)

MapReduce is the data processing layer of Hadoop. It allows for the distributed processing of large data sets across a Hadoop cluster. The MapReduce programming model involves two primary tasks: Map and Reduce. The Map task takes a set of data and converts it into key-value pairs. The Reduce task then takes these key-value pairs and performs a reduction operation to generate the output. MapReduce is highly scalable and can handle tasks ranging from sorting and filtering to more complex analytical queries.

##### Mapreduce Execution Flow

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/mapreduce-job-execution-flow-1-1.jpg)

MapReduce is designed to handle vast quantities of data in parallel by dividing the task into a set of independent chunks. The execution flow of a MapReduce job can be summarized in two main phases: the Map phase and the Reduce phase, supplemented by other stages like Shuffle and Sort. 

Initially, during the Map phase, input data is divided into fixed-size pieces called splits. Each split is processed by a separate mapper, which takes the data and converts it into a set of intermediate key-value pairs. Once all the mappers have finished, the framework sorts these pairs by key. This transition between the Map and Reduce stages is often referred to as the Shuffle and Sort phase. 

Subsequently, during the Reduce phase, these key-value pairs are aggregated based on their keys. Each reducer processes the key-value groups, providing an output that is then written back to the Hadoop Distributed File System (HDFS). It's worth noting that both the mapping and reducing are distributed processes; tasks are scheduled and run on nodes where data resides, optimizing data locality and thereby enhancing overall job performance.

- **JobTracker**: This is the master daemon that manages all jobs and resources in a MapReduce cluster. It assigns tasks to different TaskTracker nodes in the cluster, trying to keep the work as close to the data as possible.
- **TaskTracker**: These are the slave nodes that execute tasks (Map, Reduce, and Shuffle operations) as directed by the JobTracker. Each TaskTracker is configured with a set of slots that indicate the number of tasks it can accept.


---

#### YARN (Yet Another Resource Negotiator)

![image](https://github.com/ovokpus/Data-Engineering-with-Hadoop/blob/main/images/Apache-YARN-architecture-min.jpg)

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

### Differences Between Hadoop 1 and Hadoop 2

1. **Resource Management**:
   - **Hadoop 1**: Uses Job Tracker and Task Tracker for resource management and job scheduling.
   - **Hadoop 2**: Introduces YARN, which separates the roles of resource management and job scheduling, making the system more scalable and flexible.

2. **Scalability**:
   - **Hadoop 1**: Limited to around 4,000 nodes per cluster.
   - **Hadoop 2**: Can scale up to 10,000 nodes per cluster, thanks to YARN.

3. **Multi-tenancy**:
   - **Hadoop 1**: Limited to running MapReduce jobs.
   - **Hadoop 2**: Can run multiple types of distributed applications (not just MapReduce) due to YARN, making it a multi-tenant system.

4. **Data Processing Model**:
   - **Hadoop 1**: Primarily relies on batch processing.
   - **Hadoop 2**: Supports batch, interactive, and real-time data processing.

5. **Fault Tolerance**:
   - **Hadoop 1**: Has a single point of failure in the NameNode.
   - **Hadoop 2**: Introduces the concept of High Availability (HA) for the NameNode, eliminating the single point of failure.

6. **API and Compatibility**:
   - **Hadoop 1**: Uses the older MapReduce API.
   - **Hadoop 2**: Introduces a new MapReduce API (MRv2) but retains compatibility with the older version.

7. **Ecosystem**:
   - **Hadoop 1**: Limited ecosystem components.
   - **Hadoop 2**: Richer ecosystem with additional tools and capabilities.

In summary, Hadoop 2 brings significant improvements over Hadoop 1, including better scalability, flexibility, and fault tolerance, thanks to the introduction of YARN and other architectural changes.