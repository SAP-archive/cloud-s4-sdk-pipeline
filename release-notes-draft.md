# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v37

## :warning: Breaking changes

* Support for SourceClear scans was removed from the pipeline. Please use other 3rd party security scanning tools, such as Fortify, Checkmarx, WhiteSource or SonarQube instead.

## Fixes

## Improvements

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
