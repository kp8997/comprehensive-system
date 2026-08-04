Scope of variable
    Global
    Parameter: sit between Global and BLock when have default assignment for parameter
    Function/Block: inside the block scope

Phase of variable
    Declaration
    Initialization
    Assignment

    Hoist: When hoist a:
        let and const: only declaration is hoisted
            => access before using will throw ReferenceError in TDZ (Temporal Dead Zone)
        var: declaration and initialization (default is undefined) are hoisted
            => access before using will return undefined


variable, function and class always be hoisted in to the top of block or global scope. This ensure we can call the function before its declaration
    var: always global scope -> so it can be accessed from outside function of the block scope
    let and const: block scope -> can not be used without declaration

But it also affects the variable so it has some behavior with scope
    block scope: let, const can not be initialized before its declaration - TDZ (Temporal Dead Zone)
        
var in global can automatically belong to window object
