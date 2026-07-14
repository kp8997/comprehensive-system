abstract
    have constructor (optional)
    can have fields with all modifiers
    can have methods with any access modifier and body (if normal method) or without body (abstract method)
    interface: an abstract class implements an interface, it is not required to implement the interface's abstract methods.

abstract purpose for other to inherit (extends/implements) and override
    so it can not used with 'final' because final does not allow to change

can not instantiate with new keyword for abstract class, but can do with subclass reference type:
    Parent instance = new Child(); // okay
    Parent instance = new Parent(); // no

Abstract:
    methods
    class
    interface
