#!/usr/bin/env groovy

final def pipelineSdkVersion = 'v41'

library "s4sdk-pipeline-library@${pipelineSdkVersion}"
cloudSdkPipeline(script: this)
