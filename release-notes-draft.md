# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v37

## :warning: Breaking changes

* Support for SourceClear scans was removed from the pipeline. Please use other 3rd party security scanning tools, such as Fortify, Checkmarx, WhiteSource or SonarQube instead.

* The configuration for Automatic Versioning has changed, see below.

#### Automatic versioning

Automatic versioning is solely configured via the new step `artifactPrepareVersion` as documented [here](https://sap.github.io/jenkins-library/steps/artifactPrepareVersion/) 

For internal use-cases, the pipeline is configuring this step to also push a tag for each generated version.
This makes it necessary to provide the two parameters `gitHttpsCredentialsId` and `gitUserName`.
For external use-cases, the default is not to push tags.
Three types of versioning are supported via the parameter `versioningType`: `cloud`, `cloud_noTag`, and `library`.
To disable automatic versioning, set the value to `library`.
The pipeline will then pick up the artifact version configured in the build descriptor file, but not generate a new version.

If you previously turned off automatic versioning via the parameter `automaticVersioning`, this diffs shows the necessary migration of the config file:

```diff
general:
-  automaticVersioning: false
steps:
+  artifactPrepareVersion:
+    versioningType: 'library'
```

If you previously configured pushing tags for each new version, this is how the configuration can be migrated:

```diff
steps:
-  artifactSetVersion:
-    commitVersion: true
-    gitCredentialsId: 'Jenkins secret'
-    gitSshUrl: 'repo-URL'
-    gitUserEMail: 'email'
-    gitUserName: 'username'
+  artifactPrepareVersion:
+    versioningType: 'cloud'
+    gitHttpsCredentialsId: 'Jenkins secret'
```

The repository URL for the project in Jenkins needs to be configured with `https://` scheme.   
It will be used also for pushing the tag.

For Maven projects, the step `mavenExecute` is not used anymore to set the version.
Instead, it is directly done by `artifactPrepareVersion`, which avoids starting new Docker containers and will improve the performance.

If you have defined a project settings file for `mavenExecute` before, you must move this configuration into the general sections as follows:

```diff
general:
+  maven:
+    projectSettingsFile: 'settings.xml'
steps:
-  mavenExecute:
-    projectSettingsFile: 'settings.xml'
```

The same applies to other options defined for `mavenExecute`.

## Fixes

## Improvements

### Lint Stage

The pipeline can be configured to fail based on linting findings using the `failOnError` configuration option. 
By default, the pipeline does not fail based on lint findings.
This option is available when providing a custom linting script or when relying on the default linting of the pipeline.
It is not available when using the SAPUI5 best practices linter.

```diff
steps:
+  npmExecuteLint:
+    failOnError: true
```

### Jenkinsfile

We updated our bootstrapping Jenkinsfile so that it loads the pipeline directly from the library and not from the `cloud-s4-sdk-pipeline` repository anymore.
This change improves efficiency of the pipeline because it decreases the number of repositories checked out and the number of executors during the pipeline run.

The new version can be found here: https://github.com/SAP/cloud-s4-sdk-pipeline/blob/master/archetype-resources/Jenkinsfile

To benefit from the improved efficiency, please update your Jenkinsfile like this:

```diff
-node {
-    deleteDir()
-    sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git -b ${pipelineVersion} pipelines"	
-    load './pipelines/s4sdk-pipeline.groovy'	
-}	
+library "s4sdk-pipeline-library@${pipelineVersion}"
+cloudSdkPipeline(script: this)
```
