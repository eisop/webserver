import org.checkerframework.checker.signature.qual.*;

public class SignatureExampleWithWarnings {

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
      
      fqn = t2; //:: error: (assignment.type.incompatible)
      bn = t2; //:: error: (assignment.type.incompatible)
      cgn = t2; //:: error: (assignment.type.incompatible)
      
      fqn = t3; //:: error: (assignment.type.incompatible)
      bn = t3; //:: error: (assignment.type.incompatible)
      cgn = t3; //:: error: (assignment.type.incompatible)
      
      fqn = t4; //:: error: (assignment.type.incompatible)
      bn = t4; //:: error: (assignment.type.incompatible)
      cgn = t4; //:: error: (assignment.type.incompatible)
      
      fd = t5; //:: error: (assignment.type.incompatible)
      
      cgn = t6; //:: error: (assignment.type.incompatible)
      fd = t6; //:: error: (assignment.type.incompatible)
      
      fqn = t7; //:: error: (assignment.type.incompatible)
      bn = t7; //:: error: (assignment.type.incompatible)
      
      fqn = t8; //:: error: (assignment.type.incompatible)
      bn = t8; //:: error: (assignment.type.incompatible)
      fd = t8; //:: error: (assignment.type.incompatible)
      
      fqn = t9; //:: error: (assignment.type.incompatible)
      bn = t9; //:: error: (assignment.type.incompatible)
      cgn = t9; //:: error: (assignment.type.incompatible)
      
      fqn = t10; //:: error: (assignment.type.incompatible)
      bn = t10; //:: error: (assignment.type.incompatible)
      fd = t10; //:: error: (assignment.type.incompatible)
      
      fqn = t11; //:: error: (assignment.type.incompatible)
      bn = t11; //:: error: (assignment.type.incompatible)
      cgn = t11; //:: error: (assignment.type.incompatible)
      
      fqn = t12; //:: error: (assignment.type.incompatible)
      bn = t12; //:: error: (assignment.type.incompatible)
      
      fd = t13; //:: error: (assignment.type.incompatible)
      
      cgn = t14; //:: error: (assignment.type.incompatible)
      fd = t14;  //:: error: (assignment.type.incompatible) 
      
      fd = t15; //:: error: (assignment.type.incompatible)
      
      cgn = t16; //:: error: (assignment.type.incompatible)
      fd = t16; //:: error: (assignment.type.incompatible)
      
      fd = t17; //:: error: (assignment.type.incompatible)
      
      cgn = t18; //:: error: (assignment.type.incompatible)
      fd = t18; //:: error: (assignment.type.incompatible)
      
      fqn = t19; //:: error: (assignment.type.incompatible)
      fd = t19; //:: error: (assignment.type.incompatible)
      
      fqn = t20; //:: error: (assignment.type.incompatible)
      bn = t20; //:: error: (assignment.type.incompatible)
      cgn = t20; //:: error: (assignment.type.incompatible)
      fd = t20; //:: error: (assignment.type.incompatible)
      
      fqn = t21; //:: error: (assignment.type.incompatible)
      cgn = t21; //:: error: (assignment.type.incompatible)
      fd = t21; //:: error: (assignment.type.incompatible)
      
    }
}
