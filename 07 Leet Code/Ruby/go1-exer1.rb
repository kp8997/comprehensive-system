=begin
  
Learners are people using Go1 to learn something. Courses are videos, quizzes, interactive experiences, etc.

For this challenge, the dataset contains a map of LearnerIds and a list of CourseIds that the Learner's have completed.

We'd like to know about courses that have only been completed by a single learner.

Return a list of CourseId's that have only been completed by 1 Learner.

# Input example

  {
  "Learner-0001": [
    "Course-0001",
    "Course-0002",
    "Course-0003"
  ],
  "Learner-0002": [
    "Course-0002",
    "Course-0003",
    "Course-0004"
  ]
}

# Output example: ["Course-0001", "Course-0004"]

=end

# Solution

hash = {
  "Learner-0001": [
    "Course-0001",
    "Course-0002",
    "Course-0003"
  ],
  "Learner-0002": [
    "Course-0002",
    "Course-0003",
    "Course-0004"
  ]
}

new_hash_counter = Hash.new(0)

hash.each do |learner, courses|
  courses.each do |course|
    new_hash_counter[course] += 1
  end
end

new_hash_counter.reject {|k,n| n > 1}
