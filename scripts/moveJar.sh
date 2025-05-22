#!/bin/bash

echo "Checking if dist/ exists..."
if [ -f dist/ ]; then
    echo "dist/ already exists."
else
    mkdir dist/
    echo "dist/ has been created."
fi
echo "Copying JARs to dist/"
cp target/streamline-"$(scripts/getVersion.sh)".jar target/streamline.jar
cp target/streamline-"$(scripts/getVersion.sh)".jar dist/streamline.jar
echo "JARs are now in dist/"
