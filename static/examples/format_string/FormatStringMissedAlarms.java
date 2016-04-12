import java.util.Formattable;
import java.util.Formatter;

public class FormatStringMissedAlarms {
    public void testMethod() {
        // The format string checker issues no errors or warnings for the
        // following illegal invocations of format methods.
        String.format(null);          // NullPointerException (1)
        String.format("%s", new A()); // Error (2)
        String.format("%s", new B()); // Error (3)
        String.format("%c", (int)-1); // IllegalFormatCodePointException (4)
    }
}

class A {
    public String toString(){
        throw new Error();
    }
}
  
class B implements Formattable {
    public void formatTo(Formatter fmt, int f,
                         int width, int precision) {
        throw new Error();
    }
}
