# Structure of SAP S/4HANA Cloud SDK Maven Projects

## Status

Accepted

## Context

SAP S/4HANA Cloud SDK encompasses components such as Java libraries, Maven archetypes, a Virtual Data Model and a Continuous Delivery Toolkit.
The Continuous Delivery Toolkit must be able to build, test and deploy arbitrary SAP S/4HANA Cloud SDK Maven projects developed by teams within and outside of SAP.

Based on best practices gained when developing cloud-native applications, we learned that it should be possible to separate unit- and integration tests to run them in isolation. 
Unit test should be close to the application code. 

Reasoning for requiring running them in isolation are:

* Dependencies for unit tests might not be required for integration tests and vice versa, or even conflict with them
* Unit- and integration test can be run independently and parallel, locally and on CI/CD

Alternative options to separate unit- and integration tests:

* Use [Maven profiles](https://maven.apache.org/guides/introduction/introduction-to-profiles.html)
* Use [Maven multi-module](https://maven.apache.org/guides/mini/guide-multiple-modules.html) projects

Archetypes for generating valid projects are provided.
[Example projects](https://github.com/sap/cloud-s4-sdk-examples) are available.

## Decision

Projects built by SAP S/4HANA Cloud SDK Pipeline need to be Maven multi-module projects.
Besides one or more maven modules containing the application code there can be an optional one named `integration-tests` containing the integration tests.
Unit test can be placed in `src/test/java` either in the corresponding application code module or in a separate module called `unit-tests`.
The default of the archetypes and recommended approach is to place them into the corresponding application code module. 

Each module can only have a flat structure.
Nested modules are **not** allowed.
The root pom must reference each module.

## Consequences

* Users don't need to select Maven profiles for running tests
    * For inexperienced Maven users, selecting profiles for running tests might be more confusing compared to choosing the right module
    * Furthermore, there would need to be a common convention which profiles need to exist with fixed names
* The project can be built including all unit tests by running `mvn install -!pl integration-tests`
* Integration tests can be executed separately by running `mvn verify -f integration-tests/pom.xml`
* Fine grained dependency selection for each module is possible
    * In rare cases, it might be required to use different versions of a dependency in productive and test code, which is easily possible in separate modules
    * Makes it less likely that test dependencies leak into the production code, even if the developer forgets to apply `test` scope
* Forces separation of unit- and integration tests
    * It is easier to check adherence to the testing pyramid
    * We can enforce that "typical integration test dependencies" are not used in unit tests, thus advocating strict separation of them
* Allows easier implementation of the _build once_ principle, because when integration tests are executed in isolation, the previously built and installed `application` artifact can be used
    * Due to how Maven implements the `test` phase, executing integration tests in a separate stage in CI/CD would rebuild the application artifact by default if the test and application code was not split into multiple modules.
* Existing projects that want to adopt the Continuous Delivery Toolkit need to adapt to this structure
* By having a fixed name for integration tests, the pipeline can find out whether they exist and execute them.
* For scanning of the modules we can rely on the normal maven workflow which executes maven goals recursively across the modules.
