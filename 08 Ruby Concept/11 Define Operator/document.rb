class Document
  def initialize(title:, version:, content: "")
    @title = title
    @version = version
    @content = content
  end

  def +(other)
    Document.new(
      title: @title,
      version: "#{@version} + #{other.version}",
      content: "#{@content}\n#{other.content}"
    )
  end

  def !
      Document.new(title: @title, version: @version, content: "It is special exclamation mark")
  end
  
  def +@
    Document.new(title: @title, version: @version, content: "I'm sure that")
  end

  def -@
    Document.new(title: @title, version: @version, content: "I'm doubt that")
  end
end

doc = Document.new(title: 'hello', version: '1', content: 'hello world!')

puts doc

doc.!

pp doc.!

pp !doc

pp +(-(+doc))
