# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v38

## :warning: Breaking changes

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

Set branch property in non productive non PR branches in sonar. This allows usage of sonar in multi-branch setups, as sonar is made aware of which branch a check refers to. Please note that this is not available in all versions of sonar.

## Fixes

## Improvements
