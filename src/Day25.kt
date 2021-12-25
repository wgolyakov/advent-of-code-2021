fun main() {
    fun move(map: List<StringBuilder>): Pair<List<StringBuilder>, Boolean> {
        val map2 = map.map { StringBuilder(it) }
        var stop = true
        // East cucumbers move
        for (y in map.indices) {
            for ((x, c) in map[y].withIndex()) {
                if (c == '>') {
                    val xr = if (x < map[y].length - 1) x + 1 else 0
                    if (map[y][xr] == '.') {
                        map2[y][xr] = '>'
                        map2[y][x] = '.'
                        stop = false
                    }
                }
            }
        }
        // South cucumbers move
        val map3 = map2.map { StringBuilder(it) }
        for (y in map2.indices) {
            for ((x, c) in map2[y].withIndex()) {
                if (c == 'v') {
                    val yd = if (y < map2.size - 1) y + 1 else 0
                    if (map2[yd][x] == '.') {
                        map3[yd][x] = 'v'
                        map3[y][x] = '.'
                        stop = false
                    }
                }
            }
        }
        return Pair(map3, stop)
    }

    fun part1(input: List<String>): Int {
        var map = input.map { StringBuilder(it) }
        var stop: Boolean
        var step = 0
        do {
            val result = move(map)
            map = result.first
            stop = result.second
            step++
        } while (!stop)
        return step
    }

    val testInput = readInput("Day25_test")
    check(part1(testInput) == 58)

    val input = readInput("Day25")
    println(part1(input))
}
