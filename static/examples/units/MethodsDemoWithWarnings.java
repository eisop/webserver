import org.checkerframework.checker.units.UnitsTools;
import org.checkerframework.checker.units.qual.*;

// An example of declaring methods with units and calling those methods
// This allows programmers to enforce units at the boundaries of interfaces
public class MethodsDemoWithWarnings {
    void demo() {
        @m int meters = 5 * UnitsTools.m;
        @s int seconds = 2 * UnitsTools.s;
        @km int kilometers = 8 * UnitsTools.km;

        // custom defined method which calculates and returns a speed
        @mPERs double speed = getSpeed(meters, seconds);

        // radians to degrees conversion through a custom defined conversion method
        @degrees double deg = toDegrees(8 * UnitsTools.rad);

        // if an argument passed to a method does not have a matching unit, an error is produced
        // :: error: (argument.type.incompatible)
        speed = getSpeed(kilometers, seconds);
        // :: error: (argument.type.incompatible)
        deg = toDegrees(meters);

        // if the results of the method is assigned to an incompatible unit, an error is also
        // produced
        // :: error: (assignment.type.incompatible)
        @radians double rad = getSpeed(meters, seconds);
        // :: error: (assignment.type.incompatible)
        speed = toDegrees(8 * UnitsTools.rad);
    }

    // using units on the method parameters and returns declares that the
    // method must be called by passing in numbers that have the respective units
    // and that the method will always return a specific unit
    @mPERs double getSpeed(@m double meter, @s double second) {
        return meter / second;
    }

    // units conversion methods can be declared as well
    // the existing unit must be cleared first by dividing against its unit constant,
    // then multiplied by the result unit to take on the new unit
    @degrees double toDegrees(@radians double rad) {
        // degrees = rad * 180 / PI = rad / PI * 180
        return rad / (Math.PI * UnitsTools.rad) * (180 * UnitsTools.deg);
    }
}
