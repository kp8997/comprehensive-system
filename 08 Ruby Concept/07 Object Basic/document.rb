class Document

  def initialize(title, author, content)
    @content = content
    @title = title
    @author = author
  end

  def words
    @content.split
  end

  def word_count
    words.size
  end

  def about_me
    puts "I am #{self}"
    puts "Document: #{@title} by #{@author}"
    puts "I have #{word_count} words."
  end

  def to_s
    "Document printout: #{@title} by #{@author}"
  end
end

doc = Document.new( 'Ethics', 'Spinoza', 'By that which is...' )

doc.about_me

puts doc

pp doc.instance_variables

pp doc.public_methods
