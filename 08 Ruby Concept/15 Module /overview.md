1. Module is like a class but it can not instantiate. It uses for containers, (class is container and factory to create instance)

2. Module can have methods (both instance and singleton methods). We can treat this like object too (because in ruby everything is object)

3. We should separate into multiple modules if we have many methods with the same class name for clearer context

4. We should use specific classes inside a module namespace for a clearer context. This prevents a single module from becoming too cluttered with unrelated methods:
  
  ```ruby
  # not recommended (methods grouped in one place)
  PaymentProcessor.get_stripe_balance
  PaymentProcessor.get_paypal_balance

  # recommended (clear context and responsibility)
  PaymentProcessor::Stripe.get_balance
  PaymentProcessor::Paypal.get_balance
  ```

5. It also contains default values as constants of modules

6. A module with the same name can define distributed file (this is a very powerful feature in ruby, allowing us to organize the code in a very flexible way)

Example

```ruby
  # file payment_processor/charge.rb
  module PaymentProcessor
    def get_stripe_balance
    end

    class Charge
    end
  end

  # file payment_processor/refund.rb
  module PaymentProcessor
    def get_employee_bank_account
    end

    class Refund
    end
  end

  # execution

  PaymentProcessor::Charge.new
  PaymentProcessor::Refund.new

  # we still can call although they defined in 2 separate files
  PaymentProcessor.get_stripe_balance
  PaymentProcessor.get_employee_bank_account
```
