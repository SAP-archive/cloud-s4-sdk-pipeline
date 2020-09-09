# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v43

## :warning: Breaking changes


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

## New Features

## Fixes

## Improvements
