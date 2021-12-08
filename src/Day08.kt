import org.apache.commons.collections4.iterators.PermutationIterator

fun main() {
    fun part1(input: List<String>): Int {
        var count = 0
        val uniqueSegments = setOf(2, 4, 3, 7)
        for (line in input) {
            val fourDigitOutputValue = line.split('|')[1].trim().split(' ')
            count += fourDigitOutputValue.count { it.length in uniqueSegments }
        }
        return count
    }

    fun permutations(str: String): List<String> {
        return PermutationIterator(str.toList()).asSequence().map { String(it.toCharArray()) }.toList()
    }

    fun strToBits(str: String, segments: String): Int {
        var result = 0
        for (c in str) {
            result = result or (1 shl segments.indexOf(c))
        }
        return result
    }

    fun detectDigits(tenUniqueSignalPatterns: List<String>): Pair<Map<Int, Int>, String> {
        for (s in permutations("abcdefg")) {
            val bitsToDigit = mutableMapOf<Int, Int>()
            bitsToDigit[strToBits("" + s[0] + s[1] + s[2]        + s[4] + s[5] + s[6], s)] = 0
            bitsToDigit[strToBits("" +               s[2]               + s[5]       , s)] = 1
            bitsToDigit[strToBits("" + s[0]        + s[2] + s[3] + s[4]        + s[6], s)] = 2
            bitsToDigit[strToBits("" + s[0]        + s[2] + s[3]        + s[5] + s[6], s)] = 3
            bitsToDigit[strToBits("" +        s[1] + s[2] + s[3]        + s[5]       , s)] = 4
            bitsToDigit[strToBits("" + s[0] + s[1]        + s[3]        + s[5] + s[6], s)] = 5
            bitsToDigit[strToBits("" + s[0] + s[1]        + s[3] + s[4] + s[5] + s[6], s)] = 6
            bitsToDigit[strToBits("" + s[0]        + s[2]               + s[5]       , s)] = 7
            bitsToDigit[strToBits("" + s[0] + s[1] + s[2] + s[3] + s[4] + s[5] + s[6], s)] = 8
            bitsToDigit[strToBits("" + s[0] + s[1] + s[2] + s[3]        + s[5] + s[6], s)] = 9
            val allDigits = mutableSetOf<Int>()
            for (d in tenUniqueSignalPatterns) {
                val n = bitsToDigit[strToBits(d, s)] ?: break
                allDigits.add(n)
            }
            if (allDigits.size == 10) return Pair(bitsToDigit, s)
        }
        error("Wrong ten unique signal patterns: $tenUniqueSignalPatterns")
    }

    fun part2(input: List<String>): Int {
        var result = 0
        for (line in input) {
            val (tenUniqueSignalPatterns, fourDigitOutputValue) = line.split('|').map { it.trim().split(' ') }
            val (bitsToDigit, segments) = detectDigits(tenUniqueSignalPatterns)
            var number = ""
            for (digit in fourDigitOutputValue)
                number += bitsToDigit[strToBits(digit, segments)].toString()
            result += number.toInt()
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
