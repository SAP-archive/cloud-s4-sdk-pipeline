# Pipeline extensibility

__Caution:__ Extensibility is an advanced concept which the large majority of Pipeline users should not need.
Be careful with this feature.

The goal of the SAP Cloud SDK Pipeline is to help you build and continuously deliver high quality applications which run on SAP Cloud Platform without the need to write your own `Jenkinsfile`.

Still, there might be circumstances in which the individual features of the pipeline might not satisfy your exact requirements.
In order to allow for an solution in such cases, the pipeline offers an extensibility mechanism.

Extensibility works on the _stage_ level.

The basic template for an extension looks like this:

```groovy
def call(Map parameters) {
    parameters.originalStage.call()
}

return this
```

This needs to be saved to a file in your project's git repository with the name `pipeline/extensions/[Name of the Stage].groovy`.

The map named `parameters` has the properties `script`, `originalStage`, `stageName`, and `config`.

The method name `call`, its parameter `parameters` and the `return this` **may not be changed**.
The `parameters.originalStage.call()` line calls the stage as it was implemented in the pipeline.
You can have custom code before and after the `originalStage`, or you can even chose to not call it and implement the whole stage yourself.

**Breaking change**: In an older version of this document, the `.call` in `parameters.originalStage.call()` was omitted.
This is not valid anymore in recent Jenkins versions.
See [here](https://jenkins.io/redirect/pipeline-cps-method-mismatches) if you are interested in the details.

One case where the extensibility can be used with high value and low risk is the `additionalTools` stage, which is part of the _third party check_.
This stage does nothing in the Pipeline, its sole purpose is to be overwritten if you need to have custom third party tools in the pipeline.

## Custom linters

Another good use-case for extensibility is to use custom linters.
A linter is a tool that can check the source code for certain stylistic criteria, and many teams chose to use a linter to ensure a common programming style.

As an example, if you want to use Checkstyle in your codebase, you might use an extension similar to this one in a file called `pipeline/extensions/lint.groovy` in your project:

```groovy
def call(Map parameters) {

    parameters.originalStage.call() // Runs the built in linters

    mavenExecute(
        script: parameters.script,
        flags: '--batch-mode',
        pomPath: 'application/pom.xml',
        m2Path: s4SdkGlobals.m2Directory,
        goals: 'checkstyle:checkstyle',
    )

    recordIssues blameDisabled: true,
        enabledForFailure: true,
        aggregatingResults: false,
        tool: checkStyle()
}

return this
```

It is important to pass `parameters.script` as the `script` parameter to the `mavenExecute` step.
This makes sure that the correct configuration environment is used.

Don't forget the `return this` statement.

What this does is to use the Checkstyle Maven plugin to perform a scan, and then record the discovered issues in the [Jenkins Warnings Plugin - Next Generation](https://github.com/jenkinsci/warnings-ng-plugin).
Please refer to the plugin's documentation for usage instructions.

This example can be adopted for other linters of your choice.
