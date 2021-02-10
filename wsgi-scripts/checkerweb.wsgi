import os, sys
from os.path import dirname, join, abspath

# Change working directory so relative paths (and template lookup) work again
checkerweb_path = dirname(dirname(abspath(__file__)))
os.chdir(checkerweb_path)

#set file path here so we can import our real server bottle_server
sys.path.insert(0, checkerweb_path)

import bottle_server
import bottle

bottle_server.cfPath = join(checkerweb_path, "released-checker-framework")

# ... build or import your bottle application here ...
# Do NOT use bottle.run() with mod_wsgi
application = bottle.default_app()
