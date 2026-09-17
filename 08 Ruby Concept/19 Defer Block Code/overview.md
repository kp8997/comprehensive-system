We have 2 way we block code
  
  explicit
    use block code in yield syntax
    block_given? if we have block code (callback specify or not)

  implicit
    use block code in defer syntax (defer is a method that accept block code and execute it after the method is finished)
    just check the variable that hold defer code is null or not:
    
  example with we make it with log function:
  ```ruby
      def on_save( &block )
        @save_listener = block
      end

      def save
        @document.save
        @save_listener.call if @save_listener
      end
  ```

  example with lazy initialization. Here is the concept: we pass the way we want with call back provision, until we call the function (content in this case), the real code and the block code will execute at that time:
  ```ruby
  class BlockBasedArchivalDocument
    attr_reader :title, :author

    def initialize(title, author, &block)
      @title = title
      @author = author
      @content_generator = block
    end

    def content
      if @content_generator
        @content ||= @content_generator.call
        @content_generator = nil # make it garbage after exec block code
      end
      @content
    end
  end

  # execution with file
  doc = BlockBasedArchivalDocument.new('Title', 'Author') { File.read('document.txt') }
  puts doc.title

  # execution with http
  google_doc = BlockBasedArchivalDocument.new('http', 'russ') do
    Net::HTTP.get_response('http://google.com', '/index.html').body
  end
  ```

  Other ways to pre-defined (or default method) to a variable are: - The major difference is in return, next, break way
    
  lambda : DEFAULT_WITH_LOG = lambda { logger.info { 'Saving document' } }
  
  Proc : DEFAULT_LOG = Proc.new { logger.info { 'Saving document' } }

  ```ruby
    # Proc.new Behavior
    def run_proc
      my_proc = Proc.new { return "Proc finished!" }
      my_proc.call
      "This line will NEVER be reached." # The Proc hijacked the return
    end

    # Lambda Behavior
    def run_lambda
      my_lambda = lambda { return "Lambda finished!" }
      my_lambda.call
      "This line WILL be reached and returned." # Execution continues normally
    end
  ```

  Another difference is the number of arguments:
    
  Proc: execute even if more or less arguments
  
  Lambda: execute only if the number of arguments is exact, otherwise raise error

  ```ruby
    # Procs are forgiving with argument count
    forgiving_proc = Proc.new { |x, y| puts "x: #{x.inspect}, y: #{y.inspect}" }
    forgiving_proc.call(1)       # Output: x: 1, y: nil (No error)
    forgiving_proc.call(1, 2, 3) # Output: x: 1, y: 2   (Ignores the 3)

    # Lambdas are strict with argument count
    strict_lambda = lambda { |x, y| puts "x: #{x}, y: #{y}" }
    strict_lambda.call(1)       # Raises ArgumentError: wrong number of arguments (given 1, expected 2)
  ```

  **Important note**
  In the same context of how we define the block code, other scope of context - variables in this case still save in the memory even though we don't use it, it still exists => memory wasting
  So we should consider to make it available to GC (Garbage Collection) after we done with it by:
  
  ```ruby
    def some_method(doc)
      big_array = Array.new(1_000_000)

      # ... do something

      # ... don't need big_array anymore

      big_array = nil
      
      doc.on_load do |d|
        puts "Logging when loading"
      end
    end
  ```

=============
Summary

We can use block code with 2 main ways and trigger later when some events occur:
  yield
  &block - end parameter of a method

We can store the block of code by 2 ways. The main difference are arguments and return, next, break
  Proc (object - raw chunk of code)
  lambda (like method way)

Variables of the same context of difinition of block code passing will drag along in memory => should set nil if no use
