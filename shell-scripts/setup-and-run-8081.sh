#!/bin/bash
# This file needs to be run AFTER the deploy-checkerweb.sh script
if [ ! -d "../jsr308" ] ; then
    echo "Did not find jsr308 dir, did you run the deploy-checkerweb.sh first?"
    exit 1
fi

if [ ! -d "../config-8081" ] ; then
    mkdir config-8081 ;
fi

if [ ! -d "../log-8081" ] ; then
    mkdir log-8081 ;
fi

mod_wsgi-express setup-server \
		 --port 8081 wsgi-scripts/checkerweb.wsgi \
		 --server-root=../config-8081 \
		 --log-directory ../log-8081 \
		 --error-log-name error.log \
		 --access-log-name acess.log

../config-8081/apachectl start
