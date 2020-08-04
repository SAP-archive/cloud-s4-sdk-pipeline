# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v40

## :warning: Breaking changes

## New Features

### Synopsys Detect Scan (formerly BlackDuck) (Beta)

A new 3rd party stage was introduced which allows to execute Detect scans using the project "Piper" step [detectExecuteScan](https://sap.github.io/jenkins-library/steps/detectExecuteScan/).
Please note that the step is currently only available in an early version.
It might not support all variants of projects out-of-the-box.
Furthermore, you might have to configure a Docker image which provides the build tools, e.g. maven, that you want to use during the scan.

The scan can be activated by configuring the the step `detectExecuteScan`. e.g.:
```yaml
steps:
  detectExecuteScan:
    detectTokenCredentialsId: 'detect-token'
    projectName: 'My Example'
    projectVersion: '1'
    serverUrl: 'https://xyz.blackducksoftware.com'
    dockerImage: 'docker-image'
```


## Fixes

Some stages such as `backendIntegrationTests` can be configured to run with an optional sidecar image.
This was broken for a number of releases, if the Download Cache was enabled and any steps within the stage made use of it (such as `mavenExecute`).
For the time being, docker containers can be connected to one network only, which means the Download Cache has to be disabled for stages with a sidecar image defined.

## Improvements

For a step which anticipates to run with an optional sidecar image, the image may now also be defined in that step's configuration only.
This improves performance versus configuring the sidecar image in the stage, since it avoids running other steps within the stage also with that sidecar.

:warning: **If the Download Cache is enabled**, sidecar images have to be configured in the stage as before. 
The Download Cache is enabled by default on CX-Server-based Jenkins instances, unless when running Jenkins in a Kubernetes environment.   
