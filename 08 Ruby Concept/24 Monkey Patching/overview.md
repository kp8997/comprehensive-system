It is on-the-fly modification. We can use Monkey Patching to modify or add on existing definition of things in Ruby (class, module, etc). When we use patching, it is not replacement, it's just a patching with the existing one

alias_method: can copy an existing method to a new name. Then we can override the old method with new method, the alias one still keep the old method

we can also remove methods by remove_method :word_count

```ruby
class String
  alias_method :old_addition, :+

  def +(other)
    if other.kind_of? Document
      new_content = self + other.content
      return Document.new(other.title, other.author, new_content)
    end
    old_addition(other)
  end
end
```
