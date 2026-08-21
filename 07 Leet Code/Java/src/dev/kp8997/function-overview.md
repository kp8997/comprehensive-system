Arrays

List: LinkedList, ArrayList

String & StringBuilder
  String
  StringBuilder:

Map: map.put(k, v), map.putIfAbsent(k, v), map.get(k), map.getOrDefault(k), map.containsKey(k), map.remove(k), map.size()
  map.put(k, getOrDefault(k, 0) + 1) // get and put ++ for count base on key
  map.keySet(), map.values(), map.entrySet(): map.entryKey(), map.entryValue()

Set: s.add(x), s.remove(x), s.contains(x), s.isEmpty(), s.size(), s.iterator(), s.toArray(), s.clear(), s.removeAll(), s.retainAll(),

Queue & Deques
  Queue
  Deque: addFirst(), addLast(), pollFirst(), pollLast(), peekFirst(), peekLast(), isEmpty(), size()
    

Stack: s.push(x), s.pop(), s.peek(), s.isEmpty(), s.size()
  Deque stack = new ArrayDeque<>();
PriorityQueue: pq.offer(x), pq.poll(), pq.peek(), pq.isEmpty(), pq.size()

Math & Character helpers
  Math: Math.min(a,b), Math.max(a,b), Math.abs(x), Math.sqrt(x), Math.pow(b, e), Math.floor(x), Math.ceil(x)
  Character: Character.isLetterOrDigit(ch), Character.isDigit(x), Character.isLetter(x), Character.toUpperCase(ch), Character.toLowerCase(ch), Character.isWhitespace(ch),
