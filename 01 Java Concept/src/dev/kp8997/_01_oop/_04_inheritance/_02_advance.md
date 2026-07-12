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
        FrontendEngineer fe = (FrontendEngineer) dev1;
        fe.optimizeBundle(); // Works flawlessly!

        // DANGEROUS CRASH: dev2 is a BackendEngineer, it cannot be forced into a FrontendEngineer!
        FrontendEngineer brokenCast = (FrontendEngineer) dev2;
        // Throws: java.lang.ClassCastException at runtime
    Type Safety:
        if (dev instanceOf Developer fe) { ... }
        dev instanceof FrontendEngineer fe meaning: FrontendEngineer fe = (FrontendEngineer) dev;

3. Covariant Return Types

4. Hiding Mechanics: Static Methods vs. Instance Fields
    While instance methods are dynamically overridden at runtime, fields and static methods exhibit different behaviors:
        Field Hiding: If a subclass declares a field with the exact same name as a superclass field, the parent's field is hidden, not overridden. The field accessed depends entirely on the compile-time reference type, not the runtime object.
        Static Method Hiding: If a subclass defines a static method with the same signature as a static method in the parent class, it hides the parent method. Static methods are resolved at compile-time via static binding (early binding), whereas instance methods use dynamic binding (late binding).
5. Modifier level
    the subclass method must be lower level than parent
        E.g if parent method is protected, the child must be protected, public. It cannot be private or default

======================================================================

# Summary
