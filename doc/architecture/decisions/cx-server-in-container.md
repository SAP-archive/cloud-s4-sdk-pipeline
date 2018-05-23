# Move Cx Server Script in Container

## Status

Accepted

## Context

We have a bash script (`cx-server`), which orchestrates the Docker containers used by our Cx Server.
Using bash is inconvenient for Windows users, as Windows does not include bash by default.
There is options for running bash on Windows, such as the Windows Subsystem for Linux, but this is not trivial to setup and requires switching Windows to Developer Mode.
Other options include running a virtual machine locally, or connecting to a remove Linux system, but both are not always possible and have too much overhead.

Recently, we added a "companion" Docker image which is used by `cx-server` to run scripts.
Unrelated, the idea was born to move `cx-server` into this image, so the remaining `cx-server` is a very thin wrapper which can also be added as a Windows compatible script file.

## Requirements

* The CLI of `cx-server` may not change in an incompatible way
* The upgrade path for projects using the old `cx-server` script has to be smooth

## Decision

We move the bash script inside the `s4sdk/cxserver-companion` Docker image.
The old `cx-server` script just delegates the command to the script inside the companion container.
A new `cx-server.bat` script is added, doing the same for Windows.
We don't use PowerShell to increase compatibility with Windows.
