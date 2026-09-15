Module can be used with include that treat its instance method to a class instance method if that class include it

Now the Ruby interpret will ship it as superclass of the class including the module

If have many include with modules, the order of include will matter. The earlier included module will be further in the superclass chain. So it will override the method defined later
