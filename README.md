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
  
2. In the clone, run deploy-checkerweb.sh

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

Follow step 1 and 2 above to clone and install the web server, then make your changes

Note: 

1. in step 2, `deploy-checkerweb.sh` will call `build-checker-framework.sh` to auto-clone and build the newest version of Checker Framework. If you want to use your own existing local copy of CF, in the clone, before doing step 2, create a symbolic link of the root directory which contains all three components (`annotation-tools`, `checker-framework` and `jsr308-langtools`):

```ln -s <your root jsr 308 directory> ./jsr308```

the file structure of `your root jsr 308 directory` should like this:
- jsr308/
  - annotation-tools/
  - checker-framework/
  - jsr308-langtools/

If you use a symbolic link rather than using the defualt clone-and-building in `build-checker-framework.sh`, then `build-checker-framework.sh` would detect this symbolic link and do nothing at all (either clone or pull).

### Running a server in terminal using Bottle Server
In the clone, directly run:
```python bottle_server.py```

This will active a bottle server listening on port 8081, and can be stoped by ctrl-C in the terminal.

This is the easiest way to run a test server to have a look of your changes immediately.

### Deploy a Test/Deployment Server on port 8081 on apache2

This version needs install [python mod_wsgi](https://pypi.python.org/pypi/mod_wsgi) first.

In the clone, run:
  ```
  setup-and-run-8081.sh
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
