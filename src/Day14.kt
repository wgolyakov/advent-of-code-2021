import java.lang.Long.sum

fun main() {
    fun parseInput(input: List<String>): Pair<String, Map<String, Char>> {
        val template = input[0]
        val rules = mutableMapOf<String, Char>()
        for (line in input.subList(2, input.size)) {
            val (pair, element) = line.split(" -> ")
            rules[pair] = element[0]
        }
        return Pair(template, rules)
    }

    fun part1(input: List<String>): Int {
        val (template, rules) = parseInput(input)
        var polymer = template
        for (step in 1..10) {
            var nextPolymer = ""
            polymer.windowed(2).forEach { nextPolymer += "" + it[0] + rules[it] }
            polymer = nextPolymer + polymer.last()
        }
        val count = mutableMapOf<Char, Int>()
        for (c in polymer)
            count.merge(c, 1, Integer::sum)
        val quantity = count.values.sorted()
        return quantity.last() - quantity.first()
    }

    fun part2(input: List<String>): Long {
        val (template, rules) = parseInput(input)
        var pairCount = mutableMapOf<String, Long>()
        template.windowed(2).forEach { pairCount.merge(it, 1, ::sum) }
        for (step in 1..40) {
            val nextPairCount = mutableMapOf<String, Long>()
            for ((pair, count) in pairCount) {
                val c = rules[pair]
                val pair1 = "" + pair[0] + c
                val pair2 = "" + c + pair[1]
                nextPairCount.merge(pair1, count, ::sum)
                nextPairCount.merge(pair2, count, ::sum)
            }
            pairCount = nextPairCount
        }
        val letterCount = mutableMapOf<Char, Long>()
        for ((pair, count) in pairCount) {
            val c1 = pair[0]
            val c2 = pair[1]
            letterCount.merge(c1, count, ::sum)
            letterCount.merge(c2, count, ::sum)
        }
        for ((letter, count) in letterCount)
            letterCount[letter] = count / 2
        letterCount.merge(template.first(), 1, ::sum)
        letterCount.merge(template.last(), 1, ::sum)
        val quantity = letterCount.values.sorted()
        return quantity.last() - quantity.first()
    }

    val testInput = readInput("Day14_test")
    check(part1(testInput) == 1588)
    check(part2(testInput) == 2188189693529)

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
