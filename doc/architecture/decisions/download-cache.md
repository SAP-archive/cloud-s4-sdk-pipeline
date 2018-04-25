### Download Cache for Maven and NPM Packages

A download cache is required because builds are taking too long, and each build re-downloads all the same dependencies.

##### Solution assessed
- Apache Archiva
- npm lazy
- Sonatype Nexus

We have decided to use a Docker container with nexus as a caching solution.

Reasons:
- It is Open Source under a permissive license (Apache License 2.0)
- Supports both Maven and NPM caching, as opposed to the alternatives
