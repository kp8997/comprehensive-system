Ruby has some operators: + : ? ! * etc. They can be use outside the computation context. We can define them in our own class - methods.

Operators usage: expression can be similar to methods call (or send)
  E.g sum = first + second
    sum = first.+(second)
    + is like a method and second is its argument

Ruby also let us defind single object - unary operator - !
  E.g
    def !
      Document.new(title, author, "It is special exclamation mark")
    end
