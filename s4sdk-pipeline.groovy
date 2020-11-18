#!/usr/bin/env groovy

final def pipelineSdkVersion = 'v45'

library "s4sdk-pipeline-library@${pipelineSdkVersion}"
cloudSdkPipeline(script: this)
