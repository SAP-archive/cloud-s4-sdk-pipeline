# Zero Downtime Deployment for End-To-End Tests

## Status

Accepted

## Context

In the End-To-End Tests stage of the SAP S/4HANA Cloud SDK Pipeline the newly built application will be deployed to SCP Neo / SCP Cloud Foundry before executing the End-To-End Tests. 

Some consumers of the SAP S/4HANA Cloud SDK Pipeline are working in one single test landscape for all of their microservices with one instance per microservice.
Deploying multiple services at once in this scenario can cause issues when running End-To-End Tests, because a dependency of the tested service might not be available.

### Decision

We provide the possibility to activate Zero Downtime Deployment in the End-To-End Tests.
This feature is disabled by default.

## Consequences

* The pipeline becomes more complex
* When using one test landscape, separate teams can work on their microservices without affecting other teams depending on their microservice
* Zero Downtime Deployment needs more resources (e.g. memory, routes)
