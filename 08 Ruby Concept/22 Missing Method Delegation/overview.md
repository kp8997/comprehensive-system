We can use method_missing to delegate method. In a scenarios that a subclass want to use functionality from its superclass. We can define missing_method and send the method name after have some conditional check

We can check before delegate to superclass object

```ruby
class SuperSecretDocument
  DELEGATED_METHODS = [ :content, :words ]

  def method_missing (name, *args)
    check_for_expiration
    if DELEGATED_METHODS.include? ( name )
      @original_document. send (name, *args)
    else
      super
    end
  end
end
```

We can use inherit pattern to avoid unnecessary delegation: Example with to_s, that inherit from Object. The case is we only want the to_s from Document or raise error after timer expired. Set this as example:

```ruby
  class SuperSecretDocument < BasicObject
    def initialize(...)
      # ...
    end

    def time_expired?
      ::Time.now - @created_time >= @time_limit_seconds
    end

    def check_for_expiration
      raise 'Document no longer available' if time_expired?
    end

    def method_missing( method_name, *args )
      check_for_expiration
      @original_document.send(method_name, *args)
    end
  end
```

delegate.rb with SimpleDelegator class that help implement missing_method to delegate gracefully, all we need is to:

```ruby
  class DocumentWrapper < SimpleDelegator
    def initialize(real_doc)
      # it will find the superclass of real_doc and doing the delegation automatically
      super(real_doc)
    end
  end
```

Early ActiveRecord also use this pattern. We want to extract the key symbol of an Hash, usually it will be nil since there are no method name (e.g first_name, last_name), missing_method implementation cast the logic of this key symbol to method. and return the value from the hash.

Modern ActiveRecord use the mechanics of self implementation by combination of missing_method and define_method to write the method dynamically if it is not found, but in a more robust and safe way.
