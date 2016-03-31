import org.checkerframework.checker.lock.qual.*;

public class GuardedByExampleWithWarnings {
	private static Object myLock = new Object();

	@GuardedBy("GuardedByExampleWithWarnings.myLock") Object myMethod() {
		Object o = new Object();
		return o;
	}
	
	public void test() {
	// reassignments without holding the lock are OK.
 	@GuardedBy("GuardedByExampleWithWarnings.myLock") Object x = myMethod();
 	@GuardedBy("GuardedByExampleWithWarnings.myLock") Object y = x;
 	x.toString(); // ILLEGAL because the lock is not held
 	synchronized(GuardedByExampleWithWarnings.myLock) {
  	y.toString();  // OK: the lock is held
  		}
	}
	
}
