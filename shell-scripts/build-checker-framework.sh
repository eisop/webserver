#!/bin/bash

# Fail the whole script if any command fails
set -e

# first change to script directory so relative path works again
WORKING_DIR=$(pwd)
ROOT=$(cd $(dirname "$0")/.. && pwd)

ENABLED_CF=enabled-checker-framework

cd $ROOT/shell-scripts

if [ "$1" == "" ] ; then
	. ./.build-typetools-cf.sh
	cd $ROOT && rm -f ./$ENABLED_CF && ln -s $CF_LOCATION ./$ENABLED_CF
	exit 0
fi

case $1 in
	-r | --release )
		. ./.build-released-cf.sh $2
		;;
	-l | --local )
		CF_LOCATION=$(cd $WORKING_DIR && cd $2 && pwd)
		;;
	-h | --help )
		echo "=====usage====="
		echo "-r | --release <url>: build newest released version of checker framework and link it to $ROOT/$ENABLED_CF"
		echo "-l | --local <dir path>: link the existed local copy of checker framework to $ROOT/$ENABLED_CF"
		echo "default(no argument passing): build newest version of checker framework from type tools and link it to $ROOT/$ENABLED_CF"
		echo "=====end====="
		exit 0
		;;
	* )
		echo "I don't know this argument, please try -h | --help for usage information."
		exit 1
		;;
esac

echo "Enabled checker framework location is $CF_LOCATION"
cd $ROOT && rm -f $ENABLED_CF && ln -s $CF_LOCATION ./$ENABLED_CF
exit 0

