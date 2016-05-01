import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.UnitsTools;
import static org.checkerframework.checker.units.UnitsTools.m;

// An example of declaring variables and values with units
public class SimpleDemoWithWarnings {
  void demo() {
    // to declare a variable with a unit, annotate the
    // type of the variable with a units qualifier:
    @m int x;

    // to assign a unit to a number, multiply a number
    // by an appropriate unit constant from UnitsTools;
    x = 5 * UnitsTools.m;

    // with static import of the unit constant, the
    // syntax can be simplified further:
    x = 5 * m; 

    @m int meters = 5 * UnitsTools.m;
    @s int seconds = 2 * UnitsTools.s;

    @mPERs int speed = meters / seconds;

    // addition and subtraction between meters
    // and seconds do not make sense scientifically
    // so an error is produced here
    //:: error: (assignment.type.incompatible)
    @m int foo = meters + seconds;
    //:: error: (assignment.type.incompatible)
    @s int bar = seconds - meters;
  }
}
