Scope of variable
    Global
    Parameter: sit between Global and BLock when have default assignment for parameter
    Function/Block: inside the block scope. function scope for var, block scope for let and const (block scope ignored by var)

Phase of variable
    Declaration
    Initialization
    Assignment

    Hoist: When hoist a variable has a kind of:
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

-----------
If it is function and declare with var, when exit function all the variable in there will be clear out

```
var z = 7;
function test() {
    console.log(z);
    var z = 6;
    console.log(z);
}
console.log(z);
test();

output:
7
undefined
6

```
-----------
If it is function and reuse the outer scope variable, when exit function outside variable receive new value

```
var a = 7;
function test() {
    console.log(a);
    a = 6;
    console.log(a);
}
console.log(a);
test();

output:
7
7
6
```

```
var i = 10;
function test() {
    for (var i = 0; i < 5; i++) {
        console.log(i);
    }
}
console.log(i);
test();
console.log(i);

output:
10
0
1
2
3
4
10
```

```
var i = 10;
function test() {
    for (i = 0; i < 5; i++) {
        console.log(i);
    }
}
console.log(i);
test();
console.log(i);

output:
10
0
1
2
3
4
5
```

---------------

```
function func() {
    var a = 'Hello';
    let b = 'Roadside coder';

    if (true) {
        let a = 'Hi'; // Legal Shadowing
        var b = 'Bye'; // Illegal Shadowing
        console.log(a); 1.Hi ✅
        console.log(b); 2. SyntaxError: Identifier 'b' has already been declared ❌
    }
}
func();
```

==============================
To summarize

hoist:
    let and const is only declaration are hoisted
    var is both declaration and initialization are hoisted. Default value is undefined

scope
    let and const: block scope
        exit block/function, variable will be clear out

    var: function scope. var ignore block scope
        exit function, variable will be clear out
        if modify outer value within a function scope, it will return new value to outer scope
        if declare a var in the function same name as outer variable, it will create new variable, exit will clear out scope value

redeclaration within scope:
    var: okay
    let, const: error

shadow:
    we can shadow var using let
    we can't shadow let using var (because redeclare a let variable can not be allowed within same block scope).
