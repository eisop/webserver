#!/bin/bash

function build_released_cf () {

CF_NAME_REGEX='^http://types.cs.washington.edu/checker-framework/current/(checker-framework-[1-9\.]+).zip$'

if [ "$1" == "" ] ; then
	echo "missing url, please input the url of newest released version of checker framework from types.cs.washington.edu"
	exit 1
fi

if [[ $1 =~ $CF_NAME_REGEX ]] ; then
	CF_NAME=${BASH_REMATCH[1]}
	if [ -d $ROOT/$CF_NAME ] ; then
		echo "detected directory $ROOT/$CF_NAME, thus I don't download it again"
		export CF_LOCATION=$ROOT/$CF_NAME	
		return
	fi
	echo "downloading released version: $CF_NAME"
	wget $1 -O $ROOT/$CF_NAME.zip
	
	unzip $ROOT/$CF_NAME.zip -d $ROOT/
	export CF_LOCATION=$ROOT/$CF_NAME
	return
else
	echo "the url does not match regular expression $CF_NAME_REGEX, please check your url."
	exit 1
fi
}

build_released_cf $1

