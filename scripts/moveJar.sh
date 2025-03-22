#!/bin/bash

echo "Copying JARs to dist/"
cp target/streamline-"$(scripts/getVersion.sh)".jar dist/
echo "JARs are now in dist/"
