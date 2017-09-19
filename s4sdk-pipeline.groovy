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
                library "s4sdk-pipeline-library@${pipelineSdkVersion}"
                initS4SdkPipeline script: this
            }
        }

        stage('Build') {
            steps {
                parallel(
                        "Backend": {
                            node { stageBuildBackend script: this }
                        },
                        "Frontend":{
                            node { stageBuildFrontend script: this }
                        }
                        )
            }
        }

        stage('Local Tests') {
            steps {
                parallel (
                        "Static Code Checks": {
                            node { stageStaticCodeChecks script: this }
                        },
                        "Backend Unit Tests": {
                            node { stageUnitTests script: this }
                        },
                        "Backend Integration Tests": {
                            node { stageIntegrationTests script: this }
                        },
                        "Frontend Unit Tests": {
                            node { stageFrontendUnitTests script: this }
                        }
                        )
            }
        }

        stage('Remote Tests') {
            steps {
                parallel(
                        "End to End Tests": {
                            node { stageEndToEndTests script: this }
                        },
                        "Performance Tests": {
                            node { stagePerformanceTests script: this }
                        }
                        )
            }
        }
        stage('Quality Checks') {
            steps { stageS4SdkQualityChecks script: this }
        }

        stage('Production Deployment') {
            steps { stageProductionDeployment script: this }
        }
    }
    post { failure { deleteDir() } }
}