import org.checkerframework.checker.lock.qual.*;

public class GuardedByExampleWithWarnings {
    private static final Object myLock = new Object();

    @GuardedBy("GuardedByExampleWithWarnings.myLock") MyClass myMethod() {
        @GuardedBy("GuardedByExampleWithWarnings.myLock") MyClass m = new MyClass();
        return m;
    }

    public void test() {
        // reassignments without holding the lock are OK.
        @GuardedBy("GuardedByExampleWithWarnings.myLock") MyClass x = myMethod();
        @GuardedBy("GuardedByExampleWithWarnings.myLock") MyClass y = x;
        x.toString(); // ILLEGAL because the lock is not held
        x.field = new Object(); // ILLEGAL because the lock is not held
        synchronized (GuardedByExampleWithWarnings.myLock) {
            y.toString(); // OK: the lock is held
            y.field = new Object(); // OK: the lock is held
        }
    }
}

class MyClass {
    public Object field;
}
