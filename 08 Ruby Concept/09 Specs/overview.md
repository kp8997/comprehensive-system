
Test with Tooling
  Test Unit
    inherit to Test::Unit::TestCase

    override method setup (similar to beforeEach in JS)

    override method teardown (similar to afterEach in JS)

    test method start with test_

    use assertion methods
      assert_equal
      assert
      assert_not_nil

  Rspec
    before :each # :all
    after :each # :all

    describe "my class or module" do
      it "my test case" do
        @doc = Document.new('test', 'nobody', 'A bunch of words')
      end
    end

    stub and mock: often come together to mock data of object
      stub: to create a fake object via double
        Ex: doc = double("Document)

      mock: to verify interactions with test objects
        allow(@doc).to receive(:word_count).and_return(100)
        expect(@doc).to receive(:word_count)

Isolated test
