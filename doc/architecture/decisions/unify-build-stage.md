# Unify the "build" Stage

## Status

Accepted

## Context

Our CI/CD Pipeline has a build stage, which contains a `buildBackend` and a `buildFrontend` component.
`buildFrontend` is optional, and only run if the project has a frontend, which is determined by the existence of a `package.json` file.
If both are run, they are executed in parallel.
`buildFrontend` actually does not build anything, it only downloads dependencies.
"Building" the frontend is facilitated via maven, which is run in `buildBackend`.

## Decision

Both `buildBackend` and `buildFrontend` are unified into one `build` stage.

## Consequences

* Execution of maven and npm is not parallel anymore, which might delay the build process a little.
* The configuration format of `pipeline_config.yml` needs to be updated to reflect the new stage.
  Our existing legacy configuration layer could be used to mitigate this.
* We reduce the number of stashes, which makes the pipeline execute a little faster
* Existing documentation needs to be updated, if possible.
  Non-updatable documentation exists (such as the "Extending SAP S/4HANA" book for example).
