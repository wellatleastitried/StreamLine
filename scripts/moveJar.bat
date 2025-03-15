@echo off
echo Copying JARs to dist/ ...
if not exist dist mkdir dist
xcopy /Y target\*.jar dist\
echo JARs are now in dist/
