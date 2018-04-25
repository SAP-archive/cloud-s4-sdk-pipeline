## Operations Guide for Cx Server

This guide describes life-cycle management of the Cx Server for Continuous Integration and Delivery. The server is controlled with the `cx-server` script.

### Introduction

The `cx-server` directory is included in projects which are created by using the SAP S/4HANA Cloud SDK Maven Archetypes.
It contains a life-cycle management utility script `cx-server` and a configuration file `server.cfg`.

#### start
You can start the Jenkins server by launching the `start` command.

```bash
./cx-server start
``` 

When launched, it checks if the Docker container named `s4sdk-jenkins-master` already exists.
If yes, it restarts the stopped container. Otherwise, it spawns a new Docker container based on the configuration in `server.cfg`.

Example:

```bash
# Address of the used docker registry
docker_registry=registry.hub.docker.com

# Name of the used docker image
docker_image="s4sdk/jenkins-master:latest"
``` 

#### stop
The Cx Server can be stopped with the `stop` command.
```bash
./cx-server stop
``` 
This stops the Jenkins Docker container if it is running. A subsequent `start` command restores the container.

#### remove
This command removes the Jenkins container from the host if it is not running.

```bash
./cx-server remove
```

#### backup
The `jenkins_home` contains the state of the Jenkins which includes important details such as settings, Jenkins workspace and job details.
Considering the importance of it, taking regular backup of the `jenkins_home` is **highly recommended**. 

```bash
./cx-server backup
```
This command creates a backup file and stores it on a host machine inside a directory named `backup`. In order to store the backup on external storage, you can customize the location and name of the backup file in the `server.cfg`.

Example:
```bash
# Absolute path to a directory where the backup of the jenkins_volume stored
backup_directory="$(pwd)/backup"

# Name of the backup file
backup_file_name="jenkins_home_$(date -u +%Y-%m-%dT%H%M%Z).tar.gz"
```

> **Note:** Administrator of the Jenkins must ensure that the backup is stored in a safe storage.

#### restore
In an event of a server crash, the state of the Jenkins can be restored to a previously saved state if there is a backup file available. You need to execute the `restore` command along with the absolute path to the backup file that you want to restore the state to.
 
Example:

```bash
./cx-server restore /home/cx-server/backup/jenkins_home_2018-03-07T1528UTC.tar.gz
```

> **Warning:** In order to restore the Jenkins home directory, this command stops the Jenkins server first and **delete the content of the Jenkins home directory**.
> After the completion of the restore operation, it starts the Jenkins server upon user confirmation.

#### update script
The `cx-server` script can be updated via the `update script` command, if a new version is available.
```bash
./cx-server update script
```

#### update image
By default, the Cx Server image defined by `docker_image` in `server.cfg` always points to the newest released version.
In productive environments, you will however likely want to fix the Cx Server image to a specific version.
By defining `docker_image` with a version tag (e.g. `docker_image=s4sdk/jenkins-master:v3`), you avoid unintended updates as a side-effect of restarting the Continuous Delivery server.
However, this introduces the risk of getting stuck on an outdated version. Therefore, if you are using an outdated Cx Server version, the `cx-server` script will warn you and recommend to run the `cx-server update image` command.
The `cx-server update image` command updates the Cx Server to the newest available version.
If `v6` is the newest released version, running an update with `docker_image=s4sdk/jenkins-master:v3` will update the configuration to `docker_image=s4sdk/jenkins-master:v6`.
For this, it executes the following sequence of steps:
* Stop potentially running Cx Server instance
* Perform full backup of home directory
* Update `docker_image` value in `server.cfg` to newest version
* Start Cx Server

Note: The command only works if you use the default image from Docker Hub.
```bash
./cx-server update image
```

#### Caching mechanism 
The `cx-server` provides the local cache for maven and node dependencies. This is enabled by default. A Docker image of [Sonatype Nexus OSS 3.x](https://www.sonatype.com/download-oss-sonatype) is used for this. 

By default the caching service makes use of maven central and npm registry are used for downloading the dependencies. This can be customized in `server.cfg` as shown below.

```bash
### maven proxy target (default is maven central)
mvn_repository_url="https://your-local-maven-repo.corp/maven2/"

### npm proxy target (default is central npm registy)
npm_registry_url="https://your-local-npm-registry.corp/"
```

In a distributed build environment, the Nexus server is started on each agent.
The agent initializer script `launch-s4sdk-agent.sh` takes care of the automatic start of the caching server.
However, when the agent is disconnected, download cache service **will NOT be stopped** automatically.
It is the responsibility of an admin to stop the Nexus service.
This can be achieved by stopping the Docker image on the agent server. 

Example:

```bash
ssh my-user@agent-server
docker stop s4sdk-nexus
docker network remove s4sdk-network
```

If you prefer to use different caching mechanism or not using any, you can disable the caching mechanism in the `server.cfg`.

```bash
cache_enabled=false
```
