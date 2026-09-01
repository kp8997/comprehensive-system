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

  private :word_count
end

class RomanceNovel < Document
  def number_of_steamy_words
    word_count / 4
  end
end
# Works: self is a Document instance!

doc = Document.new( 'Ethics', 'Spinoza', 'By that which is...' )

doc.about_me

puts "Call doc.send(:word_count)"
puts doc.send(:word_count) # this can by pass the private (visibility)
# puts "----------------"
# puts "Call doc.word_count"
# puts doc.word_count # can not by pass the private (visibility)


pp doc.instance_variables
rn = RomanceNovel.new('Ethics2', 'Spinoza', 'By that which is 2...')
puts "This is a romance novel and it has #{rn.number_of_steamy_words} steamy words"


# pp doc.public_methods
# puts "============="
# pp doc.private_methods
