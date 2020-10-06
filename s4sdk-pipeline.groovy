#!/usr/bin/env groovy

final def pipelineSdkVersion = 'v43'

library "s4sdk-pipeline-library@${pipelineSdkVersion}"
cloudSdkPipeline(script: this)
