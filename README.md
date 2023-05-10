# [Checker Framework](http://checkerframework.org) live demo webserver

This is the live demo webserver for the [EISOP Checker
Framework](https://eisop.github.io/).

This webserver is developed based on the [Online Python
Tutor](http://github.com/pgbovine/OnlinePythonTutor/) and [java
jail](http://github.com/daveagp/java_jail).


## Production Server Deployment

### Prerequistites

1. [apache2 httpd server](http://www.apache.org)
2. two modules of apache2:
  1. [mod_wsgi](https://modwsgi.readthedocs.org/en/develop/installation.html)
  2. [mod_macros](https://httpd.apache.org/docs/2.4/mod/mod_macro.html)

Some notes on how to install these two modules in *RHEL 7* and
*Ubuntu* please see here:
https://github.com/eisop/webserver/blob/master/README-eisop.txt


### Procedure

1. Clone this project in the `/var/www` directory:

    ```
    git clone https://github.com/eisop/webserver.git
    ```

2. In the clone, run `./shell-scripts/deploy-checkerweb.sh` with one
   of these options:

* If you want to deploy this server with the newest development version
  of the Checker Framework from
  `https://github.com/typetools/checker-framework`, run:

    ```
    ./shell-scripts/deploy-checkerweb.sh
    ```

* If you want to deploy this server with the newest released
  version of the Checker Framework from `http://checkerframework.org`,
  run:

    ```
    ./shell-scripts/deploy-checkerweb.sh -r <url of the released Checker Framework zip>
    ```
    e.g.
    ```
    ./shell-scripts/deploy-checkerweb.sh -r https://github.com/typetools/checker-framework/releases/download/checker-framework-3.10.0/checker-framework-3.10.0.zip
    ```

* If you want to deploy this server with an existing local copy of
  the Checker Framework, run:

    ```
    ./shell-scripts/deploy-checkerweb.sh -l <path to checker-framework>
    ```
    e.g.
    ```
    ./shell-scripts/deploy-checkerweb.sh -l ../jsr308/checker-framework
    ```

3. Customize the vhost configuration file: *wsgi-scripts/checkerweb-wsgi.conf*

  [Detailed instructions for configuring the vhost file](https://github.com/eisop/webserver/blob/master/wsgi-scripts/README)

4. Link this file to the apache2 vhost directory:

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

5. Restart the apache2 server:

  In *RHEL 7* do:
  ```
  sudo apachectl restart
  ```

  In *Ubuntu* do:
  ```
  sudo service apache2 restart
  ```


## Setting Up a Test Server

For developers, there are two ways of setting up a test
server. One way is running a server in a terminal to see changes
immediately, and the other way is to deploy a test/development version
server to apache2.

Follow steps 1 and 2 above to clone and install the web server, then
make your changes.

Note:

1. in step 2 when running `deploy-checkerweb.sh`, if you use the `-l`
option to link your local copy of Checker Framework, the
`deploy-checkerweb.sh` will pass this location to
`build-checker-framework.sh` and link this location to
`webserver/enabled-checker-framework`, which means it is your
responsibility to ensure the passed location is correct.

2. You can simply 're-plug' the Checker Framework by running
`build-checker-framework.sh -l <new location of cf>`


### Running a server in a terminal using Bottle Server

In the clone, directly run:
```
python bottle_server.py
```

This will active a bottle server listening on port 8081, and can be
stopped by ctrl-C in the terminal.

This is the easiest way to run a test server to have a look at your
changes immediately.


### Deploy a test/development server on port 8081 on apache2

This version needs installation of
[python mod_wsgi](https://pypi.python.org/pypi/mod_wsgi) first.

In the clone, run:
  ```
  ./shell-scripts/setup-and-run-8081.sh
  ```
Then you will have a server running apache2 and listening on port 8081.


#### Loading modifications to the test server

When you're done with modifications and you want to see the changes,
  in the clone, do:
  ```
  touch wsgi-scripts/checkerweb.wsgi
  ```

Note:
 1. No need to restart apache2
 2. If you have modified `CheckerPrinter`, in the clone, execute `make
 -C CheckerPrinter clean all` first.
