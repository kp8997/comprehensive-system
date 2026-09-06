1. Focus on writing methods

2. Break the logic into small pieces ensure that they are simple enough to figure out of what they are doing just by glancing at it


Code Example

First Attempt
```ruby
  
```

Refactored Version
```ruby
  
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
