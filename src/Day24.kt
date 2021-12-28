import java.util.Collections.synchronizedList
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

fun main() {
    class Command(val var1: Int, val var2: Int?, val num: Int?, val inpIndex: Int, val instruction: (Int, Int) -> Int) {
        fun execute(vars: IntArray, inp: IntArray) {
            val num2 = if (var2 != null) vars[var2] else num ?: inp[inpIndex]
            vars[var1] = instruction(vars[var1], num2)
        }
    }

    fun parseInput(input: List<String>): List<Command> {
        val commands = mutableListOf<Command>()
        val vars = mutableMapOf("w" to 0, "x" to 1, "y" to 2, "z" to 3)
        var inpIndex = -1
        for (line in input) {
            val parts = line.split(" ")
            val instruction = parts[0]
            val aStr = parts[1]
            val bStr = if (parts.size > 2) parts[2] else null
            val aIndex = vars[aStr]!!
            val bIndex = if (bStr == null) null else vars[bStr]
            val bNum = if (bIndex == null) bStr?.toInt() else null
            val function = when (instruction) {
                "inp" -> { inpIndex++; { _: Int, b: Int -> b } }
                "add" -> { a: Int, b: Int -> a + b }
                "mul" -> { a: Int, b: Int -> a * b }
                "div" -> { a: Int, b: Int -> a / b }
                "mod" -> { a: Int, b: Int -> a % b }
                "eql" -> { a: Int, b: Int -> if (a == b) 1 else 0 }
                else -> error("Unknown instruction: $line")
            }
            commands.add(Command(aIndex, bIndex, bNum, inpIndex, function))
        }
        return commands
    }

    fun monad(version: IntArray, commands: List<Command>): Int {
        val vars = IntArray(4)
        for (command in commands)
            command.execute(vars, version)
        return vars[3]
    }

    fun monadPart(version: IntArray, commands: List<Command>, z: Int): Int {
        val vars = intArrayOf(0, 0, 0, z)
        for (command in commands)
            command.execute(vars, version)
        return vars[3]
    }

    fun iterateVersion(placeRange: IntRange, z0: Int, commands: List<Command>, action: (IntArray, Int) -> Unit) {
        val version = IntArray(14)
        val commandParts = Array(14) { listOf<Command>() }
        for (i in 0 until 14)
            commandParts[i] = commands.subList((commands.size / 14) * i, (commands.size / 14) * (i + 1))
        for (i in placeRange)
            version[i] = 1
        val results = IntArray(14)
        val placeRangeRev = placeRange.reversed()
        var n = placeRange.first
        var done = false
        while (!done) {
            for (i in n..placeRange.last) {
                val z = if (i > placeRange.first) results[i - 1] else z0
                results[i] = monadPart(version, commandParts[i], z)
            }
            action(version, results[placeRange.last])
            for (j in placeRangeRev) {
                n = j
                version[j]++
                if (version[j] <= 9)
                    break
                version[j] = 1
                if (j == placeRange.first)
                    done = true
            }
        }
    }

    fun executeScheduler(): ThreadPoolExecutor {
        val nThreads = Runtime.getRuntime().availableProcessors()
        val taskQueue = ArrayBlockingQueue<Runnable>(nThreads * 2)
        val scheduler = ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, taskQueue)
        scheduler.prestartAllCoreThreads()
        return scheduler
    }

    fun findAllValidVersions(input: List<String>): List<Long> {
        val commands = parseInput(input)
        val goodVer = mutableListOf<Pair<IntArray, Int>>()
        iterateVersion(0..7, 0, commands) { ver07, z ->
            if (z in 0..1_000_000)
                goodVer.add(Pair(ver07.copyOf(), z))
        }
        val scheduler = executeScheduler()
        val result = synchronizedList(mutableListOf<Long>())
        for ((n, ver) in goodVer.withIndex()) {
            scheduler.queue.put {
                val (ver07, z7) = ver
                iterateVersion(8..13, z7, commands) { ver813, z ->
                    if (z == 0)
                        result.add((ver07.filter { it != 0 }.joinToString("") +
                                ver813.filter { it != 0 }.joinToString("")).toLong())
                }
            }
            if (n % (goodVer.size / 100 + 1) == 0)
                println("${n * 100 / goodVer.size}%")
        }
        scheduler.shutdown()
        return result
    }

    fun part1(input: List<String>): Long {
        return findAllValidVersions(input).maxOrNull() ?: -1
    }

    fun part2(input: List<String>): Long {
        return findAllValidVersions(input).minOrNull() ?: -1
    }

    val input = readInput("Day24")
    println(part1(input))
    println(part2(input))
}
