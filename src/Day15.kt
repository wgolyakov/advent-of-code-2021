import kotlin.math.abs

fun main() {
    fun parseInput(input: List<String>): Array<Array<Int>> {
        val result = Array(input[0].length) { Array(input.size) { 0 } }
        for ((y, line) in input.withIndex())
            for ((x, n) in line.withIndex())
                result[x][y] = n.toString().toInt()
        return result
    }

    data class Node(val x: Int, val y: Int, val cost: Int) {
        var g = 0
        var h = 0
        var f = 0
        var cameFrom: Node? = null
    }

    fun heuristicCostEstimate(n1: Node, n2: Node): Int {
        return abs(n1.x - n2.x) + abs(n1.y - n2.y)
    }

    fun reconstructPath(goalNode: Node): List<Node> {
        val path = mutableListOf<Node>()
        var node: Node? = goalNode
        while (node != null) {
            path.add(node)
            node = node.cameFrom
        }
        return path.reversed()
    }

    fun neighborNodes(node: Node, nodeMap: Array<Array<Node>>): List<Node> {
        val nodes = mutableListOf<Node>()
        if (node.x > 0)
            nodes.add(nodeMap[node.x - 1][node.y])
        if (node.x < nodeMap.size - 1)
            nodes.add(nodeMap[node.x + 1][node.y])
        if (node.y > 0)
            nodes.add(nodeMap[node.x][node.y - 1])
        if (node.y < nodeMap[node.x].size - 1)
            nodes.add(nodeMap[node.x][node.y + 1])
        return nodes
    }

    // Algorithm A*
    fun findMinPath(map: Array<Array<Int>>): List<Node> {
        val nodeMap = Array(map.size) { x -> Array(map[x].size) { y -> Node(x, y, map[x][y]) } }
        val start = nodeMap[0][0]
        val goal = nodeMap.last().last()
        val closedSet = mutableSetOf<Node>()
        val openSet = mutableSetOf(start)
        start.g = 0
        start.h = heuristicCostEstimate(start, goal)
        start.f = start.g + start.h
        while (openSet.isNotEmpty()) {
            val x = openSet.minByOrNull { it.f }!!
            if (x == goal)
                return reconstructPath(goal)
            openSet.remove(x)
            closedSet.add(x)
            for (y in neighborNodes(x, nodeMap)) {
                if (closedSet.contains(y)) continue
                val tentativeGScore = x.g + y.cost
                val tentativeIsBetter: Boolean
                if (openSet.contains(y)) {
                    tentativeIsBetter = tentativeGScore < y.g
                } else {
                    openSet.add(y)
                    tentativeIsBetter = true
                }
                if (tentativeIsBetter) {
                    y.cameFrom = x
                    y.g = tentativeGScore
                    y.h = heuristicCostEstimate(y, goal)
                    y.f = y.g + y.h
                }
            }
        }
        error("Path not found")
    }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        val path = findMinPath(map)
        return path.sumOf { it.cost } - map[0][0]
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)
        val fullMap = Array(map.size * 5) { Array(map[0].size * 5) { 0 } }
        for (a in 0 until 5) {
            for (b in 0 until 5) {
                for (x in map.indices) {
                    for (y in map[x].indices) {
                        var risk = (map[x][y] + a + b) % 9
                        if (risk == 0) risk = 9
                        fullMap[a * map.size + x][b * map[x].size + y] = risk
                    }
                }
            }
        }
        val path = findMinPath(fullMap)
        return path.sumOf { it.cost } - fullMap[0][0]
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput) == 40)
    check(part2(testInput) == 315)

    val input = readInput("Day15")
    println(part1(input))
    println(part2(input))
}
