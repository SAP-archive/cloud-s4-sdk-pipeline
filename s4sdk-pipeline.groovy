#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'

pipeline {
    agent any
    options {
        timeout(time: 30, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    }
    stages {
        stage('Init') {
            steps {
                library "pipeline-sdk-lib@${pipelineSdkVersion}"
                initS4SdkPipeline script: this
                stashFiles script:this, stage:'init'
            }
        }
        stage('Clean Build') {
            steps {
                unstashFiles script: this, stage:'build'
                stageMavenCleanBuild script:this
                stashFiles script: this, stage:'build'
            }
        }

        stage('Static Code Checks') {
            steps {
                unstashFiles script: this, stage: 'staticCodeChecks'
                stageStaticCodeChecks script: this
                stashFiles script: this, stage: 'staticCodeChecks'
            }
        }

        stage('Unit Test') {
            steps {
                unstashFiles script: this, stage: 'unitTest'
                stageUnitTests script:this
                stashFiles script: this, stage: 'unitTest'
            }
        }
        stage('Integration Tests') {
            steps {
                unstashFiles script: this, stage: 'integrationTest'
                stageIntegrationTests script: this
                stashFiles script: this, stage: 'integrationTest'
            }
        }

        stage ('Quality Checks') {
            unstashFiles script:this, stage:'qualityChecks'
            steps {
                unstashFiles script: this, stage: 'qualityChecks'
                stageS4SdkQualityChecks(script: this)
                stashFiles script: this, stage: 'qualityChecks'
            }
            stashFiles script:this, stage:'qualityChecks'
        }

        stage('Production Deployment') {
            steps {
                unstashFiles script:this, stage:'deploy'
                stageProductionDeployment script: this
                stashFiles script:this, stage:'deploy'
            }
        }
    }
    post {
        failure {
            archive "**"
        }
    }
}
