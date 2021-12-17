import kotlin.math.sign
import kotlin.math.sqrt

fun main() {
    fun parseInput(input: List<String>): Pair<IntRange, IntRange> {
        // target area: x=20..30, y=-10..-5
        val (x, y) = input[0].substring("target area: ".length).split(", ")
        val (x1, x2) = x.substring("x=".length).split("..").map { it.toInt() }
        val (y1, y2) = y.substring("y=".length).split("..").map { it.toInt() }
        return Pair(x1..x2, y1..y2)
    }

    fun findHighestY(vx0:Int, vy0: Int, targetX: IntRange, targetY: IntRange): Int {
        var x = 0
        var y = 0
        var vx = vx0
        var vy = vy0
        var target = false
        var yMax = y
        while (x <= targetX.last && y >= targetY.first) {
            x += vx
            y += vy
            vx -= vx.sign
            vy--
            if (x in targetX && y in targetY)
                target = true
            if (y > yMax)
                yMax = y
        }
        return if (target) yMax else -1
    }

    fun part1(input: List<String>): Int {
        val (targetX, targetY) = parseInput(input)
        val vx0Min = ((-1.0 + sqrt(1.0 + 8.0 * targetX.first)) / 2.0).toInt()
        val vx0Max = ((-1.0 + sqrt(1.0 + 8.0 * targetX.last)) / 2.0).toInt() + 1
        var highestY = 0
        for (vx0 in vx0Min..vx0Max) {
            for (vy0 in 1..targetX.last) {
                val y = findHighestY(vx0, vy0, targetX, targetY)
                if (y > highestY)
                    highestY = y
            }
        }
        return highestY
    }

    fun part2(input: List<String>): Int {
        val (targetX, targetY) = parseInput(input)
        val vx0Min = ((-1.0 + sqrt(1.0 + 8.0 * targetX.first)) / 2.0).toInt()
        var count = 0
        for (vx0 in vx0Min..targetX.last) {
            for (vy0 in targetY.first..targetX.last) {
                val y = findHighestY(vx0, vy0, targetX, targetY)
                if (y != -1)
                    count++
            }
        }
        return count
    }

    val testInput = readInput("Day17_test")
    check(part1(testInput) == 45)
    check(part2(testInput) == 112)

    val input = readInput("Day17")
    println(part1(input))
    println(part2(input))
}
