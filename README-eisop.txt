== Some notes on setting up the RHEL 7 VM:

- Clone the repository into /var/www/checkerweb
- In the clone, run deploy-checkerweb.sh
- sudo yum install mod_wsgi
- Edit /etc/httpd/conf.modules.d/00-base.conf and add:

LoadModule macro_module modules/mod_macro.so

- In /etc/httpd/conf.d do:

ln -s /var/www/checkerweb/wsgi-scripts/checkerweb-wsgi.conf .

- Restart Apache:

sudo apachectl restart

== Some notes on setting up in Ubuntu:
prerequisites: installed apache2

- clone the repository into /var/www/checkerweb
- In the clone, run deploy-checkerweb.sh
- apt-get install libapache2-mod-wsgi
- apt-get install libapache2-mod-macro
- a2enmod macro (this command actives macro module)

- In /etc/apache2/sites-enabled do:
ln -s /var/www/checkerweb/wsgi-scripts/checkerweb-wsgi.conf .

- Restart Apache:

sudo service apache2 restart

== Reference of wsgi configuration
The wsgi configuration is based on the tutorial on modwsgi official site:
http://modwsgi.readthedocs.org/en/develop/user-guides/quick-configuration-guide.html
There also has a more detailed configuration guide in this site:
http://modwsgi.readthedocs.org/en/develop/user-guides/configuration-guidelines.html
