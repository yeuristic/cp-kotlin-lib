private val br = System.`in`.bufferedReader()

fun nextLine(): String {
    return br.readLine()
}

fun readStr() = nextLine() // string line
fun readInt() = readStr().toInt() // single int
fun readStrings() = readStr().split(" ") // list of strings
fun readInts() = readStrings().map { it.toInt() } // list of ints

class IntGraph(val size: Int) {
    private val adjacent: Array<MutableList<Int>> = Array(size) { mutableListOf() }

    fun addEdge(u: Int, v: Int, directed: Boolean = false) {
        adjacent[u].add(v)
        if (!directed) adjacent[v].add(u)
    }

    fun neighbours(n: Int): List<Int> {
        return adjacent[n]
    }
}

class Graph<E> {
    private val adjacent = mutableMapOf<E, MutableList<E>>()

    fun addNode(n: E) {
        adjacent.putIfAbsent(n, mutableListOf())
    }

    fun addEdge(a: E, b: E, directed: Boolean = false) {
        adjacent.putIfAbsent(a, mutableListOf())
        adjacent.putIfAbsent(b, mutableListOf())

        adjacent[a]!!.add(b)
        if (!directed) {
            adjacent[b]!!.add(a)
        }
    }

    fun neighbors(u: E): List<E> {
        return adjacent[u] ?: emptyList()
    }

    fun nodes(): Set<E> = adjacent.keys
}

class WGraph<E> {
    private val adjacent = mutableMapOf<E, MutableList<Pair<E, Int>>>()

    fun addNode(n: E) {
        adjacent.putIfAbsent(n, mutableListOf())
    }

    fun addEdge(a: E, b: E, directed: Boolean = false, weight: Int = 1) {
        adjacent.putIfAbsent(a, mutableListOf())
        adjacent.putIfAbsent(b, mutableListOf())

        adjacent[a]!!.add(b to weight)
        if (!directed) {
            adjacent[b]!!.add(a to weight)
        }
    }

    fun neighbors(u: E): List<Pair<E, Int>> {
        return adjacent[u] ?: emptyList()
    }

    fun nodes(): Set<E> = adjacent.keys
}

fun bfsEx(graph: IntGraph, start: Int): IntArray {
    val distance = IntArray(graph.size) { Int.MAX_VALUE }
    distance[start] = 0
    val queue = ArrayDeque<Int>()
    queue.add(start)
    while (!queue.isEmpty()) {
        val node = queue.removeFirst()
        for (neighbours in graph.neighbours(node)) {
            val newDistance = distance[node] + 1
            if (distance[neighbours] > newDistance) {
                distance[neighbours] = newDistance
                queue.add(neighbours)
            }
        }
    }
    return distance
}

fun dfsEx(graph: IntGraph, start: Int): BooleanArray {
    val visited = BooleanArray(graph.size) { false }
    val stack = ArrayDeque<Int>()
    stack.add(start)
    while (!stack.isEmpty()) {
        val node = stack.removeLast()
        if (visited[node]) {
            continue
        }
        visited[node] = true
        for (neighbours in graph.neighbours(node)) {
            if (!visited[neighbours]) {
                stack.add(neighbours)
            }
        }
    }
    return visited
}


fun IntArray.binarySearch(left: Int, right: Int, target: Int): Int {
    var start = left
    var end = right

    while (start <= end) {
        val mid = start + (end - start) / 2
        if (get(mid) == target) {
            return mid
        } else if (get(mid) < target) {
            start = mid + 1
        } else {
            end = mid - 1
        }
    }

    return -1
}

val stack = ArrayDeque<Int>()
val shiftRightOp = 2 shr 1 // 1 --> 0...010 to 0...001
val shiftLeftOp = 3 shl 1 // 6 --> 0...011 to 0...110
val orOp = 1 or 0 // 1
val andOp = 0 and 1 // 0
val xorOp = 1 xor 1 // 0

fun test() {
    stack.addLast(1)
    stack.addLast(2)
    println(stack.removeLast()) // 2
    println(shiftRightOp)
    println(shiftLeftOp)
    println(orOp)
    println(andOp)
    println(xorOp)
}