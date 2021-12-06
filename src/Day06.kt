fun main() {

    fun part1(input: List<Int>): Int {
        val fishes = input.toMutableList()
        for (day in 1..80) {
            for (i in fishes.indices) {
                if (fishes[i] == 0) {
                    fishes[i] = 6
                    fishes.add(8)
                } else {
                    fishes[i]--
                }
            }
        }
        return fishes.size
    }

    fun part2(input: List<Int>): Long {
        val timer: Array<Long> = Array(9) { 0 }
        for (fish in input)
            timer[fish]++
        for (day in 1..256) {
            val f0 = timer[0]
            timer[0] = 0
            for (i in 1..8)
                timer[i - 1] = timer[i]
            timer[6] += f0
            timer[8] = f0
        }
        return timer.sum()
    }

    val testInput = readIntsCommaSeparated("Day06_test")
    check(part1(testInput) == 5934)
    check(part2(testInput) == 26984457539)

    val input = readIntsCommaSeparated("Day06")
    println(part1(input))
    println(part2(input))
}
