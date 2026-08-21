Arrays: Arrays.sort(), Arrays.copy

List: LinkedList, ArrayList
  List.of(), list.add(x), list.remove(x), list.contains(x), list.isEmpty(), list.size(), list.toArray(), list.clear(), list.removeAll(), list.retainAll(), list.addFirst(x), list.addLast(x), list.pollFirst(), list.pollLast(), list.peekFirst(), list.peekLast()

String & StringBuilder
  String: s1.equals(s2), s1.equalsIgnoreCase(s2), s1.indexOf(s2), s1.lastIndexOf(s2), s1.startsWith(s2), s1.endsWith(s2), s1.substring(beginIndex), s1.substring(beginIndex, endIndex), s1.replace(oldChar, newChar), s1.replace(target, replacement), s1.contains(s2), s1.isEmpty(), s1.length(), s1.charAt(index), s1.toCharArray()
  StringBuilder: sb.append(x), sb.deleteCharAt(index), sb.delete(startIndex, endIndex), sb.insert(offset, str), sb.length(), sb.toString()

Map<K, V>: map.put(k, v), map.putIfAbsent(k, v), map.get(k), map.getOrDefault(k), map.containsKey(k), map.remove(k), map.size()
  map.put(k, getOrDefault(k, 0) + 1) // get and put ++ for count base on key
  map.keySet(), map.values(), map.entrySet(): map.entryKey(), map.entryValue()

Set: s.add(x), s.remove(x), s.contains(x), s.isEmpty(), s.size(), s.iterator(), s.toArray(), s.clear(), s.removeAll(), s.retainAll(),

Queue & Deques
  Queue: q.offer(x), q.poll(), q.peek(), q.isEmpty(), q.size()
  Deque: d.addFirst(), d.addLast(), d.pollFirst(), d.pollLast(), d.peekFirst(), d.peekLast(), d.isEmpty(), d.size()

Stack: s.push(x), s.pop(), s.peek(), s.isEmpty(), s.size()
  Deque stack = new ArrayDeque<>();
PriorityQueue: pq.offer(x), pq.poll(), pq.peek(), pq.isEmpty(), pq.size()

Math & Character helpers
  Math: Math.min(a,b), Math.max(a,b), Math.abs(x), Math.sqrt(x), Math.pow(b, e), Math.floor(x), Math.ceil(x)
  Character: Character.isLetterOrDigit(ch), Character.isDigit(x), Character.isLetter(x), Character.toUpperCase(ch), Character.toLowerCase(ch), Character.isWhitespace(ch),
