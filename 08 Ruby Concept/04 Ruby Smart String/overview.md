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
      
