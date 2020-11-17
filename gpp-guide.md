# Project "Piper" General Purpose Pipeline for SAP Cloud SDK Pipeline users

The purpose of this document is to describe how projects that are using SAP Cloud SDK Pipeline can switch to the General Purpose Pipeline of project "Piper".

[Project "Piper"](https://sap.github.io/jenkins-library/) is an open source project initiated by SAP which implements various continuous delivery related components.
SAP Cloud SDK Pipeline is part of project "Piper", and from now on will be merged into [General Purpose Pipeline](https://sap.github.io/jenkins-library/stages/introduction/) (also known as `piperPipeline`).

The reasoning for this change is that over time both pipelines gained a similar set of features, which made it harder to chose one over the other.
Most of SAP Cloud SDK Pipeline's features are already present in General Purpose Pipeline as of today.
By unifying both pipelines into one, we hope to avoid confusion in the future.

The goal of this document is to make the transition as smooth as possible.
If you are facing issues not described here, feel free to [open an issue in the SAP Cloud SDK Pipeline GitHub repo](https://github.com/SAP/cloud-s4-sdk-pipeline/issues).

Also, please let us know if there are features of SAP Cloud SDK Pipeline that are missing in project "Piper" General Purpose Pipeline which are important to you.
In this case, please also [open an issue in the SAP Cloud SDK Pipeline GitHub repo](https://github.com/SAP/cloud-s4-sdk-pipeline/issues).

## Revision History

| Date          | Remark        |
|:-------------:|:------------- |
| 2020-11-11    | First draft |

## General

For information on setting up the project "Piper" General Purpose Pipeline, it is highly recommended to read [the documentation pages](https://sap.github.io/jenkins-library/stages/introduction/).

This migration guide discusses specific changes existing SAP Cloud SDK Pipeline users will be facing, but *it is no comprehensive documentation* for project "Piper" General Purpose Pipeline.

It assumes you're using the latest available [release](https://github.com/SAP/cloud-s4-sdk-pipeline/releases) of SAP Cloud SDK Pipeline.
If you're using an outdated version, **please make sure to update your pipeline using the provided release notes before proceeding**.

### The `productiveBranch`

Both SAP Cloud SDK Pipeline and project "Piper" General Purpose Pipeline have a concept called `productiveBranch`.
By default that branch is `master`, which is the default branch in git repositories.
For the purpose of migrating from SAP Cloud SDK Pipeline to the project "Piper" General Purpose Pipeline, it is a good idea to do the steps described in this document in a separate branch, for example called `gpp`, and to configure that branch as the productive branch **temporarily**.

This can be done by setting it in `.pipeline/config.yml` **in the `gpp` branch**.

```yaml
general:
  productiveBranch: gpp
```

The effect of this setting is that you get the behavior of a "productive" pipeline while doing the migration.

Please be aware that this can have an impact on your productive deployment as it might replace your actual deployment.
You can avoid this by keeping the default of the stage ["Confirm"](#automatically-deploy-productive-branch).

Please be aware that the scans of security tools can interfere with the same scans being performed on the actual productive branch, e.g. overwriting results.
You can reduce the impact by keeping your `gpp` branch up-to-date.

Once you are done with setting this up, remove the `productiveBranch: gpp` line from `config.yml`.
This will let you test the behavior of the pipeline in the [PR Voting stage](#pull-request-voting).
Note, to test the PR voting stage behavior with the `gpp` branch you have to configure the parameter `customVotingBranch: 'gpp'` for the PR Voting stage.
After the pipeline works in both scenarios, you can revert the changes to the `productiveBranch` parameter and merge the changes into your actual productive branch.

### Update Jenkinsfile to reference new pipeline

`Jenkinsfile` references the ready-made pipeline you use.
If you are using SAP Cloud SDK Pipeline, it should look like this

```Groovy
String pipelineVersion = "master"

library "s4sdk-pipeline-library@${pipelineVersion}"
cloudSdkPipeline(script: this)
```

or like this (which is the older variant)

```Groovy
String pipelineVersion = "master"

node {
    deleteDir()
    sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git -b ${pipelineVersion} pipelines"
    load './pipelines/s4sdk-pipeline.groovy'
}
```

In both cases, it should be replaced with [this](https://sap.github.io/jenkins-library/stages/introduction/#2-create-jenkinsfile):

```Groovy
@Library('piper-lib-os') _

piperPipeline script: this
```

### Fixed Pipeline Versions

For the SAP Cloud SDK pipeline, we have recommended to use fixed versions of the pipeline in productive scenarios.
For the project "Piper" General Purpose Pipeline, a common practice is to always use `master`.
If you want to still use a fixed version, you can do that by loading `piper-lib-os` in a specific version.
Information on the latest versions is available on the [release page of `piper-lib-os` on GitHub](https://github.com/SAP/jenkins-library/releases).

 ```Groovy
 @Library('piper-lib-os@versionX') _

piperPipeline script: this
 ```

### Configuration validation

In order to enable automatic checks for the pipeline configuration, please add the parameter `legacyConfigSettings` in the `general` section of your pipeline configuration file (`config.yml`):

```yaml
general:
  legacyConfigSettings: 'com.sap.piper/pipeline/cloudSdkToGppConfigSettings.yml'
```

This will catch common cases of missing configuration and inform you in the log of the pipeline.

Note that the purpose of this feature is to help you make the switch to project "Piper" General Purpose Pipeline more simple.
Once you've **finished the switch, we recommend to delete** the line `legacyConfigSettings: 'com.sap.piper/pipeline/cloudSdkToGppConfigSettings.yml'` from your pipeline configuration file.

### `pipeline_config.yaml` file

In case your pipeline configuration is in a file `pipeline_config.yml` (old name) rather than in `.pipeline/config.yml` (new name), this needs to be changed.
If you already use the new name, no action needs to be taken for this item.

### Build tool

Make sure to set `buildTool` as described [in the project "Piper" docs](https://sap.github.io/jenkins-library/stages/introduction/#1-create-pipeline-configuration).
Note, this was not required to be set in SAP Cloud SDK Pipeline, but **it is required** for project "Piper" General Purpose Pipeline.
Supported build tools for projects using SAP Cloud SDK are `maven` and `mta`.

### Pull-request voting

[Pull request voting](https://sap.github.io/jenkins-library/stages/prvoting/) is a separate stage in project "Piper" General Purpose Pipeline.
This stage is executed for every pull request.
By default, the project "Piper" General Purpose Pipeline is **not** executed for non-productive, non pull request branches.

It is possible, however, to configure a branch name prefix for the pull request voting stage, to run the stage on non pull request branches named with the configured prefix:

```diff
stages:
+ 'Pull-Request Voting':
+   customVotingBranch: 'MY-PREFIX-*'
```

In order to test the PR voting [with the branch](#the-productivebranch) you use for the migration to the project "Piper" General Purpose Pipeline, you could temporarily configure `customVotingBranch` to the name of your branch while at the same time removing or disabling the key `productiveBranch` from the general section.

Note, the Pull-Request Voting stage only allows the execution of a subset of the project "Piper" General Purpose Pipeline steps, i.e., the security/compliance steps for WhiteSource and SonarQube, linting and static code checks and frontend unit tests.
Thus, it is not possible to run end to end tests or performance tests on non-productive branches.
This is different from SAP Cloud SDK Pipeline, where the pipeline is the same both for productive- and feature (pull request) branches, with the exception of some steps that are only run on productive branches.

### Automatically deploy productive branch

The default behavior of project "Piper" General Purpose Pipeline is, in contrast to SAP Cloud SDK Pipeline, to not automatically deploy to SAP Cloud Platform when pushed to the productive branch.
If you do not want manual confirmation, you may [set it in your config](https://sap.github.io/jenkins-library/stages/confirm/) like in this example:

```diff
stages:
+ Confirm:
+   manualConfirmation: false
```

### Custom defaults/ shared configuration

Custom defaults defined in the customDefaults section of your configuration file should work as before in the Cloud SDK pipeline.
Further information can be found at [the custom default configuration docs](https://sap.github.io/jenkins-library/configuration/#custom-default-configuration).

### Extensions

Project "Piper" General Purpose Pipeline uses a different naming schema for stages.

This means, that [stage level extensions](https://sap.github.io/jenkins-library/extensibility/#1-extend-individual-stages) need to be renamed.

The new name can be found in the [pipeline source code](https://github.com/SAP/jenkins-library/blob/master/vars/piperPipeline.groovy), which is also the name you see in the Jenkins user interface, such as `Build` or `Additional Unit Tests`.

### Modified Pipeline / 2nd level of Extensibility

The SAP Cloud SDK Pipeline as well as the project "Piper" General Purpose Pipeline offer the possibility to define [a custom pipeline](https://sap.github.io/jenkins-library/extensibility/#2-modified-ready-made-pipeline) based on the corresponding centrally maintained pipeline by reusing pipeline stages.
This approach can still be used in case you need a different structure or want to adapt conditions for when stages are skipped.

We recommend to start with the [General Purpose Pipeline template](https://github.com/SAP/jenkins-library/blob/master/vars/piperPipeline.groovy) and apply the changes you have made to the SAP Cloud SDK Pipeline afterwards.

### Stage config

Project "Piper" General Purpose Pipeline uses a different naming schema for stages.

Therefore, the `stages` section in `config.yml` needs to be updated accordingly. For example:

```diff
stages:
- build:
+ 'Build':
    ...

- endToEndTests:
+ Acceptance:
    ...

- performanceTests:
+ Performance:
    ...

- productionDeployment:
+ Release:
    ...
```

The new name can be found in the [pipeline source code](https://github.com/SAP/jenkins-library/blob/master/vars/piperPipeline.groovy), which is also the name you see in the Jenkins user interface, such as `Build` or `Additional Unit Tests`.

## Build and Test

To execute static code checks and linting as part of the stage [`Build`](https://sap.github.io/jenkins-library/stages/build/), the steps `mavenExecuteStaticCodeChecks` and `npmExecuteLint` need to be activated manually by configuring the stage.

For example:

```diff
stages:
+ 'Build':
+   mavenExecuteStaticCodeChecks: true
+   npmExecuteLint: true
```

If the code checks should also be executed for every pull request, the steps have to be activated for the stage [`Pull-Request Voting`](https://sap.github.io/jenkins-library/stages/prvoting/), as well.

For example:

```diff
stages:
+ 'Pull-Request Voting':
+   mavenExecuteStaticCodeChecks: true
+   npmExecuteLint: true
```

## Backend Integration Tests

To migrate the configuration for backend integration tests it is required to update the stage name to the naming schema of the Piper General Purpose Pipeline in the stages section of your configuration.
For visualizing the test results, it is required to configure the step [`testsPublishResults`](https://sap.github.io/jenkins-library/steps/testsPublishResults/) for visualizing the test results.
The available configuration parameters can be provided per stage to avoid a mix up of test results.

For example:

```diff
stages:
- integration:
+ Integration:
+   junit:
+     active: true
+     pattern: 'integration-tests/target/surefire-reports/TEST-*.xml,s4hana_pipeline/reports/backend-integration/**'
    credentials:
      - alias: 'MY-ALIAS'
        credentialId: 'MY-CREDENTIAL-ID'
```

## Frontend Integration Tests

To migrate the configuration for frontend integration tests it is required to update the stage name to the naming schema of the project "Piper" General Purpose Pipeline in the stages section of your configuration.
For visualizing the test results, it is required to configure the step [`testsPublishResults`](https://sap.github.io/jenkins-library/steps/testsPublishResults/).
The available configuration parameters can be provided per stage to avoid a mix up of test results.
In addition, it is required to configure the step [npmExecuteScripts](https://sap.github.io/jenkins-library/steps/npmExecuteScripts/) for the test execution.
It is recommended to configure the parameters for the stage instead of the step, since the step is used for different purposes throughout the pipeline.
The required parameters for the execution of frontend unit tests are [`runScripts`](https://sap.github.io/jenkins-library/steps/npmExecuteScripts/#runscripts) and [`virtualFrameBuffer`](https://sap.github.io/jenkins-library/steps/npmExecuteScripts/#virtualframebuffer). 
Note, the SAP Cloud SDK Pipeline provided these parameters by default. 

For example:

```diff
stages:
- integration:
+ Integration:
    dockerImage: 'myDockerImage'
+   runScripts: [ 'ci-it-frontend' ]
+   virtualFrameBuffer: true
+   junit:
+     active: true
+     pattern: 's4hana_pipeline/reports/frontend-integration/**/*.xml'
```
 
## Additional (Frontend) Unit Tests

For the switch to the stage [`Additional Unit Tests`](https://sap.github.io/jenkins-library/stages/additionalunittests/) it is required to update the stage name to the naming schema of the project "Piper" General Purpose Pipeline in the stages section of your configuration.
It is also required to add configuration for the visualization of test results for the step [`testsPublishResults`](https://sap.github.io/jenkins-library/steps/testsPublishResults/).
The available configuration parameters can be provided per stage to avoid a mix up of test results.

Please note, if you are using Karma to run frontend unit tests, it is required to **disable** the Piper step `karmaExecuteTests` for the stage `Additional Unit Tests`.
The SAP Cloud SDK Pipeline relies on a different Piper step for frontend unit test execution, i.e., [`npmExecuteScripts`](https://sap.github.io/jenkins-library/steps/npmExecuteScripts/).
The steps are incompatible and the step `karmaExecuteTests` might falsely get activated due to the existence of the `karma.conf.js` file as part of your project.

For example:

```diff
stages:
- additionalUnitTests:
-   dockerImage: 'myDockerImage'

+ 'Additional Unit Tests':
+   dockerImage: 'myDockerImage'
+   karmaExecuteTests: false
+   junit:
+     active: true
+     allowEmptyResults: true
+     pattern: 's4hana_pipeline/reports/frontend-unit/**/Test*.xml'
+   htmlPublisher:
+     active: true
+     reportDir: ""
+     pattern: "s4hana_pipeline/reports/**/report-html/ut/index.html"
+     reportName: "Frontend Unit Test Coverage"
```

## End to End Tests

For the switch to the stage [`Acceptance`](https://sap.github.io/jenkins-library/stages/acceptance/) it is required to update the stage name to the naming schema of the project "Piper" General Purpose Pipeline in the stages section of your configuration.

For example:

```diff
stages:
- endToEndTests:
+ Acceptance:
    ...
```

## Performance Tests

The project "Piper" General Purpose Pipeline supports performance tests with Gatling.
Performance tests with JMeter are currently not supported.
If you used the `checkJMeter` step of the Cloud SDK Pipeline and it is important to you, please [open an issue in the SAP Cloud SDK Pipeline GitHub repo](https://github.com/SAP/cloud-s4-sdk-pipeline/issues).

### Gatling

To execute tests with Gatling, the step [`gatlingExecuteTests`](https://sap.github.io/jenkins-library/steps/gatlingExecuteTests/) needs to be configured.
`gatlingExecuteTests` needs to know the path to the Maven module's pom file in which the Gatling plugin is bound to the `test` phase.
The Cloud SDK Pipeline pre-configured the `pomPath` parameter with the value `performance-tests/pom.xml`.
This needs to be passed to the step in the step-configuration now as seen in the example below.
If you used the parameter `appUrls` with a list of urls and credential IDs, it can be copied to the step configuration as well.

As part of the stage configuration you also have to provide the configuration for deployment.

Below you can find an example of the required configuration changes:

```diff
steps:
- checkJMeter: # no longer supported, see above
-   testPlan: <PATH-TO-TESTPLAN>
-   dockerImage: 'famiko/jmeter-base'
-   unstableThreshold: 70
-   failThreshold : 80

- checkGatling:
-   enabled: true

+ gatlingExecuteTests:
+   pomPath: 'performance-tests/pom.xml'

stages:
- performanceTests:
+ Performance:
    cfCreateServices:
      - serviceManifest: 'MY-SERVICES-MANIFEST.yml'
        space: 'MY-CF-SPACE'
    cfTargets:
      - space: 'MY-CF-SPACE'
        manifest: 'MY-CF-MANIFEST.yml'
```

## Security

### WhiteSource (only for SAP Cloud Application Programming Model projects)

As preparation for the WhiteSource scan, the pipeline will execute an `npm install` in order to download nodejs dependencies.
If your project has so called [post install scripts](https://docs.npmjs.com/cli/v6/using-npm/scripts) configured, it might be required to have your source code available in the stage.
However, in the stage only the `package.json` files will be available by default.
To make also the rest of your source code available, you have to add the stash `source` using the `stashContent` configuration.

For example:

```diff
steps:
  whitesourceExecuteScan:
    ...
+   stashContent:
+     - 'source'
```

### Fortify (only for MTA projects)

The configuration of Fortify has been aligned with the project "Piper" General Purpose Pipeline already.
For MTA projects, however, it is required to additionally set the `buildTool` parameter to `maven` in the step configuration.

For example:

```diff
steps:
  fortifyExecuteScan:
    ...
+   buildTool: 'maven'
```

### Checkmarx & Detect

The configuration for Checkmarx and Detect scans have been aligned with the project "Piper" General Purpose Pipeline already, thus no configuration changes are necessary.

## Compliance (SonarQube)

Sonar scan is part of [the Build stage](https://sap.github.io/jenkins-library/stages/build/).
To enable it, be sure to configure the [`sonarExecuteScan`](https://sap.github.io/jenkins-library/steps/sonarExecuteScan/) step in your pipeline config.

**Note**: This integration of sonar might not be what you are expecting based on what SAP Cloud SDK Pipeline provided.
For example, as sonar is run without prior execution of tests, it will not provide any test results or code coverage information.

Also, please be aware that any prior extensions you used for the `Compliance` stage might not just work with the `Build` stage due to how the stages work internally.

## Artifact Deployment

If you used the Cloud SDK Pipeline stage `artifactDeployment` to upload build artifacts to a Nexus Repository Manager, you have to move the configuration to the step [`nexusUpload`](https://sap.github.io/jenkins-library/steps/nexusUpload/).
This will automatically enable the General Purpose Pipeline's stage [`Promote`](https://sap.github.io/jenkins-library/stages/promote/) to run and execute the step.

Please note if you previously relied on the manual confirmation behavior of the Cloud SDK Pipeline's stage `artifactDeployment`, the default behaviour of the General Purpose Pipeline stage [`Confirm`](https://sap.github.io/jenkins-library/stages/confirm/) is a fully compatible substitute.

The following diff shows the necessary changes.
Please note that some parameter names for `nexusUpload` have slightly different names.

```diff
steps:
+ nexusUpload:
+   version: 'nexus3'
+   url: 'MY-NEXUS-URL'
+   mavenRepository: 'maven-releases'
+   nexusCredentialsId: 'MY-NEXUS-CREDENTIALS'

stages:
- artifactDeployment:
-   nexus:
-     version: nexus3
-     url: cloudpot.cpe.c.eu-de-1.cloud.sap:8081
-     repository: maven-releases
-     credentialsId: nexus-upload

```

## Production Deployment

For the switch to the stage [`Release`](https://sap.github.io/jenkins-library/stages/release/) it is required to update the stage name to the naming schema of the project "Piper" General Purpose Pipeline in the stages section of your configuration.
If you are using blue-green deployment, which was the default for SAP Cloud SDK Pipeline, you have to configure the [`enableZeroDowntimeDeployment` parameter](https://sap.github.io/jenkins-library/steps/multicloudDeploy/#parameters) for the stage `Release`.

For example:

```diff
stages:
- productionDeployment:
+ Release:
+   enableZeroDowntimeDeployment: true
    cfTargets:
      - org: 'MY-CF-ORG'
        space: 'MY-CF-SPACE'
        apiEndpoint: 'CF-API-ENDPOINT'
        credentialsId: 'MY-CF-CREDENTIALS-ID'
    appUrls:
      - url: 'MY-APP-URL'
    ...
```

## Multi Module Maven Project

In case your project contains maven modules in addition to the ones defined by the SAP Cloud Application Programming Model or SAP Cloud SDK archetypes, and these modules have dependencies between each other, this section might be relevant for you.

Typically, you will see the following error message:

```
Could not resolve dependencies for project
com.sap.cloud.s4hana.examples:address-manager-application:war:1.0-xyz:
Failure to find com.sap.cloud.s4hana.examples:address-manager-utils:jar:1.0-xyz
in http://cx-nexus:8081/repository/mvn-proxy/ was cached ...
```

The example error message above occurs in the following setup.
You may have an additional module `utils`, which the default module `application` depends on:

```
|-pom.xml
|-application
  |-pom.xml
|-utils
   |-pom.xml
|-integration-tests
  |-pom.xml
```

With project "Piper" General Purpose Pipeline, it might occur that your pipeline fails because maven can't find the artifact (jar file) produced by the `utils` module.
This is indicated by the error message shown above.

In this case, you have to install the artifacts before the corresponding steps by setting the option `installArtifacts` to `true`.
You also have to unstash the build results to make these artifacts available in the steps which are not running in the build stage.

```yaml
steps:
  mavenExecuteStaticCodeChecks:
    installArtifacts: true
    ...
  whitesourceExecuteScan:
    installArtifacts: true
    ...
    stashContent:
      - 'buildResult'
  detectExecuteScan:
    installArtifacts: true
    ...
```

If your project only contains the modules generated by the archetypes (i.e. `srv`, `application`, `unit-tests`, `integration-tests`) or you don't have any dependencies between the maven modules, please do not activate the installation of the artifacts in your project.
