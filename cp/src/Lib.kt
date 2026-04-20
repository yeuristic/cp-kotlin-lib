import java.util.PriorityQueue
import kotlin.collections.ArrayDeque
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.Set
import kotlin.collections.emptyList
import kotlin.collections.map
import kotlin.collections.mutableListOf
import kotlin.collections.mutableMapOf

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

    fun addEdge(v1: Int, v2: Int, directed: Boolean = false) {
        adjacent[v1].add(v2)
        if (!directed) adjacent[v2].add(v1)
    }

    fun neighbours(n: Int): List<Int> {
        return adjacent[n]
    }
}

class WIntGraph(val size: Int) {
    private val adjacent: Array<MutableList<Pair<Int, Int>>> = Array(size) { mutableListOf() }

    fun addEdge(v1: Int, v2: Int, weight: Int = 1, directed: Boolean = false) {
        adjacent[v1].add(v2 to weight)
        if (!directed) adjacent[v2].add(v1 to weight)
    }

    fun neighbours(n: Int): List<Pair<Int, Int>> {
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

class Dsu(val size: Int) {
    val parents = IntArray(size){ i -> i }
    val groupSize = IntArray(size) { 1 }
    var numOfGroup = size

    fun find(x: Int): Int {
        //recursive until find root. parents[x] == x means x is root
        if (parents[x] != x) {
            // update all parents of the set to root, flatten
            parents[x] = find(parents[x])
        }
        return parents[x]
    }

    fun union(x: Int, y: Int) {
        val rootX =  find(x)
        val rootY = find(y)

        //already in same set
        if (rootX == rootY) return

        val minRoot: Int
        val maxRoot: Int
        if (groupSize[rootX] > groupSize[rootY]) {
            maxRoot = rootX
            minRoot = rootY
        } else {
            maxRoot = rootY
            minRoot = rootX
        }
        parents[minRoot] = maxRoot
        groupSize[maxRoot] += groupSize[minRoot]
        numOfGroup--
    }
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

fun dijkstra(graph: WIntGraph, start: Int): IntArray {
    val distance = IntArray(graph.size) { Int.MAX_VALUE }
    val heap = PriorityQueue<Pair<Int, Int>> { a, b -> a.second.compareTo(b.second) }
    heap.add(start to 0)
    while (!heap.isEmpty()) {
        val node = heap.poll()!!
        val currVertex = node.first
        val currDistance = node.second
        if (currDistance > distance[currVertex]) {
            continue
        }
        distance[currVertex] = currDistance
        for (neighbour in graph.neighbours(currVertex)) {
            val (v, d) = neighbour
            val newDistance = distance[currVertex] + d
            if (distance[v] > newDistance) {
                distance[v] = newDistance
                heap.add(v to newDistance)
            }
        }

    }
    return distance
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
//SYNTAX
val maxHeap = PriorityQueue(Comparator<Int> { a, b -> b.compareTo(a) })
val minHeap = PriorityQueue<Int>()
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