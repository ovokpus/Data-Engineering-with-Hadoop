# Data-Engineering-with-Hadoop

This is a Learning project Repository, whose goal is exploring Data Engineering capabilities within the Hadoop Ecosystem. Hadoop is now being perceived as a legacy framework for big data. However, the knowledge of Hadoop is now even more valuable, as opportunities arise where Enterprises are lifting and Shifting Hadoop workloads from on-prem systems into the Public Cloud. 

---

![image](hadoop)

---

# Overview of the Hadoop Ecosystem

Let's dive deeper into each of the core components and some of the auxiliary tools in the Hadoop ecosystem.

### Core Components

![image](hadoop-ecosystem)

#### Hadoop Distributed File System (HDFS)

![image](basic-hadoop-architecture)

HDFS is the storage layer of Hadoop, designed to store vast amounts of data across multiple nodes in a distributed fashion. It is optimized for high-throughput and fault-tolerance, with data automatically replicated across different nodes. HDFS is particularly well-suited for storing large files and is optimized for sequential read operations, making it ideal for batch processing tasks.

- **NameNode**: This is the master server that manages the metadata and namespace of the HDFS. It keeps track of the structure of the file system tree and the metadata for all the files and directories.
- **DataNode**: These are the worker nodes responsible for storing the actual data. They serve read and write requests from clients and perform block creation, deletion, and replication upon instruction from the NameNode.
- **Secondary NameNode**: Contrary to its name, it's not a backup for the NameNode. It's responsible for merging the fsimage and the edits log files periodically to prevent the edits log file from becoming too large.

## Local Development Setup

![image](cloudera-quickstart)

We'll be using the Cloudera Quickstart Docker Container for this project. The Cloudera Quickstart Docker image provides an easy way to test Cloudera's distribution, including Apache Hadoop and built-in components, on a single-node cluster. Below is a set of instructions on how to pull and run the Cloudera Quickstart Docker image for local development and testing:

### Prerequisites:

1. Ensure you have Docker installed on your system. If you don't, you can download it from the Docker's official website and follow their installation instructions.

### Instructions:

1. **Pull the Cloudera Quickstart Docker Image:**

   Open your terminal or command prompt and enter the following command to pull the Cloudera Quickstart image:

   ```
   docker pull cloudera/quickstart:latest
   ```

   Note: The tag `latest` will pull the latest version of the Cloudera Quickstart image. You can replace it with a specific version if needed.

2. **Run the Docker Image:**

   After pulling the image, you can run it using the following command:

        ```bash
        docker run -m 8g --hostname=quickstart.cloudera \
        --privileged=true -t -i -v /home/ubuntu/Data-Engineering-with-Hadoop/cdh_files:/src \
        --publish-all=true -p 8888:8888 ovokpus/cloudera-quickstart /usr/bin/docker-quickstart
        ```

   Explanation:

   - `--hostname=quickstart.cloudera`: This sets the hostname of the container.
   - `--privileged=true`: Allows the container to run in privileged mode.
   - `-t -i`: Allows you to have a terminal access to the running container.
   - `-p 8888:8888 -p 80:80`: Maps ports from the host to the container. For instance, this makes it possible to access the Hue interface on port 8888.
   - `/usr/bin/docker-quickstart`: This is the startup script for the Cloudera services.

3. **Access Cloudera Manager and Other Services:**

   Once the container is running, you can access the Cloudera Manager, Hue, and other services through your web browser:

   - Hue: `http://localhost:8888`
   - Cloudera Manager (if it's part of the Quickstart version you pulled): `http://localhost:7180`

4. **Stop the Docker Container:**

   To stop the Cloudera Docker container, you can use:

   ```
   docker stop [CONTAINER_ID]
   ```

   Where `[CONTAINER_ID]` is the ID of the running Cloudera container. You can find this ID using `docker ps`.

### Important Notes:

- Cloudera Quickstart is intended for testing and development purposes only. It shouldn't be used in production environments.
- The Cloudera Quickstart container can be quite resource-intensive. Ensure your machine has adequate resources (RAM, CPU) allocated to Docker to avoid performance issues.

---

### Inside the Container environment

inside the container switch user from root to cloudera
Notice file environments changing from root to the `cloudera` user

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

---

Open Hue with browser from Docker Desktop

![image](open-hue-browser)

### HDFS commands compared to Linux commands

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
