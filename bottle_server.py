# This wsgi server for the Checker Framework is
# based on Online Python Tutor's OPT server:
# https://github.com/pgbovine/OnlinePythonTutor/ with MIT Lisence
#
# Major Modification:
# 1. route_static : a) using template to render index page
#    b) add 'static' prefix in @route('<filepath:path>') to get better
#       file struture
#    c) pass bottle.get_url to index template, in order
#       to generate correct url inside index page.
# 2. exec: a) using Popen to fork sub process
#    b) add a simple exception catch of sub process result
#    c) add explict encoding transfer on user_data
# 3. to mount to apache: add appPath to get correct working directory
# of the instance of this wsgi server
#
#===Original comment of this file in OPT server as below===
# Lightweight OPT server that works on both Python 2 and 3

# to invoke, run 'python bottle_server.py'
# and visit http://localhost:8080/index.html
#
# external dependencies: bottle
#
# easy_install pip
# pip install bottle

# From an OPT user: A couple notes: to get bottle_server.py running,
# I had to replace cStringIO with io and urllib2 with urllib, for
# compatibility from 2.x to 3.x Ii was running from /v3/).

from os.path import join, dirname, abspath
import subprocess

from bottle import route, get, request, run, template, static_file, url, default_app, Bottle, TEMPLATE_PATH, abort, response
app = Bottle()
default_app.push(app)

import StringIO # NB: don't use cStringIO since it doesn't support unicode!!!
import json
# import pg_logger
import urllib
import urllib2

appPath = dirname(abspath(__file__))

cfPath = join(appPath, "dev-checker-framework")
isRise4Fun = False

@route('/')
@route('/static/<filepath:path>', name='static')
def route_static(filepath=None):
    if filepath is None:
        print("file path is None")
        # rise4fun is a web service, and it not serves as a web interface.
        # Thus we should deny user acess the index page from rise4fun url.
        if isRise4Fun:
          abort(401, "Sorry, access denied.")
        return template('index', root=appPath, get_url=app.get_url)
    return static_file(filepath, root=join(appPath, 'static'))

@get('/exec', name='exec')
def get_exec():
  java_backend = subprocess.Popen(['./shell-scripts/run-checker.sh', request.query.frontend_data.encode('utf8'),
    cfPath, str(isRise4Fun)], stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  (stdout, stderr) = java_backend.communicate()
  if java_backend.returncode != 0:
    print ("Error: CheckerPrinter failed %d %s %s" % (java_backend.returncode,stdout, stderr))
    result = json.dumps({'backend_status':'exception', 'exception_msg':'500 Server Internal Error.'})
    return result
  else:  
    result = stdout
  return result

if __name__ == "__main__":
    run(host='127.0.0.1', port=8081, reloader=True)
    # run(host='0.0.0.0', port=8081, reloader=True) # make it externally visible - DANGER this is very insecure since there's no sandboxing!
