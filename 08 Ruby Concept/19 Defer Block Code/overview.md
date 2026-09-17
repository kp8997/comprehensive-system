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
    class ArchivalDocument
    end
  ```
  
