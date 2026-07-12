1. getter and setter

2. modifier
    private - only in the same class
    protected - subclass can access
    default - non-pecified, classes in a same package can access
    public: every classes can access

3. constructor: 
    Architecture:
        Never return even void
        Have the same name as class itself
        Can have access modifier
    Behavior:
        Call:
            Implicit and Explicit: When call with new (explicit) the constructor will be invoked (implicit)
        Not inherited: subclass has their own constructor, if want, can call super() for parent's constructor
        Chain capability:
            Can call other constructor with super(params) or this(params) only
            If defined, must be in the first line

4. static, instance and reference
    fields:
        static - apply for all objects
        instance - apply for each object. A instance have their own reference. If variables have the same reference, meaning that they point to the same instance (object)
    methods:
        instance method can invoke instance and static methods
        static method can invoke only static methods


==============================

# Advance

5. instance and static initializer

==============================

# Summary
