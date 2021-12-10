import java.util.*

fun main() {
    fun part1(input: List<String>): Int {
        val openToClose = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        val score = mapOf(')' to 3, ']' to 57, '}' to 1197, '>' to 25137)
        var result = 0
        for (line in input) {
            val stack = Stack<Char>()
            for (c in line) {
                if (c in openToClose.keys)
                    stack.push(c)
                else if (!stack.empty() && openToClose[stack.pop()] != c)
                    result += score[c]!!
            }
        }
        return result
    }

    fun part2(input: List<String>): Long {
        val openToClose = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        val score = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)
        val incompletePoints = mutableListOf<Long>()
        for (line in input) {
            val stack = Stack<Char>()
            var corruptedLine = false
            for (c in line) {
                if (c in openToClose.keys) {
                    stack.push(c)
                } else if (!stack.empty() && openToClose[stack.pop()] != c) {
                    corruptedLine = true
                    break
                }
            }
            if (!stack.empty() && !corruptedLine) {
                val points = stack.asReversed().fold(0L) { points, c -> points * 5 + score[c]!! }
                incompletePoints.add(points)
            }
        }
        return incompletePoints.sorted()[incompletePoints.size / 2]
    }

    val testInput = readInput("Day10_test")
    check(part1(testInput) == 26397)
    check(part2(testInput) == 288957L)

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
