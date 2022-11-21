#!/bin/sh

# $1 is a string representing a JSON object that the Java backend
# expects as input, such as: java_jail/cp/traceprinter/test-input.txt

# tricky! use a heredoc to pipe the $1 argument into the stdin of the
# java executable WITHOUT interpreting escape chars such as '\n' ...
# echo doesn't work here since it interprets '\n' and other chars
#
# TODO: use -Xmx512m if we need more memory

# first change to script directory so relative path works again
cd $(dirname "$0")

if [ "$(uname)" = "Darwin" ] ; then
  JAVA_HOME=${JAVA_HOME:-$(/usr/libexec/java_home)}
else
  JAVA_HOME=${JAVA_HOME:-$(dirname "$(dirname "$(readlink -f "$(command -v javac)")")")}
fi

# CF=$(cd ../enabled-checker-framework && pwd)
CF=$2

# if CF=$2 doesnt work, manually set CF to your local copy's path to the checker framework as shown
# CF=/home/[your_user]/var/www/webserver/[local_copy_folder]/checker-framework

IS_RISE4FUN=$3

cat <<ENDEND | $JAVA_HOME/bin/java -Xmx2500M -Xbootclasspath/a:$CF/checker/dist/javac.jar -ea -ea:com.sun.tools... -cp $CF/checker/dist/checker.jar:../CheckerPrinter/bin:../CheckerPrinter/javax.json-1.0.jar:$JAVA_HOME/lib/tools.jar checkerprinter.InMemory $CF $IS_RISE4FUN
$1
ENDEND
