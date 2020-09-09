# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v42

## :warning: Breaking changes

### createHdiContainer step removal
The `backendIntegrationTests` stage has been aligned with project "Piper" in version v42 and activation of the `createHdiContainer` step was not migrated.
If you still need the functionalities provided by the `createHdiContainer` step, please open an issue at our [pipeline repository](https://github.com/SAP/cloud-s4-sdk-pipeline/issues/new?template=pipeline-issue.md).
In the meantime it is also possible to implement an extension for the `backendIntegrationTests` stage using the extensibility concept explained in the [documentation](https://sap.github.io/jenkins-library/extensibility/). 
The step `createHdiContainer` is still available for use in extensions, configuration options must be passed via parameters only.

### Backend integration tests stage (only for JS integration tests)
The name `ci-integration-test` for the npm script which is executed as part of the backend integration tests stage is deprecated.
From v42 onwards it is required to change the name of the script in your `package.json` files to the new name `ci-it-backend`, since the script `ci-integration-test` will not be executed as part of the backend integration tests anymore.

### Frontend unit tests stage
The name `ci-test` for the npm script which is executed as part of the frontend unit tests stage is deprecated.
From v42 onwards it is required to change the name of the script in your `package.json` files to the new name `ci-frontend-unit-test`, since the script `ci-test` will not be executed as part of the frontend unit tests anymore.

### Lint stage
The `lint` stage has been aligned with project "Piper" in version v42 and the `checkUi5BestPractices` step was not migrated, since the used tool is deprecated.
In addition, the linting will now be executed as part of the `build` stage instead of in the dedicated `lint` stage.
Thus, the configuration for the `lint` stage has to be removed, as it will not have an effect anymore. 
Instead, please configure the step `npmExecuteLint` in the steps section of your project configuration, as described in the [documentation](https://sap.github.io/jenkins-library/steps/npmExecuteLint/).
For example:
```diff
stages:
-  lint:
-    ui5BestPractices:
-      esLanguageLevel: es2020
-      failThreshold:
-        error: 3
-        warning: 5
-        info: 7
steps:
+  npmExecuteLint:
+    failOnError: true
```

### Static code checks stage
The `staticCodeChecks` stage has been aligned with project "Piper" in version v42. 
The static code checks will now be executed as part of the `build` stage instead of in the dedicated `staticCodeChecks` stage.  

### Renaming of keys in runStage configuration map
Due to further alignment efforts with project "Piper" the keys (identifiers for the different stages) used in the `runStage` map have changed.
This is a breaking change for users that use a custom pipeline or overwrite the `runStage` map or its entries as part of an extension.
In particular, the keys have been changed from the upper case notation used before, to their respective stage names in camel case.
For example:
```diff
- script.commonPipelineEnvironment.configuration.runStage.BACKEND_INTEGRATION_TESTS = false
+ script.commonPipelineEnvironment.configuration.runStage.backendIntegrationTests = false
```

## New Features

## Fixes

* In versions v40 and v41 of the Cloud SDK Pipeline, the Lint stage could fail while trying to record issues, if the version of the Jenkins plugin `warnings-ng` was older than 8.4.0.
* In versions v39, v40 and v41 of the Cloud SDK Pipeline, the `productionDeployment` stage did **not** enable zero downtime deployments by default.
For the affected versions it is possible to fix this problem by adding `enableZeroDowntimeDeployment: true` to the `productionDeployment` stage configuration or to update to v42 of the Cloud SDK Pipeline.

## Improvements

* The results of tests when viewed via the Jenkins Blue Ocean interface are now separated by the stage where the tests have been performed.
  In previous versions of the SAP Cloud SDK Pipeline, the same test results could be listed under multiple stages.
