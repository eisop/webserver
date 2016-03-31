import java.util.Locale;

/**If the Format String Checker issues no errors, it provides the following guarantees:
1. The following guarantees hold for every format method invocation:
  (a) The format method’s first parameter (or second if a Locale is provided) is 
      a valid format string (or null). 
  (b) A warning is issued if one of the format string’s conversion categories is UNUSED.
  (c) None of the format string’s conversion categories is NULL.
  
2. If the format arguments are passed to the format method as varargs, 
the Format String Checker guarantees the following additional properties:
  (a) No fewer format arguments are passed than required by the format string.
  (b) A warning is issued if more format arguments are passed than required by 
  the format string. 
  (c) Every format argument’s type satisfies its conversion category’s restrictions.
  
3. If the format arguments are passed to the format method as array,
a warning is issued by the Format String Checker.*/

public class FormatStringExampleWithWarnings {
  
  public void testMethod () {
    String.format("%d", 42); //OK
    String.format(Locale.GERMAN, "%d", 42); //OK
    String.format("%y"); // error (1a)
    String.format("%2$s", "unused", "used"); // warning (1b)
    String.format("%1$d %1$f", 5.5); // error (1c)
    String.format("%1$d %1$f %d", null, 6); // error (1c)
    String.format("%s"); // error (2a)
    String.format("%s", "used", "ignored"); // warning (2b)
    String.format("%c",4.2); // error (2c)
    String.format("%c", (String)null); // error (2c)
    String.format("%1$d %1$f", new Object[]{1}); // warning (3)
    String.format("%s", new Object[]{"hello"}); // warning (3)
    }
  }
