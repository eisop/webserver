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

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.json.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class JsonPrinter extends Printer {

    static JsonObjectBuilder buildException(String msg) {
        return Json.createObjectBuilder()
                .add("type", "exception")
                .add("msg", msg);
    }

    static JsonObjectBuilder buildCompileInfo(String errmsg, String msgtype, long row, long col) {
        return Json.createObjectBuilder()
             .add("line", ""+row)
             .add("type", msgtype)
             .add("offset", ""+col)
             .add("exception_msg", errmsg);
        }

    @Override
    public void printException(String msg) {
        JsonObjectBuilder output = Json.createObjectBuilder()
                .add("backend_status", "exception")
                .add("exception_msg", msg);
        if(usercode != null) {
            output.add("code", this.usercode);
        } else {
            output.add("code", "[could not get user code]");
        }
        printOutput(output.build());
    }

    @Override
    public void printSuccess() {
        assert this.usercode != null : " a success info should given based on non-null usercode";
        assert this.execCmd != null : "a success info should given based on non-null execute command";

        JsonObjectBuilder output = Json.createObjectBuilder()
                .add("backend_status", "pass")
                .add("code", this.usercode)
                .add("exec_cmd", this.execCmd)
                .add("cfg", this.cfg);
        printOutput(output.build());
    }

    @Override
    public void printDiagnosticReport(List<Diagnostic<? extends JavaFileObject>> diagnosticList) {
        assert this.usercode != null : "a diagnostic report should given based on non-null usercode";
        assert this.execCmd != null : "a diagnostic report should given based on non-null execute command";

        JsonObjectBuilder output = Json.createObjectBuilder()
                .add("backend_status", "diagnostic")
                .add("code", this.usercode)
                .add("exec_cmd", this.execCmd)
                .add("cfg", this.cfg);
        JsonArrayBuilder errorReportBuilder = Json.createArrayBuilder();

        for (Diagnostic<? extends JavaFileObject> err : diagnosticList) {
            Diagnostic.Kind errKind = err.getKind();
            String errHeader = "";
            String errType = "";
            if (errKind == Diagnostic.Kind.ERROR) {
                errHeader = "Error: ";
                errType = "error";
            } else if (errKind == Diagnostic.Kind.WARNING) {
                errHeader = "Warning: ";
                errType = "warning";
            } else if (errKind == Diagnostic.Kind.MANDATORY_WARNING) {
                errHeader = "Warning: ";
                errType = "warning";
            } else if (errKind == Diagnostic.Kind.NOTE) {
                errHeader = "Note: ";
                errType = "info";
            } else {
                this.printException("Error: Compiler doesn't work, please contact admin to report a bug.");
                return;
            }
            JsonObjectBuilder errorBuilder = 
                    JsonPrinter.buildCompileInfo(errHeader + err.getMessage(null), errType,
                            Math.max(0, err.getLineNumber()),
                            Math.max(0, err.getColumnNumber())
                            );
            errorReportBuilder.add(errorBuilder);
        }
        JsonArray errorReport = errorReportBuilder.build();

        assert errorReport.size() > 0 : "bytecode is null or diagnosticList is not empty, but error report is empty.";
        output.add("error_report", errorReport);
        printOutput(output.build());
    }

    void printOutput(JsonObject output) {
        try {
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            out.print(output);
        }
        catch (UnsupportedEncodingException e) { //fallback
            System.out.print(output);
        }
    }
}
