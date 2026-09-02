# file: document_spec.rb
require_relative 'document'

describe Document do
  # Runs before each individual example
  before(:each) do
    @doc = Document.new('test', 'nobody', 'A bunch of words')
  end

  it 'should hold on to the contents' do
    @doc.content.should == 'A bunch of words'
  end

  it 'should include individual words' do
    @doc.words.should include('bunch')
  end
end
