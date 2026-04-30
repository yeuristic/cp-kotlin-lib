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

class IntDsu(val size: Int) {
    val parents = IntArray(size) { i -> i }
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
        val rootX = find(x)
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

class Dsu<E> {
    val parents = mutableMapOf<E, E>()
    val groupSize = mutableMapOf<E, Int>()
    var numOfGroup = 0

    fun addNode(n: E) {
        parents[n] = n
        groupSize[n] = 1
        numOfGroup++
    }

    fun find(x: E): E {
        //recursive until find root. parents[x] == x means x is root
        if (parents[x] != x) {
            // update all parents of the set to root, flatten
            parents[x] = find(parents[x]!!)
        }
        return parents[x]!!
    }

    fun union(x: E, y: E): Boolean {
        val rootX = find(x)
        val rootY = find(y)

        //already in same set
        if (rootX == rootY) return false

        val minRoot: E
        val maxRoot: E
        if (groupSize[rootX]!! > groupSize[rootY]!!) {
            maxRoot = rootX
            minRoot = rootY
        } else {
            maxRoot = rootY
            minRoot = rootX
        }
        parents[minRoot] = maxRoot
        groupSize[maxRoot] = groupSize[maxRoot]!! + groupSize[minRoot]!!
        numOfGroup--
        return true
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

//Kahn's Algorithm
fun topologicalSort(size: Int, graph: IntGraph): IntArray {
    val inDegreeArray = IntArray(size)
    for (i in 0 until size) {
        for (j in graph.neighbours(i)) {
            inDegreeArray[j] += 1
        }
    }
    val queue = ArrayDeque<Int>()
    for (i in inDegreeArray.indices) {
        val inDegree = inDegreeArray[i]
        if (inDegree == 0) {
            queue.add(i)
        }
    }
    val result = IntArray(size)
    var counter = 0
    while (!queue.isEmpty()) {
        val node = queue.removeFirst()
        for (neighbours in graph.neighbours(node)) {
            inDegreeArray[neighbours] -= 1
            if (inDegreeArray[neighbours] == 0) {
                queue.add(neighbours)
            }
        }
        result[counter++] = node
    }

    //if DAG detected return empty array
    return if (counter == size) result else IntArray(0)
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

fun getLPS(pattern: String): IntArray {
    if (pattern.isEmpty()) return IntArray(0)
    val result = IntArray(pattern.length)
    var currMax = 0
    var i = 1
    while (i < pattern.length) {
        if (pattern[currMax] == pattern[i]) {
            result[i++] = ++currMax
        } else {
            if (currMax != 0) {
                //check if we can use prev lps data, and compare next char of it with current char
                currMax = result[currMax - 1]
            } else {
                result[i++] = 0
            }
        }
    }
    return result
}

fun kmpSearch(text: String, pattern: String): List<Int> {
    if (pattern.isEmpty()) return emptyList()
    val lps = getLPS(pattern)
    var i = 0
    var j = 0

    var result = mutableListOf<Int>()

    while (i in text.indices) {
        if (text[i] == pattern[j]) {
            i++
            j++
        }

        if (j == pattern.length) {
            result.add(i - j)
            j = lps[j - 1]
        } else if (i in text.indices && text[i] != pattern[j]) {
            if (j != 0) {
                j = lps[j - 1]
            } else {
                i++
            }
        }
    }
    return result
}

// Assume all items are positive
fun IntArray.radixSort() {
    var read = this
    var write = IntArray(this.size)
    // last digit for sign is not checked
    for (bit in 0 until 31) {
        var zeroCount = 0
        for (num in read) {
            if ((num shr bit) and 1 == 0) {
                zeroCount++
            }
        }
        var zeroPointer = 0
        var onePointer = zeroCount
        for (num in read) {
            if ((num shr bit) and 1 == 0) {
                write[zeroPointer++] = num
            } else {
                write[onePointer++] = num
            }
        }
        val temp = read
        read = write
        write = temp
    }
    for (i in indices) {
        this[i] = read[i]
    }
}

// Assume all items are positive
fun LongArray.radixSort() {
    var read = this
    var write = LongArray(this.size)
    // last digit for sign is not checked
    for (bit in 0 until 63) {
        var zeroCount = 0
        for (num in read) {
            if ((num shr bit) and 1L == 0L) {
                zeroCount++
            }
        }
        var zeroPointer = 0
        var onePointer = zeroCount
        for (num in read) {
            if ((num shr bit) and 1L == 0L) {
                write[zeroPointer++] = num
            } else {
                write[onePointer++] = num
            }
        }
        val temp = read
        read = write
        write = temp
    }
    for (i in indices) {
        this[i] = read[i]
    }
}

class IntSegmentTree(originalArray: IntArray, val initialValue: Int, val block: (Int, Int) -> Int) {
    val size: Int = originalArray.size
    val tree: IntArray = IntArray(size * 2) { initialValue }

    // Example for sum and the original array = [2,5,6,1,3,1,8]
    // tree array will be = [_,26,15,11,11,4,9] U [2,5,6,1 ,3 ,1 ,8 ]
    //             Index  = [0,1 ,2 ,3 ,4 ,5,6] U [7,8,9,10,11,12,13]
    init {
        for (i in originalArray.indices) {
            tree[size + i] = originalArray[i]
        }
        for (i in size - 1 downTo 1) {
            tree[i] = block(tree[i * 2] , tree[i * 2 + 1])
        }
    }

    fun query(start: Int, endExcl: Int): Int {
        var lResult = initialValue
        var rResult = initialValue
        var l = start + size
        var r = endExcl + size
        while (l < r) {
            if (l and 1 == 1) {
                lResult = block(lResult, tree[l++])
            }
            if (r and 1 == 1) {
                rResult = block(tree[--r], rResult)
            }
            l /= 2
            r /= 2
        }
        return block(lResult, rResult)
    }

    fun update(index: Int, value: Int) {
        var treeIndex = index + size
        tree[treeIndex] = value

        while (treeIndex > 1) {
            treeIndex /= 2
            tree[treeIndex] = block(tree[2 * treeIndex], tree[2 * treeIndex + 1])
        }
    }
}

class Heap<E>(private val data: MutableList<E>, val comparator: Comparator<E>) {
    constructor(initialData: Collection<E>, comparator: Comparator<E>) : this(initialData.toMutableList(), comparator)
    constructor(initialSize: Int, comparator: Comparator<E>): this(ArrayList<E>(initialSize), comparator)

    init {
        heapify()
    }

    private fun heapify() {
        for (i in parent(data.lastIndex) downTo 0) {
            percolateDown(i)
        }
    }

    private fun percolateDown(index: Int) {
        var idx = index
        while (idx in data.indices) {
            val leftIndex = leftChild(idx)
            val rightIndex = rightChild(idx)
            val currValue = data[idx]
            val leftValue = if (leftIndex in data.indices) data[leftIndex] else null
            val rightValue = if (rightIndex in data.indices) data[rightIndex] else null
            if (leftValue != null && rightValue != null) {
                val (smallestIdx, smallestChild) = if (comparator.compare(leftValue, rightValue) < 0) leftIndex to leftValue else rightIndex to rightValue
                if (comparator.compare(currValue, smallestChild) > 0) {
                    data[smallestIdx] = currValue
                    data[idx] = smallestChild
                    idx = smallestIdx
                } else {
                    break
                }
            } else if (leftValue != null) {
                if (comparator.compare(currValue, leftValue) > 0) {
                    data[leftIndex] = currValue
                    data[idx] = leftValue
                    idx = leftIndex
                } else {
                    break
                }
            } else {
                break
            }
        }
    }

    private fun percolateUp(index: Int) {
        var idx = index
        while (idx > 0) {
            val parentIndex = parent(idx)
            if (comparator.compare(data[idx], data[parentIndex]) < 0) {
                val temp = data[parentIndex]
                data[parentIndex] = data[idx]
                data[idx] = temp
                idx = parentIndex
            } else {
                break
            }
        }
    }

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun add(element: E) {
        data.add(element)
        percolateUp(data.lastIndex)
    }

    fun peek(): E? = if (isEmpty()) null else data.first()

    fun pop(): E? = if (isEmpty()) null else {
        val lastData = data.last()
        val firstData = data.first()
        data[0] = lastData
        data.removeLast()
        percolateDown(0)
        firstData
    }

    fun size() = data.size

    private fun parent(i: Int) = (i - 1) / 2
    private fun leftChild(i: Int) = 2 * i + 1
    private fun rightChild(i: Int) = 2 * i + 2
}

class TrieNode {
    val children = HashMap<Char, TrieNode>()
    var isLeaf: Boolean = false
}

class Trie {
    val RESULT_EXACT_AND_PREFIX = 14
    val RESULT_EXACT = 15
    val RESULT_PREFIX= 16
    val RESULT_NOT_FOUND = 17
    val root = TrieNode()

    fun insert(dictionary: String) {
        var node = root
        for (i in dictionary.indices) {
            val c = dictionary[i]
            if (node.children.containsKey(c)) {
                node = node.children[c]!!
            } else {
                val newNode = TrieNode()
                node.children[c] = newNode
                node = newNode
            }
        }
        node.isLeaf = true
    }

    fun search(word: Iterable<Char>): Int {
        var node = root
        for (c in word) {
            val nextNode = node.children[c] ?: return RESULT_NOT_FOUND
            node = nextNode
        }
        if (node.isLeaf) {
            return if (node.children.isEmpty()) RESULT_EXACT else RESULT_EXACT_AND_PREFIX
        } else {
            return RESULT_PREFIX
        }
    }
}