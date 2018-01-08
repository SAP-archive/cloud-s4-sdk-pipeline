## Pipeline configuration

The SAP S/4HANA Cloud SDK Pipeline can be configured via the `pipeline_config.yml` file, which needs to reside in the root of a project.
To adjust the SAP S/4HANA Cloud SDK Pipeline to your project's needs, it can be customized on multiple levels. This comprises:
 * the general configuration on project level,
 * the stage level configurations to set configuration values for specific stages,
 * the step configurations to set default values for steps,
 * and the post action configurations to configure post build behavior.

 If a property is configured on step as well as stage level, the stage level value takes precedence.

### General configuration

| Property | Default Value | Description |
| --- | --- | --- |
| `productiveBranch` | master | The name of your default branch. This branch will be used for deploying your application. Other branches will skip deployment. |

#### features

This section allows to enable or disable certain optional features.
This concept is known as *Feature Toggles*.

| Property | Default Value | Description |
| --- | --- | --- |
| `parallelTestExecution` | `off` | Run E2E Tests in parallel. This feature is disabled by default because it is [not supported](https://issues.jenkins-ci.org/browse/JENKINS-38442) in Blue Ocean. If this feature is enabled, we suggest not using the Blue Ocean interface and rely on the classic UI instead. |

```yaml
general:
  productiveBranch: 'master'
  projectName: 'My_Project'
  features:
    parallelTestExecution: on
```

### Stage configuration

#### buildBackend

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The docker image to be used for building the application backend. **Note:** This will only change the docker image used for building the backend. Tests and other maven based stages will still use their individual default values. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |

#### buildFrontend

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The docker image to be used for building the application frontend. **Note:** This will only change the docker image used for building the frontend. End to end tests and other npm based stages will still use their individual default values. For switching all npm based steps to a different npm or chromium version, you should configure the dockerImage via the executeNpm step. |

#### staticCodeChecks

| Property | Default Value | Description |
| --- | --- | --- |
| `pmdExcludes` | | A comma separated list of exclusions expressed as an [Ant style pattern](http://ant.apache.org/manual/dirtasks.html#patterns) relative to the application folder. Example: `src/main/java/generated/**` |
| `findbugsExcludesFile` | | Path to a [FindBugs XML exclusion file](http://findbugs.sourceforge.net/manual/filter.html) relative to the application folder. |

#### unitTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The docker image to be used for running unit tests. **Note:** This will only change the docker image used for executing the unit tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |

#### integrationTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The docker image to be used for running integration tests. **Note:** This will only change the docker image used for executing the integration tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |
| `retry` | 1 | The amount of maximal times that integration tests will try before aborting the build. **Note:** This will consume more time for the jenkins build. |
| `forkCount` | 1C | The number of JVM processes that are spawned to run the tests in parallel. |
| `credentials` | | The list of system credentials to be injected during integration tests. The following example will provision the username and password for the systems with the aliases ERP and SFSF. For this, it will use the Jenkins credentials entries erp-credentials and successfactors-credentials. You have to ensure that corresponding credential entries exist in your Jenkins configuration |

Example for `credentials`:
```yaml
credentials:
  - alias: 'ERP'
    credentialId: 'erp-credentials'
  - alias: 'SFSF'
    credentialId: 'successfactors-credentials'
```

#### frontendUnitTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The docker image to be used for running frontend unit tests. **Note:** This will only change the docker image used for unit testing in the frontend. For switching all npm based steps to a different npm or chromium version, you should configure the dockerImage via the executeNpm step. |

#### endToEndTests

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The CloudFoundry deployment targets to be used for running the end to end tests. |
| `neoTargets` | | The Neo deployment targets to be used for running the end to end tests. |
| `appUrls` | |  The URLs under which the app is available after deployment. Each appUrl can be a string with the URL or a map containing a property url and a property credentialId as shown below  |

Example for target defintions:
```yaml
cfTargets:
  - org: 'MyOrg'
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

Example for appUrls defintion with credentials:
```yaml
appUrls:
 - url: 'https://approuter.cfapps.hana.ondemand.com'
   credentialId: e2e-test-user
```
#### performanceTests

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The list of CloudFoundry deployment targets required for the performance test stage. |
| `neoTargets` | | The list of Neo deployment targets required for the performance test stage. |

#### s4SdkQualityChecks

| Property | Default Value | Description |
| --- | --- | --- |
| `jacocoExcludes` | | A list of exclusions expressed as an [Ant style pattern](http://ant.apache.org/manual/dirtasks.html#patterns) relative to the application folder. An example can be found below.|
| `nonErpDestinations` | | List of destination names that do not refer to ERP systems. Use this parameter to exclude specific destinations from being checked in context of ERP API whitelists. |

Example of jacocoExcludes:
```yaml
  s4SdkQualityChecks:
    jacocoExcludes:
      - '**/HelloWorld.class'
      - '**/generated/**'
``` 

#### productionDeployment

| Property | Default Value | Description |
| --- | --- | --- |
| `cfTargets` | | The list of productive CloudFoundry deployment targets to be deployed when a build of your productive branch succeeds. |
| `neoTargets`| | The list of productive Neo deployment targets to be deployed when a build of your productive branch succeeds. |
| `appUrls` | |  The URLs under which the app is available after deployment. Each appUrl can be a string with the URL or a map containing a property url and a property credentialId. An example is shown in the configuration for the stage endToEndTests. |

#### artifactDeployment

##### nexus

 The deployment of artifacts to nexus can be configured with a map containing the following properties:

| Property | Default Value | Description |
| --- | --- | --- |
| `version`| nexus3 | Version of nexus. Can be `nexus2` or `nexus3`. |
| `url` | | URL of the nexus. The scheme part of the URL will not be considered, because only `http` is supported. |
| `repository` | | Name of the nexus repository. |
| `additionalClassifiers` | | List of additional classifiers that should be deployed to nexus. Each item is a map of a type and a classifier name.|

Example
```yaml
artifactDeployment:
  nexus:
    version: nexus2
    url: nexus.mycorp:8080/nexus
    repository: snapshots
    additionalClassifiers:
      - type: jar
        classifier: classes
```
### Step configuration

#### executeMaven

The executeMaven step is used for all invocations of the mvn build tool. It is either used directly for executing specific maven phases such as `test`, or indirectly for steps that execute maven plugins such as `checkPmd`.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The image to be used for executing maven commands. |
| `globalSettingsFile` | | The global settings.xml to be used for maven builds. You can specify a relative path to your project root or a URL starting with http or https. |
| `projectSettingsFile` | | The project settings.xml to be used for maven builds. You can specify a relative path to your project root or a URL starting with http or https. |

#### executeNpm

The executeNpm step is used for all invocations of the npm build tool. It is, for example, used for building the frontend and for executing end to end tests.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The image to be used for executing npm commands. |


### Post action configuration

#### sendNotifications

The `sendNotifications` post build action can be used to send notifications to project members in case of a unsuccessful build outcome or if the build goes back to normal.
By default, an email is sent to the list of users who committed a change since the last non-broken build. Additionally, a set of recipients can be defined that should always receive notifications.

| Property | Default Value | Description |
| --- | --- | --- |
| `enabled` | false | If set to `true`, notifications will be sent. |
| `skipFeatureBranches` | false | If set to `true`, notifications will only be sent for the productive branch as defined in the general configuration section. |
| `recipients` | | List of email adresses that should be notified in addition to the standard recipients. |

Example for `sendNotifications`: 
```yaml
postActions:
  enabled: true
  skipFeatureBranches: true
  recipients:
    - ryan.architect@foobar.com
    - john.doe@foobar.com
```
