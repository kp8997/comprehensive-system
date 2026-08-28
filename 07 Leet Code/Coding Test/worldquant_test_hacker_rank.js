function getDelayedFlights(flightNodes = 0, flightFrom = [], flightTo = [], delayed = []) {
  // 1. Guard against empty or invalid flight count
  if (typeof flightNodes !== 'number' || flightNodes <= 0) {
    return [];
  }

  // 2. Sanitize and validate the initial `delayed` list
  const validDelayed = Array.isArray(delayed)
    ? delayed.filter(id => typeof id === 'number' && id >= 1 && id <= flightNodes)
    : [];

  // Short-circuit: If no valid flights are delayed, return early
  if (validDelayed.length === 0) {
    return [];
  }

  // 3. Initialize adjacency map for all valid nodes
  const adj = new Map();
  for (let i = 1; i <= flightNodes; i++) {
    adj.set(i, []);
  }

  // 4. Sanitize and populate edges, filtering out invalid or missing pairs
  const safeFrom = Array.isArray(flightFrom) ? flightFrom : [];
  const safeTo = Array.isArray(flightTo) ? flightTo : [];
  const edgeCount = Math.min(safeFrom.length, safeTo.length);

  for (let i = 0; i < edgeCount; i++) {
    const from = safeFrom[i];
    const to = safeTo[i];

    // Validate that both ends are valid flight numbers
    if (
      typeof from === 'number' && from >= 1 && from <= flightNodes &&
      typeof to === 'number' && to >= 1 && to <= flightNodes
    ) {
      adj.get(from).push(to);
    }
  }

  // 5. Multi-source BFS traversal
  const visited = new Set(validDelayed);
  const queue = [...validDelayed];
  let head = 0;

  while (head < queue.length) {
    const current = queue[head++];
    const neighbors = adj.get(current) || [];

    for (let i = 0; i < neighbors.length; i++) {
      const neighbor = neighbors[i];
      if (!visited.has(neighbor)) {
        visited.add(neighbor);
        queue.push(neighbor);
      }
    }
  }

  // 6. Return sorted numeric result
  return Array.from(visited).sort((a, b) => a - b);
}

// Example scenarios:
console.log(getDelayedFlights(5, [1, 2, , null], [2, 3, 4, 5], [1])); 
// Output: [1, 2, 3] (ignores invalid edge pairs gracefully)

console.log(getDelayedFlights(5, [1, 2], [2, 3], [])); 
// Output: [] (empty delayed array returns early)

console.log(getDelayedFlights(5, [], [], [3, null, 1])); 
// Output: [1, 3] (cleans invalid items and sorts)
