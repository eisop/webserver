import org.checkerframework.checker.units.UnitsTools;
import org.checkerframework.checker.units.qual.*;

// An example of declaring methods utilizing parametric polymorphism of units
public class PolyUnitDemoWithWarnings {
    void demo() {
        @m int meters = 5 * UnitsTools.m;
        @s int seconds = 2 * UnitsTools.s;
        @km int kilometers = 8 * UnitsTools.km;

        // @PolyUnit evaluates and returns the least upper bound of @m and @m, which is @m
        meters = sum(meters, meters);
        // @PolyUnit evaluates and returns the least upper bound of @s and @s, which is @s
        seconds = sum(seconds, seconds);

        // @PolyUnit evaluates and returns the least upper bound of @s and @m, which is
        // @UnknownUnits
        // :: error: (assignment.type.incompatible)
        meters = sum(seconds, meters);

        // @PolyUnit evaluates and returns the least upper bound of @m and @km, which is @Length
        // :: error: (assignment.type.incompatible)
        meters = sum(meters, kilometers);
    }

    // @PolyUnit allows programmers to declare methods which can work with any unit through
    // parametric polymorphism of units
    @PolyUnit int sum(@PolyUnit int x, @PolyUnit int y) {
        // the least upper bound of the two units along with the value of x + y is returned
        return x + y;
    }
}
