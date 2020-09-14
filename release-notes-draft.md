# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v43

## :warning: Breaking changes


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
## New Features

## Fixes

## Improvements
