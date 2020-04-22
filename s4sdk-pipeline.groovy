#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'

library "s4sdk-pipeline-library@${pipelineSdkVersion}"
cloudSdkPipeline(script: this)
