import java.util.*;
import org.checkerframework.checker.aliasing.qual.*;

// Annotations on the StringBuffer class, used in the examples below.
// class StringBuffer {
//  @Unique StringBuffer();
//  StringBuffer append(@LeakedToResult StringBuffer this, @NonLeaked String s);
// }

public class AliasingExample {
    public void foo() {
        StringBuffer sb = new StringBuffer();    // sb is refined to @Unique.

        StringBuffer sb2 = sb;                   // sb loses its refinement.
        // Both sb and sb2 have aliases and because of that have type @MaybeAliased.
    }

    public void bar() {
        StringBuffer sb = new StringBuffer();     // sb is refined to @Unique.

        sb.append("someString");
        // sb stays @Unique, as no aliases are created.

        StringBuffer sb2 = sb.append("someString");
        // sb is leaked and becomes @MaybeAliased.

        // Both sb and sb2 have aliases and because of that have type @MaybeAliased.
    }
}

    
