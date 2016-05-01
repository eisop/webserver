#!/bin/bash

# Fail the whole script if any command fails
set -e

# first change to script directory so relative path works again
cd $(dirname "$0")

make -C ../CheckerPrinter clean all

if [ -L '../enabled-checker-framework' ] ; then
	echo "Note: detect a symbolic link of enabled-checker-framework before deployment, thus I do not clone or building checker framework."
else
	./build-checker-framework.sh $1 $2
fi

echo "===TEST CheckerPrinter and run-checker.sh==="
cd ../test && ./test.sh

echo "REMINDER: still need to config wsgi file if need to mount to apache2. see README in wsgi-scripts/ about how to config\n"
