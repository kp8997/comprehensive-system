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

8. @Override has 3 rules
    implement different behavior from parent
    call the same as parent method (explicitly calling super.method() in this case but redundant)
    combine both ways above
    
===============

# Summary

Inheritance is about
    modifier
    super and this keyword and some rule about how to use it or impliciy/explicit call
    override, overload, hide (for static)
    method binding (static vs dynamic) ReferenceType (compile understood type)= Object type (In-memory type)
    
    
