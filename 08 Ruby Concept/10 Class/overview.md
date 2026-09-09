1. Focus on writing methods

2. Break the logic into small pieces ensure that they are simple enough to figure out of what they are doing just by glancing at it

3. Methods overhead is small when creating new method: just def and end. Approach of creating methods for single responsibility is a good one.
  principle: short and coherent

4. Heavy fine-grained methods tend to make classes easier to test.
  Not everytime we need to separate too much for a piece of logic. Sometimes we can compose of it (the key is make sure it only has a purpose for current function only - no more reuseable)

Code Example:

First Attempt
```ruby
class TextCompressor
  attr_reader :unique, :index

  def initialize( text )
    @unique = []
    @index = []
    words = text.split
    words.each do |word|
      i = @unique.index(word)
      if i
        @index << i
      else
        @unique << word
        @index << unique.size - 1
      end
    end
  end
end
```

Refactored Version
```ruby
class TextCompressor
  attr_reader :unique, :index
  def initialize( text )
    @unique = []
    @index = []
    words = text.split
    words. each do |word|
      i = unique_index_of(word)
      if i
        @index << i
      else
        @index << add_unique_word(word)
      end
    end
  end
  
  def unique_index_of(word)
    @unique.index(word)
  end

  def add_uniqueword( word )
    @unique «< word unique.size - 1
  end
end
```

2nd Refactored Version
```ruby
class TextCompressor
  attr_reader :unique, :index

  def initialize(text)
    @unique = []
    @index = []
    add_text(text)
  end
  
  def add_text(text)
    words = text.split
    words.each { |word| add_word(word) }
  end

  def add_word(word)
    i = unique_index_of(word) || add_unique_word(word)
    @index << i
  end

  def unique_index_of(word)
    @unique.index(word)
  end

  def add_unique_word(word)
    @unique << word
    unique.size - 1
  end
end
```
