# Usage Analytics

This document describes which data is collected by the s4sdk-maven-plugin as well as the Continuous Delivery Toolkit.

Please note that this additionally includes the analytics done by SAP/jenkins-library, [see here](https://sap.github.io/jenkins-library/configuration/#collecting-telemetry-data) for details.

## Data Collected by Maven plugin

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

## Data Collected by Build Pipeline

* Name of the activity, for example
    * _Pipeline [Step]_
    * _Cx Server [Lifecycle phase]_
    * _Maven Archetype generate_
* Hash of Maven group id + artifact id (refer to [section hashing](#hashing) for details)
* Hash of the Jenkins job URL and the Jenkins job ID(refer to [section hashing](#hashing) for details)
* Repository branch type ("productive" or "non-productive")
* Configuration parameters of the pipeline step, for example
    * Docker image
* Versions of used artifacts
    * Continuous Delivery Server
    * SAP S/4HANA Cloud SDK Pipeline
    * SAP S/4HANA Cloud SDK Java libraries
* Outcome of the action (success or failure)
* Operating system information (name, version, locale, ..)

## Opt-out

To opt out from usage data collection, follow the guide [here](https://blogs.sap.com/2018/10/23/usage-analytics-s4sdk/).
