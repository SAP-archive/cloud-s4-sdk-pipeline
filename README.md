# Pipelines for the SAP S/4HANA Cloud SDK
 
 ![alt text](s4sdk-pipeline.png)
 
 ## What is it?

 The [SAP S/4HANA Cloud SDK](https://sap.com/s4sdk) helps to develop S/4HANA extension application on the SAP Cloud Platform. 
 Continuous integration and delivery (CI/CD) is an important aspect of cloud application development.
 This repository contains a Jenkins pipeline as code designed for the requirements and structure of application written with this SDK.
 
 ## How to use?
 
 To setup the environment you can start a preconfigured Jenkins server using the cx-server script included in the archetypes of the SAP S/4HANA Cloud SDK. 
 
 In order to use the pipeline just load the pipeline within your Jenkinsfile placed in the root of your project repository. You can use the following example code:
 
 ```groovy
 #!/usr/bin/env groovy 
 
 node {
     deleteDir()
     sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git pipelines"
     load './pipelines/s4sdk-pipeline.groovy'
 }
```

## Licence
The pipeline is licensed under [Apache License 2](LICENSE).
