# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

## v35

### :warning: Breaking changes

#### Fortify

The past Fortify scan implementation was replaced by a new step from project "Piper".
The configuration options for the step `executeFortifyScan` and the stage `fortifyScan` have been removed.
Instead, Fortify is solely configured using the step `fortifyExecuteScan` as documented [here](https://sap.github.io/jenkins-library/steps/fortifyExecuteScan/) 

The new implementation might be slightly different from the old one:
Even for CAP projects based on MTA we will use the groupId, artifactId and version from the pom file to generate values for the project name and project version. 

The `fortifyProjectName` it not required anymore.
Instead, it will be generated from the groupId and artifactId.
Also, the versioning behaves differently.
Before you could configure a `projectVersionId` and with every scan the version was updated to the current one generated in the pipeline.
The new one tries to automatically detect the version id by using the `projectName` (generated or configured) and the major version of the artifact.
The pipeline will fail if the version cannot be found.   
For the migration we recommend creating a new version in the Fortify dashboard by copying the last project version and setting the name of the copy to the major version of your artifact in the project root, i.e. `1`.

As the new step does not only scan the maven modules, the set of scanned files might be different. 
You can control them by using the configuration options `src` and `exclude`.

This diff shows how the change looks like:

```diff
steps:
-  executeFortifyScan:
-    dockerImage: 'fortify-docker-image:latest'
+  fortifyExecuteScan:
+    dockerImage: 'fortify-docker-image:latest'
+    fortifyCredentialsId: 'fortifyTokenCredentialId'
+    serverUrl: 'https://fortify.dummy.corp.domain/ssc'
stages:
-  fortifyScan:	
-    sscUrl: 'https://fortify.dummy.corp.domain/ssc'	
-    fortifyCredentialId: 'fortifyCredentialId'	
-    fortifyProjectName: 'mySampleProject'	
-    projectVersionId: '12345'
```

Please note that it might be a good idea to configure some of these values, such as the serverUrl, centrally, e.g. in a project extension, as it done for example within SAP.
