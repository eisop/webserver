/*****************************************************************************
CHECKER2JSON: util class for printing check result to JSON format
This file is modified based on the traceprinter package from java_jail:https://github.com/daveagp/java_jail 
with  GNU AFFERO GENERAL PUBLIC LICENSE Version 3. 
Modified by Charles Chen (charleszhuochen@gmail.com) Mar 2016
===original doc as below===
traceprinter: a Java package to print traces of Java programs
David Pritchard (daveagp@gmail.com), created May 2013

The contents of this directory are released under the GNU Affero 
General Public License, versions 3 or later. See LICENSE or visit:
http://www.gnu.org/licenses/agpl.html

See README for documentation on this package.

******************************************************************************/

package checkerprinter;

import javax.json.*;

public class CHECKER2JSON {
 
    public static StringBuilder userlogged;

    public static void userlog(String S) {
        if (userlogged == null) userlogged = new StringBuilder();
        userlogged.append(S).append("\n");
    }
    
    static JsonObject ExceptionError(String usercode, String msg) {
    	JsonArrayBuilder exceptionMsg = Json.createArrayBuilder().add(
    			Json.createObjectBuilder()
    			.add("type", "exception")
    			.add("msg", msg)
    			.build());
    	return output(usercode, exceptionMsg.build());
    }

    static JsonObjectBuilder compileError(String errmsg, String msgtype, long row, long col) {
    	return Json.createObjectBuilder()
    	     .add("line", ""+row)
    	     .add("type", msgtype)
    	     .add("offset", ""+col)
    	     .add("exception_msg", errmsg);
        }

    static String fakify(String realcode) {
	String[] x = realcode.split("\n", -1);
	for (int i=0; i<x.length; i++) {
	    int pos = x[i].indexOf("//><");
	    if (pos >= 0)
		x[i] = x[i].substring(pos+4);	    
	}
	StringBuilder sb = new StringBuilder();
	for (String s:x) {sb.append("\n");sb.append(s);}
	return sb.substring(1);
    }

    static JsonObject output(String usercode, JsonArray errorReport) {
        JsonObjectBuilder result = Json.createObjectBuilder();
        result
            .add("code", fakify(usercode))
            .add("error_report", errorReport);
        if (userlogged != null) result.add("userlog", userlogged.toString());
        return result.build();
    }
}
