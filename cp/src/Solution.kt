import java.util.PriorityQueue
import kotlin.collections.first
import kotlin.collections.last

class Solution {
    fun findCheapestPrice(n: Int, flights: Array<IntArray>, src: Int, dst: Int, k: Int): Int {
        val g = WIntGraph(n)
        for (flight in flights) {
            g.addEdge(flight[0], flight[1], flight[2], true)
        }
        val distance = Array(n) { IntArray(k + 1) { Int.MAX_VALUE } }
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
                    heap.add(Triple(v, newDistance, newStops))
                }
            }
        }
        val result = distance[dst].min()
        if (result == Int.MAX_VALUE) {
            return -1
        }
        return result
    }

    fun findCircleNum(isConnected: Array<IntArray>): Int {
        val size = isConnected.first().size
        val intDsu = IntDsu(size)
        for (i in isConnected.indices) {
            for (j in isConnected.first().indices) {
                if (isConnected[i][j] == 1) {
                    intDsu.union(i, j)
                }
            }
        }
        return intDsu.numOfGroup
    }

    inner class Point(val x: Int, val y: Int) {
        override fun equals(other: Any?): Boolean {
            if (other !is Point) {
                return false
            }
            return x == other.x && y == other.y
        }

        override fun toString(): String {
            return "x=$x, y=$y"
        }

        override fun hashCode(): Int {
            var result = x
            result = 31 * result + y
            return result
        }
    }

    inner class Edge(
        val start: Point,
        val end: Point,
    ) : Comparable<Edge> {
        fun distance(): Int {
            val xDist = Math.abs(start.x - end.x)
            val yDist = Math.abs(start.y - end.y)
            return xDist + yDist
        }

        override fun compareTo(other: Edge): Int {
            return distance() - other.distance()
        }
    }

    // Kruskal
    fun minCostConnectPoints(points: Array<IntArray>): Int {
        val dsu = Dsu<Point>()
        val heap = PriorityQueue<Edge>()
        for (i in points.indices) {
            for (j in points.indices) {
                if (i != j) {
                    val start = Point(points[i].first(), points[i].last())
                    val end = Point(points[j].first(), points[j].last())
                    heap.add(Edge(start, end))
                }
            }
        }
        for (point in points) {
            dsu.addNode(Point(point.first(), point.last()))
        }

        var cost = 0

        while (dsu.numOfGroup > 1) {
            val minEdge = heap.poll()
            val success = dsu.union(minEdge.start, minEdge.end)
            if (success) {
                cost += minEdge.distance()
            }
        }
        return cost
    }

}