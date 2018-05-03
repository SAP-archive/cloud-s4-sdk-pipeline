#!/usr/bin/env groovy

final def pipelineSdkVersion = 'master'

pipeline {
    agent any
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
                stageInitS4sdkPipeline script: this
                abortOldBuilds script: this
            }
        }

        stage('Build') {
            parallel {
                stage("Backend") { steps { stageBuildBackend script: this } }
                stage("Frontend") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.FRONT_END_BUILD } }
                    steps { stageBuildFrontend script: this }
                }
            }
        }

        stage('Local Tests') {
            parallel {
                stage("Static Code Checks") { steps { stageStaticCodeChecks script: this } }
                stage("Backend Unit Tests") { steps { stageUnitTests script: this } }
                stage("Backend Integration Tests") { steps { stageIntegrationTests script: this } }
                stage("Frontend Unit Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.FRONT_END_TESTS } }
                    steps { stageFrontendUnitTests script: this }
                }
                stage("Node Security Platform Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.NODE_SECURITY_SCAN } }
                    steps { stageNodeSecurityPlatform script: this }
                }
            }
        }

        stage('Remote Tests') {
            when { expression { commonPipelineEnvironment.configuration.skipping.REMOTE_TESTS } }
            parallel {
                stage("End to End Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.E2E_TESTS } }
                    steps { stageEndToEndTests script: this }
                }
                stage("Performance Tests") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.PERFORMANCE_TESTS } }
                    steps { stagePerformanceTests script: this }
                }
            }
        }

        stage('Quality Checks') {
            steps { stageS4SdkQualityChecks script: this }
        }

        stage('Third-party Checks') {
            when { expression { commonPipelineEnvironment.configuration.skipping.THIRD_PARTY_CHECKS } }
            parallel {
                stage("Checkmarx Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.CHECKMARX_SCAN } }
                    steps { stageCheckmarxScan script: this }
                }
                stage("WhiteSource Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.WHITESOURCE_SCAN } }
                    steps { stageWhitesourceScan script: this }
                }
                stage("SourceClear Scan") {
                    when { expression { commonPipelineEnvironment.configuration.skipping.SOURCE_CLEAR_SCAN } }
                    steps { stageSourceClearScan script: this }
                }
            }

        }

        stage('Artifact Deployment') {
            when { expression { commonPipelineEnvironment.configuration.skipping.ARTIFACT_DEPLOYMENT } }
            steps { stageArtifactDeployment script: this }
        }

        stage('Production Deployment') {
            when { expression { commonPipelineEnvironment.configuration.skipping.PRODUCTION_DEPLOYMENT } }
            steps { stageProductionDeployment script: this }
        }

    }
    post {
        always {
            script {
                if (commonPipelineEnvironment.configuration.skipping.SEND_NOTIFICATION) {
                    postActionSendNotification script: this
                }
            }
        }
        failure { deleteDir() }
    }
}
