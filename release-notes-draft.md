# Release Notes Draft for next Releases

This document describes the changes which will be part of the next release and are already available in the latest version (master branch) of the pipeline.

# v41

## :warning: Breaking changes
### Configuration option for SAP NPM Registry
The configuration option `sapNpmRegistry` was removed, due to the migration of all packages from the SAP NPM registry to the default public registry at npmjs.org.
Thus, no separate configuration of the SAP NPM registry is required anymore. 
Any configuration for the parameter `sapNpmRegistry` will be ignored by the pipeline. 
If your project requires a custom registry configuration, use the `defaultNpmRegistry` parameter instead. For example:

```diff
  npmExecuteScripts:
    defaultNpmRegistry: 'https://registry.npmjs.org/'
-    sapNpmRegistry: 'https://...'
```
## New Features

## Fixes

## Improvements
