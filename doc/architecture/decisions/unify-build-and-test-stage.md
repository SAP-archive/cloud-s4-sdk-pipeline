# Unify the "build" and "unit-tests" stage

## Status

Accepted

## Context

Our CI/CD Pipeline has a build stage, which builds the backend and if available the frontend of the application.
Due to many projects which created their unit-tests next to their productive code, which is also a standard recommended by maven, the SAP Cloud SDK Pipeline should be able to execute the unit-tests and handle their result in the build stage.

## Decision

Both `build` (backend as well as frontend) and `unit-tests` are unified into one `build-and-test` stage.
First the backend will be built and tested and in case a `package.json` and no `node_modules` exist the frontend will be built.
The behaviour for projects using NPM as their primary build-tool is identical.

## Consequences

- The Unit-Tests-Stage is removed from the pipeline which will be executed at runtime.

- Users cannot configure the dockerimage used for unit-tests anymore
