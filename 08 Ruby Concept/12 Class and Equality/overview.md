main comparison approach

== or ===

eql? or equal?

We can have several function for check equality like
  a.respond_to?(:method_name) to make sure we have that method
  a.kind_of?(self) to check type or subclass
  a.instance_of?(self) to check exact type of object
  a.equal?(self) to check exact same object (memory address)
  
Remember that we have some point to adhere, careful when override the == operator
  a == b and b == a, sometimes b == a is false if the subclass of it doesn't override ==
  transitive property: a == b and b == c => a == c. This == operator can easily violate the usage of it

=== somehow use for regular expression in case when then else end. It follow the check in ==. If we override ==, === will adhere

Hash can use object as key. That is the time we use eql?
  a.hash == b.hash
  a.eql?(b)

Keys things when using
  We leverage the usage of existing behavior instead custom on own way.
  If we have to, try to keep or delegate the primitive behavior
  If we have to write all, try with as simplest as possible solution, avoid cross class comparison if needed
