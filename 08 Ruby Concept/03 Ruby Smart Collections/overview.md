3. Arrays and Hash

  Arrays
    
    poem_words = [ 'twinkle', 'little', 'star', 'how', 'I', 'wonder' ]

    poem_words = %w{ twinkle little star how I wonder }

  Hash

    freq = { "I" => 1, "don't" => 1, "like" => 1, "spam" => 963 }

    symbol as key: book_info = { :first_name => 'Russ', :last_name => 'Olsen' }

    string as key: book_info = { first_name: 'Russ', last_name: 'Olsen' }

  From the method call (arguments)

    arguments as list params:
      ```
      def echo_all( *args )
        args.each { |arg| puts arg }
      end
      echo_all( 'one', 'two', 'three' )
      ```
    
    arguments of arbitrary type:
    
      ```
      def print(name, size = 12)
        ...
      end
      ```
    
    a single parameter as array

      ```
      def filter_books(books)
        books.join(' ')
      end
      filter_book(["hello", "world", "this", "is", "a", "test"])
      ```
    ------------------------

    hash as parameters: we can use parenthesis or not when passing the hash argument (only one argument)

      ```
      def load_font(fonts_specs)
      end

      load_font(:size => 12, :family => "Arial")
      load_font :size => 12, :family => "Arial"
      ```
    
    loop if arguments is hash
      ```
        def load_font(fonts_specs)
          fonts_specs.each {|key, value| p "Loading #{key} => #{value}" }

          // or

          fonts_specs.each do |entry|
            pp entry
          end
        end
      ```
