# Sidecar-Container in Integration-Tests

## Status

Accepted

## Context
Some projects need downstream systems, e.g. a database, for their Integration-tests.
To enable tests with a downstream system in a containerized Jenkins environment it is required to spin up sidecar-containers on demand.
Jenkins supports sidecar-containers with help of the [docker-workflow-plugin](https://github.com/jenkinsci/docker-workflow-plugin). There is also an official [example](https://jenkins.io/doc/book/pipeline/docker/) how to use sidecar-containers in Jenkins.
Unfortunately with the current implementation of the plugin it is not possible to connect the sidecar-container to multiple docker networks.
An [issue](https://issues.jenkins-ci.org/browse/JENKINS-56561) has already been created in Jenkins JIRA.

## Decision
To provide the consumer of the pipeline the possibility to use sidecar-containers in their pipelines (in docker as well as kubernetes environments) we decided to use the already available functionality of the `dockerExecute` step provided by [jenkins-library](https://github.com/SAP/jenkins-library).

## Consequences

* Only zero or one sidecar container is supported
* Download cache must be disabled if a sidecar container is configured in integration tests
