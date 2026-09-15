Code block is like callback in javascript

Code block in ruby allow to execute outside code (Method) with arguments of the current context (block method that defined) via yield syntax
  we can use yield argument1, argument2 (as arguments of callback function)
  or value = yield (as return of the code block caller - return of callback)

Code block that call the method, similar to definition of the callback function with params

Can use with iterator to yield a series of values, then when in block caller we can call and it will execute in multiple times
