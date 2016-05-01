# [Checker Framework](http://checkerframework.org) live demo webserver 

This is the live demo webserver of [Checker Framework](http://checkerframework.org)

This webserver is developed based on [Online Python Tutor](http://github.com/pgbovine/OnlinePythonTutor/) and [java jail](http://github.com/daveagp/java_jail)

## Production Server Deployment

### Prerequistites

1. [apache2 httpd server](http://www.apache.org)
2. two modules of apache2:
  1. [mod_wsgi](https://modwsgi.readthedocs.org/en/develop/installation.html)
  2. [mod_macros](https://httpd.apache.org/docs/2.4/mod/mod_macro.html)

Some notes on how to install these two modules in *RHEL 7* and *Ubuntu* please see here: https://github.com/eisop/webserver/blob/master/README-eisop.txt 

### Procedure

1. clone this project to /var/www directory:

  ```git clone https://github.com/eisop/webserver.git```
  
2. In the clone, run ./shell-scripts/deploy-checkerweb.sh with below options:
    
  1. If you want to deploy this server with newest developing version of checker framework from https://github.com/typetools/checker-framework, run:
  
    ```
    ./shell-scripts/deploy-checkerweb.sh
    ```
  2. If you want to deploy this server with newest released version of checker framework from http://checkerframework.org, run:
  
    ```
    ./shell-scripts/deploy-checkerweb.sh -r <url of the released checker framework zip>
    ```
    e.g.
    ```
    ./shell-scripts/deploy-checkerweb.sh -r http://types.cs.washington.edu/checker-framework/current/checker-framework-1.9.13.zip
    ```
  3. If you want to deploy this server with an existed local copy of checker framework, run:
  
    ```
    ./shell-scripts/deploy-checkerweb.sh -l <path to checker-framework>
    ```
    e.g.
    ```
    ./shell-scripts/deploy-checkerweb.sh -l ../jsr308/checker-framework
    ```
  
3. Customize the vhost configuration file: *wsgi-scripts/checkerweb-wsgi.conf*

  [Details Instruction of configuring vhost file](https://github.com/eisop/webserver/blob/master/wsgi-scripts/README)

4. Link this file to apache2 vhost directory:
  
  In *RHEL 7* do:
  ```
  cd /etc/httpd/conf.d
  ln -s /var/www/checkerweb/wsgi-scripts/checkerweb-wsgi.conf .
  ```

  In *Ubuntu* do:
  ```
  cd /etc/apache2/sites-enabled
  ln -s /var/www/checkerweb/wsgi-scripts/checkerweb-wsgi.conf .
  ```
  
5. Restart the apache:

  In *RHEL 7* do:
  ```sudo apachectl restart```
  
  In *Ubuntu* do:
  ```sudo service apache2 restart```

## Setting Up a Test Server

For the developers, there are two ways of setting up a test server. One way is running a server in terminal to see the changes immediately, and the other way is to deploy a test/develop version server on apache2.

Follow step 1 and 2 above to clone and install the web server, then make your changes.

Note: 

1. in step 2 when running `deploy-checkerweb.sh`, if you using `-l` option to link your local copy of checker framework, the `deploy-checkerweb.sh` will passing this location to `build-checker-framework.sh` and just simply link this location to `webserver/enabled-checker-framework`, which means it is your responsibility to ensure the passed location is correct.

2. You can simply 're-plug' the checker framework by running `build-checker-framework.sh -l <new location of cf>`  


### Running a server in terminal using Bottle Server
In the clone, directly run:
```python bottle_server.py```

This will active a bottle server listening on port 8081, and can be stoped by ctrl-C in the terminal.

This is the easiest way to run a test server to have a look of your changes immediately.

### Deploy a Test/Deployment Server on port 8081 on apache2

This version needs install [python mod_wsgi](https://pypi.python.org/pypi/mod_wsgi) first.

In the clone, run:
  ```
  ./shell-scripts/setup-and-run-8081.sh
  ```
Then you will have a server running by apache2 and listenning on port 8081.

#### Loading modifications to the test server

When you done some modifications and want to see the changes, in the clone, do:
  ```
  touch wsgi-scripts/checkerweb.wsgi
  ```
 Note:
 1. No need to restart apache2
 2. If you have modified *CheckerPrinter*, in the clone, execute `make -C CheckerPrinter clean all` first.
