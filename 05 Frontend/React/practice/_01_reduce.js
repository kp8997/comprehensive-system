/**
 * @template T, U
 * @param {(previousValue: U, currentValue: T, currentIndex: number, array: T[]) => U} callbackFn
 * @param {U} [initialValue]
 * @return {U}
 */
Array.prototype.myReduce = function (callbackFn, initialValue) {
  // const type = typeof initialValue;
  // console.log("initialValue ", initialValue);

  if (typeof initialValue === "function") {
    throw TypeError("initialValue error");
  }
  const arr = this.flat();

  if (arr === null || arr === undefined) {
    console.log("initialValue ", initialValue);
    throw TypeError("type must be array");
  }

  if (arr.length === 0 && 
  (initialValue === null || initialValue === undefined)) {
    throw TypeError("must provide array or initialValue");
  }
  
  if (
    arr.length === 1 &&
    (initialValue === null || initialValue === undefined)
  ) {
    return arr[0];
  }

  let accumulator;
  let j = 0;

  // if (initialValue === null || initialValue === undefined) {
  //   if (arguments.length >= 2) {
  //     accumulator = initialValue;
  //   } else {
  //     accumulator = arr[0];
  //     j = 1;
  //   }
  // }  else {
  //   accumulator = initialValue;
  // } 

  if (arguments.length >= 2) {
      accumulator = initialValue;
  }
  else {
    accumulator = arr[0];
    j = 1;
  }

  for (let i = j; i < arr.length; i++) {
    accumulator = callbackFn(accumulator, arr[i], i, arr);
  }
  return accumulator;
};
