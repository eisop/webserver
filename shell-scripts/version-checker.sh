#!/bin/bash
version=$(cd ../ && cd dev-checker-framework/checker-framework-3.9.1 &&  unzip -p checker/dist/checker.jar META-INF/MANIFEST.MF | grep "Implementation-Version:" | cut -d " " -f 2)

echo "$version"

