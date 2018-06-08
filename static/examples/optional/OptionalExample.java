import java.util.Optional;

/**
 * Test case for flow-sensitivity of Optional.isPresent().
 * Source file:
 *   checker/tests/optional/FlowSensitivity.java
 */
abstract class OptionalExample {

    abstract Optional<String> getOpt();

    String hasCheck1() {
        Optional<String> opt = getOpt();
        if (opt.isPresent()) {
            return opt.get();
        } else {
            return "default";
        }
    }

    String hasCheck2() {
        Optional<String> opt = getOpt();
        if (!opt.isPresent()) {
            return "default";
        }
        return opt.get();
    }
}
