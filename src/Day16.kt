fun main() {
    class Packet {
        var version: Int = 0
        var typeId: Int = 0
        var literalValue: Long = 0
        val packets = mutableListOf<Packet>()
        var size: Int = 0

        fun versionSum(): Int {
            return version + packets.sumOf { it.versionSum() }
        }
    }

    fun decodePacket(bits: String): Packet {
        val packet = Packet()
        var i = 0
        packet.version = bits.substring(i, i + 3).toInt(2)
        i += 3
        packet.typeId = bits.substring(i, i + 3).toInt(2)
        i += 3
        if (packet.typeId == 4) {
            // literal value
            val literalBits = StringBuilder()
            var prefixBit = "1"
            while (prefixBit == "1") {
                prefixBit = bits.substring(i, i + 1)
                i++
                literalBits.append(bits.substring(i, i + 4))
                i += 4
            }
            packet.literalValue = literalBits.toString().toLong(2)
            packet.size = i
        } else {
            // operator
            val lengthTypeId = bits.substring(i, i + 1)
            i++
            if (lengthTypeId == "0") {
                // total length in bits
                val totalLengthInBits = bits.substring(i, i + 15).toInt(2)
                i += 15
                val subBits = bits.substring(i, i + totalLengthInBits)
                i += totalLengthInBits
                packet.size = i
                var j = 0
                while (j < subBits.length) {
                    val subPacket = decodePacket(subBits.substring(j))
                    packet.packets.add(subPacket)
                    j += subPacket.size
                }
            } else {
                // number of sub-packets
                val numberOfSubPackets = bits.substring(i, i + 11).toInt(2)
                i += 11
                for (n in 0 until numberOfSubPackets) {
                    val subPacket = decodePacket(bits.substring(i))
                    i += subPacket.size
                    packet.packets.add(subPacket)
                }
                packet.size = i
            }
            packet.literalValue = when (packet.typeId) {
                // sum +
                0 -> packet.packets.sumOf { it.literalValue }
                // product *
                1 -> packet.packets.fold(1L) { mul, p -> mul * p.literalValue }
                // minimum
                2 -> packet.packets.minOf { it.literalValue }
                // maximum
                3 -> packet.packets.maxOf { it.literalValue }
                // greater >
                5 -> if (packet.packets[0].literalValue > packet.packets[1].literalValue) 1 else 0
                // less <
                6 -> if (packet.packets[0].literalValue < packet.packets[1].literalValue) 1 else 0
                // equal ==
                7 -> if (packet.packets[0].literalValue == packet.packets[1].literalValue) 1 else 0
                else -> 0
            }
        }
        return packet
    }

    fun hexToBits(hex: String): String {
        val bits = StringBuilder()
        for (c in hex)
            bits.append(c.digitToInt(16).toString(2).padStart(4, '0'))
        return bits.toString()
    }

    fun part1(input: String): Int {
        val bits = hexToBits(input)
        val packet = decodePacket(bits)
        return packet.versionSum()
    }

    fun part2(input: String): Long {
        val bits = hexToBits(input)
        val packet = decodePacket(bits)
        return packet.literalValue
    }

    val testInput1 = readInput("Day16_test1")
    check(part1(testInput1[0]) == 16)
    check(part1(testInput1[1]) == 12)
    check(part1(testInput1[2]) == 23)
    check(part1(testInput1[3]) == 31)

    val testInput2 = readInput("Day16_test2")
    check(part2(testInput2[0]) == 3L)
    check(part2(testInput2[1]) == 54L)
    check(part2(testInput2[2]) == 7L)
    check(part2(testInput2[3]) == 9L)
    check(part2(testInput2[4]) == 1L)
    check(part2(testInput2[5]) == 0L)
    check(part2(testInput2[6]) == 0L)
    check(part2(testInput2[7]) == 1L)

    val input = readInput("Day16")
    println(part1(input[0]))
    println(part2(input[0]))
}
