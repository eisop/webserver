import org.checkerframework.checker.valuechecker.qual.*;
import java.util.*;

public class ConstantValueExample {

    public void flowSensitivityExample(boolean b) {
        int i = 1;     // i has type:  @IntVal({1}) int
        if (b) {
            i = 2;     // i now has type:  @IntVal({2}) int
        }
                       // i now has type:  @IntVal({1,2}) int
        i = i + 1;     // i now has type:  @IntVal({2,3}) int
    }

    @StaticallyExecutable @Pure
    public int myAdd(int a, int b) {
        return a + b;
    }

    public void bar() {
        int a = 5;            // a has type:  @IntVal({5}) int
        int b = 4;            // b has type:  @IntVal({4}) int
        int c = myAdd(a, b);  // c has type:  @IntVal({9}) int
    }
}