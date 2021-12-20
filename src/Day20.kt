fun main() {
    fun parseInput(input: List<String>): Pair<String, Array<String>> {
        val enhAlg = input[0]
        val map = Array(input.size - 2) { "" }
        for (i in 2 until input.size) {
            map[i - 2] = input[i]
        }
        return Pair(enhAlg, map)
    }

    fun enhance(map: Array<String>, enhAlg: String, empty: Char): Array<String> {
        val enhMap = Array(map.size + 2) { StringBuilder(".".repeat(map[0].length + 2)) }
        for (y in enhMap.indices) {
            for (x in enhMap[y].indices) {
                val strBin = StringBuilder()
                for (i in -1..1) {
                    val m = y - 1 + i
                    for (j in -1..1) {
                        val n = x - 1 + j
                        val c = if (m >= 0 && m < map.size && n >= 0 && n < map[m].length) map[m][n] else empty
                        strBin.append(if (c == '#') '1' else '0')
                    }
                }
                val index = strBin.toString().toInt(2)
                enhMap[y][x] = enhAlg[index]
            }
        }
        return enhMap.map { it.toString() }.toTypedArray()
    }

    fun enhanceEmptySpace(empty: Char, enhAlg: String): Char {
        return when("${enhAlg.first()}${enhAlg.last()}") {
            ".." -> '.'
            ".#" -> '.'
            "#." -> if (empty == '.') '#' else '.'
            else -> '#'
        }
    }

    fun part1(input: List<String>): Int {
        val (enhAlg, map) = parseInput(input)
        var empty = '.'
        var m = map
        for (i in 1..2) {
            m = enhance(m, enhAlg, empty)
            empty = enhanceEmptySpace(empty, enhAlg)
        }
        return m.sumOf { it.count { c -> c == '#' } }
    }

    fun part2(input: List<String>): Int {
        val (enhAlg, map) = parseInput(input)
        var empty = '.'
        var m = map
        for (i in 1..50) {
            m = enhance(m, enhAlg, empty)
            empty = enhanceEmptySpace(empty, enhAlg)
        }
        return m.sumOf { it.count { c -> c == '#' } }
    }

    val testInput = readInput("Day20_test")
    check(part1(testInput) == 35)
    check(part2(testInput) == 3351)

    val input = readInput("Day20")
    println(part1(input))
    println(part2(input))
}
