# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v39

## :warning: Breaking changes

In the process of aligning the pipeline configuration with the concepts of other project "Piper" pipelines to delivery a common user experience, the configuration of the SAP Cloud Platform Transport Management upload was moved from the stage `productionDeployment` to the `tmsUpload` step:

```diff
steps:
+  tmsUpload:
+    nodeName: 'TEST'
+    credentialsId: 'TMS-UPLOAD'
+    customDescription: 'A custom description for the node upload'
stages:
-  productionDeployment:
-    tmsUpload:
-      nodeName: 'TEST'
-      credentialsId: 'TMS-UPLOAD'
-      customDescription: 'A custom description for the node upload'
```

### Stages endToEndTests and productionDeployment
- The `appUrls` property of the stages `endToEndTests` and `productionDeployment` must not be a string anymore. 
Instead, `appUrls` is required to be a list of maps, where for each `appUrl` the mandatory property `url` is defined. 
Example:

```diff
  endToEndTests:
-    appUrls: 'https://my-app-url.com'
+    appUrls:
+      - url: 'https://my-app-url.com'
```

- In addition, the optional property `parameters` which can be defined for each `appUrl`, must not be a string anymore.
  Instead, it is required to be a list of strings, where each string corresponds to one element of the parameters. For Example:
  
```diff
  endToEndTests:
    appUrls:
      - url: 'https://my-app-url.com'
-       parameters: '--tag scenario1'
+       parameters: ['--tag', 'scenario1']
```

## New Features

## Fixes

## Improvements
