# Checked Qualities in the SAP S/4HANA Cloud SDK Pipeline

The goal of the SAP S/4HANA Cloud SDK Pipeline is to help you build high quality applications which run on SAP Cloud Platform.
To achieve this, the SAP S/4HANA Cloud SDK Pipeline checks qualities when building your application.
This document summarizes the qualities that are checked by the SAP S/4HANA Cloud SDK Pipeline.

## SAP S/4HANA Cloud SDK Specific Checks

### Required Dependencies

For the SAP S/4HANA Cloud SDK specific checks to work, a few dependencies are required in unit and integration tests.
If one of the following dependencies is missing in either the unit tests or integration tests module, the build fails:

* `com.sap.cloud.s4hana.quality:odata-querylistener`
* `com.sap.cloud.s4hana.quality:rfc-querylistener`
* `com.sap.cloud.s4hana.quality:httpclient-listener`

### Only Depend on Official API

This quality checks for usage of unofficial RFC and OData services.
Only official API from the [SAP API Business Hub](https://api.sap.com/) should be used, since unofficial API don't provide any stable interfaces.
A list of official API can be found in [this blog post](https://blogs.sap.com/2017/09/22/quality-checks/).

### Resilient Network Calls

When building extension applications on SAP Cloud Platform, you always deal with a distributed system.
There is at least two applications in this scenario: Your extension application, and SAP S/4HANA.
In distributed systems, you may not assume that the network is reliable.
To mitigate unreliable networks, a pattern called _circuit breaker_ is commonly used.
The idea is that you define a fallback action in case the network fails too often in a short time span.
The fallback might use cached data, or default values, depending on what works best in your problem domain.

To implement this pattern, the SAP S/4HANA Cloud SDK integrates with the [Hystrix](https://github.com/Netflix/Hystrix) library.

This quality check tests, that your remote calls are wrapped in a Hystrix command.
The build will fail with a error message like `Your project accesses downstream systems in a non-resilient manner` if this is not the case.

More information on building resilient applications is available in [this blog post](https://blogs.sap.com/2017/06/23/step-5-resilience-with-hystrix/).

## Functional Tests

Ensuring the functional correctness of an application requires automated tests, which are part of the application code.
Those qualities depend on the test code written by the application developer.

### Unit Tests

The purpose of unit tests is to verify the correctness of a single _unit_ in isolation.
Other components than the _unit under test_ may be mocked for testing purposes.

Place your unit tests in the appropriate Maven module (`unit-tests`) in order to make the pipeline run them automatically.

### Integration Tests

Integration tests work on a higher level compared to unit tests.
They should ensure that independently tested units work together as they need to.
In the context of extension applications on SAP Cloud Platform, this means to ensure _interoperability of your application with S/4HANA_ and _interoperability between your application's backend and frontend component_.

Place your integration tests in the appropriate Maven module (`integration-tests`) in order to make the pipeline run them automatically.

For more detailed description, refer to [this blog post](https://blogs.sap.com/2017/09/19/step-12-with-sap-s4hana-cloud-sdk-automated-testing/).

### End-to-End Tests

End-to-end tests use your application, like a human user would by clicking buttons, entering text into forms and waiting for the result.

Place your end-to-end tests in the `e2e-tests` directory and ensure the `ci-e2e` script in `package.json` runs the right command.
The output folder for the reports needs to be `s4hana_pipeline/reports/e2e`.

### Code Coverage

Code coverage refers to how much of your application code is tested.
The build fails, if the test coverage of your code drops below a certain threshold.
To fix such a build failure, check which parts of your code are not tested yet and write missing tests.

The code coverage is tested using [JaCoCo Java Code Coverage Library](https://www.eclemma.org/jacoco/).

## Non-Functional Tests

### Performance

Performance relates to how quickly your application reacts under heavy load.
For implementing performance tests, you can chose between to Open Source tools: [JMeter](https://jmeter.apache.org/) and [Gatling](https://gatling.io/).
If you're not familiar with both of them, we recommend using Gatling.

More information on testing the performance of your application is available in [this blog post](https://blogs.sap.com/2018/01/11/step-23-with-sap-s4hana-cloud-sdk-performance-tests/).

### Static Code Checks

Static code checks look for potential issues in code without running the program.
The SAP S/4HANA Cloud SDK Pipeline includes commonly used static checks using both [PMD](https://pmd.github.io/) and [SpotBugs](https://spotbugs.github.io/).

In addition to the default checks of those tools, it adds the following SAP S/4HANA Cloud SDK specific checks:

* When instantiating a logger, always use `CloudLoggerFactory` to use the logger provided by the SDK
* To make post-mortem debugging possible
    * Log the exception in the catch block or in a called handling method or reference it in a new thrown exception
    * Reference the exception when logging inside a catch block
* In order to allow a smooth transition from Neo to Cloud Foundry, you should use the platform independent abstractions provided by the SAP S4HANA Cloud SDK

### Third-Party Tools

The SAP S/4HANA Cloud SDK Pipeline also integrates with commercial third party code analyzer services, if you wish to use them.
Currently, [Checkmarx](https://www.checkmarx.com/), [WhiteSource](https://www.whitesourcesoftware.com/), and [SourceClear](https://www.sourceclear.com/) are available.
For those scans to be enabled, they need to be configured in the [pipeline configuration file](../../configuration.md).
