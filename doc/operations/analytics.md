# Usage Analytics

This document describes which data is collected by the SAP Cloud SDK Maven Plugin as well as its Continuous Delivery Toolkit.

To learn more about the background, we recommend to read [this blog post](https://blogs.sap.com/2018/10/23/usage-analytics-s4sdk/).

Please note that third party tools used by the SAP Cloud SDK may also collect usage data, which is not covered by this document.
Consult the documentation of these tools, for example, [Jenkins telemetry](https://jenkins.io/blog/2018/10/09/telemetry/).

## Data Collected by Maven Plugin

The following project-specific usage data is collected:
  - Project identifier (SHA-256 hash of root project's groupId + artifactId + optional salt)
  - Operating system information
    - Name
    - Version
    - Locale
  - Java information
    - Vendor
    - Version
  - Maven information
    - Version
  - Compiler information
      - Maven plugin version
      - Source argument (-source)
      - Target argument (-target)
  - SDK depedencies (groupId, artifactId, version)
  - SDK plugins (groupId, artifactId, version)
  - Third-party dependencies (groupId, version) with groupIds that start with: 
    - "org.springframework"
    - "javax"
    - "org.projectlombok"
    - "com.google.guava"
  - Third-party plugins (groupId, version) with groupIds that start with:
    - "org.projectlombok"
  - Project information
    - is compatible with Continuous Delivery Toolkit?
    - is Application Programming Model project?

**Disclaimer:** This list is maintained manually and may therefore not always reflect the latest state of data collection.
You can check which information is transmitted by considering the log output of the Maven plugin.
```
[INFO] SAP Cloud SDK - Usage Analytics
[INFO] 
[INFO] Thank you for contributing to our anonymized usage statistics!
[INFO] This allows us to improve the SAP Cloud SDK based on your usage.
[INFO] 
[INFO] We respect your privacy and intellectual property. Therefore, we only
[INFO] collect non-sensitive data about the use of the SDK. We do not collect
[INFO] personal information or data about the inner workings of your project.
[INFO] 
[INFO] If you prefer to opt out or want to learn more, visit:
[INFO] https://blogs.sap.com/2018/10/23/usage-analytics-s4sdk/
[INFO] 
[INFO] Project identifier:
[INFO]   17ed01aa4e6291157fc684cc2c19e00802b6195919176a011562390c2999efd5
[INFO]   (salted hash of project's groupId and artifactId)
[INFO] 
[INFO] Operating System:  Mac OS X, 10.14.1, en_US
[INFO] Java version:      1.8.0_192, Oracle Corporation
[INFO] Maven version:     3.6.0
[INFO] Compiler:          plugin 3.1, source (n/a), target (n/a)
[INFO] 
[INFO] SDK artifacts:
[INFO]   cloudplatform:scp-cf:2.8.1
[INFO]   plugins:s4sdk-maven-plugin:2.8.1
[INFO]   quality:listeners-all:2.8.1
[INFO]   s4hana-all:2.8.1
[INFO]   testutil:2.8.1
[INFO] 
[INFO] Relevant third party artifacts:
[INFO]   javax.inject:1
[INFO]   javax.servlet:3.1.0
[INFO] 
[INFO] Project structure:
[INFO]   [x] compatible with Continuous Delivery Toolkit
[INFO]   [ ] Application Programming Model project
[INFO] 
[INFO] Sending usage data to SAP analytics service ...
[INFO] Success (0.286 s).
```
Furthermore, you can use the `-X` debug flag of Maven to inspect the request that is sent to the SAP analytics service.

## Data Collected by Build Pipeline

* Name of the activity, for example
    * _Pipeline [Step]_
    * _Cx Server [Lifecycle phase]_
    * _Maven Archetype generate_
* Hash of Maven group id + artifact id (If configured in the file `application/pom.xml`, the hash is salted, see [this blog post](https://blogs.sap.com/2018/10/23/usage-analytics-s4sdk/) for details)
* Hash of the Jenkins job URL and the Jenkins job ID
* Repository branch type ("productive" or "non-productive")
* Configuration parameters of the pipeline step, for example
    * Docker image
* Versions of used artifacts
    * Continuous Delivery Server
    * SAP Cloud SDK Pipeline
    * SAP Cloud SDK Java libraries
* Outcome of the action (success or failure)
* Operating system information (name, version, locale, ..)

Please note that this additionally includes the analytics done by SAP/jenkins-library, [see here](https://sap.github.io/jenkins-library/configuration/#collecting-telemetry-data) for details.

**Disclaimer:** This list is maintained manually and may therefore not always reflect the latest state of data collection.
You can check which information is transmitted by searching the log of your Jenkins jobs for the string `Sending telemetry data`.
Please consider the following example log message:
```
18:02:54 Sending telemetry data: [event_type:pipeline_stage, custom3:stage_name, e_3:initS4sdkPipeline, custom4:stage_result, e_4:SUCCESS, custom5:start_time, e_5:1541437347881, custom6:duration, e_6:26677, custom7:project_extensions, e_7:false, custom8:global_extensions, e_8:false, action_name:SAP S/4HANA Cloud SDK, idsite:2ff12ff4-f7cc-e3a0-1eca-eec552a3d077, idsitesub:pipeline, url:https://github.com/SAP/cloud-s4-sdk-pipeline/tree/master/doc/operations/analytics.md, custom1:build_url_hash, e_a:86648dd02153fedb42d8b293060573bb513a6d11, custom10:build_number, e_10:3db762cb24aeeb39e8b6c92ef365c48560240538, custom2:project_id_hash, e_2:693db57a02e56cf24823bc5be20c9e7767042285]
```

## Opt-Out

Collection of usage data is enabled by default.

If you wish to disable it, please perform the following steps:

For both the SAP Cloud SDK Pipeline and SAP/jenkins-library, set `collectTelemetryData` to `false` in your `pipeline_config.yml` in the general section as in this example:

```
general:
  collectTelemetryData: false
```

In your Maven projects, set the `skipUsageAnalytics` flag in the configuration of the `s4sdk-maven-plugin` to `true`:

```
<plugin>
    <groupId>com.sap.cloud.s4hana.plugins</groupId>
    <artifactId>s4sdk-maven-plugin</artifactId>
    ...
    <configuration>
        <skipUsageAnalytics>true</skipUsageAnalytics>
    </configuration>
    ...
</plugin>
```
