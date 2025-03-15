#!/bin/bash

echo $(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
