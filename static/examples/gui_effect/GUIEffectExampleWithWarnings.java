import org.checkerframework.checker.guieffect.qual.*;

public class GUIEffectExampleWithWarnings {
    public void nonUIStuff(
            final UIElement e,
            final GenericTaskUIConsumer uicons,
            final GenericTaskSafeConsumer safecons) {
        // :: error: (call.invalid.ui)
        e.dangerous(); // should be bad
        e.runOnUIThread(
                new IAsyncUITask() {
                    final UIElement e2 = e;

                    @Override
                    public void doStuff() { // should inherit UI effect
                        e2.dangerous(); // should be okay
                    }
                });
        uicons.runAsync(
                new @UI IGenericTask() {
                    final UIElement e2 = e;

                    @Override
                    public void doGenericStuff() { // Should be inst. w/ @UI eff.
                        e2.dangerous(); // should be okay
                    }
                });
        safecons.runAsync(
                new @AlwaysSafe IGenericTask() {
                    final UIElement e2 = e;

                    @Override
                    public void doGenericStuff() { // Should be inst. w/ @AlwaysSafe
                        // :: error: (call.invalid.ui)
                        e2.dangerous(); // should be an error
                        safecons.runAsync(this); // Should be okay, this:@AlwaysSafe
                    }
                });
        // :: error: (argument.type.incompatible)
        safecons.runAsync(
                new @UI IGenericTask() {
                    final UIElement e2 = e;

                    @Override
                    public void doGenericStuff() { // Should be inst. w/ @UI
                        e2.dangerous(); // should be ok
                        // :: error: (argument.type.incompatible)
                        safecons.runAsync(this); // Should be error, this:@UI
                    }
                });
        safecons.runAsync(
                new IGenericTask() {
                    @Override
                    // :: error: (override.effect.invalid.nonui)
                    @UIEffect
                    public void doGenericStuff() {
                        UIByPackageDecl.implicitlyUI();
                    }
                });
    }
}

@SafeType
class SafeByDecl {
    public static void safeByTypeDespiteUIPackage() {}
}

class UIByPackageDecl {
    public static void implicitlyUI() {
        // don't need to do anything here
    }
}

@UIType
interface UIElement {
    public void dangerous();

    @SafeEffect
    public void repaint();

    @SafeEffect
    public void runOnUIThread(IAsyncUITask task);
}

interface GenericTaskUIConsumer {
    @SafeEffect
    public void runAsync(@UI IGenericTask t);
}

interface GenericTaskSafeConsumer {
    @SafeEffect
    public void runAsync(@AlwaysSafe IGenericTask t);
}

@UIType
interface IAsyncUITask {
    public void doStuff();
}

@PolyUIType
@PolyUI
interface IGenericTask {
    @PolyUIEffect
    public void doGenericStuff();
}
