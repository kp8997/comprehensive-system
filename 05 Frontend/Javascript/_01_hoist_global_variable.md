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
            => var also ignore the block scope, which means it can access and reassign value from outside the block scope
        var in global can automatically belong to window object
