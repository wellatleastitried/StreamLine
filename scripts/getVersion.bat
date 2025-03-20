@echo off

for /f %%i in ('mvn help:evaluate -Dexpression=project.version -q -DforceStdout') do set VERSION=%%i
echo %VERSION%
