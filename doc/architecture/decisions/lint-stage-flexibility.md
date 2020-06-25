# Lint Stage Flexibility

## Status

Accepted

## Context

The SAP Cloud SDK Pipeline has a Lint stage, which currently only checks SAPUI5 components, if present, for the SAPUI5 recommended best practices.
Many projects, however, also want to enable linting of non SAPUI5 related files as part of the lint stage. 
Therefore, the lint stage of the SAP Cloud SDK Pipeline should provide more flexibility, i.e., adding the support for running custom linting scripts as part of the lint stage.

### Decision

To enable the execution of custom linting scripts as part of the lint stage, we decided that the user can define a script called `ci-lint` in the package.json of the project.
The new behavior of the lint stage of the SAP Cloud SDK Pipeline is as follows. 

* For each `package.json` of the project, where the script `ci-lint` is defined, the script will be executed and the results are aggregated as well as visualized using the warnings-ng plugin in Jenkins UI.
* If no script `ci-lint` is defined, the pipeline will check SAPUI5 components, if present, for the SAPUI5 recommended best practices.
* If none of the scenarios described apply and Javascript or Typescript files are present in the project, the pipeline will automatically execute ESLint. For the execution of ESLint there are two cases:
    1.	If no ESLint configuration files are present in the project directory, a general purpose configuration is used to lint all Javascript and/or Typescript files of the project.
    2.	If ESLint configuration files created by the user exist in the project, they will be used instead to lint Javascript files. The execution happens according to ESLint's default execution behavior, i.e., for each JS file the ESLint config in that directory or one of the parent directories will be used to lint the file. 

In addition to the new behavior, we decided to not let the execution of the pipeline fail when linting errors or warnings are discovered.
The thresholds defined in the pipeline configuration only apply for the built-in check of SAPUI5 components.

ESLint is used as linting tool for the default linting, since it is the most-widely used and feature rich linting tool, at the time of this writing.  

## Consequences

* The user defined `ci-lint` script is prioritized over the SAPUI5 Lint to enable the user to override this linter by providing a custom version of the SAPUI5 Linter, as required for some projects.
* The user can only use linting tools which support the output of xml files in checkstyle format.
* The default of executing ESLint aims at providing at least a basic form of linting to the user.
    * Using potentially existing ESLint configs increases the probability of having a configuration that addresses project specific requirements.
        * In this case only those files will be linted, for which an ESLint config exists. 
        * Also, we need to limit the linting to JS files, since we cannot check easily if the user provided an ESLint configuration which allows to lint files other than JS files.
    * Providing a general purpose configuration for ESLint increases the maintenance burden when ESLint is updated and the rule sets are extended. 
    * Currently, the default linting behavior can only be turned off by defining a script `"ci-lint" : "exit 0"`
    
