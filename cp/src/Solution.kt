import java.util.PriorityQueue

class Solution {
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        val g = WIntGraph(n)
        for (flight in flights) {
            g.addEdge(flight[0], flight[1], flight[2], true)
        }
        val distance = Array(n) { IntArray(k + 1){ Int.MAX_VALUE } }
        val heap = PriorityQueue<Triple<Int, Int, Int>> { a, b -> a.second.compareTo(b.second) }
        heap.add(Triple(src, 0, -1))
        while (!heap.isEmpty()) {
            val node = heap.poll()!!
            val currVertex = node.first
            val currDistance = node.second
            val currStops = node.third
            if (currStops >= k) {
                continue
            }
            if (currStops >= 0 && currDistance < distance[currVertex][currStops]) {
                distance[currVertex][currStops + 1] = currDistance
            }
            for (neighbour in g.neighbours(currVertex)) {
                val (v, d) = neighbour
                val newStops = currStops + 1
                val newDistance = if (currStops < 0) d else distance[currVertex][currStops] + d
                if (distance[v][newStops] > newDistance) {
                    distance[v][newStops] = newDistance
                    heap.add(Triple(v , newDistance, newStops))
                }
            }
        }
        val result = distance[dst].min()
        if (result == Int.MAX_VALUE) {
            return -1
        }
        return result
    }
}