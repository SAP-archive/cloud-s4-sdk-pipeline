# Artifacts Deployment

## Status

Accepted

## Context

Deploying artifacts to nexus was done with this plugin before, but a bug was reported.
When reading a child pom without `version`, for example, it does not retrieve the information inherited from the parent pom.
We have to investigate alternatives.

### Alternatives
* [Apache Maven Deploy Plugin](http://maven.apache.org/plugins/maven-deploy-plugin/)
* Maven lifecycle phase : deploy
* [Nexus Artifact Uploader](https://wiki.jenkins.io/display/JENKINS/Nexus+Artifact+Uploader)

### Pros and Cons

#### Apache Maven Deploy Plugin (deploy:deploy-file)
For this option, we only consider the goal `deploy:deploy-file`.
##### :+1:
- Official maven plugin for deployment, which is perfect for any maven projects if you only care whether your artifacts are deployed correctly.
##### :-1:
- A list of parameters has to be generated before using the plugin, including `artifactId` and `version`, which is the same case as the `Nexus Artifact Uploader`.
- Credential info has to be stored in the `settings.xml`, which introduces additional implementation.
Let's assume users have saved all the credentials in the Jenkins server.
We may inject a list of `server` tags under the `servers` tag with credentials info into the global `settings.xml`.
To make it as secrets, `mvn --encrypt-master-password <password>` has to be executed afterwards.


#### Maven lifecycle phase: deploy
By default, the maven lifecycle phase `deploy` binds to the goal `deploy:deploy` of the `Apache Maven Deploy Plugin`.
##### :+1:
- Same as the `Apache Maven Deploy Plugin`
- You don't have to pass the parameters as `Apache Maven Deploy Plugin` and `Nexus Artifact Uploader`, 
because `package` phase is executed implicitly and makes the parameters ready before `deploy` phase.
##### :-1:
- Same case as the `Apache Maven Deploy Plugin` for handling credentials.
- As a maven phase, a list of phases is triggered implicitly before this phase, including `compile`, `test` and `package`.
To follow the build-once principle, all these phases have to be skipped.
However, it's not possible to skip some of the maven goals binding to certain phases.
For example, if the `<packaging>` tag of the `pom.xml` is set to `jar`, then the `jar:jar` goal of the [`Apache Maven JAR Plugin`](https://maven.apache.org/plugins/maven-jar-plugin/) is bound to `package` phase.
Unfortunately, however, `Apache Maven JAR Plugin` does not provide an option to skip the the `jar:jar` goal.
**This is the main reason why we cannot use this option.**


#### Nexus Artifact Uploader
##### :+1:
- Without the pain of handling the credentials, which was mentioned above in `Apache Maven Deploy Plugin` section.
- It's promising, when the plugin is used properly
##### :-1:
- Same as the `Apache Maven Deploy Plugin`. A list of parameters has to be prepared.

### Decision
`Nexus Artifact Uploader` is chosen, because:
- `Maven lifecycle phase: deploy` does not meet our build-once principle.
- `Nexus Artifact Uploader` has the same situation regarding parameters as `Apache Maven Deploy Plugin`, but can handle credentials as a Jenkins plugin.
