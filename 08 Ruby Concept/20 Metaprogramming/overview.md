there are some hook method of class/module definition (with self - singleton level):
  self.included(other_module)
  self.inherited(subclass)
  self.prepended(other_module)
  at_exit do
    puts "good bye"
  end

We can have multiple at_exit, and LIFO (last in, first out) will execute by that order

Recommend that we should combine include, extend on one go is better pattern

```ruby
module InstanceMethod
  module SingletonMethod
  end

  def self.included( host_class)
    host_class.extend(SingletonMethod)
  end

  def an_instance_method
  # ...
  end
end

class ExampleClass
  include InstanceMethod
end
```
