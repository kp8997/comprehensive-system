Heap separate into small distinct region called **Generations**


1. Young Generation: short-lived data, size is about 1MB - 64MB, temp variable, intermediate object. Depend on the kind of data (variable), V8 will store in Young Generation or directly in stack:
    Young generation: [Object Payloads | Heavy Strings | Floating Points | Arrays].
    Stack Address/pointer, execution context, local variables

2. Old Generation: long-lived data, size is about multi-MB to GB range. E.g global state, cached data, database pool, singleton, etc.

3. Specialized Spaces: big object that exceed the allocation in Young Generation
