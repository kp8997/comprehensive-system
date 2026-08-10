Javascript has different context depending on how it is called
- Standalone function in strict mode
    => undefined
- Standalone function in normal mode (non-strict)
    => global object (window in browser)
- Arrow function
    => lexical this
- Method of an object
    => object
- Inside a class method
    => instance of class

Sometime it loses the context: arrow function, setTimeout, setInterval, event handler, callback function. That's why we have some functions below to preserve the context.

1. Apply
    - definition: help bind context and allow to bind params in a single variable via ...args or array
    - syntax: func.apply(context, [args])
    - example:
        const person = {
            name: "John",
            age: 30
        }
        const greet = function(age) {
            console.log(`Hello ${this.name} I'm ${age}`);
        }
        greet.apply(person, [30]);

2. Call
    - definition: help bind context and allow to bind params one by one
    - syntax: func.call(context, arg1, arg2, ...)
    - example:
        const person = {
            name: "John",
            age: 30
        }
        const greet = function(age) {
            console.log(`Hello ${this.name} I'm ${age}`);
        }
        greet.call(person, 30);

3. Bind
    - definition: help bind context and return a new function with the context and params already bound
    - syntax: func.bind(context, arg1, arg2, ...)
    - example:
        const person = {
            name: "John",
            age: 30
        }
        const greet = function(age) {
            console.log(`Hello ${this.name} I'm ${age}`);
        }
        const greetWithPerson = greet.bind(person);
        greetWithPerson(30);
