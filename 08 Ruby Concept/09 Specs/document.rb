class Document
  attr_reader :content # for testing purpose

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
