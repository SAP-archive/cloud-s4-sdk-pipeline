#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'
def stageConfig = [:]
pipeline {
    agent any
    options {
        timeout(time: 60, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    }
    stages {
        stage('Init') {
            steps {
                library "s4sdk-pipeline-library@${pipelineSdkVersion}"
                script { stageConfig = initS4SdkPipeline script: this }
            }
        }

        stage('Build') {
            parallel {
                stage("Backend") { steps { node('') { stageBuildBackend script: this } } }
                stage("Frontend") {
                    when { expression { stageConfig.FRONT_END_BUILD } }
                    steps { node('') { stageBuildFrontend script: this } }
                }
            }
        }

        stage('Local Tests') {
            parallel {
                stage("Static Code Checks") { steps { node('') { stageStaticCodeChecks script: this } } }
                stage("Backend Unit Tests") { steps { node('') { stageUnitTests script: this } } }
                stage("Backend Integration Tests") { steps { node('') { stageIntegrationTests script: this } } }
                stage("Frontend Unit Tests") {
                    when { expression { stageConfig.FRONT_END_TESTS } }
                    steps { node('') { stageFrontendUnitTests script: this } }
                }
            }
        }

        stage('Remote Tests') {
            when { expression { stageConfig.REMOTE_TESTS } }
            parallel {
                stage("End to End Tests") {
                    when { expression { stageConfig.E2E_TESTS } }
                    steps { node('') { stageEndToEndTests script: this } }
                }
                stage("Performance Tests") {
                    when { expression { stageConfig.PERFORMANCE_TESTS } }
                    steps { node('') { stagePerformanceTests script: this } }
                }
            }
        }

        stage('Quality Checks') {
            steps { node('') { stageS4SdkQualityChecks script: this } }
        }

        stage('Security Checks') {
            when { expression { stageConfig.SECURITY_CHECKS } }
            parallel {
                stage("Checkmarx Scan") {
                    when { expression { stageConfig.CHECKMARX_SCAN } }
                    steps { node('') { stageCheckmarxScan script: this } }
                }
                stage("WhiteSource Scan") {
                    when { expression { stageConfig.WHITESOURCE_SCAN } }
                    steps { node('') { stageWhitesourceScan script: this } }
                }
                stage("Node Security Platform Scan") {
                    when { expression { stageConfig.NODE_SECURITY_SCAN } }
                    steps { node('') { stageNodeSecurityPlatform script: this } }
                }
            }

        }

        stage('Deployment') {
            parallel {
                stage('Production Deployment') {
                    when { expression { stageConfig.PRODUCTION_DEPLOYMENT } }
                    steps { node('') { stageProductionDeployment script: this } }
                }

                stage('Artifact Deployment') {
                    when { expression { stageConfig.ARTIFACT_DEPLOYMENT } }
                    steps { node('') { stageArtifactDeployment script: this } }
                }
            }
        }
    }
    post { failure { deleteDir() } }
}
