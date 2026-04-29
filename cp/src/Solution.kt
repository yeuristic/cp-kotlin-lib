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

    //Topological sort
    fun findOrder(numCourses: Int, prerequisites: Array<IntArray>): IntArray {
        val graph = IntGraph(numCourses)
        for (prerequisite in prerequisites) {
            val dependency = prerequisite.last()
            val dependant = prerequisite.first()
            graph.addEdge(dependency, dependant, true)
        }
        val indegreeArr = IntArray(numCourses)
        for (i in 0 until numCourses) {
            for (neighbour in graph.neighbours(i)) {
                indegreeArr[neighbour] += 1
            }
        }
        val queue = ArrayDeque<Int>()
        for (i in 0 until indegreeArr.size) {
            if (indegreeArr[i] == 0) {
                queue.add(i)
            }
        }

        val result = IntArray(numCourses)
        var counter = 0
        while (!queue.isEmpty()) {
            val node = queue.removeFirst()
            result[counter++] = node
            for (neighbour in graph.neighbours(node)) {
                indegreeArr[neighbour] -= 1
                if (indegreeArr[neighbour] == 0) {
                    queue.add(neighbour)
                }
            }
        }

        return if (counter == numCourses) result else IntArray(0)
    }

    //Rolling Hash
    fun findLength(nums1: IntArray, nums2: IntArray): Int {
        val minLength = Math.min(nums1.size, nums2.size)

        //binary search, search sizeK which repeated subarray exist
        var start = 0
        var end = minLength

        var lastMax = 0
        while (start <= end) {
            var mid = start + (end - start) / 2
            val repeatedIndex = searchRepeatedSubarray(nums1, nums2, mid)
            if (repeatedIndex >= 0) {
                lastMax = mid
                start = mid + 1
            } else {
                end = mid - 1
            }
        }
        return lastMax
    }

    private fun searchRepeatedSubarray(nums1: IntArray, nums2: IntArray, k: Int): Int {
        if (k == 0) return 0
        val base = 101L
        val mod = Int.MAX_VALUE.toLong()
        //compute first window
        var aHash = 0L
        var bHash = 0L
        for (i in 0 until k) {
            aHash = (aHash + nums1[i] * modPow(base, k - i - 1, mod) % mod) % mod
            bHash = (bHash + nums2[i] * modPow(base, k - i - 1, mod) % mod) % mod
        }
        fun checkSame(aIdx: Int, bIdx: Int): Boolean {
            if (aHash == bHash) {
                for (i in 0 until k) {
                    if (nums1[i+aIdx] != nums2[i+bIdx]) {
                        return false
                    }
                }
                return true
            }
            return false
        }
        if (checkSame(0, 0)) {
            return 0
        }
        val bOriginalHash = bHash
        val power = modPow(base, k - 1, mod)
        for (i in 0 until (nums1.size - k + 1)) {
            val aPrevIdx = i - 1
            val aNextIdx = i - 1 + k
            bHash = bOriginalHash
            if (aPrevIdx >= 0) {
                aHash = (aHash - nums1[aPrevIdx] * power % mod + mod) % mod
                aHash = (aHash * base + nums1[aNextIdx]) % mod
            }
            for (j in 0 until (nums2.size - k + 1)) {
                if (i == 0 && j == 0) {
                    continue
                }
                val bPrevIdx = j - 1
                val bNextIdx = j - 1 + k
                if (bPrevIdx >= 0) {
                    bHash = (bHash - nums2[bPrevIdx] * power % mod + mod) % mod
                    bHash = (bHash * base + nums2[bNextIdx]) % mod
                }

                if (checkSame(i, j)) {
                    return i
                }
            }
        }

        return -1
    }


    // a ^ b % mod
    fun modPow(a: Long, b: Int, mod: Long): Long {
        var result = 1L
        var base = a % mod
        var exp = b

        while (exp > 0) {
            //for odd exp
            //same as exp % 2 == 1, don't know if the compiler can output same byte code, so I use this bitwise operation
            if (exp and 1 == 1) {
                result = (result * base) % mod
            }
            base = (base * base) % mod

            // faster version of divide by 2
            exp = exp shr 1
        }
        return result
    }


    class TreeNode(var `val`: Int) {
        var left: TreeNode? = null
        var right: TreeNode? = null
    }

    fun isSameTree(p: TreeNode?, q: TreeNode?): Boolean {
        if (p == null && q == null) {
            return true
        } else if (p == null) {
            return false
        } else if (q == null) {
            return false
        } else {
            if (p.`val` != q.`val`) {
                return false
            }
            return isSameTree(p.left, q.left) && isSameTree(p.right, q.right)
        }
    }

//    fun maxArea(height: IntArray): Int {
//        val lines = height.mapIndexed { index, i -> Position(index, i) }
//        val heapLeft = PriorityQueue<Position>()
//        heapLeft.addAll()
//    }
}
