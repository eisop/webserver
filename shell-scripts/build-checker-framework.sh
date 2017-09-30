#!/bin/bash

# Fail the whole script if any command fails
set -e

# first change to script directory so relative path works again
WORKING_DIR=$(pwd)
ROOT=$(cd $(dirname "$0")/.. && pwd)

DEV_CF=dev-checker-framework
RELEASE_CF=released-checker-framework

cd $ROOT/shell-scripts

case $1 in
	"")
		#default option: build newest version of checker framework from typetools
		. ./.build-typetools-cf.sh
		cd $ROOT && rm -f ./$DEV_CF && ln -s $CF_LOCATION ./$DEV_CF
	;;
	-r | --release )
		. ./.build-released-cf.sh $2
		echo "Released checker framework location is $CF_LOCATION"
		cd $ROOT && rm -f $RELEASE_CF && ln -s $CF_LOCATION ./$RELEASE_CF
		;;
	-l | --local )
		CF_LOCATION=$(cd $WORKING_DIR && cd $2 && pwd)
		echo "Enabled checker framework location is $CF_LOCATION"
		cd $ROOT && rm -f $DEV_CF && ln -s $CF_LOCATION ./$DEV_CF
		;;
	-h | --help )
		echo "=====usage====="
		echo "-r | --release <url>: build newest released version of checker framework and link it to $ROOT/$RELEASE_CF"
		echo "-l | --local <dir path>: link the existed local copy of checker framework to $ROOT/$DEV_CF"
		echo "default(no argument passing): build newest version of checker framework from typetools and link it to $ROOT/$DEV_CF"
		echo "=====end====="
		exit 0
		;;
	* )
		echo "I don't know this argument, please try -h | --help for usage information."
		exit 1
		;;
esac

exit 0

