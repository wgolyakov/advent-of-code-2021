import kotlin.math.abs

fun main() {
    data class Burrow(val hall: MutableList<Char>, val rooms: MutableList<MutableList<Char>>) {
        fun copy() = Burrow(hall.toMutableList(), rooms.map { it.toMutableList() }.toMutableList())

        fun map(): String {
            val s = StringBuilder("#############\n")
            s.append("#${String(hall.toCharArray())}#\n")
            for (i in rooms[0].indices) {
                val line = StringBuilder()
                if (i == 0)
                    line.append("###")
                else
                    line.append("  #")
                for (room in rooms)
                    line.append("${room[i]}#")
                if (i == 0)
                    line.append("##\n")
                else
                    line.append("\n")
                s.append(line)
            }
            s.append("  #########")
            return s.toString()
        }

        override fun toString(): String {
            val s = StringBuilder("[${String(hall.toCharArray())}]  ")
            for (room in rooms) {
                s.append('[')
                for (c in room) s.append(c)
                s.append("]  ")
            }
            return s.toString()
        }
    }

    fun parseInput(input: List<String>): Burrow {
        val hall = input[1].substring(1, input[1].length - 1).toCharArray().toMutableList()
        val room1 = mutableListOf(input[2][3], input[3][3])
        val room2 = mutableListOf(input[2][5], input[3][5])
        val room3 = mutableListOf(input[2][7], input[3][7])
        val room4 = mutableListOf(input[2][9], input[3][9])
        return Burrow(hall, mutableListOf(room1, room2, room3, room4))
    }

    fun freeHallWay(h1: Int, h2: Int, burrow: Burrow): Boolean {
        val way = if (h1 < h2) h1..h2 else h2..h1
        for (h in way)
            if (h != h1 && burrow.hall[h] != '.')
                return false
        return true
    }

    val moveCost = mapOf('A' to 1, 'B' to 10, 'C' to 100, 'D' to 1000)
    val goodHallCells = listOf(0, 1, 3, 5, 7, 9, 10)
    val roomToHall = mapOf(0 to 2, 1 to 4, 2 to 6, 3 to 8)
    val destinationRoom = charArrayOf('A', 'B', 'C', 'D')
    var minEnergy = 0

    fun isOrganized(burrow: Burrow): Boolean {
        for ((r, room) in burrow.rooms.withIndex())
            for (a in room)
                if (a != destinationRoom[r])
                    return false
        return true
    }

    fun isRoomOrganizedAfter(room: MutableList<Char>, roomIndex: Int, startIndex: Int): Boolean {
        for (i in startIndex until room.size)
            if (room[i] != destinationRoom[roomIndex])
                return false
        return true
    }

    // 01234567890
    //   0 0 0 0
    //   1 1 1 1
    fun nextMoves(burrow: Burrow): Map<Burrow, Int> {
        val result = mutableMapOf<Burrow, Int>()
        // Move from rooms to hall
        for ((r, room) in burrow.rooms.withIndex()) {
            val h1 = roomToHall[r]!!
            for ((i, amphipod) in room.withIndex()) {
                if (amphipod != '.') {
                    if (!isRoomOrganizedAfter(room, r, i)) {
                        for (h2 in goodHallCells) {
                            if (freeHallWay(h1, h2, burrow)) {
                                val copy = burrow.copy()
                                copy.rooms[r][i] = '.'
                                copy.hall[h2] = amphipod
                                result[copy] = moveCost[amphipod]!! * (abs(h1 - h2) + 1 + i)
                            }
                        }
                    }
                    break
                }
            }
        }
        // Move from hall to rooms
        for (h1 in goodHallCells) {
            val a1 = burrow.hall[h1]
            if (a1 == '.') continue
            val r = destinationRoom.indexOf(a1)
            val room = burrow.rooms[r]
            for ((i, a2) in room.withIndex()) {
                if (a2 != '.') break
                if (isRoomOrganizedAfter(room, r, i + 1)) {
                    val h2 = roomToHall[r]!!
                    if (freeHallWay(h1, h2, burrow)) {
                        val copy = burrow.copy()
                        copy.hall[h1] = '.'
                        copy.rooms[r][i] = a1
                        result[copy] = moveCost[a1]!! * (abs(h1 - h2) + 1 + i)
                    }
                    break
                }
            }
        }
        return result
    }

    fun scanMoves(burrow: Burrow, energy: Int) {
        if (isOrganized(burrow)) {
            if (energy < minEnergy)
                minEnergy = energy
        } else {
            for ((move, cost) in nextMoves(burrow))
                scanMoves(move, energy + cost)
        }
    }

    // #D#C#B#A#
    // #D#B#A#C#
    fun unfold(burrow: Burrow): Burrow {
        val rooms = burrow.rooms
        val room1 = mutableListOf(rooms[0][0], 'D', 'D', rooms[0][1])
        val room2 = mutableListOf(rooms[1][0], 'C', 'B', rooms[1][1])
        val room3 = mutableListOf(rooms[2][0], 'B', 'A', rooms[2][1])
        val room4 = mutableListOf(rooms[3][0], 'A', 'C', rooms[3][1])
        return Burrow(burrow.hall, mutableListOf(room1, room2, room3, room4))
    }

    fun part1(input: List<String>): Int {
        val burrow = parseInput(input)
        minEnergy = Int.MAX_VALUE
        scanMoves(burrow, 0)
        return minEnergy
    }

    fun part2(input: List<String>): Int {
        var burrow = parseInput(input)
        burrow = unfold(burrow)
        minEnergy = Int.MAX_VALUE
        scanMoves(burrow, 0)
        return minEnergy
    }

    val testInput = readInput("Day23_test")
    check(part1(testInput) == 12521)
    check(part2(testInput) == 44169)

    val input = readInput("Day23")
    println(part1(input))
    println(part2(input))
}
