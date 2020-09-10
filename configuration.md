:construction: Content has moved to [project "Piper" documentation](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/), please update your bookmarks.
Individual sub-sections below link to the new location.

# [customDefaults](https://sap.github.io/jenkins-library/configuration/#custom-default-configuration)

# [General configuration](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#general-configuration)

## [features](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#features)

## [jenkinsKubernetes](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#jenkinskubernetes)

# [Stage configuration](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#stage-configuration)

## [staticCodeChecks](https://sap.github.io/jenkins-library/stages/build/)

The `staticCodeChecks` stage functionality was incorporated into the `build` stage.

To configure static code checks, please configure the step `mavenExecuteStaticCodeChecks` as described [here](https://sap.github.io/jenkins-library/steps/mavenExecuteStaticCodeChecks/).

## [backendIntegrationTests](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#backendintegrationtests)

## [frontendIntegrationTests](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#frontendintegrationtests)

## [frontendUnitTests](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#frontendunittests)

## [endToEndTests](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#endtoendtests)

## [npmAudit](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#npmaudit)

## [performanceTests](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#performancetests)

## [s4SdkQualityChecks](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#s4sdkqualitychecks)

## [checkmarxScan](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#checkmarxscan)

## [productionDeployment](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#productiondeployment)

## [cfCreateServices](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#cfcreateservices)

## [cfTargets and neoTargets](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#cftargets-and-neotargets)

## [Examples](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#examples)

## [artifactDeployment](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#artifactdeployment)

### [nexus](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#nexus)

## [Choosing what to deploy into the npm repository](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#choosing-what-to-deploy-into-the-npm-repository)

## [whitesourceScan](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#whitesourcescan)

## [fortifyScan](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#fortifyscan)

## [lint](https://sap.github.io/jenkins-library/stages/build/)

The lint stage functionality was incorporated into the `build` stage.

The options for the use of linting tools remain the same and are described in the [build tools section](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/build-tools/#lint).

Note, the available configuration options can be found in the related [step documentation](https://sap.github.io/jenkins-library/steps/npmExecuteLint/#parameters).

## [sonarQubeScan](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#sonarqubescan)

## [postPipelineHook](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#postpipelinehook)

# [Step configuration](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#step-configuration)

## [artifactPrepareVersion](https://sap.github.io/jenkins-library/steps/artifactPrepareVersion/)

## [mavenExecute](https://sap.github.io/jenkins-library/steps/mavenexecute)

## [mavenExecuteStaticCodeChecks](https://sap.github.io/jenkins-library/steps/mavenexecutestaticcodechecks)

## [executeNpm](https://sap.github.io/jenkins-library/steps/npmExecute/)

## [cloudFoundryDeploy](https://sap.github.io/jenkins-library/steps/cloudFoundryDeploy/)

## [neoDeploy](https://sap.github.io/jenkins-library/steps/neoDeploy/)

## [checkGatling](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#checkgatling)

## [checkJMeter](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#checkjmeter)

## [detectExecuteScan](https://sap.github.io/jenkins-library/steps/detectExecuteScan/)

## [fortifyExecuteScan](https://sap.github.io/jenkins-library/steps/fortifyExecuteScan/)

## [mtaBuild](https://sap.github.io/jenkins-library/steps/mtaBuild/)

## [tmsUpload](https://sap.github.io/jenkins-library/steps/tmsUpload/)

## [debugReportArchive](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#debugarchivereport)

# [Post action configuration](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#post-action-configuration)

## [sendNotification](https://sap.github.io/jenkins-library/pipelines/cloud-sdk/configuration/#sendnotification)
