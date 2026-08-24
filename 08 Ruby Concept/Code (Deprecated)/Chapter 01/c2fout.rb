print "Hello. Please enter a Celsius value: "
celsius = gets.to_i
fahrenheit = (celsius * 9 / 5) + 32
puts "Saving result to output file 'temp.out'"

file = File.new("temp.out", "w")
file.puts(fahrenheit)
file.close()
