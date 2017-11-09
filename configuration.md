## Pipeline configuration

To adjust the SAP S/4HANA Cloud SDK Pipeline to your project's needs, it can be customized on multiple levels. Currently, this comprises a general configuration on project level, a step configuration to set default values for steps, and a stage level to set configuration values for a specific stage. If a property is configured on step as well as stage level, the stage level value takes precedence.

### General configuration

| Property | Default Value | Description |
| --- | --- | --- |
| `productiveBranch` | master | The name of your default branch. This branch will be used for deploying your application. Other branches will skip deployment. |

### Stage configuration

#### buildBackend

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-7-alpine | The docker image to be used for building the application backend. **Note:** This will only change the docker image used for building the backend. Tests and other maven based stages will still use their individual default values. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |

#### buildFrontend

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The docker image to be used for building the application frontend. **Note:** This will only change the docker image used for building the frontend. End to end tests and other npm based stages will still use their individual default values. For switching all npm based steps to a different npm or chromium version, you should configure the dockerImage via the executeNpm step. |

#### staticCodeChecks

| Property | Default Value | Description |
| --- | --- | --- |
| `pmdExcludes` | | A comma separated list of exclusions expressed as an [Ant style pattern](http://ant.apache.org/manual/dirtasks.html#patterns) relative to the application folder. Example: `src/main/java/generated/**` |
| `findbugsExcludesFile` | | Path to a [FindBugs XML exclusion file](http://findbugs.sourceforge.net/manual/filter.html) relative to the application folder. |

#### unitTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-7-alpine | The docker image to be used for running unit tests. **Note:** This will only change the docker image used for executing the unit tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |

#### integrationTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-7-alpine | The docker image to be used for running integration tests. **Note:** This will only change the docker image used for executing the integration tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |
| `credentials` | | The list of system credentials to be injected during integration tests. The following example will provision the username and password for the systems with the aliases ERP and SFSF. For this, it will use the Jenkins credentials entries erp-credentials and successfactors-credentials. You have to ensure that corresponding credential entries exist in your Jenkins configuration |

Example for `credentials`:
```
credentials:
  - alias: 'ERP'
    credentialId: 'erp-credentials'
  - alias: 'SFSF'
    credentialId: 'successfactors-credentials'
```

#### frontendUnitTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The docker image to be used for running frontend unit tests. **Note:** This will only change the docker image used for unit testing in the frontend. For switching all npm based steps to a different npm or chromium version, you should configure the dockerImage via the executeNpm step. |

#### endToEndTests

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The CloudFoundry deployment targets to be used for running the end to end tests. |
| `neoTargets` | | The Neo deployment targets to be used for running the end to end tests. |
| `appUrls` | |  The URLs under which the app is available after deployment. |

Example for target defintions:
```
cfTargets:
  -  org: 'MyOrg'
    space: 'Test'
    apiEndpoint: 'https://api.cf.sap.hana.ondemand.com'
    appName: 'testapp'
    manifest: 'manifest-test.yml'
    credentialsId: 'deploy-test'
    deploymentType: 'standard'

neoTargets:
  - host: 'int.sap.hana.ondemand.com'
    account: 'x5e5e111a'
    application: 'testapp'
    credentialsId: 'deploy-test'
      ev:
        - 'STAGE=Production'
      vmArguments: '-Dargument1=value1 -Dargument2=value2'
      runtime: 'neo-javaee6-wp'
      runtimeVersion: '2'
```

#### performanceTests

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The list of CloudFoundry deployment targets required for the performance test stage. |
| `neoTargets` | | The list of Neo deployment targets required for the performance test stage. |

#### s4SdkQualityChecks

| Property | Default Value | Description |
| --- | --- | --- |
| `jacocoExcludes` | | The list of classes that should be excluded from the code coverage check, e.g. generated classes. |
| `nonErpDestinations` | | List of destination names that do not refer to ERP systems. Use this parameter to exclude specific destinations from being checked in context of ERP API whitelists. |

#### productionDeployment

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The list of productive CloudFoundry deployment targets to be deployed when a build of your productive branch succeeds. |
| `neoTargets`| | The list of productive Neo deployment targets to be deployed when a build of your productive branch succeeds. |

### Step configuration

#### executeMaven

The executeMaven step is used for all invocations of the mvn build tool. It is either used directly for executing specific maven phases such as `test`, or indirectly for steps that execute maven plugins such as `checkPmd`.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-7-alpine | The image to be used for executing maven commands. |
| `globalSettingsFile` | | The global settings.xml to be used for maven builds. You can specify a relative path to your project root or a URL starting with http or https. |
| `projectSettingsFile` | | The project settings.xml to be used for maven builds. You can specify a relative path to your project root or a URL starting with http or https. |

#### executeNpm

The executeNpm step is used for all invocations of the npm build tool. It is, for example, used for building the frontend and for executing end to end tests.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The image to be used for executing npm commands. |
