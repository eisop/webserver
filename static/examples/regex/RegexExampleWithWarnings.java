import java.util.regex.*;

public class RegexExampleWithWarnings {

    public void testMethod() {
        String regexStr = "colou?r";
        Pattern p = Pattern.compile(regexStr);
        Matcher m = p.matcher("color");
        String s = /*infered with @Regex(0)*/ m.group(1); // illegal, because only 0 groups
        // guarrenteed in m
    }

}
