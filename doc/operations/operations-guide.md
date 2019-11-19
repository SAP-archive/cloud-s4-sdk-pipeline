## Operations Guide for Cx Server
This document has been moved.
Please consult [devops-docker-cx-server repository](https://github.com/SAP/devops-docker-cx-server/blob/master/docs/operations/cx-server-operations-guide.md) for the up to date operations-guide.

## Migration

When using the cx-server script of the [SAP Cloud SDK docker images repository](https://github.com/SAP/cloud-s4-sdk-pipeline-docker), which is deprecated since October 2019, it is highly recommended to migrate to the images of the [devops-docker-cx-server repository](https://github.com/SAP/devops-docker-cx-server/).

Every time cx-server is executed you will be asked to do a guided migration.
In case there is enough disk space the migration will backup the jenkins_home volume so that no data will get lost. Otherwise the migration will be terminated.

After the backup cx-server will remove the docker containers as well as the docker networks and update the server.cfg as well as the cx-server script.
After the migration you are asked to execute `./cx-server start` to spin up the new containers.
