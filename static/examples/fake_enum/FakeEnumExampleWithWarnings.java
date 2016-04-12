import org.checkerframework.checker.fenum.qual.*;

@SuppressWarnings("fenum:assignment.type.incompatible") // initialization of fake enums
class TestStatic {
    public static final @Fenum("A") int ACONST1 = 1;
    public static final @Fenum("A") int ACONST2 = 2;
    public static final @Fenum("B") int BCONST1 = 4;
    public static final @Fenum("B") int BCONST2 = 5;
}

public class FakeEnumExampleWithWarnings {
    @Fenum("A") int state1 = TestStatic.ACONST1; // ok
    @Fenum("B") int state2 = TestStatic.ACONST1; // Incompatible fenums forbidden!
    void fenumArg(@Fenum("A") int p) {}

    void foo() {
        state1 = 4; // Direct use of value forbidden!
        state1 = TestStatic.BCONST1; // Incompatible fenums forbidden!
        state1 = TestStatic.ACONST2; // ok

        fenumArg(5); // Direct use of value forbidden!
        fenumArg(TestStatic.BCONST1); // Incompatible fenums forbidden!
        fenumArg(TestStatic.ACONST1); // ok
    }
}
