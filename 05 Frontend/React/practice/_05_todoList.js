/*
You're given some existing HTML for a Todo List app. Add the following functionality to the app:

Add new tasks on clicking the "Submit" button.
The <input> field should be cleared upon successful addition.
Remove tasks from the Todo List upon clicking the "Delete" button.
Notes
The focus of this question is on functionality, not styling. There's no need to write any custom CSS.
You may modify the markup (e.g. adding ids, data attributes, replacing some tags, etc.), but the result should remain the same visually.
You may want to think about ways to improve the user experience of the application and implement them (you get bonus credit for doing that during interviews).
*/

import { useState } from "react";

const defaultTasks = [
  { id: 1, name: "Walk the dog" },
  { id: 2, name: "Water the plants" },
  { id: 3, name: "Wash the dishes" },
];

export default function App() {
  const [tasks, setTasks] = useState(defaultTasks);
  const [currentInput, setCurrentInput] = useState("");
  const handleDelete = (i) => {
    
    if (i > tasks.length) {
      return;
    }
    const copiedArr = [...tasks];

    const deleted = copiedArr.splice(i, 1);
    if (deleted) {
      setTasks(copiedArr);
    }
  };

  const handleAdd = () => {
    setTasks(prev => [...prev, {id: tasks.length + 1, name: currentInput}]);
  };

  return (
    <div>
      <h1>Todo List</h1>
      <div>
        <input
          value={currentInput}
          onChange={(e) => setCurrentInput(e.target.value)}
          type="text"
          placeholder="Add your task"
        />
        <div>
          <button onClick={handleAdd}>Submit</button>
        </div>
      </div>
      <ul>
        {tasks?.map((task, i) => {
          return (
            <li key={task.id + task.name}>
              <span>{task.name}</span>
              <button onClick={() => handleDelete(i)}> Delete </button>
            </li>
          );
        })}
      </ul>
    </div>
  );
}
