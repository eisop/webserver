import os, sys
from os.path import dirname
# Change working directory so relative paths (and template lookup) work again
# os.chdir(os.path.dirname(__file__))
checkerweb_path = dirname(dirname(__file__))
os.chdir(checkerweb_path)
# os.chdir('/var/www/checkerweb')

#set file path here so we can import our real server bottle_server
sys.path.insert(0, checkerweb_path)
import bottle_server
import bottle
# ... build or import your bottle application here ...
# Do NOT use bottle.run() with mod_wsgi
application = bottle.default_app()
