there are some hook method of class/module definition (with self - singleton level):
  self.included( class ): when a module included by a class
  self.inherited( subclass )
  self.prepended( class )
  at_exit do
    puts "good bye"
  end
  method_missing: when a undefined method is called
  method_added: when a new method is added (with)
  trace_var: global variables change hook
  set_trace_func(&block_code): whenever have an open/close of a class, method call/return,

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

Caveat:
  It is not about how many hooks we are, but how the hooks are used - how they are organized and triggered. Understand this is more important.
    E.g self.inherited happend for all level of subclasses, not just direct subclasses
    E.g sometimes, at_exit won't be called
