# Treatment of application with a frontend that don't have a package lock file

## Status

Accepted

## Context

The node package manager (npm) creates a so called ["lock file"](https://docs.npmjs.com/files/package-locks) when running `npm install`.
The recommended action is to commit this file to version control, as stated by the command itself:

```
$ npm install
npm notice created a lockfile as package-lock.json. You should commit this file.
```

Some npm commands, which are part of the pipeline, such as `ci` and `audit` require that a package lock file exists.

Thus, if a project lacks the package lock file, there are tree options:

* Refuse to build the project, which enforces the recommended practice of committing the lock file
* Silently create a package lock file as part of the pipeline
* Avoid npm commands which require a package lock
    * Would disable the npm audit stage

## Decision

If a project has a `package.json` file, but no package lock, we create a package lock file as part of the pipeline and warn the user about this.
The created package lock file is not archived as part of the pipeline build artifacts.

## Consequences

* Jenkins builds don't fail when developers forget to commit the package lock
* If the developers chose not to commit the package lock long term, the build is less reliable
* npm audit can run always on projects with a frontend component
