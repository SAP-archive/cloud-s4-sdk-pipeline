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

## Improvements
