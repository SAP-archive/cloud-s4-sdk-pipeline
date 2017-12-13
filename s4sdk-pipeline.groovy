#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'

pipeline {
    agent none
    options {
        timeout(time: 120, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
        skipDefaultCheckout()
    }
    stages {
        stage('Init') {
            steps {
                library "s4sdk-pipeline-library@${pipelineSdkVersion}"
                node('') {
                    checkout scm
                    initS4SdkPipeline script: this
                }
            }
        }

        stage('Build') {
            parallel {
                stage("Backend") { steps { node('') { stageBuildBackend script: this } } }
                stage("Frontend") {
                    when { expression { pipelineEnvironment.skipConfiguration.FRONT_END_BUILD } }
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
                    when { expression { pipelineEnvironment.skipConfiguration.FRONT_END_TESTS } }
                    steps { node('') { stageFrontendUnitTests script: this } }
                }
            }
        }

        stage('Remote Tests') {
            when { expression { pipelineEnvironment.skipConfiguration.REMOTE_TESTS } }
            parallel {
                stage("End to End Tests") {
                    when { expression { pipelineEnvironment.skipConfiguration.E2E_TESTS } }
                    steps { node('') { stageEndToEndTests script: this } }
                }
                stage("Performance Tests") {
                    when { expression { pipelineEnvironment.skipConfiguration.PERFORMANCE_TESTS } }
                    steps { node('') { stagePerformanceTests script: this } }
                }
            }
        }

        stage('Quality Checks') {
            steps { node('') { stageS4SdkQualityChecks script: this } }
        }

        stage('Security Checks') {
            when { expression { pipelineEnvironment.skipConfiguration.SECURITY_CHECKS } }
            parallel {
                stage("Checkmarx Scan") {
                    when { expression { pipelineEnvironment.skipConfiguration.CHECKMARX_SCAN } }
                    steps { node('') { stageCheckmarxScan script: this } }
                }
                stage("WhiteSource Scan") {
                    when { expression { pipelineEnvironment.skipConfiguration.WHITESOURCE_SCAN } }
                    steps { node('') { stageWhitesourceScan script: this } }
                }
                stage("Node Security Platform Scan") {
                    when { expression { pipelineEnvironment.skipConfiguration.NODE_SECURITY_SCAN } }
                    steps { node('') { stageNodeSecurityPlatform script: this } }
                }
            }

        }

        stage('Deployment') {
            parallel {
                stage('Production Deployment') {
                    when { expression { pipelineEnvironment.skipConfiguration.PRODUCTION_DEPLOYMENT } }
                    steps { node('') { stageProductionDeployment script: this } }
                }

                stage('Artifact Deployment') {
                    when { expression { pipelineEnvironment.skipConfiguration.ARTIFACT_DEPLOYMENT } }
                    steps { node('') { stageArtifactDeployment script: this } }
                }
            }
        }
    }
    post {
        always{
            script{
                if(pipelineEnvironment.skipConfiguration.SEND_NOTIFICATION){
                    postActionSendNotification script: this
                }
            }
        }
        failure { deleteDir() }
    }
}
