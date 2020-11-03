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
- backendIntegrationTests:
+ integration:
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

### Conditional Execution of Stages

It is possible to consistently enable or disable all **conditional** stages with the config key `runInAllBranches`.
The stages `productionDeployment`, `artifactDeployment`, `compliance`, and `security` are by default disabled **for non-productive** branches.
They can be enabled also for non-productive branches by configuring the respective stage with `runInAllBranches`.
Example `.pipeline/config.yml` file snippet to enable `security` also in non-productive branches:

```yaml
stages:
  security:
    runInAllBranches: true
```

Similarily, if there are stages which you do **not** want to run by default also in your **non-productive** branches, you can disable them like this:

```yaml
stages:
  endToEndTests:
    runInAllBranches: false
```

This would then deviate from the default behavior and run the End to End Tests stage only for the productive branch.

### Disable Usage of Deprecated Jenkins Plugins

The [`checksPublishResults`](https://sap.github.io/jenkins-library/steps/checksPublishResults/) step uses some Jenkins plugins which have been deprecated in favor of [`warnings-ng`](https://plugins.jenkins.io/warnings-ng/).
When replacing the SAP Cloud SDK Pipeline specific build stage with the more generic build stage of project "Piper", those plugins became a requirement of SAP Cloud SDK Pipeline, which was not intended.
Due to backwards compatibility concerns in [project "Piper" general purpose pipeline](https://sap.github.io/jenkins-library/stages/introduction/), the old plugins are still available, but they have been disabled by default in SAP Cloud SDK Pipeline so that having those plugins installed is not required anymore.

If you need any of the old plugins, see the docs of the [`checksPublishResults`](https://sap.github.io/jenkins-library/steps/checksPublishResults/) step to enable them in your pipeline config file.
