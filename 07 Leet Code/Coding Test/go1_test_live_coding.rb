=begin
Learners and Courses
Learners are people using Go1 to learn something. Courses are videos, quizzes, interactive experiences, etc.

For this challenge, the dataset contains a map of LearnerIds and a list of CourseIds that the Learner's have completed.

We'd like to know about courses that have only been completed by a single learner.

Return a list of CourseId's that have only been completed by 1 Learner.

Example input:

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
Example output:

[ "Course-0001", "Course-0004" ]
=end

# input = {
#   "Learner-0001": [
#     "Course-0001",
#     "Course-0002",
#     "Course-0003"
#   ],
#   "Learner-0002": [
#     "Course-0002",
#     "Course-0003",
#     "Course-0004"
#   ]
# }

# input = {
#   "Learner-0001": [
#     "Course-0001"
#   ],
#   "Learner-0002": [
#     "Course-0002"
#   ],
#   "Learner-0003": [
#     "Course-0003"
#   ],
#   "Learner-0004": [
#     "Course-0004"
#   ]
# }

# input = {
#   "Learner-0001": [
#     "Course-0001",
#     "Course-0002",
#     "Course-0003"
#   ],
#   "Learner-0002": [
#     "Course-0002",
#     "Course-0003",
#     "Course-0004"
#   ]
# }

# input = {
#   "Learner-0001": [
#     "Course-0001",
#     "Course-0002",
#     "Course-0003"
#   ]
# }

# input = {
#   "Learner-0001": [
#     "Course-0001",
#     "Course-0002",
#     "Course-0003",
#     "Course-0001",
#     "Course-0002",
#     "Course-0003"
#   ],
#   "Learner-0002": [
#     "Course-0002",
#     "Course-0003",
#     "Course-0004"
#   ]
# }

# input = {
#   "Learner-0001": [
#     "Course-0001",
#     "Course-0002",
#     "Course-0003"
#   ],
#   "Learner-0002": [
#     "Course-0002",
#     "Course-0003",
#     "Course-0004"
#   ],
#   "Learner-0003": [
#     "Course-0004",
#     "Course-0005",
#     "Course-0006"
#   ],
#   "Learner-0004": [
#     "Course-0005",
#     "Course-0006",
#     "Course-0007"
#   ]
# }

input = {
  "Learner-0001": [
    "Course-0001",
    "Course-0002",
    "Course-0003"
  ],
  "Learner-0002": [
    "Course-0002",
    "Course-0003"
  ],
  "Learner-0003": [
    "Course-0002",
    "Course-0003",
    "Course-0004"
  ]
}

answer = {}

input.each do |learner_id, courses|
  set_answer = Set.new
  courses.each do |course|
    set_answer.add(course)
  end

  set_answer.each do |course|
    if !answer[course]
      answer[course] = 0
    end
    answer[course] += 1
  end
end

puts answer

results = []
answer.select do |key, value|
  if value == 1
    results.push(key)
  end
end

puts results

=begin
Return a map<learner, list<courses>> of courses where that learner is the only learner to take the course.
(3-step variant of the problem, using the output of the original problem).

Return a map of learners to the list of courses where that learner is the only learner that has completed a course.

Example input:
{  "Learner-0001": [    "Course-0001",    "Course-0002",    "Course-0003"  ],
  "Learner-0002": [    "Course-0002",    "Course-0003",    "Course-0004"  ]}

Example output:{  "Learner-0001": ["Course-0001"],  "Learner-0002": ["Course-0004"]}
=end

answer2 = {}

input.each do |learner_id, courses|
  set_answer = Set.new
  courses.each do |course|
    set_answer.add(course)
  end

  set_answer.each_with_index do |course, index|
    results.each do |result|
      if result == course
        if !answer2[learner_id]
          answer2[learner_id] = []
        end
        answer2[learner_id].push(course)
      end
    end
  end
end

puts answer2
