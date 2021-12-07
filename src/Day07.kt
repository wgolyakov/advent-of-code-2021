import kotlin.math.*

fun main() {

    fun part1(input: List<Int>): Int {
        val maxPosition = input.maxOrNull() ?: return 0
        val fuelByPosition = mutableListOf<Int>()
        for (alignPosition in 0..maxPosition) {
            val fuel = input.sumOf { abs(it - alignPosition) }
            fuelByPosition.add(fuel)
        }
        return fuelByPosition.minOrNull() ?: 0
    }

    fun part2(input: List<Int>): Int {
        val maxPosition = input.maxOrNull() ?: return 0
        val fuelByPosition = mutableListOf<Int>()
        for (alignPosition in 0..maxPosition) {
            //val fuel = input.sumOf { (1..abs(it - alignPosition)).sum() }
            val fuel = input.sumOf {
                val n = abs(it - alignPosition)
                (1 + n) * n / 2
            }
            fuelByPosition.add(fuel)
        }
        return fuelByPosition.minOrNull() ?: 0
    }

    val testInput = readIntsCommaSeparated("Day07_test")
    check(part1(testInput) == 37)
    check(part2(testInput) == 168)

    val input = readIntsCommaSeparated("Day07")
    println(part1(input))
    println(part2(input))
}
