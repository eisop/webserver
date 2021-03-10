#!/bin/sh
CF=$1
version=$(cd $CF && unzip -p checker/dist/checker.jar META-INF/MANIFEST.MF | grep "Implementation-Version:" | cut -d " " -f 2)
echo "$version"

