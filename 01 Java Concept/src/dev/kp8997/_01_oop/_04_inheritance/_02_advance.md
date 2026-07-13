1. Lifecycle of super class and subclass
    Static Initializers: All static blocks and static variables from the superclass down to the subclass are executed (only once when the class is loaded).
    Instance Initializers & Fields: Superclass instance variables are initialized, and the superclass instance block runs.
    Superclass Constructor: The superclass constructor executes via super().
    Subclass Initialization: Subclass instance variables are initialized, the subclass instance block runs, and finally, the subclass constructor completes.
2. Upcasting, Downcasting, and Type Safety
    Upcasting: treat subclass as superclass (safe)
        Vehicle v = new Car();
        v.move(); // run Car's move0()
    Downcasting: treat superclass as subclass (unsafe, compile time error)
        Developer dev1 = new (FrontendEngineer) dev;(); // Upcast
        Developer dev2 = new BackendEngineer();  // Upcast

        // Explicit Downcast (Safe, because dev1 really points to a FrontendEngineer)
        FrontendEngineer fe = (FrontendEngineer) dev1; // This is downcast
        fe.optimizeBundle(); // Works flawlessly!

        // DANGEROUS CRASH: dev2 is a BackendEngineer, it cannot be forced into a FrontendEngineer!
        FrontendEngineer brokenCast = (FrontendEngineer) dev2;
        // Throws: java.lang.ClassCastException at runtime
    Type Safety:
        if (dev instanceOf Developer fe) { ... }
        dev instanceof FrontendEngineer fe meaning: FrontendEngineer fe = (FrontendEngineer) dev;

3. Covariant Return Types
    override: When overriding a method, the subclass method is permitted to return a subtype of the return type declared in the superclass method. This allows you to avoid messy explicit casting at the call site.
        e.g: class VehicleFactory {
            public Vehicle manufacture() { return new Vehicle(); }
        }

        class CarFactory extends VehicleFactory {
            @Override
            public Car manufacture() { return new Car(); } // Covariant return type (Car IS-A Vehicle)
        }

4. Hiding Mechanics: Static Methods vs. Instance Fields
    While instance methods are dynamically overridden at runtime, fields and static methods exhibit different behaviors:
        Field Hiding: If a subclass declares a field with the exact same name as a superclass field, the parent's field is hidden, not overridden. The field accessed depends entirely on the compile-time reference type, not the runtime object.
        Static Method Hiding: If a subclass defines a static method with the same signature as a static method in the parent class, it hides the parent method. Static methods are resolved at compile-time via static binding (early binding), whereas instance methods use dynamic binding (late binding).
5. Modifier level and Exception Type
    the subclass method must be lower level than parent
        E.g if parent method is protected, the child must be protected, public. It cannot be private or default
    the same as exception type must be lower

======================================================================

# Summary

1. Lifecycle: 
    1. static methods and fields of superclass and subclass (only once when loaded)
    2. superclass instance variables and instance block
    3. superclass constructor
    4. subclass instance variables and instance block
    5. subclass constructor

2. Upcasting: make the type is larger (safe)
    downcasting: make the type is smaller
    Type safety: check the right tyoe with instanceof keyword

3. covariant return type
    method1 of class A return type A
    method2 of class B override of method 1 can return type B
    because override have covariant property (B IS-A A)

4. static method hiding and field hiding
    Vehicle v = new Car();
    v.move(); // run Vehicle's move() if move is a static method
    v.move(); // run Car's move() if move is instance method
    v.name // always run Vehicle's name because of field hiding (call hidden field from parent because compile time)

5. modifier and exception type must be narrower on subclass
    protected -> protected or public
    default -> default, protected or public
