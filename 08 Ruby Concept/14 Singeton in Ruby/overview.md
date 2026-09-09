Singleton in ruby is totally different from other language or singleton pattern in design pattern

It is just like a customized method for an instance. (Remember it is instance not the whole class). It can apply for all type of object except for Number and Symbol

It overrides any instance method existed before

We can create static method by singleton method (actually they are self.method inside a class) by
  def Document.explain

  end

It will lead to a way to define static methods. the below implementation is the same
  ```ruby
    class Document
      class << self
        def find_by_name( name )
        end

        def find_by_id( id )
        end
      end
    end


    class Document
      def self.find_by_name( name )
      end

      def self.find_by_id( id )
      end
    end
  ```

--------
Summary:
Singleton in Ruby is different to design pattern and serves 2 purpose:
  - add a new method for only an instance
  - make the method act as static method for a class
