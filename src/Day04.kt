class Board {
    private data class Cell(val value: Int, var marked: Boolean = false)
    private val cells: Array<Array<Cell>> = Array(5) { Array(5) { Cell(0) } }
    private var win: Boolean = false

    fun setRow(y: Int, row: List<Int>) {
        cells[y] = row.map { Cell(it) }.toTypedArray()
    }

    fun markNumber(number: Int) {
        for (row in cells) {
            for (cell in row) {
                if (cell.value == number) cell.marked = true
            }
        }
        updateWinState()
    }

    private fun updateWinState() {
        if (win) return
        for (x in 0..4) {
            var columnWin = true
            for (y in 0..4) {
                if (!cells[y][x].marked) {
                    columnWin = false
                    break
                }
            }
            if (columnWin) {
                win = true
                return
            }
        }
        for (y in 0..4) {
            var rowWin = true
            for (x in 0..4) {
                if (!cells[y][x].marked) {
                    rowWin = false
                    break
                }
            }
            if (rowWin) {
                win = true
                return
            }
        }
    }

    fun isWin(): Boolean = win

    fun getUnmarkedSum(): Int {
        return cells.sumOf { row -> row.sumOf { if (!it.marked) it.value else 0 } }
    }
}

fun main() {
    fun parseInput(input: List<String>): Pair<List<Int>, List<Board>> {
        val randomNumbers = input[0].split(',').map { it.toInt() }
        val boards = mutableListOf<Board>()
        var y = 0
        var board = Board()
        for (line in input) {
            if (line.isEmpty() || line.contains(',')) continue
            board.setRow(y, line.trim().split("\\s+".toRegex()).map { it.toInt() })
            y++
            if (y >= 5) {
                boards.add(board)
                y = 0
                board = Board()
            }
        }
        return Pair(randomNumbers, boards)
    }

    fun part1(input: List<String>): Int {
        val (randomNumbers, boards) = parseInput(input)
        for (number in randomNumbers) {
            for (board in boards) {
                board.markNumber(number)
                if (board.isWin()) return board.getUnmarkedSum() * number
            }
        }
        return -1
    }

    fun part2(input: List<String>): Int {
        val (randomNumbers, boards) = parseInput(input)
        var winCount = 0
        for (number in randomNumbers) {
            for (board in boards) {
                val winBeforeMark = board.isWin()
                board.markNumber(number)
                if (!winBeforeMark && board.isWin()) {
                    winCount++
                    if (winCount == boards.size) return board.getUnmarkedSum() * number
                }
            }
        }
        return -1
    }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 4512)
    check(part2(testInput) == 1924)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
