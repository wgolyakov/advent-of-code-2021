fun main() {
    fun parseInput(input: List<String>): Array<Array<Int>> {
        val result = Array(input[0].length) { Array(input.size) { 0 } }
        for ((y, line) in input.withIndex())
            for ((x, n) in line.withIndex())
                result[x][y] = n.toString().toInt()
        return result
    }

    fun findLowPoints(map: Array<Array<Int>>): List<Pair<Int, Int>> {
        val lowPoints = mutableListOf<Pair<Int, Int>>()
        for (x in map.indices) {
            for (y in map[x].indices) {
                val height = map[x][y]
                val left = if (x > 0) map[x - 1][y] else 9
                val right = if (x < map.size - 1) map[x + 1][y] else 9
                val up = if (y > 0) map[x][y - 1] else 9
                val down = if (y < map[x].size - 1) map[x][y + 1] else 9
                if (height < left && height < right && height < up && height < down)
                    lowPoints.add(Pair(x, y))
            }
        }
        return lowPoints
    }

    fun fillBasin(point: Pair<Int, Int>, basin: MutableSet<Pair<Int, Int>>, map: Array<Array<Int>>) {
        if (basin.contains(point)) return
        basin.add(point)
        val x = point.first
        val y = point.second
        val h = map[x][y]
        val left = if (x > 0) map[x - 1][y] else 9
        val right = if (x < map.size - 1) map[x + 1][y] else 9
        val up = if (y > 0) map[x][y - 1] else 9
        val down = if (y < map[x].size - 1) map[x][y + 1] else 9
        if (h < left && left != 9)
            fillBasin(Pair(x - 1, y), basin, map)
        if (h < right && right != 9)
            fillBasin(Pair(x + 1, y), basin, map)
        if (h < up && up != 9)
            fillBasin(Pair(x, y - 1), basin, map)
        if (h < down && down != 9)
            fillBasin(Pair(x, y + 1), basin, map)
    }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        return findLowPoints(map).sumOf { (x, y) -> 1 + map[x][y] }
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)
        val basins = mutableListOf<Int>()
        for (lowPoint in findLowPoints(map)) {
            val basin = mutableSetOf<Pair<Int, Int>>()
            fillBasin(lowPoint, basin, map)
            basins.add(basin.size)
        }
        basins.sort()
        return basins.takeLast(3).reduce { mul, basin -> mul * basin }
    }

    val testInput = readInput("Day09_test")
    check(part1(testInput) == 15)
    check(part2(testInput) == 1134)

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
