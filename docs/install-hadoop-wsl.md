# Install Hadoop on WSL2

To install Hadoop on Windows Subsystem for Linux 2 (WSL2), you need to follow a series of steps. Here's a playbook to guide you through the process:

### Prerequisites
1. **Windows Subsystem for Linux (WSL2):** Ensure WSL2 is installed and running on your Windows machine. You can follow Microsoft’s official documentation to set it up.

2. **Distribution:** Choose a Linux distribution (like Ubuntu) from the Microsoft Store and install it.

3. **Windows Terminal:** Install Windows Terminal for a better command-line experience.

4. **Java:** Hadoop requires Java to run, so make sure Java is installed in your WSL2 Linux distribution.

### Installation Steps

#### Step 1: Update and Upgrade Packages
1. Open WSL2 and update the package list:
   ```bash
   sudo apt update
   ```
2. Upgrade the packages:
   ```bash
   sudo apt upgrade
   ```

#### Step 2: Install Java
1. Install Java (OpenJDK):
   ```bash
   sudo apt install default-jdk
   ```
2. Verify the installation:
   ```bash
   java -version
   ```

#### Step 3: Download and Install Hadoop
1. Download Hadoop from the official Apache website. Find the link for the binary tar.gz file.
2. Use `wget` to download Hadoop:
   ```bash
   wget [Hadoop Download URL]
   ```
3. Extract the Hadoop files:
   ```bash
   tar -xzf [Hadoop tar.gz file]
   ```
4. Move Hadoop to a suitable directory, like `/usr/local/`:
   ```bash
   sudo mv [Hadoop folder] /usr/local/hadoop
   ```

#### Step 4: Configure Hadoop
1. Edit Hadoop environment settings:
   ```bash
   nano /usr/local/hadoop/etc/hadoop/hadoop-env.sh
   ```
   Set the `JAVA_HOME` variable to your Java installation path.

2. Configure core Hadoop settings in `core-site.xml`, `hdfs-site.xml`, and other configuration files as per your requirements.

#### Step 5: Set Environment Variables
1. Open `.bashrc` or `.zshrc` for editing:
   ```bash
   nano ~/.bashrc
   ```
2. Add the following lines to set Hadoop environment variables:
   ```bash
   export HADOOP_HOME=/usr/local/hadoop
   export PATH=$PATH:$HADOOP_HOME/bin
   export PATH=$PATH:$HADOOP_HOME/sbin
   export HADOOP_MAPRED_HOME=$HADOOP_HOME
   export HADOOP_COMMON_HOME=$HADOOP_HOME
   export HADOOP_HDFS_HOME=$HADOOP_HOME
   export YARN_HOME=$HADOOP_HOME
   ```
3. Apply the changes:
   ```bash
   source ~/.bashrc
   ```

#### Step 6: Format the Hadoop Filesystem
1. Format the Hadoop filesystem:
   ```bash
   hdfs namenode -format
   ```

#### Step 7: Start Hadoop
1. Start Hadoop services:
   ```bash
   start-dfs.sh
   start-yarn.sh
   ```

#### Step 8: Verification
1. Check if the Hadoop services are running:
   ```bash
   jps
   ```
   You should see the Namenode, Datanode, and other services listed.

### Post-Installation
- Access Hadoop's web interface by navigating to `http://localhost:9870/` in your browser.
- Begin uploading files to HDFS or running MapReduce jobs as per your requirement.

Remember, this playbook is a basic guide. You may need to adjust steps based on your specific environment and Hadoop version. Always refer to the official Hadoop documentation for detailed instructions and best practices.