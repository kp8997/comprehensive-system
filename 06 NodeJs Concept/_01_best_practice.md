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

2. declare a new class of response and override toJson() method to avoid modification by third party or another devs

```
const user1 = {
    username: 'pojo',
    email: 'pojo@example.org'
};
    
class User {
    constructor(username, email) {
        this.username = username;
        this.email = email;
    }

    toJSON() {
        return {
            username: this.username,
            email: this.email,
        };
    }
}
const user2 = new User('class', 'class@example.org');

// res.send(user1); // POJO
// res.send(user2); // Class Instance

user1.password = user2.password = 'hunter2';

output:
{"username":"pojo","email":"pojo@example.org","password":"hunter2"}
{"username":"class","email":"class@example.org"}
```

This will leak a new field by mistake, use class to adhere encapsulation concept to avoid this.
