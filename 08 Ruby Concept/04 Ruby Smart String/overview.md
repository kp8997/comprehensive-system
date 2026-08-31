Type of String and initialization. Remmber that we can use \ to escapse and #{} for embeding expression. Can use as replacement to avoid escape character
  
  1. with quote

    single quote: can not use with interpolation and escapse sequence (\n \t) except for \
      greeting = 'hello, i\'m Johnathan'
      greeting = 'hello, #{name}'

    double quote
      greeting = "hello, #{name}"
      greeting = "hello, \"Johnathan\""

  2. with symbol: %q = '' and %Q = "". So can not use with interpolation and escapse sequence (\n \t) except for \
    symbol %() default to %Q.
    %q can go with any characters as long as the beginning and ending with a similar string
   
    symbol with ()
      greeting = %q(hello)

      greeting = %Q(hello)

    symbol with {}
      greeting = %q{hello}
      greeting = %Q{hello}
    
    symbol with []
      greeting = %q[hello]
      greeting = %Q[hello]
    
    symbol with <>
      greeting = %q<hello>
      greeting = %Q<hello>

    symbol with any characters
      greeting = %q!hello!
      greeting = %^hello^
      
  3. Some api/built-in function for strings

    modification: 
      lstrip, rstrip, strip: trim white space
      chomp, chop: delete last character or last end symbol like \n
      sub, gsub: replace string with the new one, replace all or just first matched

    search:
      index

    transform:
      iterate form: each_char, each_byte, each_line
    
    inflection facility: it lies in ActiveSupport::Inflector module

      single <-> plural
        pluralize: "string".pluralize

        singularize: "strings".singularize

      rails code <-> database

        tableize: "string".tableize

        classify: "tableize_name".classify

      rails code <-> UI
        pascalize: "rails_code".pascalize
        humanize: "underscore_name".humanize
        titleize: "underscore_name".titleize
        dasherize: "underscore_name".dasherize

      case:
        camelize: "rails_code".camelize
        underscore: "Pascalize".underscore
        snakize: "underscore_name".snakize

  4. caveat

    String in ruby similar to javascript except for this. Variable assignment is reference
    ```
    s = "hello"
    s[1] = 'd'
    # javascript can not do this
    ```

    make the string truely immutable by freeze. will cause error in ruby if modifying
    ```
    s.freeze
    s << "world"
    ```

  