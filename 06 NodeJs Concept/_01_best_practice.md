1. Callbacks should always be asynchronous:

    Good:
    ```
    function foo(count, callback) {
        if (count <= 0) {
            return process.nextTick(() => callback(new TypeError('count > 0')));
        }
        myAsyncOperation(count, callback);
    }
    ```

    Bad:
    ```
    // Antipattern
    function foo(count, callback) {
        if (count <= 0) {
            return callback(new TypeError('count > 0'));
        }
        myAsyncOperation(count, callback);
    }
    ```
    This is Zalgo - when you mix synchronous code with asynchronous code in the same callback

    By wrapping the error callback in process.nextTick(), foo guarantees that any code written synchronously after foo(...) will finish executing before the callback is ever invoked

2. 
