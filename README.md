# Data-Engineering-with-Hadoop
Exploring Data Engineering capabilities within the Hadoop Ecosystem

---

### Start container


```bash
docker run -m 8g --hostname=quickstart.cloudera --privileged=true -t -i -v /home/ubuntu/Data-Engineering-with-Hadoop/cdh_files:/src --publish-all=true -p 8888 ovokpus/cloudera-quickstart /usr/bin/docker-quickstart
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
| `kil`l              | N/A                    | N/A                     | Kill a running process (not applicable) |
| `cp local_file HDFS_destination` | `hdfs dfs -copyFromLocal local_file HDFS_destination` | `hadoop fs -copyFromLocal local_file HDFS_destination` | Copy a file from the local file system to HDFS |
| `cp HDFS_source local_destination` | `hdfs dfs -copyToLocal HDFS_source local_destination` | `hadoop fs -copyToLocal HDFS_source local_destination` | Copy a file from HDFS to the local file system |
