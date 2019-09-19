---
name: Pipeline issue
about: Report an issue with the pipeline
title: ''
labels: ''
assignees: ''

---

<!--
Thank you for reporting an issue with the Cloud SDK Pipeline. Please take the time to fill out the information in this template which is necessary to find out what went wrong. Be sure to remove **any confidential** information before publishing the issue on the internet.
-->

## Issue Description (Mandatory)
<!--
Please describe what you know about the issue, and what you tried to solve it, and what your expectation is how the pipeline should behave.
-->

The pipeline is failing in the stage: PUT NAME OF THE STAGE HERE

PLEASE PLACE YOUR DESCRIPTION HERE

## Log Output (Mandatory)

<!--
Please provide the relevant Jenkins log output below. Be sure to check above and below the actual error for helpful information.
Be sure to redact confidential information before posting.
-->

```
PLEASE PASTE YOUR LOG HERE WITHIN THE BACKTICKS
```

## Link to Jenkins Job (if it can be shared)

<!--
It really helps to look into the Jenkins job, so if anonymous access is possible, please provide the link.
-->

PLEASE PLACE THE LINK HERE

## Reproduce the Issue Locally (Mandatory)

In case a maven/npm command or similar failed:
* [ ] I tried to reproduce the issue locally
* [ ] I could reproduce the issue locally

<!--
Please provide the steps to reproduce the issue locally, for example like this:

* Step 1: Type `mvn clean install` in the project's root directory
* Step 2: Something else
* Step 3: ...
-->

## Search for existing solution beforehand (Mandatory)

* [ ] I searched for an existing solution on [StackOverflow](https://stackoverflow.com/questions/tagged/sap-cloud-sdk) and my question was not answered there
* [ ] I searched for a similar GitHub issue before and found none
* [ ] I checked the [pipeline configuration docs](https://github.com/SAP/cloud-s4-sdk-pipeline/blob/master/configuration.md) and [the general pipeline docs](https://github.com/SAP/cloud-s4-sdk-pipeline/tree/master/doc/pipeline) for an answer of my issue

***Please try the GitHub search, it works really well***

## Project Details (Mandatory)

<!--
Please provide as much information about your project as possible.
If you cannot share your project for confidentiality reasons, please consider providing a minimal working example https://en.wikipedia.org/w/index.php?title=Minimal_working_example&oldid=893866607
-->

* Link to GitHub repo: PLACE YOUR LINK HERE
* Project type, for example:
    * [ ] SDK Maven Archetype
    * [ ] MTA (which types of modules?)
    * [ ] JavaScript
    * [ ] None of the above (unsupported)
