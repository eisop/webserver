#!/bin/bash
#ROOT=$TRAVIS_BUILD_DIR/..

# Fail the whole script if any command fails
set -e

if [ ! -d "jsr308" ] ; then mkdir ./jsr308; fi 
cd ./jsr308

export SHELLOPTS

## Build Checker Framework
(git clone https://github.com/typetools/checker-framework.git)
# This also builds annotation-tools and jsr308-langtools
(cd checker-framework/ && ./.travis-build-without-test.sh)
