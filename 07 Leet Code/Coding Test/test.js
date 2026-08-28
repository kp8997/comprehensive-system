/*
Watched Courses
Go1 learners interact with multiple courses in a session. Courses could be videos, interactive training, quizzes, etc.

For this challenge, we'll be looking at what courses learners watch next. The dataset contains a list of learner sessions. Each session is an ordered list of course IDs.

We're interested in knowing for each course, what course do most learners complete next?

Return the most common next course for each course in the session data.

Example input:

[
    ["Course_001", "Course_002", "Course_003", "Course_004"]
]
Example output:

{
    "Course_001" : "Course_002",
    "Course_002" : "Course_003",
    "Course_003" : "Course_004"
}

*/

const courses = [
    ["Course_001"],
    ["Course_001","Course_002"],
    ["Course_002","Course_003"],
    ["Course_001","Course_002","Course_003","Course_004"],
    ["Course_002","Course_003","Course_001","Course_004"]
]

let relationships = {};

for (let i = 0; i < courses.length; i++) {
  for (let j = 0; j < courses[i].length - 1; j++) {
    if (relationships[courses[i][j]]) {
      relationships[courses[i][j]][courses[i][j + 1]] = (relationships[courses[i][j]][courses[i][j + 1]] || 0) + 1;
    } else {
      relationships[courses[i][j]] = {
        [courses[i][j + 1]]: 1
      };
    }
  }
}

// relationships[courses[i][]]
// count what is the max value of relationships[courses[i][j]]

console.log(relationships)
