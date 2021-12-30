import kotlin.math.*

fun main() {
    data class Point(val x: Int, val y: Int)
    data class Line(val a: Point, val b: Point)

    fun parseInput(input: List<String>): List<Line> {
        val lines = mutableListOf<Line>()
        for (line in input) {
            val (point1, point2) = line.trim().split("->")
            val (x1, y1) = point1.trim().split(',').map { it.toInt() }
            val (x2, y2) = point2.trim().split(',').map { it.toInt() }
            lines.add(Line(Point(x1, y1), Point(x2, y2)))
        }
        return lines
    }

    fun overlapCount(lines: List<Line>): Int {
        val maxX = lines.maxOf { max(it.a.x, it.b.x) }
        val maxY = lines.maxOf { max(it.a.y, it.b.y) }
        val diagram: Array<Array<Int>> = Array(maxX + 1) { Array(maxY + 1) { 0 } }
        for (line in lines)
            when {
                line.a.x == line.b.x -> {
                    val yRange = if (line.a.y < line.b.y) line.a.y..line.b.y else line.b.y..line.a.y
                    for (y in yRange) diagram[line.a.x][y]++
                }
                line.a.y == line.b.y -> {
                    val xRange = if (line.a.x < line.b.x) line.a.x..line.b.x else line.b.x..line.a.x
                    for (x in xRange) diagram[x][line.a.y]++
                }
                else -> {
                    val len = abs(line.b.x - line.a.x)
                    val xSign = (line.b.x - line.a.x).sign
                    val ySign = (line.b.y - line.a.y).sign
                    for (i in 0..len)
                        diagram[line.a.x + i * xSign][line.a.y + i * ySign]++
                }
            }
        var count = 0
        for (column in diagram)
            for (value in column)
                if (value > 1) count++
        return count
    }

    fun part1(input: List<String>): Int {
        val lines = parseInput(input).filter { it.a.x == it.b.x || it.a.y == it.b.y }
        return overlapCount(lines)
    }

    fun part2(input: List<String>): Int {
        val lines = parseInput(input)
        return overlapCount(lines)
    }

    val testInput = readInput("Day05_test")
    check(part1(testInput) == 5)
    check(part2(testInput) == 12)

    val input = readInput("Day05")
    println(part1(input))
    println(part2(input))
}
