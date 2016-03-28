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

import subprocess

from bottle import route, get, request, run, template, static_file
import StringIO # NB: don't use cStringIO since it doesn't support unicode!!!
import json
# import pg_logger
import urllib
import urllib2


@route('/<filepath:path>')
def index(filepath):
    # # special-case for testing name_lookup.py ...
    # if 'name_lookup.py' in filepath:
    #     return json.dumps(dict(name='TEST NAME', email='TEST EMAIL'))
    return static_file(filepath, root='.')

@get('/exec')
def get_exec():
  out_s = StringIO.StringIO()

  def json_finalizer(input_code, output_trace):
    ret = dict(code=input_code, trace=output_trace)
    json_output = json.dumps(ret, indent=None)
    out_s.write(json_output)

  java_backend = subprocess.Popen(['./run-checker.sh', request.query.frontend_data],
    stdout=subprocess.PIPE, stderr=subprocess.PIPE)
  (stdout, stderr) = java_backend.communicate()
  if java_backend.returncode != 0:
    print ("Error: CheckerPrinter failed %d %s %s" % (java_backend.returncode,stdout, stderr))
    result = json.dumps({'error_report': [{'type':'exception', 'msg':'500 Server Internal Error.'},]})
    return result
  else:  
    result = stdout
  return result

if __name__ == "__main__":
    run(host='127.0.0.1', port=8080, reloader=True)
    # run(host='0.0.0.0', port=8003, reloader=True) # make it externally visible - DANGER this is very insecure since there's no sandboxing!
