Ruby has some operators: + : ? ! * etc. They can be use outside the computation context. We can define them in our own class - methods.

Operators usage: expression can be similar to methods call (or send)
  E.g sum = first + second
    sum = first.+(second)
    + is like a method and second is its argument

Ruby also let us define single object - unary operator - !
  Unary definition: E.g
    def !
      Document.new(title, author, "It is special exclamation mark")
    end

  unary call:
    doc.!
    !doc

  binary call:
    sum = a + b
    sum = a.+(b)

  Special unary case: + and - can have both unary and binary operator, so the definition of it might different for unary one
    E.g
    def +@
    end
  
  hash operator
    def [](index)
      words[index]
    end

    def initialize
      @slots = {}
    end

    # Defines behavior for box[key] = value
    def []=(key, value)
      @slots[key] = value
    end

Final warning:
  should use when:
    - it conveys the same meaning as the original operator like + for the Vector or Matrix (math context)
    - use with fallback aliases - so other developers who don't like the operator can also have a clear alias to use
    - use with adhering math context - like symmetric property (a + b = b + a), etc
    - document non-standard behavior (e.g. Time.now + 60 is working but 60 + Time.now is TypeError)
  shouldn't use when:
    invent a new way to use it: department = employee + employee (not recommended - will be trouble)
