# Automatic Versioning and Release of Cloud Applications

## Status

Accepted

## Context

We provide a CD Pipeline for SAP Cloud Platform applications, which adheres to the "build once" principle.
Still, the pipeline does each build step, like building the artifact, running tests or static code checks in separate stages.
We use Maven for building the application, using Gradle or other build tools is not an option without much rework.

For this document, the term "release" (noun) refers to a uniquely identifiable version of software.
This includes the source code version (commit or tag) from which the artifacts are built, and the build artifacts themselves.

The verb "to release" refers to the process of creating a new release.
Part of this process is to determine the version number of the release candidate.
The release candidate becomes a release, when its build pipeline succeeded, and the build artifact is deployed to the Cloud Platform and the artifact repository.

With Maven, this is usually facilitated with the [Maven Release Plugin](http://maven.apache.org/maven-release/maven-release-plugin/).
Using this plugin does not satisfy our requirements as described below.

The pipeline automatically uploads build artifacts to an artifact repository and deploys the app to the Cloud Platform.
Artifact uploads and deployments happen only for commits on the so co called "productive branch" (`master` by default).
Maven's versioning schema appends `SNAPSHOT` suffix to versions which are not released.
A version like `1.0.2-SNAPSHOT` does not say from which commit this was built.
Artifact repositories might delete `SNAPSHOT` versions after some time, because those are not releases.

## Requirements

* _Auditability_: It must be clear which version of the software is deployed to the Cloud Platform.
This implies that `SNAPHSOT` versions can't be used, because it is unclear which build a `SNAPHSOT` version refers to.
For example, `1.0.2-SNAPSHOT` is not a unique identifier, many distinct build artifacts might have this "version".
Also, artifact repositories such as Nexus might delete "Snapshots" after a while, which is an issue for auditability.
* _Automation_: It is not feasible to have a manual release or deployment process, since this can happen multiple times a day.
Usually, only the most recent version is in use.
Older versions are only of interest if a version has a critical error and must be rolled back.

## Decision

We implement an automated versioning schema, in which each commit to the productive branch is equivalent to a new release.
This feature is enabled by default, but can be disabled.

The version number shall contain a human readable _build_ time stamp (ISO 8601, without colons for file-name compatibility on Windows, always `UTC`) and the git commit id of the most recent commit to `master`, for example `2.7.3-2018-03-02T114757UTC_ff46bb0f00a663018f3efea697b2fb5e86fe6d41`.
An auto-created release does not imply creating a tag in the repository.
Creating tags may be done manually to mark noteworthy versions by the developer.

### Reasoning

* Each commit on `master` is a new release: We assume the work happens in feature branches, which are merged once they implement a feature and meet the team's definition of done.
Merging to `master` is implicitly approval for release.
* Feature can be disabled: You might still have builds which don't follow this release approach.
For those, it must be possible to disable automatic versioning.
* _Build_ instead of _commit_ time stamp: This implies that multiple builds of the same commit have a different version number.
This avoids conflicts, when uploading a second build of a commit to a artifact repository.
* Always ISO 8601 date-time format: Can be sorted in lexical order which results in a chronological list.
* Always `UTC`: Most simple solution, avoids daylight saving time issues and is unambiguous for teams working distributed in multiple time zones.
* Don't create git tags: The version number contains the commit id, which is sufficient to check out this particular version.
If we created tags automatically for each version, tags would be cluttered very quickly.
Tags still can be used to mark a version on purpose, with semantic versioning if desired.
