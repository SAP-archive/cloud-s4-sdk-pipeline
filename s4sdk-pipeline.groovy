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
                milestone 10
                library "s4sdk-pipeline-library@${pipelineSdkVersion}"
                stageInitS4sdkPipeline script: this
                abortOldBuilds script: this
            }
        }

        stage('Build') {
            steps {
                milestone 20
                stageBuild script: this
            }
        }

        stage('Local Tests') {
            parallel {
                stage("Static Code Checks") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.STATIC_CODE_CHECKS } }
                    steps { stageStaticCodeChecks script: this }
                }
                stage("Lint") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.LINT } }
                    steps { stageLint script: this }
                }
                stage("Backend Unit Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.BACKEND_UNIT_TESTS } }
                    steps { stageUnitTests script: this }
                }
                stage("Backend Integration Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.BACKEND_INTEGRATION_TESTS } }
                    steps { stageBackendIntegrationTests script: this }
                }
                stage("Frontend Integration Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.FRONTEND_INTEGRATION_TESTS } }
                    steps { stageFrontendIntegrationTests script: this }
                }
                stage("Frontend Unit Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.FRONTEND_UNIT_TESTS } }
                    steps { stageFrontendUnitTests script: this }
                }
                stage("NPM Dependency Audit") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.NPM_AUDIT } }
                    steps { stageNpmAudit script: this }
                }
            }
        }

        stage('Remote Tests') {
            when { expression { commonPipelineEnvironment.configuration.runStage.REMOTE_TESTS } }
            parallel {
                stage("End to End Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.E2E_TESTS } }
                    steps { stageEndToEndTests script: this }
                }
                stage("Performance Tests") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.PERFORMANCE_TESTS } }
                    steps { stagePerformanceTests script: this }
                }
            }
        }

        stage('Quality Checks') {
            when { expression { commonPipelineEnvironment.configuration.runStage.QUALITY_CHECKS } }
            steps {
                milestone 50
                stageS4SdkQualityChecks script: this
            }
        }

        stage('Third-party Checks') {
            when { expression { commonPipelineEnvironment.configuration.runStage.THIRD_PARTY_CHECKS } }
            parallel {
                stage("Checkmarx Scan") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.CHECKMARX_SCAN } }
                    steps { stageCheckmarxScan script: this }
                }
                stage("WhiteSource Scan") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.WHITESOURCE_SCAN } }
                    steps { stageWhitesourceScan script: this }
                }
                stage("SourceClear Scan") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.SOURCE_CLEAR_SCAN } }
                    steps { stageSourceClearScan script: this }
                }
                stage("Fortify Scan") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.FORTIFY_SCAN } }
                    steps { stageFortifyScan script: this }
                }
                stage("Additional Tools") {
                    when { expression { commonPipelineEnvironment.configuration.runStage.ADDITIONAL_TOOLS } }
                    steps { stageAdditionalTools script: this }
                }
                stage('SonarQube Scan'){
                    when { expression { commonPipelineEnvironment.configuration.runStage.SONARQUBE_SCAN } }
                    steps { stageSonarQubeScan script: this }
                }
            }
        }

        stage('Artifact Deployment') {
            when { expression { commonPipelineEnvironment.configuration.runStage.ARTIFACT_DEPLOYMENT } }
            steps {
                milestone 70
                stageArtifactDeployment script: this
            }
        }

        stage('Production Deployment') {
            when { expression { commonPipelineEnvironment.configuration.runStage.PRODUCTION_DEPLOYMENT } }
            //milestone 80 is set in stageProductionDeployment
            steps { stageProductionDeployment script: this }
        }

    }
    post {
        always {
            script {
                postActionArchiveDebugLog script: this
                if (commonPipelineEnvironment?.configuration?.runStage?.SEND_NOTIFICATION) {
                    postActionSendNotification script: this
                }
                postActionCleanupStashesLocks script:this
                sendAnalytics script:this
            }
        }
        success {
            script {
                if (commonPipelineEnvironment?.configuration?.runStage?.ARCHIVE_REPORT) {
                    postActionArchiveReport script: this
                }
            }
        }
        failure { deleteDir() }
    }
}
