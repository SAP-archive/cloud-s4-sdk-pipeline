# Analytics

We do want to improve the SAP S/4HANA Cloud SDK Pipeline based on information about how it is used.
To do so, we do collect non-personal telemetry data.

Please note that this additionally includes the analytics done by SAP/jenkins-library, [see here](https://sap.github.io/jenkins-library/configuration/#collecting-telemetry-data) for details.

## Opt-out

Analytics is enabled by default.

If you wish to disable it both for SAP S/4HANA Cloud SDK Pipeline and SAP/jenkins-library, set `collectTelemetryData` to `false` in your `pipeline_config.yml` in the `general` section as in this example:

```
general:
  collectTelemetryData: false
```

## Information we collect

* Name of the activity, for example
    * _Pipeline [Step]_
    * _Cx Server [Lifecycle phase]_
    * _Maven Archetype generate_
* Hash of Maven group id + artifact id (refer to [section hashing](#hashing) for details)
* Hash of the Jenkins job URL (refer to [section hashing](#hashing) for details)
* Repository branch type ("productive" or "non-productive")
* Configuration parameters of the pipeline step, for example
    * Docker image
* Versions of used artifacts
    * Continuous Delivery Server
    * SAP S/4HANA Cloud SDK Pipeline
    * SAP S/4HANA Cloud SDK Java libraries
* Outcome of the action (success or failure)
* Operating system information (name, version, locale, ..)

## Hashing

We use hashed values for some data points, to allow us to correlate data by projects.
The hashes are not reversible, and can't be used to identify users.

The hash is calculated locally on your machine and no original values are transmitted.

A per-project generated salt value is used to compute the hashes.
This salt is private to the project, and not known to SAP SE.

With this salted hash, it is possible to correlate actions triggered in the same project.
It is not possible to know the name of the project, or other identifying attributes.

For example, the data point we want to collect might be the URL of a Jenkins job, like `http://my-ci.corp/job/address-manager/`.
The salt is a random string, like `KOMNTRTMYK` for example.

The resulting hash looks like `918414eb6ea8a10db9467a08e9cab28d3d4e1299`, and will be the same if the job URL and the salt are the same over multiple entries, allowing to correlate them.
It is not possible to see when two different salted hashes had the same original value.
