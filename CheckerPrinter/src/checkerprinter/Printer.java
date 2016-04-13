package checkerprinter;

import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public abstract class Printer {
    String execCmd;
    String usercode;

    public abstract void printException(String msg);
    public abstract void printSuccess();
    public abstract void printDiagnosticReport(List<Diagnostic<? extends JavaFileObject>> diagnosticList);

    public void setUsercode(String realcode) {
        if (realcode != null) {
            this.usercode  = fakify(realcode);
        } else {
            this.usercode = null;
        }
    }

    public void setExecCmd(List<String> optionsList) {
       StringBuilder cmdSb = new StringBuilder().append("javac");
       for (String option : optionsList) {
           cmdSb.append(" "+option);
       }
       this.execCmd = cmdSb.toString();
    }

    String fakify(String realcode) {
        String[] x = realcode.split("\n", -1);
        for (int i = 0; i < x.length; i++) {
            int pos = x[i].indexOf("//><");
            if (pos >= 0) {
                x[i] = x[i].substring(pos+4);
            }
        }
        StringBuilder sb = new StringBuilder();
        for (String s : x) {
            sb.append("\n");
            sb.append(s);
        }
        return sb.substring(1);
    }
}
