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

    String mainClass;
    String exceptionMsg;
    List<String> checkerOptionsList;
    Printer checkerPrinter;

    static final Map<String, String> checkerMap;
    static {
        HashMap<String, String> tempMap = new HashMap<String, String>();
        tempMap.put("nullness", "org.checkerframework.checker.nullness.NullnessChecker");
        tempMap.put("optional", "org.checkerframework.checker.optional.OptionalChecker");
        //since generally don't directly call map key checker, I mapping it to nullness here
        tempMap.put("map_key", "org.checkerframework.checker.nullness.NullnessChecker");
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
        tempMap.put("index", "org.checkerframework.checker.index.IndexChecker");
        checkerMap = Collections.unmodifiableMap(tempMap);
    }
    private final String CHECKER_FRAMEWORK;
    Map<String, byte[]> bytecode;

    public final static long startTime = System.currentTimeMillis();

    public static void main(String[] args) {
        assert args.length == 2 : "this program needs two command line arguments: "
                + "args[0]: location of checker framework"
                + "argd[1]: isRise4Fun, indicates whether should use Rise4FunPrinter";
            boolean isRise4Fun = Boolean.valueOf(args[1]);
            Printer checkerPrinter = null;
            if(isRise4Fun) {
                checkerPrinter = new Rise4FunPrinter();
            } else {
                checkerPrinter = new JsonPrinter();
            }

        try {
            new InMemory(Json.createReader(new InputStreamReader(System.in, "UTF-8")).readObject(), args[0],
                    checkerPrinter);
        } 
        catch (IOException e) {
            checkerPrinter.setUsercode(null);
            checkerPrinter.printException("Internal IOException");
        }
    }

    protected boolean initCheckerArgs(JsonObject optionsObject) {
        String checker = InMemory.checkerMap.get(optionsObject.getString("checker"));
        if (checker == null) {
            this.exceptionMsg = "Error: Cannot find indicated checker.";
            return false;
        }
        this.checkerOptionsList = Arrays.asList( "-Xbootclasspath/p:" +
                this.CHECKER_FRAMEWORK + "/checker/dist/jdk8.jar",
                "-processor",
                checker);
        if (optionsObject.getBoolean("has_cfg")) {
            // String cfgLevel = optionsObject.getString("cfg_level");
            // TODO: add CFG Visualization
        }
        return true;
    }

    // figure out the class name, then compile and run main([])
    InMemory(JsonObject frontend_data, String enabled_cf, Printer checkerPrinter) {
        String usercode = frontend_data.getJsonString("usercode").getString();
        this.CHECKER_FRAMEWORK = enabled_cf;
        this.checkerPrinter = checkerPrinter;
        this.checkerPrinter.setUsercode(usercode);
        if (!initCheckerArgs(frontend_data.getJsonObject("options"))) {
            this.checkerPrinter.printException(this.exceptionMsg);
            return;
        }
        // not 100% accurate, if people have multiple top-level classes + public inner classes
        // first search public class, to avoid wrong catching a classname from  comments in usercode
        // as the public class name (if the comment before the public class declaration contains a "class" word)
        Pattern p = Pattern.compile("public\\s+class\\s+([a-zA-Z0-9_]+)\\b");
        Matcher m = p.matcher(usercode);
        if (!m.find()) {
            // if usercode does not have a public class, then is safe to using looser rgex to catch class name
            p = Pattern.compile("class\\s+([a-zA-Z0-9_]+)\\b");
            m = p.matcher(usercode);
            if(!m.find()) {
                this.exceptionMsg = "Error: Make sure your code includes at least one 'class \u00ABClassName\u00BB'";
                this.checkerPrinter.printException(this.exceptionMsg);
                return;
            }
        }
        mainClass = m.group(1);
        System.err.println(mainClass);
        CompileToBytes c2b = new CompileToBytes();

        c2b.compilerOutput = new StringWriter();
        c2b.options = this.checkerOptionsList;

        DiagnosticCollector<JavaFileObject> errorCollector = new DiagnosticCollector<>();
        c2b.diagnosticListener = errorCollector;

        bytecode = c2b.compileFile(mainClass, usercode);

        List<Diagnostic<? extends JavaFileObject>> diagnosticList = errorCollector.getDiagnostics();

        assert this.checkerOptionsList.size() > 1 : "at least should have -Xbootclasspath/p: flag";

        this.checkerPrinter.setExecCmd(this.checkerOptionsList
                .subList(1, this.checkerOptionsList.size()));

        if(bytecode != null && diagnosticList.size() == 0){
            this.checkerPrinter.printSuccess();
        } else {
            this.checkerPrinter.printDiagnosticReport(diagnosticList);
        }
    } 
}
