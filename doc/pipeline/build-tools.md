# Build Tools

The SAP S/4HANA Cloud SDK supports multiple programming languages (Java and JavaScript) and can be used in the SAP Cloud Application Programming Model.
For each of these variants project templates exists.
These templates introduce standard tooling, such as build tools, and a standard structure.

The SAP S/4HANA Cloud SDK Continuous Delivery Toolkit expects that the project follows this structure and depends on the build tools introduced by these templates.

The supported build tools are: [Maven](https://maven.apache.org/) for Java projects, [NPM](https://www.npmjs.com/) for JavaScript projects, and [MTA](https://help.sap.com/viewer/4505d0bdaf4948449b7f7379d24d0f0d/2.0.03/en-US/4486ada1af824aadaf56baebc93d0256.html) for SAP Cloud Application Programming Model.

*Note: The JavaScript/NPM pipeline variant is in an early state. Some interfaces might change. We recommend to consume a fixed released version as described in the project [Readme](../../README.md#versioning).*

## Feature Matrix

Support for the different features of the pipeline may vary in each variant of the SDK pipeline build tool. The following table gives an overview over the features available per build tool.

| Feature                   | Maven | NPM | MTA |
| ------------------------- |------ | --- | --- |
| Automatic Versioning      |   x   |     |  x  |
| Build                     |   x   |  x  |  x  |
| Backend Integration Tests |   x   |  x  |  x  |
| Backend Unit Tests        |   x   |  x  |  x  |
| Frontend Unit Tests       |   x   |  x  |  x  |
| NPM Dependency Audit      |   x   |  x  |  x  |
| Linting                   |   x   |     |  x  |
| Static Code Checks        |   x   |     |  x  |
| End-To-End Tests          |   x   |     |  x  |
| Performance Tests         |   x   |     |  x  |
| Resilience Checks         |   x   |     |  x  |
| S4HANA Public APIs        |   x   |     |  x  |
| CodeCoverage Checks       |   x   |  x  |  x  |
| Checkmarx Integration     |   x   |     |  x  |
| Fortify Integration       |   x   |     |     |
| SourceClear Integration   |   x   |     |     |
| Whitesource Integration   |   x   |     |  x  |
| Deployment to Nexus       |   x   |     |  x  |
| Zero Downtime Deployment  |   x   |  x  |  x  |
| Download Cache            |   x   |  x  |  x  |

*Note: The MTA version of the pipeline currently supports only Java based backend services.*

## Projects Requirements

Each variant of the pipeline has different requirements regarding the project structure, location of reports and tooling.

Stages not listed here do not have a special requirements.
In any case, please also consult the [documentation of the pipeline configuration](../../configuration.md), as some stages have to be activated by providing configuration values.

### Java / Maven

For Maven the pipeline expects the following structure.
The project should have three maven modules named:

- `application`
- `unit-tests`
- `integration-tests`

The module `application` should contain the application code.
The modules `unit-tests` and `integration-tests` should contain the corresponding tests.
Details about the reasoning behind this structure can be found in the corresponding [architecture decision record](../architecture/decisions/project-structure.md).

Furthermore, the test modules have to include the following dependency:

```xml
<dependency>
    <groupId>com.sap.cloud.s4hana.quality</groupId>
    <artifactId>listeners-all</artifactId>
    <scope>test</scope>
</dependency>
```

### JavaScript / NPM

The project has to use NPM and include a package.json in the root directory.
In the pipeline stages, specific scripts in the package.json are called to build the project or run tests.
Furthermore, the pipeline expects reports, such as test results, to be written into certain folders.
These stage specific requirements are documented below.

#### Build

By default `npm ci` will be executed.
After `npm ci` the command  `npm  run ci-build` will be executed.
This script can be used to, for example, compile Typescript resources or webpack the frontend.
In the build stage, also development dependencies are installed and tests should also be compiled.

Afterwards the command `npm run ci-package` will be executed.
This step should prepare the deployment by copying all deployment relevant files into the folder `deployment` located in the root of the project.
This folder should not contain any non-production-related resources, such as tests or development dependencies.
This directory has to be defined as path in the `manifest.yml`.

*Note: This steps runs isolated from the steps before. Thus, e.g. modifying node_modules with `npm prune --production` will not have an effect for later stages, such as the test execution.*

#### Backend Integration Tests

The command `npm run ci-integration-test` will be executed in this stage.
Furthermore, the test results have to be stored in the folder `./s4hana_pipeline/reports/backend-integration` in the root directory of the project.
The required format of the test result report is the JUnit format as an `.xml` file.
For the code coverage the results have to be stored in the folder `./s4hana_pipeline/reports/coverage-reports/backend-integration/` in the cobertura format as an `xml` file.
The user is responsible to use a proper reporters for generating the results.
We recommend the tools used in the `package.json` of this [example project](https://github.com/SAP/cloud-s4-sdk-examples/blob/scaffolding-js/package.json).

#### Backend Unit Tests

The command `npm run ci-backend-unit` will be executed in this stage.
Furthermore, the test results have to be stored in the folder `./s4hana_pipeline/reports/backend-unit/` in the root directory of the project.
The required format of the test result report is the JUnit format as an `.xml` file.
For the code coverage the results have to be stored in the folder `./s4hana_pipeline/reports/coverage-reports/backend-unit/` in the cobertura format as an `xml` file.
The user is responsible to use a proper reporters for generating the results.
We recommend the tools used in the `package.json` of this [example project](https://github.com/SAP/cloud-s4-sdk-examples/blob/scaffolding-js/package.json).

### Build Tool Independent Requirements

#### Frontend Unit Tests

The command `npm run ci-frontend-unit-test` will be executed in this stage.
Furthermore, the test results have to be stored in the folder `./s4hana_pipeline/reports/frontend-unit` in the root directory.
The required format of the test result report is the JUnit format as an `.xml` file.
The code coverage report has to be stored in the directory `./s4hana_pipeline/reports/frontend-unit/coverage/report-html/ut/` as an `index.html` file.
This coverage report will then be published in Jenkins.
The user is responsible to use a proper reporter for generating the results.

#### End-to-End Tests

This stage is only executed if you configured it in the file `pipeline_config.yml`.

The command `npm run ci-e2e` will be executed in this stage.
The url which is defined as `appUrl` in the file `pipeline_config.yml` will be passed as argument named `launchUrl` to the tests.
This can be reproduced locally by executing:

```
npm run ci-e2e -- --launchUrl=https://path/to/your/running/application
```

The credentials also defined in the file `pipeline_config.yml` will be available during the test execution as environment variables named `e2e_username` and `e2e_password`.

The test results have to be stored in the folder `./s4hana_pipeline/reports/e2e` in the root directory.
The required format of the test result report is the Cucumber format as an `.json` file.
Also screenshots can be stored in this folder
The screenshots and reports  will then be published in Jenkins.
The user is responsible to use a proper reporter for generating the results.

#### Performance Tests

This stage is only executed if you configured it in the file `pipeline_config.yml`.

Performance tests can be executed using [JMeter](https://jmeter.apache.org/) or [Gatling](https://gatling.io/).

If only JMeter is used as a performance tests tool then test plans can be placed in a default location, which is the directory `{project_root}/performance-tests`. However, if JMeter is used along with Gatling, then JMeter test plans should be kept in a subdirectory under a directory `performance-tests` for example`./performance-tests/JMeter/`.

The gatling test project including the `pom.xml` should be placed in the directory `{project_root}/performance-tests`.
Afterwards, Gatling has to be enable in the configuration.

#### Deployments

For all deployments to Cloud Foundry (excluding MTA) there has to be a file called `manifest.yml`.
This file may only contain exactly one application.
*Note: For JavaScript projects the path of the application should point to the folder `deployment`.*
