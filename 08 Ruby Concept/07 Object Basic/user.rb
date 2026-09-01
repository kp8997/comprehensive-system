class User
  def initialize(secret_pin)
    @secret_pin = secret_pin
  end

  def print_my_pin
    # ALLOWED: Implicit receiver (calls on self)
    puts "My PIN is #{pin}"
  end

  def compare_pin_bad(other_user)
    # ERROR! `other_user.pin` has an explicit receiver (`other_user`), which private forbids.
    self.pin == other_user.pin 
  end

  private

  def pin
    @secret_pin
  end
end

user_a = User.new(1234)
user_b = User.new(1234)

user_a.print_my_pin          # => Works: "My PIN is 1234"
# user_a.pin                 # => NoMethodError (called from outside)
# user_a.compare_pin_bad(user_b) # => NoMethodError: private method `pin' called for #<User:...>
