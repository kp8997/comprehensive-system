1. modifier and inheritance
    public
    default
    protect
    private
subclass
    public -> every class can access
    default -> class in same package can access
    protect -> class in same package and subclass can access
    private -> only same class can access

2. final can prevent the override and inherit

3. super and this can not be called in the same constructor

4. One child only extends one parent

5. is a and has a relationship

6. if we don't have any constructor in parent class, the compiler will create a default constructor
    If we have constructor with arguments in parent class, the compiler will NOT create a default constructor
        => in subclass we must call super(arguments) constructor explicitly, otherwise, it will show a compile time error

7. Every class inherits from Object class
