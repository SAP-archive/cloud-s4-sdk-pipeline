# Analytics

We do want to improve SAP S/4HANA Cloud SDK Pipeline based on information about how it is used.
To do so, we do collect non-personal telemetry data.

## Hashing

We use hashed values for some data points, to allow us to correlate data by projects.
The hashes are not reversible, and can't be used to identify users.

The hash is calculated locally on your machine and no original values are transmitted.

A per-project generated salt value is used to compute the hashes.
This salt is private to the project, and not known to SAP SE.

With this salted hash, it is possible to correlate actions triggered in the same project.
It is not possible to know the name of the project, or other identifying attributes.

## Information we collect

* Name of the activity, for example
    * TODO add examples
* Hash of Maven group id + artifact id
* Hash of the Jenkins job URL
* Repository branch type ("productive" or "non-productive")
* Configuration parameters
    * Todo list here
* Versions of used artifacts
    * Continuous Delivery Server
    * SAP S/4HANA Cloud SDK Pipeline
    * SAP S/4HANA Cloud SDK Java libraries
* Outcome of the action (success or failure)
* Operating system information (name, version, locale, ..)

## Opt-out

Analytics is enabled by default.

If you wish to disable it, TODO