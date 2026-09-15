Block of code is also used for wrapper execution of code along with definition of caller - callback

```ruby
class SomeApplication
  def do_something
    with_logging('load') { @doc = Document.load( 'resume.txt' ) }

    with_logging('save') { @doc.save }
  end

  # ...

  def with_logging(description)
    begin
      @logger.debug { "starting #{description}" }
      yield
      @logger.debug { "completed #{description}" }
    rescue
      @logger.error { "#{description} failed" }
    end
  end
end
```


```ruby
# frozen_string_literal: true

class TimeRecorder
  def initialize(logger = Rails.logger)
    @logger = logger
  end

  def record_time(description)
    start_time = Time.current
    yield
  ensure
    log_message = "#{description} completed in #{Time.current - start_time}s"
    @logger.info { log_message }
  end
end
```
We can include the yield in initialize method for wrapping block of code of constructor of a class

```ruby
  class Document
    def initialize(title, content, author)
      @title = title
      @content = content
      @author = author
      yield(self) if block_given?
    end
  end

  Document.new('Title', 'Content', 'Author') do |doc|
    puts doc.title
  end
```
