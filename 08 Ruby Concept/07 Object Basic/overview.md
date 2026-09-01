Some highlight to remember about Object and class

  1. Definition of to_s and its return will be result of **puts**

  2. self: similar to this, it is like default instance of a method. It is also the execution context. 4 points:
    global execution context
    inside a class as static methods
    inside a class as instance methods and instance variable
    for module: similar to class static

  3. private: have 2 ways
  
    private section after the start of the keyword to end of the class

    private with method name as parameter symbol
      private :word_count
  
  4. Subclass can access private method of superclass
  
  5. Every class is a child of Object
    Have some instance variable about Object like
      instance.public_methods, instance.private_methods, instance.instance_variables

  6. Send can violate the private, protected keyword
    doc.send(:word_count) # still worked although word_count is private

  7. private & protected
    protected allow to call from any method in same class or subclass
    check the example in user.rb for more information
