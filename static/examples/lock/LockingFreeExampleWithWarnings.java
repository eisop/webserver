import org.checkerframework.checker.lock.qual.*;
import org.checkerframework.dataflow.qual.SideEffectFree;

import java.util.concurrent.locks.ReentrantLock;

public class LockingFreeExampleWithWarnings {
    private Object myField;
    private final ReentrantLock lock; // Initialized in the constructor
    private @GuardedBy("lock") Object x; // Initialized in the constructor

    public LockingFreeExampleWithWarnings () {
        this.lock = new ReentrantLock();
        this.x = new Object();
    }

    /* @LockingFree is useful when a method does not make any use of
     * synchronization or locks but causes other side effects
     * (hence @SideEffectFree is not appropriate).
     * @SideEffectFree implies @LockingFree, therefore if both are applicable,
     * you should only write @SideEffectFree. */
    @LockingFree
    // This method does not use locks or synchronization but cannot
    // be annotated as @SideEffectFree since it alters myField.
    void myLockingFreeMethod() {
        myField = new Object();
    }

    @SideEffectFree
    int mySideEffectFreeMethod() {
        return 0;
    }

    @MayReleaseLocks
    void myUnlockingMethod() {
        lock.unlock();
    }

    void myUnannotatedEmptyMethod() {
    }

    @MayReleaseLocks
    void clientMethod() {
        if (lock.tryLock()) {
            x.toString(); // OK: the lock is held
            myLockingFreeMethod();
            x.toString(); // OK: the lock is still known to be held since myLockingFreeMethod is locking-free
            mySideEffectFreeMethod();
            x.toString(); // OK: the lock is still known to be held since mySideEffectFreeMethod
                          // is side-effect-free
            myUnlockingMethod();
            x.toString(); // ILLEGAL: myUnlockingMethod may have released a lock
        }

        if (lock.tryLock()) {
            x.toString(); // OK: the lock is held
        }
        if (lock.isHeldByCurrentThread()) {
            x.toString(); // OK: the lock is known to be held
        }
    }
}
