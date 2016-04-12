import org.checkerframework.checker.units.qual.*;
import org.checkerframework.checker.units.UnitsTools;

// An simple example of dividing meters by seconds
public class SimpleDemoWithWarnings {
  void demo1() {
    @m int meters = 5 * UnitsTools.m;
    @s int seconds = 2 * UnitsTools.s;
    
    @mPERs int speed = meters / seconds;
    
    // addition and subtraction between meters
    // and seconds do not make sense scientifically
    // so an error is produced here
    //:: error: (assignment.type.incompatible)
    @m int x = meters + seconds;
    //:: error: (assignment.type.incompatible)
    @s int y = seconds - meters;
  }
}
