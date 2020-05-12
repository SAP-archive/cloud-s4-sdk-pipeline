#!/usr/bin/env groovy

final def pipelineSdkVersion = 'v33'

library "s4sdk-pipeline-library@${pipelineSdkVersion}"
cloudSdkPipeline(script: this)
