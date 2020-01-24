import org.checkerframework.checker.linear.qual.*;

class Pair {
    Object a;
    Object b;

    public String toString() {
        return "<" + String.valueOf(a) + "," + String.valueOf(b) + ">";
    }
}

public class LinearExampleWithWarnings {
    void print(@Linear Object arg) {
        System.out.println(arg);
    }

    @Linear Pair printAndReturn(@Linear Pair arg) {
        System.out.println(arg.a);
        System.out.println(arg.b); // OK: field dereferencing does not use up the reference arg
        return arg;
    }

    @Linear Object m(@Linear Object o, @Linear Pair lp, int a, int b) {
        @Linear Object lo2 = o; // ERROR: aliases may exist
        @Linear Pair lp3 = lp;
        @Linear Pair lp4 = lp; // ERROR: reference lp was already used
        // lp3.a;
        // lp3.b;                   // OK: field dereferencing does not use up the reference
        print(lp3);
        print(lp3); // ERROR: reference lp3 was already used
        // lp3.a;                   // ERROR: reference lp3 was already used
        @Linear Pair lp4 = new @Linear Pair();
        lp4.toString();
        lp4.toString(); // ERROR: reference lp4 was already used
        lp4 = new @Linear Pair(); // OK to reassign to a used-up reference
        // If you need a value back after passing it to a procedure, that
        // procedure must return it to you.
        lp4 = printAndReturn(lp4);
        if (a < b) { // some condition here, a < b just for illustration
            print(lp4);
        }
        if (b <= a) {
            return lp4; // ERROR: reference lp4 may have been used
        } else {
            return new @Linear Object();
        }
    }
}
