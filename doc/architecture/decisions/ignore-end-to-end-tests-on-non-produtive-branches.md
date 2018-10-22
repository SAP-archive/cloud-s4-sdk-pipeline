# Skip End-to-End Tests on Non-Productive Branches

## Status

Accepted

## Context

SAP S/4HANA Cloud SDK Pipeline can execute end-to-end tests, which simulate how a human would test the application.
End-to-end tests tend to run quite long, which might impede how fast pull requests can be merged.

### Decision

We allow to skip running end-to-end tests on non-productive branches.
We do not allow skipping them on the productive branch.

This feature is disabled by default.

## Consequences

* The pipeline becomes more complex to test and maintain
* The productive branch is more likely to fail in the pipeline due to undiscovered defects
* More pull requests with a smaller scope are encouraged, since the pipeline won't run as long for pull request branches
