fun main() {
    fun parseInput(input: List<String>): Array<Array<Int>> {
        val result = Array(input[0].length) { Array(input.size) { 0 } }
        for ((y, line) in input.withIndex())
            for ((x, n) in line.withIndex())
                result[x][y] = n.toString().toInt()
        return result
    }

    fun doStep(map: Array<Array<Int>>) {
        for (x in map.indices)
            for (y in map[x].indices)
                map[x][y]++
        var needFlash: Boolean
        do {
            needFlash = false
            for (x in map.indices) {
                for (y in map[x].indices) {
                    if (map[x][y] > 9) {
                        map[x][y] = 0
                        for (a in x - 1..x + 1)
                            for (b in y - 1..y + 1)
                                if (a >= 0 && b >= 0 && a < map.size && b < map[x].size && map[a][b] > 0)
                                    map[a][b]++
                        needFlash = true
                    }
                }
            }
        } while (needFlash)
    }

    fun countFlashes(map: Array<Array<Int>>): Int {
        return map.sumOf { col -> col.count { it == 0 } }
    }

    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        var flashCount = 0
        for (step in 1..100) {
            doStep(map)
            flashCount += countFlashes(map)
        }
        return flashCount
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input)
        val all = map.size * map[0].size
        var step = 0
        do {
            step++
            doStep(map)
        } while (countFlashes(map) < all)
        return step
    }

    val testInput = readInput("Day11_test")
    check(part1(testInput) == 1656)
    check(part2(testInput) == 195)

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
