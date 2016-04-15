#!/bin/bash

# Fail the whole script if any command fails
set -e

if [ ! -d "jsr308" ] ; then
    mkdir ./jsr308;
fi 

export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(dirname $(readlink -f $(/usr/bin/which java)))))}
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8
# export SHELLOPTS

cd ./jsr308

## Build Checker Framework
if [ -d ./checker-framework ] ; then
    # Older versions of git don't support the -C command-line option
    # echo "do pull"
    (cd ./checker-framework && git pull)
else
    echo cloning
    git clone https://github.com/typetools/checker-framework.git
fi

# echo here

# This also builds annotation-tools and jsr308-langtools
cd checker-framework/
./.travis-build-without-test.sh
