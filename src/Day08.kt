fun main() {
    fun deduceDigits(tenUniqueSignalPatterns: List<Set<Char>>): Map<Set<Char>, Int> {
        val digits = Array<Set<Char>>(10) { emptySet() }
        val digits069 = mutableListOf<Set<Char>>()
        val digits235 = mutableListOf<Set<Char>>()
        for (word in tenUniqueSignalPatterns) {
            when (word.size) {
                2 -> digits[1] = word
                3 -> digits[7] = word
                4 -> digits[4] = word
                5 -> digits235.add(word)
                6 -> digits069.add(word)
                7 -> digits[8] = word
            }
        }
        digits[2] = digits235.find { (it - digits[4] - digits[7]).size == 2 }!!
        digits[3] = digits235.find { (it - digits[2]).size == 1 }!!
        digits[5] = (digits235 - setOf(digits[2], digits[3])).first()
        digits[9] = digits069.find { (it - digits[4] - digits[7]).size == 1 }!!
        digits[0] = (digits069 - setOf(digits[9])).find { (it - digits[5]).size == 2 }!!
        digits[6] = (digits069 - setOf(digits[0], digits[9])).first()
        return digits.withIndex().associate { it.value to it.index }
    }

    fun part1(input: List<String>): Int {
        var count = 0
        val uniqueSegments = setOf(2, 4, 3, 7)
        for (line in input) {
            val fourDigitOutputValue = line.split('|')[1].trim().split(' ')
            count += fourDigitOutputValue.count { it.length in uniqueSegments }
        }
        return count
    }

    fun part2(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val (tenUniqueSignalPatterns, fourDigitOutputValue) = line.split('|')
                .map { it.trim().split(' ').map { word -> word.toCharArray().toSet() } }
            val digitsMap = deduceDigits(tenUniqueSignalPatterns)
            result += fourDigitOutputValue.map { digitsMap[it] }.joinToString("").toInt()
        }
        return result
    }

    val testInput = readInput("Day08_test")
    check(part1(testInput) == 26)
    check(part2(testInput) == 61229)

    val input = readInput("Day08")
    println(part1(input))
    println(part2(input))
}
