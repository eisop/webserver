import org.checkerframework.checker.signature.qual.*;

public class SignatureExample {

    // The hierarchy of type representations contains:
    //     UnannotatedString.class,
    //     FullyQualifiedName.class,
    //     BinaryName.class,
    //     SourceName.class,
    //     ClassGetName.class,
    //     BinaryNameForNonArray.class,
    //     FieldDescriptor.class,
    //     FieldDescriptorForArray.class,
    //     SignatureBottom.class
    // There are also signature representations, which are not handled yet.

    void m() {

        // All the examples from the manual
        String t1 = "I";
        String t2 = "LMyClass;";
        String t3 = "Ljava/lang/Integer;";
        String t4 = "Lpackage/Outer$Inner;";
        String t5 = "MyClass";
        String t6 = "MyClass[]";
        String t7 = "[LMyClass;";
        String t8 = "[Ljava.lang.Integer;";
        String t9 = "[Ljava/lang/Integer;";
        String t10 = "[Lpackage.Outer$Inner;";
        String t11 = "[Lpackage/Outer$Inner;";
        String t12 = "[[I";
        String t13 = "int";
        String t14 = "int[][]";
        String t15 = "java.lang.Integer";
        String t16 = "java.lang.Integer[]";
        String t17 = "package.Outer.Inner";
        String t18 = "package.Outer.Inner[]";
        String t19 = "package.Outer$Inner";
        String t20 = "Lpackage.Outer$Inner;";
        String t21 = "package.Outer$Inner[]";

        String us; // @UnannotatedString
        @FullyQualifiedName String fqn;
        @BinaryName String bn;
        @ClassGetName String cgn;
        @FieldDescriptor String fd;

        us = t1;
        fqn = t1;
        bn = t1;
        cgn = t1;
        fd = t1;

        us = t2;
        fd = t2;

        us = t3;
        fd = t3;

        us = t4;
        fd = t4;

        us = t5;
        fqn = t5;
        bn = t5;
        cgn = t5;

        us = t6;
        fqn = t6;
        bn = t6;

        us = t7;
        cgn = t7;
        fd = t7;

        us = t8;
        cgn = t8;

        us = t9;
        fd = t9;

        us = t10;
        cgn = t10;

        us = t11;
        fd = t11;

        us = t12;
        cgn = t12;
        fd = t12;

        us = t13;
        fqn = t13;
        bn = t13;
        cgn = t13;

        us = t14;
        fqn = t14;
        bn = t14;

        us = t15;
        fqn = t15;
        bn = t15;
        cgn = t15;

        us = t16;
        fqn = t16;
        bn = t16;

        us = t17;
        fqn = t17;
        bn = t17;
        cgn = t17;

        us = t18;
        fqn = t18;
        bn = t18;

        us = t19;
        bn = t19;
        cgn = t19;

        us = t20;

        us = t21;
        bn = t21;
    }
}
