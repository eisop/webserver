import org.checkerframework.checker.lock.qual.*;

public class HoldingExampleWithWarnings {
	
  void helper1(@GuardedBy("MyClass.myLock") Object a) {
    a.toString(); // ILLEGAL: the lock is not held
    synchronized(MyClass.myLock) {
      a.toString();  // OK: the lock is held
    }
  }

  @Holding("MyClass.myLock")
  void helper2(@GuardedBy("MyClass.myLock") Object b) {
    b.toString(); // OK: the lock is held
  }

  void helper3(Object c) {
    helper1(c); // OK: passing a subtype in place of a the @GuardedBy supertype
    c.toString(); // OK: no lock constraints
  }

  void helper4(@GuardedBy("MyClass.myLock") Object d) {
    d.toString(); // ILLEGAL: the lock is not held
  }

  void myMethod2(@GuardedBy("MyClass.myLock") Object e) {
    helper1(e);  // OK to pass to another routine without holding the lock
    e.toString(); // ILLEGAL: the lock is not held
    synchronized (MyClass.myLock) {
      helper2(e);
      helper3(e);
      helper4(e); // OK, but helper4’s body still does not type-check
        }  

    }

}

class MyClass {
	public static Object myLock = new Object();
}
