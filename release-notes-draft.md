# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v44

## :warning: Breaking changes

### Backend & Frontend Integration Tests

Due to alignments with project "Piper" the stages `backendIntegrationTests` and `frontendIntegrationTests` have been merged into the project "Piper" stage `integration`. 
Please move any existing configuration for the stages `backendIntegrationTests` and `frontendIntegrationTests` to the configuration of the stage `integration`.

For example:

```diff
stages:
-   backendIntegrationTests:
+   integration:
      retry: 2
      credentials:
        - alias: 'ERP'
          credentialId: 'erp-credentials'
        - alias: 'SF'
          credentialId: 'successfactors-credentials'
```

## New Features

## Fixes

## Improvements
