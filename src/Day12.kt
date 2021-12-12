fun main() {
    data class Cave(val name: String) {
        val connections: MutableSet<Cave> = mutableSetOf()
        fun isSmall() = name[0].isLowerCase()
    }
    data class PathCount(var value: Int)

    fun parseInput(input: List<String>): Map<String, Cave> {
        val caves = mutableMapOf<String, Cave>()
        for (line in input) {
            val (name1, name2) = line.split('-')
            val cave1 = caves.getOrPut(name1) {  Cave(name1) }
            val cave2 = caves.getOrPut(name2) {  Cave(name2) }
            cave1.connections.add(cave2)
            cave2.connections.add(cave1)
        }
        return caves
    }

    fun scanPaths1(cave: Cave, path: MutableList<Cave>, pathCount: PathCount) {
        if (cave.name == "end") {
            pathCount.value++
            return
        }
        if (cave.isSmall() && path.contains(cave))
            return
        path.add(cave)
        for (c in cave.connections) {
            scanPaths1(c, path, pathCount)
        }
        path.removeLast()
    }

    fun caveCount(cave: Cave, path: List<Cave>): Int {
        return path.count { it == cave }
    }

    fun smallCaveVisitedTwice(path: List<Cave>): Boolean {
        for (cave in path)
            if (cave.isSmall() && caveCount(cave, path) > 1) return true
        return false
    }

    fun scanPaths2(cave: Cave, path: MutableList<Cave>, pathCount: PathCount) {
        if (cave.name == "end") {
            pathCount.value++
            return
        }
        if (cave.name == "start" && path.isNotEmpty())
            return
        if (cave.isSmall() && path.contains(cave) && (smallCaveVisitedTwice(path) || caveCount(cave, path) > 1))
            return
        path.add(cave)
        for (c in cave.connections) {
            scanPaths2(c, path, pathCount)
        }
        path.removeLast()
    }

    fun part1(input: List<String>): Int {
        val caves = parseInput(input)
        val startCave = caves["start"]!!
        val pathCount = PathCount(0)
        scanPaths1(startCave, mutableListOf(), pathCount)
        return pathCount.value
    }

    fun part2(input: List<String>): Int {
        val caves = parseInput(input)
        val startCave = caves["start"]!!
        val pathCount = PathCount(0)
        scanPaths2(startCave, mutableListOf(), pathCount)
        return pathCount.value
    }

    val testInput = readInput("Day12_test")
    check(part1(testInput) == 10)
    check(part2(testInput) == 36)

    val testInput2 = readInput("Day12_test2")
    check(part1(testInput2) == 19)
    check(part2(testInput2) == 103)

    val testInput3 = readInput("Day12_test3")
    check(part1(testInput3) == 226)
    check(part2(testInput3) == 3509)

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
