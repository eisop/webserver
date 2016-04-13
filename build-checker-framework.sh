#!/bin/bash

# Fail the whole script if any command fails
set -e

if [ ! -d "jsr308" ] ; then mkdir ./jsr308; fi 
cd ./jsr308

export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(dirname $(readlink -f $(/usr/bin/which java)))))}
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
export SHELLOPTS

## Build Checker Framework
(git clone https://github.com/typetools/checker-framework.git)

# This also builds annotation-tools and jsr308-langtools
(cd checker-framework/ && ./.travis-build-without-test.sh)
