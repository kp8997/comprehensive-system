method_missing hook basically were call if a ruby interpreter fail to find a method. It will search current to superclass to find any match, if not, it will call method_missing hook from current to superclass (Object by default) and get called.

```ruby
def method_missing( method_name, *args)
end
```

const_missing: must be defined in singleton mode with an argument as name

```ruby
class Document
  def self.const_missing( const_name )
    msg = %Q{
    You tried to reference the constant #{const_name}
    There is no such constant in the Document class.
    }
  end
end
```

Caveat:
  1. Not using it if we don't really need it. Method missing will slow down performance if used too much. 99% of normal errors are handled by default of Ruby
  2. Careful when using to avoid infinite loop when handle a method call. Make sure not to call any nonexistent method in this hook.
