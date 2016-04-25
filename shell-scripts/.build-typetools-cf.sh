#!/bin/bash

# Fail the whole script if any command fails
set -e

# if [ -L "../jsr308" ] ; then
# echo "detect a symbolic link of jsr308. Exit without clone and building."
# exit 0
# fi

if [ ! -d "$ROOT/jsr308" ] ; then
    mkdir $ROOT/jsr308;
fi 

export JAVA_HOME=${JAVA_HOME:-$(dirname $(dirname $(dirname $(readlink -f $(/usr/bin/which java)))))}
export JAVA_TOOL_OPTIONS=-Dfile.encoding=UTF8

cd $ROOT/jsr308

## Build Checker Framework
if [ -d ./checker-framework ] ; then
    # Older versions of git don't support the -C command-line option
    # echo "do pull"
    (cd ./checker-framework && git pull)
else
    echo "cloning from https://github.com/typetools/checker-framework.git"
    git clone https://github.com/typetools/checker-framework.git
fi

# This also builds annotation-tools and jsr308-langtools
cd checker-framework/
./.travis-build-without-test.sh

export CF_LOCATION=$ROOT/jsr308/checker-framework
