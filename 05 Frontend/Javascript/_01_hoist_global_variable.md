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

Examine the example below:
----------

```
{
    console.log(x);
    var x = 6;
    console.log(x);
}
console.log(x);

output:
undefined
6
6
```

----------
```
var y = 7;
{
    console.log(y);
    var y = 6;
    console.log(y);
}
console.log(y);

output
7
6
6

```
