Some basic of raw Regex. We can divide into 2 types:
  - a match on a character
  - a match with group (by start with and end with)

  1. Match on a character
    dot: . equals any character

    range: [abc] equals 1 character that could be a, b, or c

    From the range we have some relevant concepts:

      [a-z] or [0-9] to match a character that is letter or digit

      \d: digit

      \s: whitespace, newline, tab

      \w: word, alphabet and underscore

  2. Match with group (by start with and end with)

    * : zero or any characters. Often use with dot "."
      [aeiou].*: match with word start with vowel


Ruby with Regex

  // =~ ""
  puts /PM/ =~ '10:24 PM'
