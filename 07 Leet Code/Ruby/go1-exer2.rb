=begin

Quiz Results
Go1 supports learners completing quizzes on subjects they are learning. For this challenge, we'll focus on multiple-choice format quizzes (each answer to the quiz is one of A, B, C or D).

For this challenge, we'll be looking at the quality of a given quiz. The dataset contains a list of correct answers and a list of anonymised learner answers.

We're interested in finding out about what's the easiest question in the quiz. The easiest question is question most learners get correct.

Return the index of the question most learners get correct.

Example input:

// Correct Answers
["A", "B", "C"]

// Learner Responses
[
    ["A", "B", "B"],
    ["C", "B", "C"],
    ["A", "B", "C"],
    ["B", "B", "A"],
    ["A", "B", "C"]
]
Example output:

"The easiest question is index 1"
=end

results = []

answers = ["A", "B", "C"]

input = [
    ["A", "B", "B"],
    ["C", "B", "C"],
    ["A", "B", "C"],
    ["B", "B", "A"],
    ["A", "B", "C"]
]

input.each do |response|
  response.each_with_index do |user, index|
    if answers[index] == user
      results[index] = 0 if results[index].nil?
      results[index] += 1
    end
  end
end
