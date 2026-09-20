Help to reduce repetitious code. Consider the below example

```ruby
# before refactor
class Paragraph
  attr_accessor : font_name, :font_size, :font_emphasis
  attr_accessor :text
  
  def initialize( font_name, font_size, font_emphasis, text='')
    @font_name = font_name
    @font_size = font_size
    @font_emphasis = font_emphasis
    @text = text
  end
  
  def to_s
    @text
  end
end

class StructuredDocument
  attr_accessor :title, :author, :paragraphs
  
  def initialize( title, author )
    @title = title
    @author = author
    @paragraphs = []
    yield( self ) if block_given?
  end
  
  def <<(paragraph)
    @paragraphs << paragraph
  end

  def content
    @paragraphs.inject('') { |text, para| "#{text}\n#{para}" }
  end
end

# Execution
russ_cv = StructuredDocument.new( 'Resume', 'RO' ) do |cv|
  cv << Paragraph.new( :nimbus, 14, :bold, 'Russ Olsen' )
  cv << Paragraph.new( :nimbus, 12, :italic, '1313 Mocking Bird Lane')
  cv << Paragraph.new( :nimbus, 12, :none, 'russêrussolsen.com')
#.. and so on
end
```
The problem is if we have many documents, so we have to add paragraph like this. The first solution is we will try to add a new class that help init this so we can reduce the repetitious code

```ruby
class Resume < StructuredDocument
  def name ( text )
  paragraph = Paragraph.new( :nimbus, 14, :bold, text )
  self < paragraph
  end

  def address ( text )
  paragraph = Paragraph.new( :nimbus, 12, :italic, text )
  self < paragraph
  end

  def email ( text )
  paragraph = Paragraph.new( :nimbus, 12, :none, text )
  self < paragraph
  end
end

# now we only have to do this instead of create paragraph ourself
russ_cv = Resume.new( 'russ', 'resume') do |cv|
cv.name ( 'Russ Olsen' )
cv.address ( '1313 Mocking Bird Lane' ) cv.email( 'russ@russolsen.com' )
# Btc...
end
```
