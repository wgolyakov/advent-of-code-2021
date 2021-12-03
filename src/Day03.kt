fun main() {
    fun part1(input: List<String>): Int {
        if (input.isEmpty()) return 0
        var gammaRate = ""
        var epsilonRate = ""
        for (i in 0 until input[0].length) {
            val oneCount = input.count { it[i] == '1' }
            val zeroCount = input.size - oneCount
            if (oneCount >= zeroCount) {
                gammaRate += '1'
                epsilonRate += '0'
            } else {
                gammaRate += '0'
                epsilonRate += '1'
            }
        }
        return gammaRate.toInt(2) * epsilonRate.toInt(2)
    }

    fun part2(input: List<String>): Int {
        var oxyList = input
        var i = 0
        do {
            val oneCount = oxyList.count { it[i] == '1' }
            val zeroCount = oxyList.size - oneCount
            val bit = if (oneCount >= zeroCount) '1' else '0'
            oxyList = oxyList.filter { it[i] == bit }
            i++
        } while (oxyList.size > 1)
        val oxygenGeneratorRating = oxyList[0]

        var co2List = input
        i = 0
        do {
            val oneCount = co2List.count { it[i] == '1' }
            val zeroCount = co2List.size - oneCount
            val bit = if (oneCount >= zeroCount) '0' else '1'
            co2List = co2List.filter { it[i] == bit }
            i++
        } while (co2List.size > 1)
        val co2ScrubberRating = co2List[0]
        return oxygenGeneratorRating.toInt(2) * co2ScrubberRating.toInt(2)
    }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 198)
    check(part2(testInput) == 230)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
