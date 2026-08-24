1. Basic Convention

  Code should simple so we can look their definition, they can tell what they do just by glimpse of. That is the purpose of Ruby: **Simple** and **Concise**. Base on that we have some conventions

  Name: Class must be camel case (DocumentContract). other cases (variable, methods, etc.) should be snake case (read_with_filter)

  Comment: Not necessary to add it, focus on 'how to use' it instead 'how it work'. Sometimes we need 'how it work' on some special hard case. But when add comment, think of about we can refactor into a simpler form of codebase

  Indentation: Should use 2 space for each indent level for not collide with other.

  Parenthesis: Should add for clarity. If the case is too obvious (puts "abc") or empty params, we can skip it.

  Line break: A statement a line. If need break line, break after = operator
