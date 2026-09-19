We can put expression inside class, when interpreter come up it will execute it

Have multiple method definition, the last one will win

Have multiple class reopen, the latest one will win (Monkey Patching)

With the concept of modifying class structure, we can use class method - self.method to define instance method and call in the context of between class - end.
Pattern is have definition of instance method inside self.method and call that method in the class
  E.g:

  ```ruby
  class Document
    def self.enable_encryption( enabled )
      if enabled
        def encrypt_string(string)
          # Base64.encode64(string)
          string.tr('a-zA-Z', 'N-ZA-Mn-za-m')
        end
      else
        def encrypt_string(string)
          string
        end
      end
    end

    # call that self method inside class definition
    enable_encryption( ENCRYPTION_ENABLED )
  end
  ```

We later can turn it off by called:
  ```ruby
    Document.enable_encryption( false )
  ```

Unit test mandate is for metaprogramming branching logic like this in order to detect hidden error

--------------
It is useful when writing code for multiple environments

Use unit test to verify the logic of self-modifying code

================
Summary

3 main ideas
  Classes in Ruby can be executable.
  Base on that, we can have self-modifying structure
  Unit test to verify them to avoid hidden bugs
