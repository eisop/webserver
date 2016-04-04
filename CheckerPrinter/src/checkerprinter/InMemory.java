/*****************************************************************************
InMemory: major entry of checkerprinter
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

This file was originally based on 
com.sun.tools.example.trace.Trace, written by Robert Field.

******************************************************************************/

package checkerprinter;

import java.util.regex.*;
import java.util.*;
import java.io.*;

import javax.tools.*;

import traceprinter.ramtools.*;

import javax.json.*;

public class InMemory {

    String usercode;
    String mainClass;
    List<String> checkerOptionsList;
    static final Map<String, String> checkerMap;
    static {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("nullness", "org.checkerframework.checker.nullness.NullnessChecker");
        tempMap.put("regex", "org.checkerframework.checker.regex.RegexChecker");
        tempMap.put("interning", "org.checkerframework.checker.interning.InterningChecker");
        tempMap.put("aliasing", "org.checkerframework.common.aliasing.AliasingChecker");
        tempMap.put("lock", "org.checkerframework.checker.lock.LockChecker");
        tempMap.put("fake_enum", "org.checkerframework.checker.fenum.FenumChecker");
        tempMap.put("tainting", "org.checkerframework.checker.tainting.TaintingChecker");
        tempMap.put("format_string", "org.checkerframework.checker.formatter.FormatterChecker");
        tempMap.put("linear", "org.checkerframework.checker.linear.LinearChecker");
        tempMap.put("igj", "org.checkerframework.checker.igj.IGJChecker");
        tempMap.put("javari", "org.checkerframework.checker.javari.JavariChecker");
        tempMap.put("signature", "org.checkerframework.checker.signature.SignatureChecker");
        tempMap.put("gui_effect", "org.checkerframework.checker.guieffect.GuiEffectChecker");
        tempMap.put("units", "org.checkerframework.checker.units.UnitsChecker");
        tempMap.put("cons_value", "org.checkerframework.common.value.ValueChecker");
        checkerMap = Collections.unmodifiableMap(tempMap);
    }
    private final String JSR308;
    Map<String, byte[]> bytecode;

    public final static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        try {
            new InMemory(
                         Json.createReader(new InputStreamReader
                                           (System.in, "UTF-8"))
                         .readObject(), args[0]);
        } 
        catch (IOException e) {
            System.out.print(CHECKER2JSON.ExceptionError("[could not read user code]",
                                                         "Internal IOException"));
        }
    }
    
    protected boolean initCheckerArgs(JsonObject optionsObject) {
    	String checker = InMemory.checkerMap.get(optionsObject.getString("checker"));
    	if(checker == null) {
    		System.out.print(CHECKER2JSON.ExceptionError(this.usercode, 
    		        "Error: Cannot found indicated checker."));
    		return false;
    	}
    	this.checkerOptionsList = Arrays.asList(
    	        "-Xbootclasspath/p:" +
    	        this.JSR308 + "/checker-framework/checker/jdk/annotated:" +
    	        this.JSR308 + "/checker-framework/checker/dist/jdk8.jar",
        		"-processor",
        		checker);
    	if(optionsObject.getBoolean("has_cfg")) {
//    		String cfgLevel = optionsObject.getString("cfg_level");
    		//TODO: add CFG Visualization
    	}
    	return true;
    }
    
    
    // figure out the class name, then compile and run main([])
    InMemory(JsonObject frontend_data, String JSR308) {
        this.usercode = frontend_data.getJsonString("usercode").getString();
        this.JSR308 = JSR308;
        if(!initCheckerArgs(frontend_data.getJsonObject("options")))
        	return;
        

        // not 100% accurate, if people have multiple top-level classes + public inner classes
        Pattern p = Pattern.compile("public\\s+class\\s+([a-zA-Z0-9_]+)\\b");
        Matcher m = p.matcher(usercode);
        if (!m.find()) {
        	System.out.print(CHECKER2JSON.ExceptionError(this.usercode, 
        			"Error: Make sure your code includes 'public class \u00ABClassName\u00BB'"));
            return;
        }

        mainClass = m.group(1);

        CompileToBytes c2b = new CompileToBytes();

        c2b.compilerOutput = new StringWriter();
        c2b.options = this.checkerOptionsList;

        DiagnosticCollector<JavaFileObject> errorCollector = new DiagnosticCollector<>();
        c2b.diagnosticListener = errorCollector;

            bytecode = c2b.compileFile(mainClass, usercode);
        
        printCheckerReport(errorCollector);
    }
    
    protected void printCheckerReport(DiagnosticCollector<JavaFileObject> errorCollector) {
    	JsonArrayBuilder errorReportBuilder = Json.createArrayBuilder();
    	List<Diagnostic<? extends JavaFileObject>> diagnosticList = errorCollector.getDiagnostics();
    	if(bytecode != null && diagnosticList.size() == 0) {
    		errorReportBuilder.add(Json.createObjectBuilder()
    				.add("type", "pass"));
    		System.out.print(CHECKER2JSON.output(this.usercode, errorReportBuilder.build()));
    		return;
    	}

    	for (Diagnostic<? extends JavaFileObject> err : diagnosticList){
//    	    System.out.println(err.getKind());
    	    Diagnostic.Kind errKind = err.getKind();
    	    String errHeader = "";
    	    String errType = "";
    		if(errKind == Diagnostic.Kind.ERROR) {
    		    errHeader = "Error: ";
    		    errType = "error";
    		} else if(errKind == Diagnostic.Kind.WARNING) {
    		    errHeader = "Warning: ";
    		    errType = "warning";
    		} else if(errKind == Diagnostic.Kind.MANDATORY_WARNING) {
    		    errHeader = "Warning: ";
                errType = "warning";
    		} else if(errKind == Diagnostic.Kind.NOTE) {
    		    errHeader = "Note: ";
                errType = "info";
            } else {
    			System.out.print(CHECKER2JSON.ExceptionError(this.usercode, 
    					"Error: Compiler doesn't work, please contact admin to report a bug."));
    			return;
    		}
    		JsonObjectBuilder errorBuilder = 
                    CHECKER2JSON.compileError(errHeader + err.getMessage(null), errType,
                            Math.max(0, err.getLineNumber()),
                            Math.max(0, err.getColumnNumber())
                            );
                    errorReportBuilder.add(errorBuilder);
    		
    	}
        JsonArray errorReport = errorReportBuilder.build();
        
        assert errorReport.size() > 0 : "bytecode is null or diagnosticList is not empty, but error report is empty.";
        
    	JsonObject output = CHECKER2JSON.output(usercode, errorReport);
    	
    	try {
            PrintStream out = new PrintStream(System.out, true, "UTF-8");
            out.print(output);
        }
        catch (UnsupportedEncodingException e) { //fallback
            System.out.print(output);
        }
    }
    
}
