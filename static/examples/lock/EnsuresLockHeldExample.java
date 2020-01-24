import java.util.concurrent.locks.ReentrantLock;
import org.checkerframework.checker.lock.qual.*;

public class EnsuresLockHeldExample {

    private final ReentrantLock lock = new ReentrantLock();

    /* @EnsuresLockHeld, the given expressions are known to be objects
     * used as locks and are known to be in a locked state after the
     * method returns; this is useful for annotating a method that
     * takes a lock. */
    @EnsuresLockHeld("lock")
    public void enter() {
        lock.lock();
    }

    /* With @EnsuresLockHeldIf, if the annotated method returns the
     * given boolean value (true or false), the given expressions are
     * known to be objects used as locks and are known to be in a
     * locked state after the method returns; */
    @EnsuresLockHeldIf(expression = "lock", result = true)
    public boolean tryEnter() {
        return lock.tryLock();
    }
}
