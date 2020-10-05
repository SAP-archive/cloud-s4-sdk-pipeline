# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v43

## :warning: Breaking changes

### Quality Checks stage

Due to alignments with project "Piper" the `s4SdkQualityChecks` stage has been removed.
Please remove any configuration for the stage `s4SdkQualityChecks` from your pipeline configuration or custom default configuration.

### Additional tools stage

The `Third-party Checks` stage has been aligned with project "Piper" and the `additionalTools` stage was not migrated.
In case you have a custom extension for the `additionalTools` stage, please migrate it to be an extension of the stage `security` instead.
The `security` stage is documented in [project "Piper"](https://sap.github.io/jenkins-library/stages/security/).

In addition, this stage was used for running code analysis tools, e.g., Vulas, for internal projects, which has been removed as well.

### Checkmarx Scan stage

Similarly, the `checkmarxScan` stage has been merged into the project "Piper" stage `security`.
The configuration of Checkmarx needs to be moved to the step configuration of the [checkmarxExecuteScan](https://sap.github.io/jenkins-library/steps/checkmarxExecuteScan/) step.

For example:

```diff
- stages:
-   checkmarxScan:
+ steps:
+   checkmarxExecuteScan:
      groupId: <Checkmarx GroupID>
      vulnerabilityThresholdMedium: 5
      checkMarxProjectName: 'My_Application'
      vulnerabilityThresholdLow: 999999
      filterPattern: '!**/*.log, !**/*.lock, !**/*.json, !**/*.html, !**/Cx*, **/*.js, **/*.java, **/*.ts'
      fullScansScheduled: false
      generatePdfReport: true
      incremental: true
      preset: '36'
      checkmarxCredentialsId: CHECKMARX-SCAN
      checkmarxServerUrl: http://localhost:8089
```

### NPM dependency audit stage

Due to alignments with project "Piper" the `npmAudit` stage has been removed.
Please remove any configuration for the stage `npmAudit` from your pipeline configuration or custom default configuration.

### Lint stage

The `lint` stage has been aligned with project "Piper" in version v43 and the `checkUi5BestPractices` step was not migrated, since the used tool is deprecated.
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

The `staticCodeChecks` stage has been aligned with project "Piper" in version v43. 
The static code checks will now be executed as part of the `build` stage instead of in the dedicated `staticCodeChecks` stage.  

### Frontend unit tests stage

The `frontendUnitTests` stage has been aligned with project "Piper" in version v43. 
The stage has been renamed to `additionalUnitTests`. 
Please move any existing stage configuration for the stage `frontendUnitTests` to the stage `additionalUnitTests`. 

For Example:

```diff
stages:
-  frontendUnitTests:
+  additionalUnitTests:
    dockerImage: 'myDockerImage'
```

### Renaming of sonarQubeScan stage

Continuing with the alignment efforts, the execution of the step `sonarExecuteScan` has been integrated into the project "Piper" stage `Compliance`, and the Cloud SDK Pipeline executes that stage instead.
To activate this stage, the step `sonarExecuteScan` needs to be configured in your `.pipeline/config.yml` as described in the [documentation](https://sap.github.io/jenkins-library/steps/sonarExecuteScan/).
By default, the pipeline will run the stage only for the productive branch, as before, but you can run it in all branches by configuring the option `runInAllBranches: true` for the stage `compliance`.
Also note that the parameter `sonarProperties` has been renamed to `options`.

:warning: If you have created an extension for this stage, it needs to be renamed to `compliance.groovy`.

The following diff shows the necessary migration of the configuration:

```diff
steps:

+ sonarExecuteScan:
+   projectKey: "my-project"
+   instance: "MySonar"
+   dockerImage: "myDockerImage"
+   options:
+     - "sonar.sources=./application"

stages:

- sonarQubeScan:
+ compliance:                # The stage config is only necessary,
    runInAllBranches: true   # if you need to activate 'runInAllBranches'.
-   projectKey: "my-project"
-   instance: "MySonar"
-   dockerImage: "myDockerImage"
-   sonarProperties:
-     - "sonar.jacoco.reportPaths=s4hana_pipeline/reports/coverage-reports/unit-tests.exec,s4hana_pipeline/reports/coverage-reports/integration-tests.exec"
-     - "sonar.sources=./application"
```

Specifying `sonar.jacoco.reportPaths` as previously documented is no longer necessary.

Recent versions of the SonarQube plugin (8.x) no longer supports coverage reports in .exec binary format.
It only supports .xml reports generated from the JaCoCo maven plugin.
As of now, it is a known issue that importing code coverage into the SonarQube service does not work in the Cloud SDK Pipeline out of the box.
If you need this, please open [an issue on GitHub](https://github.com/sap/cloud-s4-sdk-pipeline/issues).

### Migration to whitesourceExecuteScan step

The stage `whitesourceScan` has been replaced with the project "Piper" stage `security`. 
Now the step `whitesourceExecuteScan` will be executed and the stage is activated **if** the step `whitesourceExecuteScan` is configured in your `.pipeline/config.yml` file.
The existing configuration for the stage `whitesourceScan` has to be moved to the step `whitesourceExecuteScan` with some modifications:

```diff
steps:
+ whitesourceExecuteScan:
+   productName: 'THE PRODUCT NAME AS IN WHITESOURCE'
+   orgAdminUserTokenCredentialsId: 'Jenkins-credentials-id-org-token'
+   userTokenCredentialsId: 'Jenkins-credentials-id-user-token'
+   productVersion: 'current' # replaces staticVersion
+   cvssSeverityLimit: 5 # optional

stages:
- whitesourceScan:
-   product: 'THE PRODUCT NAME AS IN WHITESOURCE'
-   credentialsId: 'Jenkins-credentials-id-org-token'
-   whitesourceUserTokenCredentialsId: 'Jenkins-credentials-id-user-token'
-   staticVersion: true
```

Note that the step will now **fail the pipeline if the scan finds security vulnerabilities** in any module that exceed the defined severity limit.
This can be controlled with the new parameter `cvssSeverityLimit`.
For more information about the step `whitesourceExecuteScan`, please refer its projert "Piper" [documentation](https://sap.github.io/jenkins-library/steps/whitesourceExecuteScan/).

With using the new step implementation, there is also a potential change in naming the WhiteSource projects with regard to the version.
The naming scheme for each WhiteSource project that is part of a scan is "<module name> - <version>".
The version part is now guaranteed to be consistent across a single scan.
If the parameter `productVersion` is configured (formerly `staticVersion`), it is taken from there.
Otherwise it is taken from the main build descriptor file (i.e. mta.yaml) in accordance to the step parameter `versioningModel`, which defaults to `major`.
The version in the build descriptor files for each module is ignored.

### Send notification post action

Due to alignments with project "Piper" the `sendNotification` post action has been removed.
Please remove any configuration for the post action `sendNotification` from your pipeline configuration or custom default configuration.

## New Features

## Fixes

## Improvements
