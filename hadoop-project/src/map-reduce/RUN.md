# Running MR jobs

1. Run locally
    ```bash
    python[maria_dev@sandbox-hdp ~]$ python RatingsBreakdown.py u.data
    ```
    
    ```bash
    No configs found; falling back on auto-configuration
    No configs specified for inline runner
    Creating temp directory /tmp/RatingsBreakdown.maria_dev.20231227.141429.110392
    Running step 1 of 1...
    job output is in /tmp/RatingsBreakdown.maria_dev.20231227.141429.110392/output
    Streaming final output from /tmp/RatingsBreakdown.maria_dev.20231227.141429.110392/output...
    "4"     34174
    "5"     21203
    "1"     6111
    "2"     11370
    "3"     27145
    Removing temp directory /tmp/RatingsBreakdown.maria_dev.20231227.141429.110392...
    ```

2. Run using the Hadoop cluster
    ```bash
    [maria_dev@sandbox-hdp ~]$ python RatingsBreakdown.py -r hadoop --hadoop-streaming-jar /usr/hdp/current/hadoop-mapreduce-client/hadoop-streaming.jar u.data
    ```

    ```bash
    No configs found; falling back on auto-configuration
    No configs specified for hadoop runner
    Looking for hadoop binary in $PATH...
    Found hadoop binary: /usr/bin/hadoop
    Using Hadoop version 2.7.3.2.6.5.0
    Creating temp directory /tmp/RatingsBreakdown.maria_dev.20231227.141942.348662
    uploading working dir files to hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662/files/wd...
    Copying other local files to hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662/files/
    Running step 1 of 1...
    packageJobJar: [] [/usr/hdp/2.6.5.0-292/hadoop-mapreduce/hadoop-streaming-2.7.3.2.6.5.0-292.jar] /tmp/streamjob8057347270467858952.jar tmpDir=null
    Connecting to ResourceManager at sandbox-hdp.hortonworks.com/172.18.0.2:8032
    Connecting to Application History server at sandbox-hdp.hortonworks.com/172.18.0.2:10200
    Connecting to ResourceManager at sandbox-hdp.hortonworks.com/172.18.0.2:8032
    Connecting to Application History server at sandbox-hdp.hortonworks.com/172.18.0.2:10200
    Total input paths to process : 1
    number of splits:2
    Submitting tokens for job: job_1703686104933_0001
    Submitted application application_1703686104933_0001
    The url to track the job: http://sandbox-hdp.hortonworks.com:8088/proxy/application_1703686104933_0001/
    Running job: job_1703686104933_0001
    Job job_1703686104933_0001 running in uber mode : false
    map 0% reduce 0%
    map 100% reduce 0%
    map 100% reduce 100%
    Job job_1703686104933_0001 completed successfully
    Output directory: hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662/output
    Counters: 49
            File Input Format Counters
                    Bytes Read=2088191
            File Output Format Counters
                    Bytes Written=49
            File System Counters
                    FILE: Number of bytes read=800030
                    FILE: Number of bytes written=2073012
                    FILE: Number of large read operations=0
                    FILE: Number of read operations=0
                    FILE: Number of write operations=0
                    HDFS: Number of bytes read=2088549
                    HDFS: Number of bytes written=49
                    HDFS: Number of large read operations=0
                    HDFS: Number of read operations=9
                    HDFS: Number of write operations=2
            Job Counters
                    Data-local map tasks=2
                    Launched map tasks=2
                    Launched reduce tasks=1
                    Total megabyte-milliseconds taken by all map tasks=7780750
                    Total megabyte-milliseconds taken by all reduce tasks=4758750
                    Total time spent by all map tasks (ms)=31123
                    Total time spent by all maps in occupied slots (ms)=31123
                    Total time spent by all reduce tasks (ms)=19035
                    Total time spent by all reduces in occupied slots (ms)=19035
                    Total vcore-milliseconds taken by all map tasks=31123
                    Total vcore-milliseconds taken by all reduce tasks=19035
            Map-Reduce Framework
                    CPU time spent (ms)=7880
                    Combine input records=0
                    Combine output records=0
                    Failed Shuffles=0
                    GC time elapsed (ms)=1083
                    Input split bytes=358
                    Map input records=100003
                    Map output bytes=600018
                    Map output materialized bytes=800036
                    Map output records=100003
                    Merged Map outputs=2
                    Physical memory (bytes) snapshot=543870976
                    Reduce input groups=5
                    Reduce input records=100003
                    Reduce output records=5
                    Reduce shuffle bytes=800036
                    Shuffled Maps =2
                    Spilled Records=200006
                    Total committed heap usage (bytes)=270532608
                    Virtual memory (bytes) snapshot=5835874304
            Shuffle Errors
                    BAD_ID=0
                    CONNECTION=0
                    IO_ERROR=0
                    WRONG_LENGTH=0
                    WRONG_MAP=0
                    WRONG_REDUCE=0
    job output is in hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662/output
    Streaming final output from hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662/output...
    "1"     6111
    "2"     11370
    "3"     27145
    "4"     34174
    "5"     21203
    Removing HDFS temp directory hdfs:///user/maria_dev/tmp/mrjob/RatingsBreakdown.maria_dev.20231227.141942.348662...
    Removing temp directory /tmp/RatingsBreakdown.maria_dev.20231227.141942.348662...
    ```