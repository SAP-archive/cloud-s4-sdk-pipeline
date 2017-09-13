# Pipelines for the SAP S/4HANA Cloud SDK
 
 ![Picture of Pipeline](s4sdk-pipeline.png)
 
 ## Description

 The [SAP S/4HANA Cloud SDK](https://sap.com/s4sdk) helps to develop S/4HANA extension application on the SAP Cloud Platform. 
 Continuous integration and delivery (CI/CD) is an important aspect of cloud application development.
 This repository contains a Jenkins pipeline as code designed for the requirements and structure of application written with this SDK. 
 It contains the steps for building, testing and deploying to the SAP Cloud Platform.
 
 ## Requirements
 
 You have a repository on github or any other version control system.
 
 You have to setup a [Jenkins](https://jenkins.io/) server. In addition you have to add the [s4sdk-pipeline-library](https://github.com/SAP/cloud-s4-sdk-pipeline-lib) as shared library. 

 The best way to setup the server with all requirements is to use the cx-server script, which is included in every new project created with the SAP S/4HANA Cloud SDK. You can find the details [here](https://blogs.sap.com/2017/05/19/step-3-with-sap-s4hana-cloud-sdk-helloworld-on-scp-cloudfoundry/). 
 
 Furthermore, you have to create a Jenkins job, which is connected to your version control system and can build that project.
  
 ## Download and Installation
 
In order to use the pipeline just load the pipeline within your Jenkinsfile that is placed in the root of your project repository. 
Create a file called Jenkinsfile and add the following example code:
 
 ```groovy
 #!/usr/bin/env groovy 
 
 node {
     deleteDir()
     sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git pipelines"
     load './pipelines/s4sdk-pipeline.groovy'
 }
```

After you commit your changes and the Jenkins server starts to build the project it will automatically use the pipeline. 

## Known Issues
Currently, there are no known issues.

## How to obtain support
If you need any support, have any question or have found a bug, please report it as issue in the repository.

## License
Copyright (c) 2017 SAP SE or an SAP affiliate company. All rights reserved.
This file is licensed under the Apache Software License, v. 2 except as noted otherwise in the [LICENSE file](LICENSE).”
