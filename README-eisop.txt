Some notes on setting up the RHEL 7 VM:

- Clone the repository into /var/www/checkerweb
- In the clone, run deploy-checkerweb.sh
- sudo yum install mod_wsgi
- Edit /etc/httpd/conf.modules.d/00-base.conf and add:

LoadModule macro_module modules/mod_macro.so

- In /etc/httpd/conf.d do:

ln -s /var/www/checkerweb/wsgi-scripts/checkerweb-wsgi.conf .

- Restart Apache:

sudo apachectl restart

TODO: Move Ubuntu instructions from wsgi-scripts to here
