1. meaning: many forms
    make code simpler
    encourage code extensibility

2. it is actually using keywords:
    overload: same methods but different number of parameters, parameter types, parameter order, return type
        instance methods and static methods:
            subclass inherits all the overloaded methods from parent class
            subclass can also write new overload methods
        
    override: same methods but different class
        instance methods: 
            implement different behavior from parent
            call the same as parent method (explicitly calling super.method() in this case but redundant)
            combine both ways above
        static methods
            subclass cannot override static method
            subclass can define a new static method with same name (this is called hide method)
    

3.  private method will not be inherited, so it cannot be overriden
    final method cannot be overriden
    static method cannot be overriden, but it is hidden
        class ReportGenerator {
            public static void printHeader() {
                System.out.println("[PARENT] --- System Report ---");
            }
        }
        class FinancialReportGenerator extends ReportGenerator {
            public static void printHeader() {
                System.out.println("[CHILD] --- Financial Report ---");
            }
        }

        ReportGenerator polymorphicRef = new FinancialReportGenerator();
        polymorphicRef.printHeader();  // will call printHeader() from ReportGenerator at runtime

        if printHeader is an instance method:
        polymorphicRef.printHeader();  // will call printHeader() from FinancialReportGenerator at runtime

4.  Polymorphism is the engine that powers abstraction.
    Abstract Classes: Used when classes share a tight identity and common implementation details.
    Interfaces: Used to establish a contract of behavior across completely unrelated classes. An interface is a pure polymorphic contract.

Check more advance of _04_inheritance to gain knowledge

=====================

| Capability / Behavior | Static Methods | Instance Methods |
| -------- | -------- | -------- |
| Can be Overloaded?    | Yes (Within the same class or across parent/child via different parameters)   | Yes (Within the same class or across parent/child via different parameters)   |
| Can be Overridden?    | No (They are hidden, not overridden)   | Yes (Achieves true polymorphic runtime overriding)   |
| Binding Mechanism    | Static Binding (Resolved at compile-time by the compiler based on the reference type)   | Dynamic Binding (Resolved at runtime by the JVM based on the actual object type)   |
| Polymorphism Support    | Does not support runtime polymorphism   | Fully supports runtime polymorphism   |

=====================

# Summary
Polymorphism is about:
    overloading and overriding methods
    hiding static methods and fields, not inherited with private and final methods
    use combination with abstract and interface.
