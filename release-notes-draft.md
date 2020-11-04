# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v45

## :warning: Breaking changes

### checkGatling renamed to gatlingExecuteTests

The step `checkGatling` has been migrated as `gatlingExecuteTests` into Project 'Piper', adopting the naming convention for steps.
This step is executed by the Cloud SDK Pipeline in the stage `performanceTests`, but only if it is enabled via step configuration.
This step configuration has to be adapted in your `.pipeline/config.yml` as shown in the diff below:

```diff
steps:
- checkGatling:
+ gatlingExecuteTests:
    enabled: true
```

## New Features

## Fixes

## Improvements
