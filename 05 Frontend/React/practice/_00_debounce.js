/**
 * @param {(...args: Array<unknown>) => unknown} func
 * @param {number} wait
 * @returns {(...args: Array<unknown>) => void}
 */
export default function debounce(func, wait) {
  let timer = null;
  return function (...args) {
    clearTimeout(timer);
    const context = this;
    timer = setTimeout(() => {
      timer = null // reset
      func.apply(context, args);
    }, wait)
  };
  // throw "Not implemented!";
}

let i = 0;
function increment(step = 1) {
  i += step;
}
const debouncedIncrement = debounce(increment, 100);

// t = 0: Call debouncedIncrement().
debouncedIncrement(); // i = 0
debouncedIncrement(4);
debouncedIncrement(4);

debouncedIncrement(5);
debouncedIncrement(5);

console.log(i) // 9

//
