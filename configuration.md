## Pipeline configuration

The SAP S/4HANA Cloud SDK Pipeline can be configured via the `pipeline_config.yml` file, which needs to reside in the root of a project.
To adjust the SAP S/4HANA Cloud SDK Pipeline to your project's needs, it can be customized on multiple levels. This comprises:
 * the general configuration on the project level,
 * the stage level configurations to set configuration values for specific stages,
 * the step configurations to set default values for steps,
 * and the post action configurations to configure post-build behavior.

 If a property is configured on a step as well as the stage level, the stage level value takes precedence.

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
| `pmdExcludes` | | A comma-separated list of exclusions expressed as an [Ant-style pattern](http://ant.apache.org/manual/dirtasks.html#patterns) relative to the application folder. Example: `src/main/java/generated/**` |
| `findbugsExcludesFile` | | Path to a [FindBugs XML exclusion file](http://findbugs.sourceforge.net/manual/filter.html) relative to the application folder. |

#### unitTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The docker image to be used for running unit tests. **Note:** This will only change the docker image used for executing the unit tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |

#### integrationTests

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The docker image to be used for running integration tests. **Note:** This will only change the docker image used for executing the integration tests. For switching all maven based steps to a different maven or JDK version, you should configure the dockerImage via the executeMaven step. |
| `retry` | 1 | The number of times that integration tests will retry before aborting the build. **Note:** This will consume more time for the jenkins build. |
| `forkCount` | 1C | The number of JVM processes that are spawned to run the tests in parallel. |
| `credentials` | | The list of system credentials to be injected during integration tests. The following example will provide the username and password for the systems with the aliases ERP and SFSF. For this, it will use the Jenkins credentials entries erp-credentials and successfactors-credentials. You have to ensure that corresponding credential entries exist in your Jenkins configuration |

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

Example for target definitions:
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

Example for appUrls definitions with credentials:
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
| `jacocoExcludes` | | A list of exclusions expressed as an [Ant-style pattern](http://ant.apache.org/manual/dirtasks.html#patterns) relative to the application folder. An example can be found below.|
| `nonErpDestinations` | | List of destination names that do not refer to ERP systems. Use this parameter to exclude specific destinations from being checked in context of ERP API whitelists. |

Example of jacocoExcludes:
```yaml
s4SdkQualityChecks:
  jacocoExcludes:
    - '**/HelloWorld.class'
    - '**/generated/**'
``` 
#### checkmarxScan
[Checkmarx](https://www.checkmarx.com/) is one of the security analysis tools which is supported by the  pipeline. 
| Property | Default Value | Description |
| --- | --- | --- |
| `groupId` | | Checkmarx Group ID|
| `checkMarxProjectName` | | Name of the project on Checkmarx server.|
| `filterPattern` |`!**/*.log, !**/*.lock, !**/*.json, !**/*.html, !**/Cx*, **/*.js, **/*.java, **/*.ts`| Files which needs to be skipped during scanning.|
| `fullScansScheduled` | false| Toggle to enable or disable full scan on a certain schedule.|
| `incremental` | true| Perform incremental scan with every run. If turned `false`, complete project is scanned on every submission.|
| `vulnerabilityThresholdMedium` |0| The threshold for medium level threats. If the findings are greater than this value, pipeline execution will result in failure.|
| `vulnerabilityThresholdLow` |99999| The threshold for low level threats. If the findings are greater than this value, pipeline execution will result in failure.|
| `preset` |36| A predefined set of that can be executed on the project. You can configure this value in *Checkmarx->Management->Scan Settings-> Preset Manager*.|
| `checkmarxCredentialsId` | | The Credential ID to connect to Checkmarx server.|
| `checkmarxServerUrl` | | An URL to Checkmarx server.|

Example Checkmarx configuration
```yaml
checkmarxScan:
  groupId: '898faddf-9dca-0ce4-2221-5ba1e873a7a6'
  vulnerabilityThresholdMedium: 5
  checkMarxProjectName: 'My_Application'
  vulnerabilityThresholdLow: 999999
  filterPattern: '!**/*.log, !**/*.lock, !**/*.json, !**/*.html, !**/Cx*, **/*.js, **/*.java, **/*.ts'
  fullScansScheduled: false
  generatePdfReport: true
  incremental: true
  preset: '36'
  checkmarxCredentialsId: CHECKMARX-SCAN
  checkmarxServerUrl: http://localhost:8089
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

#### whitesourceScan

Configure credentials for [WhiteSource](https://www.whitesourcesoftware.com/) scans.

| Property | Default Value | Description |
| --- | --- | --- |
| `product` | | Name of your product in WhiteSource. |
| `orgToken` | | Unique identifier of your organization in WhiteSource, also referred to as 'API Token'. |

Please note that this configuration only affects the backend project.
For the frontend, a `whitesource.config.json` file needs to be present.
See the [official documentation](https://www.whitesourcesoftware.com/how-to-install-npm/) for more info.

### Step configuration

#### executeMaven

The executeMaven step is used for all invocations of the mvn build tool. It is either used directly for executing specific maven phases such as `test`, or indirectly for steps that execute maven plugins such as `checkPmd`.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | maven:3.5-jdk-8-alpine | The image to be used for executing maven commands. |
| `projectSettingsFile` | | The project settings.xml to be used for maven builds. You can specify a relative path to your project root or a URL starting with http or https. |

#### executeNpm

The executeNpm step is used for all invocations of the npm build tool. It is, for example, used for building the frontend and for executing end to end tests.

| Property | Default Value | Description |
| --- | --- | --- |
| `dockerImage` | s4sdk/docker-node-chromium | The image to be used for executing npm commands. |
| `defaultNpmRegistry` | | The default npm registry url to be used as the remote mirror. Bypasses the local download cache if specified.  |

#### checkFindbugs
[FindBugs](http://findbugs.sourceforge.net/) static code analysis is executed as part of the static code checks. 

| Property | Default Value | Description |
| --- | --- | --- |
| `includeFilterFile` | s4hana_findbugs_include_filter.xml | Bug definition filter file. |

#### checkJMeter
[Apache JMeter](http://jmeter.apache.org/) is executed as part of performance tests of the application. The user is free to choose between JMeter and Gatling or both.

| Property | Default Value | Description |
| --- | --- | --- |
| `options` |  | Options such as proxy. |
| `testPlan` | `./performance-tests/*` | The directory where the test plans reside. Should reside in a subdirectory under `performance-tests` directory if both JMeter and Gatling are enabled.|
| `dockerImage` | `famiko/jmeter-base` | JMeter docker image. |
| `failThreshold ` | `100` | Marks build as `FAILURE` if the value exceeds the threshold. |
| `unstableThreshold ` | `90` | Marks build as `UNSTABLE` if the value exceeds the threshold. |

Example configuration file

```yaml
checkJMeter:
  options: '-H my.proxy.server -P 8000'
  testPlan: './performance-tests/JMeter/*' # mandatory parameter if both JMeter and gatling are enabled
  dockerImage: 'famiko/jmeter-base'
  failThreshold : 80
  unstableThreshold: 70
```
### Post action configuration

#### sendNotification

The `sendNotification` post-build action can be used to send notifications to project members in case of an unsuccessful build outcome or if the build goes back to normal.
By default, an email is sent to the list of users who committed a change since the last non-broken build. Additionally, a set of recipients can be defined that should always receive notifications.

| Property | Default Value | Description |
| --- | --- | --- |
| `enabled` | false | If set to `true`, notifications will be sent. |
| `skipFeatureBranches` | false | If set to `true`, notifications will only be sent for the productive branch as defined in the general configuration section. |
| `recipients` | | List of email addresses that should be notified in addition to the standard recipients. |

Example for `sendNotification`: 
```yaml
postActions:
  sendNotification:
    enabled: true
    skipFeatureBranches: false
    recipients:
    - ryan.architect@foobar.com
    - john.doe@foobar.com
```
