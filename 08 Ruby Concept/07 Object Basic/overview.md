Some highlight to remember about Object and class

  Definition of to_s and its return will be result of **puts**

  self: similar to this, it is like default instance of a method. It is also the execution context. 4 points:
    global execution context
    inside a class (as static methods or variable)

  private: have 2 ways
  
    private section after the start of the keyword to end of the class

    private with method name as parameter symbol
      private :word_count
  
  Subclass can access private method of superclass
  
  Every class is a child of Object
    Have some instance variable about Object like
      instance.public_methods, instance.private_methods, instance.instance_variables

  send can violate the private, protected keyword
    doc.send(:word_count) # still worked although word_count is private
