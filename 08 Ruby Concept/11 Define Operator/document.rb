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
end
