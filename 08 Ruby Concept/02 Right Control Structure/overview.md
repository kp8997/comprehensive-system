2. Control structure:

  if -> unless
    if else end
    statement if condition: Prefer one line syntax is there's only a logic

  while -> until
    statement while condition: Prefer one line syntax is there's only a logic

  for -> each
    f.each { |x| "123" } // Prefer one line syntax is there's only a logic
    f.each do
      statement
    end

  case -> switch: case is like the logic in postgresql. should use === for condition in case
    var a = case
      when condition then statement
      when another_condition then statement
      else statement
    end

  default coalescing operator: @first_name ||= '' // similar to @first_name = @first_name || ''

  Default boolean: only nil and false consider false, other infinite things is true -> focus on check false
