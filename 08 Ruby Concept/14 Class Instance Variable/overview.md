we can think of instance variable @variable for that instance of that object
and class variable @@variable as class variable (for all instance) - Think of it static variable of other languages

We can use singleton (self.method or singleton class) to define a default value of a class and then assign it to a class instance variable instead of using class variable. Class variable is such a mess since it will look up all the inheritance tree to find the value. The last one load the value will be apply for all class of hierarchy.

All class inheritance hierarchy share the same @@ variable (class variable)
