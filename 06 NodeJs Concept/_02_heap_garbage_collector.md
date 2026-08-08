Heap separate into small distinct region called **Generations**

Heap Architecture
  1. Young Generation: short-lived data, size is about 1MB - 64MB, temp variable, intermediate object. Depend on the kind of data (variable), V8 will store in Young Generation or directly in stack:
      Young generation: [Object Payloads | Heavy Strings | Floating Points | Arrays]
      Stack Address/pointer, execution context, local variables
      TurboFan: Optimizing compiler. Will alter the default above behaviors of 2 types of memory:
        If a object in function stack, that not escape (return) the function, they will let stack handle the memory of that object (heap to stack)
        If they find a closure usage into function, they will let the heap store that value (stack to heap)

  2. Old Generation: long-lived data, size is about multi-MB to GB range. E.g global state, cached data, database pool, singleton, etc.

  3. Specialized Spaces: big object that exceed the allocation in Young Generation

Garbage Collector
  1. Scavenger (mini GC): for Young Generation (1-100 ms)
  3. Mark Sweep Compact (major GC): for Old Generation (10-1000 ms)
  