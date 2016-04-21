#!/bin/bash

# Fail the whole script if any command fails
set -e

ENABLED_CF=enabled-checker-framework

if [ "$1" == "" ] ; then
	./build-typetools-cf.sh
	# if [ "$?" != 0 ] ; then
	# 	echo "error in build-typetools-cf.sh. Failed and exist."
	# 	exit 1
	# fi
	cd ../ && ln -s ./jsr308/checker-framework ./$ENABLED_CF
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
