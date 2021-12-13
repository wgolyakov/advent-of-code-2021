fun main() {
    data class Dot(val x: Int, val y: Int)

    data class Fold(val type: String, val value: Int) {
        fun transform(dot: Dot): Dot {
            var x = dot.x
            var y = dot.y
            when (type) {
                "x" -> if (x > value) x = value - (x - value)
                "y" -> if (y > value) y = value - (y - value)
                else -> error("Wrong fold: $type=$value")
            }
            return Dot(x, y)
        }

        fun transform(dots: Set<Dot>): Set<Dot> {
            return dots.map { transform(it) }.toSet()
        }
    }

    fun parseInput(input: List<String>): Pair<Set<Dot>, List<Fold>> {
        val dots = mutableSetOf<Dot>()
        val folds = mutableListOf<Fold>()
        for (line in input) {
            if (line.isEmpty()) continue
            val str = line.substringAfter("fold along ")
            if (str == line) {
                val (x, y) = str.split(',').map { it.toInt() }
                dots.add(Dot(x, y))
            } else {
                val (foldType, foldValue) = str.split('=')
                folds.add(Fold(foldType, foldValue.toInt()))
            }
        }
        return Pair(dots, folds)
    }

    fun part1(input: List<String>): Int {
        val (dots, folds) = parseInput(input)
        return folds[0].transform(dots).size
    }

    fun part2(input: List<String>): String {
        var (dots, folds) = parseInput(input)
        for (fold in folds)
            dots = fold.transform(dots)
        val maxX = dots.maxOf { it.x }
        val maxY = dots.maxOf { it.y }
        val paper = Array(maxY + 1) { StringBuilder(".".repeat(maxX + 1)) }
        for (dot in dots)
            paper[dot.y][dot.x] = '#'
        return paper.joinToString("\n")
    }

    val testInput = readInput("Day13_test")
    check(part1(testInput) == 17)
    check(part2(testInput) ==
            "#####\n" +
            "#...#\n" +
            "#...#\n" +
            "#...#\n" +
            "#####")

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
