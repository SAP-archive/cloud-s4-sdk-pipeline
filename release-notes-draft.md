# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v37

## :warning: Breaking changes

- Support for SourceClear scans was removed from the pipeline.
  Please use other 3rd party security scanning tools, such as Fortify, Checkmarx, WhiteSource or SonarQube instead.
- We reimplemented the mechanism how custom defaults are retrieved while implementing some improvements as explained below.
  Please note that the old parameter `extensionRepository` cannot be used anymore.
  Instead, please use the option `globalExtensionsRepository`.
  The option `globalExtensionsRepository` does not allow to specify a version with the option `-b` anymore.
  Instead, please use the parameter `globalExtensionsVersion` to configure a version.
  Please note that you can also configure these values as part of your custom defaults / shared configuration.
  The precedence has not changed.

Example:

```diff
general:
-  extensionRepository: 'https://my.git.example/extensions.git -b v35'
+  globalExtensionsRepository: 'https://my.git.example/extensions.git'
+  globalExtensionsVersion: 'v35'
```

### Automatic versioning

Automatic versioning is solely configured via the new step `artifactPrepareVersion` as documented [here](https://sap.github.io/jenkins-library/steps/artifactPrepareVersion/)

For internal use-cases, the pipeline is configuring this step to also push a tag for each generated version.
This makes it necessary to provide the two parameters `gitHttpsCredentialsId` and `gitUserName`.
For external use-cases, the default is not to push tags.
Three types of versioning are supported via the parameter `versioningType`: `cloud`, `cloud_noTag`, and `library`.
To disable automatic versioning, set the value to `library`.
The pipeline will then pick up the artifact version configured in the build descriptor file, but not generate a new version.

If you previously turned off automatic versioning via the parameter `automaticVersioning`, this diffs shows the necessary migration of the config file:

```diff
general:
-  automaticVersioning: false
steps:
+  artifactPrepareVersion:
+    versioningType: 'library'
```

If you previously configured pushing tags for each new version, this is how the configuration can be migrated:

```diff
steps:
-  artifactSetVersion:
-    commitVersion: true
-    gitCredentialsId: 'Jenkins secret'
-    gitSshUrl: 'repo-URL'
-    gitUserEMail: 'email'
-    gitUserName: 'username'
+  artifactPrepareVersion:
+    versioningType: 'cloud'
+    gitHttpsCredentialsId: 'Jenkins secret'
```

The repository URL for the project in Jenkins needs to be configured with `https://` scheme.
It will be used also for pushing the tag.

For Maven projects, the step `mavenExecute` is not used anymore to set the version.
Instead, it is directly done by `artifactPrepareVersion`, which avoids starting new Docker containers and will improve the performance.

If you have defined a project settings file for `mavenExecute` before, you must move this configuration into the general sections as follows:

```diff
general:
+  maven:
+    projectSettingsFile: 'settings.xml'
steps:
-  mavenExecute:
-    projectSettingsFile: 'settings.xml'
```

The same applies to other options defined for `mavenExecute`.

## Fixes

## Improvements

### Authenticated Access for Custom Defaults and Global Pipeline Extensions

We updated the download mechanism for custom defaults and global pipeline extensions so that both can also be stored on
locations which are secured by basic authentication.

If you specify the option for `customDefaultsCredentialsId` in the section general of the configuration, the username and password will be used for accessing all custom defaults urls defined in the section customDefaults.
Please find further details here: https://sap.github.io/jenkins-library/steps/setupCommonPipelineEnvironment/
If you specify the option for `globalExtensionsRepositoryCredentialsId` in the section general of the configuration, the username and password will be used for cloning the extension repository.

### Specify Global Pipeline Extensions in Shared Config

Now it is possible to configure all configuration options regarding the global extensions as part of your custom defaults / shared configuration.

### Lint Stage

The pipeline can be configured to fail based on linting findings using the `failOnError` configuration option. 
By default, the pipeline does not fail based on lint findings.
This option is available when providing a custom linting script or when relying on the default linting of the pipeline.
It is not available when using the SAPUI5 best practices linter, which is using thresholds instead.

```diff
steps:
+  npmExecuteLint:
+    failOnError: true
```

### Jenkinsfile

We updated our bootstrapping Jenkinsfile so that it loads the pipeline directly from the library and not from the `cloud-s4-sdk-pipeline` repository anymore.
This change improves efficiency of the pipeline because it decreases the number of repositories checked out and the number of executors during the pipeline run.

The new version can be found here: https://github.com/SAP/cloud-s4-sdk-pipeline/blob/master/archetype-resources/Jenkinsfile

To benefit from the improved efficiency, please update your Jenkinsfile like this:

```diff
-node {
-    deleteDir()
-    sh "git clone --depth 1 https://github.com/SAP/cloud-s4-sdk-pipeline.git -b ${pipelineVersion} pipelines"
-    load './pipelines/s4sdk-pipeline.groovy'
-}
+library "s4sdk-pipeline-library@${pipelineVersion}"
+cloudSdkPipeline(script: this)
```

### Merged "build" stage with Project "Piper" general purpose pipeline

In an effort to reduce differences between _project "Piper" general purpose pipeline_ and _SAP Cloud SDK pipeline_, both use the same stage for "Build and Unit Test" now.
This is a change under the hood and it should not require any changes in your project in most cases.

A notable exception is the JavaScript pipeline.

First, the new pipeline does not only run ci scripts in the top-level `package.json` file, but also in sub-directories.
This only applies if the `package.json` file implements the respective scripts.
Older pipeline versions required you to orchestrate the build inside nested `package.json` files.
If your project has a build setup where the top level `package.json` file takes care of building sub-modules, please take care that this should not be done anymore.

This is the list of scripts that are automatically executed by the pipeline, if they are implemented:

- `ci-build`
- `ci-backend-unit-test`
- `ci-package`

This might in particular be an issue if any of the mentioned scripts uses [lerna](https://lerna.js.org/).
If you still want lerna to orchestrate the execution, make sure to use the mentioned names in the `package.json` files in the root of your project, and to use different names in sub-modules.

You can try this out locally by running `piper npmExecuteScripts --install --runScripts=ci-build,ci-backend-unit-test,ci-package` in the [project "Piper" cli](https://sap.github.io/jenkins-library/cli/).

Second, the new pipeline does not run `ci-package` in an isolated file system anymore.
Therefore, make sure that `ci-package` only changes the deployment directory.

If your JavaScript/TypeScript project was generated from an older template project, you might have to adjust the `ci-package` command like so:

```diff
"scripts": {
-     "ci-package": "mkdir -p deployment/ && npm prune --production && cp -r node_modules dist package.json package-lock.json frontend index.html deployment/",	
+     "ci-package": "sap-cloud-sdk package --include=dist/**/*,package.json,package-lock.json,frontend/**/*,index.html",
},
  "devDependencies": {
+    "@sap-cloud-sdk/cli": "^0.1.9",
}
```

If you notice any regressions, please let us know by opening an [issue](https://github.com/sap/cloud-s4-sdk-pipeline/issues).
