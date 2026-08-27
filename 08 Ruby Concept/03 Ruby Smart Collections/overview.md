3. Arrays and Hash

  Arrays
    
    poem_words = [ 'twinkle', 'little', 'star', 'how', 'I', 'wonder' ]

    poem_words = %w{ twinkle little star how I wonder }

  Hash

    freq = { "I" => 1, "don't" => 1, "like" => 1, "spam" => 963 }

    symbol as key: book_info = { :first_name => 'Russ', :last_name => 'Olsen' }

    string as key: book_info = { first_name: 'Russ', last_name: 'Olsen' }

  From the method call (arguments)

    1. arguments as list params:
      ```
      def echo_all( *args )
        args.each { |arg| puts arg }
      end
      echo_all( 'one', 'two', 'three' )
      ```
    
    2. arguments of arbitrary type:
    
      ```
      def print(name, size = 12)
        ...
      end
      ```
    
    3. a single parameter as array

      ```
      def filter_books(books)
        books.join(' ')
      end
      filter_book(["hello", "world", "this", "is", "a", "test"])
      ```
    ------------------------

    1. hash as parameters: we can use parenthesis or not when passing the hash argument (only one argument)

      ```
      def load_font(fonts_specs)
      end

      load_font(:size => 12, :family => "Arial")
      load_font :size => 12, :family => "Arial"
      ```
    
    2. loop if arguments is hash
      ```
        def load_movie(config)
          config.each {|key, value| p "Loading #{key} => #{value}" }

          // or

          config.each do |entry|
            pp entry
          end
        end
        config = { title: '2001', genre: 'sci fl', rating: 10}
        load_font(config)
      ```
  Other function/method for array

  1. find index with 2 ways
    
    ```
    def index_for(word)
      i = 0
      words.each do |this_word|
        return i if word == this_word
        i += 1
      end
      nil
    end

    def index_for(word)
      words.find_index { |w| w == word }
    end
    ```
    
  2. map

    ```
    lower_case_words = doc.words.map { |word| word.downcase}
    ```

  3. inject: we can think of it is reduce in javascript

    ```
    def average_word_length
      total = words.inject(0.0) { |result, word| word.size + result}
      total / word_count
    end
    ```

  Bang symbol (exclamation mark (!)): bang means beware of the method -> it mutate the variable. Beside Bang function, some function without bang can also change the source data like pop, push, delete, shift.

  ```
  a = [1,2,3]
  p a.reverse // [3,2,1]
  p a // [1,2,3]

  p a.reverse! // [3,2,1]
  p a // [3,2,1]
  ```

  4. beware of this, since after delete -10, the index is 1, and will increase to 2, but after deletion, -9 now shifting into index 1 that cause the missing of number

  ```
    array = [0, -10, -9, 5, 9]
    array.each_index { |i| array.delete_at(i) if array[i] < 0}
    pp array
  ```

--------------

summarize
  we have map, reduce, filter, sort, some, every in javascript
  we have map, inject, select, sort, any, all in ruby
