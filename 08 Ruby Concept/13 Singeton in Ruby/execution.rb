hand_built_stub_printer = Object.new

def hand_built_stub_printer.available
  true
end

p hand_built_stub_printer.available

# another syntax for lot of singletons

class << hand_built_stub_printer
  def available
    true
  end

  def render
    "I'm rendering"
  end
end

p hand_built_stub_printer.available

p hand_built_stub_printer.render

my_object = DocumentIdentifier

def my_object.check
  puts "self is #{self}"
  puts "and its class is #{self.class}"
end

my_object.explain
