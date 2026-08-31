We can think about symbol as a way to represent a string. It is immutable and can be used as a hash key.

  String can be used at processing data because of its features
    
  Symbol can be used at 'a string in coding' that stand for something about the code. In another way, they serve a purpose to make code more readable and meaningful in their way
    e.g Books.find(:all, :conditions => { :id => 1 })
    a = :all
    b = :all
    a == b # true
    a.equal?(b) # true

    c = "all"
    d = "all"
    c == d # true
    c.equal?(d) # false

    In ruby == for comparison with content
    while equal? for comparison with identity (memory reference)
