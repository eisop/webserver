#!/bin/bash
# This file needs to be run AFTER the deploy-checkerweb.sh script

if [ ! -d "config-8081" ] ; then
    mkdir config-8081 ;
fi

mod_wsgi-express setup-server \
		 --port 8081 \
		 wsgi-scripts/checkerweb.wsgi \
		 --server-root=./config-8081
./config-8081/apachectl start
