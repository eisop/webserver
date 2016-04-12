import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.dataflow.qual.SideEffectFree;

import java.util.Map;
import java.util.HashMap;

public class MapKeyExampleWithWarnings {
    private Map<String,Object> m;
    // The type of k defaults to @UnknownKeyFor String
    private String k;

    public MapKeyExampleWithWarnings () {
        this.m = new HashMap<String, Object>();
        k = "theKey";
    }

    public void myMethod() {	
        @KeyFor("m") String km;
        if (m.containsKey(k)) {
            km = k;   // OK: the type of k is refined to @KeyFor("m") String
            sideEffectFreeMethod();
            km = k;   // OK: the type of k is not affected by the method call
                      // and remains @KeyFor("m") String
            otherMethod();
            km = k; // error: At this point, the type of k is once again
                    // @UnknownKeyFor String, because otherMethod might have
                    // side-effected k such that it is no longer a key for map m.
        }
    }

    @SideEffectFree
    private void sideEffectFreeMethod() {
    }

    private void otherMethod() {
    }
}
