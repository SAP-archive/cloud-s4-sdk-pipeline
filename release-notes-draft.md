# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v42

## :warning: Breaking changes

### createHdiContainer step removal
The `backendIntegrationTests` stage has been aligned with Project 'Piper' in version v42 and activation of the `createHdiContainer` step was not migrated.
If you still need the functionalities provided by the `createHdiContainer` step, please open an issue at our [pipeline repository](https://github.com/SAP/cloud-s4-sdk-pipeline/issues/new?template=pipeline-issue.md).
In the meantime it is also possible to implement an extension for the `backendIntegrationTests` stage using the extensibility concept explained in the [documentation](https://sap.github.io/jenkins-library/extensibility/). 
The step `createHdiContainer` is still available for use in extensions, configuration options must be passed via parameters only.

### Backend integration tests stage (only for JS integration tests)
The name `ci-integration-test` for the npm script which is executed as part of the backend integration tests stage is deprecated.
From v42 onwards it is required to change the name of the script in your `package.json` files to the new name `ci-it-backend`, since the script `ci-integration-test` will not be executed as part as the backend integration tests anymore.

## New Features

## Fixes

## Improvements
