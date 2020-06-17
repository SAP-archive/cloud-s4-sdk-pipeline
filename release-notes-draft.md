# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v37

## :warning: Breaking changes

* Support for SourceClear scans was removed from the pipeline. Please use other 3rd party security scanning tools, such as Fortify, Checkmarx, etc., instead.

## Fixes

## Improvements

### Jenkinsfile

We updated our bootstrapping Jenkinsfile so that it loads the pipeline directly from the library and not from this repository anymore.
With this change we decrease the number of repositories checked out and the number of executors during the pipeline run.

The new version can be found here: https://github.com/SAP/cloud-s4-sdk-pipeline/blob/master/archetype-resources/Jenkinsfile

You have to adopt the Jenkinsfile accordingly to the following change:
```diff
-node {
-    deleteDir()
-    sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git -b ${pipelineVersion} pipelines"	
-    load './pipelines/s4sdk-pipeline.groovy'	
-}	
+library "s4sdk-pipeline-library@${pipelineVersion}"
+cloudSdkPipeline(script: this)
```
