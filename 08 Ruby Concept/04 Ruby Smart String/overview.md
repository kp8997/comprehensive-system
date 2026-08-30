Type of String and initialization. Remmber that we can use \ to escapse and #{} for embeding expression. Can use as replacement to avoid escape character
  
  1. with quote

    single quote
      greeting = 'hello, i\'m Johnathan'
      greeting = 'hello, #{name}'

    double quote
      greeting = "hello, #{name}"
      greeting = "hello, \"Johnathan\""

  2. with symbol
   
    symbol with ()
      greeting = %q(
        hello
      )

    symbol with {}
      greeting = %Q{
      hello
      }
    
