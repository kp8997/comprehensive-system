# file: document_test.rb
require 'test/unit'
require_relative 'document' # your source class

class DocumentTest < Test::Unit::TestCase
  # Runs before EACH test method
  def setup
    @doc = Document.new('test', 'nobody', 'A bunch of words')
  end

  # Runs after EACH test method
  def teardown
    # Clean up files, reset state, etc.
  end

  def test_contents_are_retained
    assert_equal 'A bunch of words', @doc.content, 'Contents are still there'
  end

  def test_word_inclusion
    assert @doc.words.include?('bunch')
  end
end
