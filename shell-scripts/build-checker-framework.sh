#!/bin/bash

# Fail the whole script if any command fails
set -e

# first change to script directory so relative path works again
ROOT=$(cd $(dirname "$0")/.. && pwd)

ENABLED_CF=enabled-checker-framework

cd $ROOT/shell-scripts

if [ "$1" == "" ] ; then
	. ./.build-typetools-cf.sh
	cd $ROOT && ln -s $CF_LOCATION ./$ENABLED_CF
	exit 0
fi

case $1 in
	-r | --release )
		echo "execute build-released-cf.sh"
		;;
	-h | --help )
		echo "=====usage====="
		echo "-r | --release: build newest released version of checker framework and link it to ../enabled-checker-framework"
		echo "default(no argument passing): build newest version of checker framework from type tools and link it to ../enabled-checker-framework"
		echo "=====end====="
		;;
	* )
		echo "I don't know this argument, please try -h | --help for usage information."
		;;
esac
