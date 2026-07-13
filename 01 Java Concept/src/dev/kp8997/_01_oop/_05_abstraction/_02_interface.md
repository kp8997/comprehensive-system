1. variables in interface is public static final
    interface can not hold instance state, only constant (static)

2. methods in interface is public abstract

3. default: allows adding new methods to interfaces without breaking existing implementations
    when we already implemented interface in many classes, we can not add new method to interface. In this case, we can use default method (add new method as default methods), and we can override it in subclass if we want and don't break the compile time.

4. static
    static method can be called by default or other static method in interface

5. a class can implement MANY interfaces

6. Type of interface
    Marker Interfaces: Can have an interface without any fields or methods. It serves as meta tag for reusability of an annotation.
        e.g Runnable, Comparator
    Functional Interfaces: An interface with only one abstract method is called a functional interface .a interface with only method can be lamda in Java
        e.g @FunctionalInterface
