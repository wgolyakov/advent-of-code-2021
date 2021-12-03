fun main() {
    fun part1(input: List<String>): Int {
        var horizontalPosition = 0
        var depth = 0
        for (command in input) {
            val commandParts = command.split(' ')
            if (commandParts.size != 2) print("Wrong command: $command")
            val direction = commandParts[0]
            val x = commandParts[1].toInt()
            when (direction) {
                "forward" -> horizontalPosition += x
                "down" -> depth += x
                "up" -> depth -= x
                else -> print("Wrong command: $command")
            }
        }
        return horizontalPosition * depth
    }

    fun part2(input: List<String>): Int {
        var horizontalPosition = 0
        var depth = 0
        var aim = 0
        for (command in input) {
            val commandParts = command.split(' ')
            if (commandParts.size != 2) print("Wrong command: $command")
            val direction = commandParts[0]
            val x = commandParts[1].toInt()
            when (direction) {
                "forward" -> {
                    horizontalPosition += x
                    depth += aim * x
                }
                "down" -> aim += x
                "up" -> aim -= x
                else -> print("Wrong command: $command")
            }
        }
        return horizontalPosition * depth
    }

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 150)
    check(part2(testInput) == 900)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
