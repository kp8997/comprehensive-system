variable, function and class always be hoisted in to the top of block or global scope. This ensure we can call the function before its declaration
    var: always global scope -> so it can be accessed from outside function of the block scope
    let and const: block scope -> can not be used without declaration

But it also affects the variable so it has some behavior with scope
    block scope: let, const can not be initialized before its declaration - TDZ (Temporal Dead Zone)
        
var in global can automatically belong to window object
