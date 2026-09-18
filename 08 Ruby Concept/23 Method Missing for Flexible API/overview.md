We can leverage missing_method to make up virtual function to handle dynamic demand. Example we have a FormLetter class to handle replace template variable. We don't know how many variable in advance, so we can use missing_method to handle this.

```ruby
class FormLetter < Document

  def replace word (old word, new word)
    string_name = name.to_s
    return super unless string_name =~ /'replace_\w+/
    old_word = extract_old_word(string_name)
    replace_word(old_word, args.first)
  end

  def extract_old_word(name)
    name_parts = name.split('_')
    name_parts[1].upcase
  end
end
```

We have OpenStruct missing method as example for this

Active Record also make up function for this, like Account.find_by_first_name_and_find_by_last_name
