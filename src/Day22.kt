fun main() {
    fun split(range: IntRange, delimiter: IntRange): List<IntRange> {
        if (range.first == range.last)
            return listOf(range)
        var delim = delimiter
        if (delim.first < range.first)
            delim = range.first..delim.last
        if (range.last < delim.last)
            delim = delim.first..range.last
        return if (delim.first == range.first && delim.last == range.last)
            listOf(range)
        else if (delim.first == range.first)
            listOf(delim, (delim.last + 1)..range.last)
        else if (delim.last == range.last)
            listOf(range.first..(delim.first - 1), delim)
        else
            listOf(range.first..(delim.first - 1), delim, (delim.last + 1)..range.last)
    }

    data class Cuboid(val x: IntRange, val y: IntRange, val z: IntRange, val on: Boolean = true) {
        fun contains(cuboid: Cuboid): Boolean {
            return cuboid.x.first in x && cuboid.x.last in x &&
                    cuboid.y.first in y && cuboid.y.last in y &&
                    cuboid.z.first in z && cuboid.z.last in z
        }

        fun intersects(cuboid: Cuboid): Boolean {
            return (cuboid.x.first in x || cuboid.x.last in x || x.first in cuboid.x || x.last in cuboid.x) &&
                    (cuboid.y.first in y || cuboid.y.last in y || y.first in cuboid.y || y.last in cuboid.y) &&
                    (cuboid.z.first in z || cuboid.z.last in z || z.first in cuboid.z || z.last in cuboid.z)
        }

        fun split(cuboid: Cuboid): List<Cuboid> {
            val result = mutableListOf<Cuboid>()
            val xRanges = split(x, cuboid.x)
            val yRanges = split(y, cuboid.y)
            val zRanges = split(z, cuboid.z)
            for (xr in xRanges)
                for (yr in yRanges)
                    for (zr in zRanges)
                        result.add(Cuboid(xr, yr, zr))
            return result
        }

        fun minus(cuboid: Cuboid): List<Cuboid> {
            if (cuboid.contains(this)) return emptyList()
            if (!cuboid.intersects(this)) return listOf(this)
            val list = split(cuboid)
            return list.filter { !cuboid.contains(it) }
        }
    }

    fun parseInput(input: List<String>): List<Cuboid> {
        val cuboids = mutableListOf<Cuboid>()
        for (line in input) {
            val on = line.startsWith("on")
            val strOn = if (on) "on " else "off "
            val ranges = line.substringAfter(strOn).split(',')
                .map { it.substring(2).split("..") }
                .map { it[0].toInt()..it[1].toInt() }
            val cuboid = Cuboid(ranges[0], ranges[1], ranges[2], on)
            cuboids.add(cuboid)
        }
        return cuboids
    }

    fun union(cuboids: List<Cuboid>): List<Cuboid> {
        val set = cuboids.toMutableSet()
        var done: Boolean
        do {
            done = false
            for (c1 in set.toList()) {
                if (!set.contains(c1)) continue
                for (c2 in set.toList()) {
                    if (c1 == c2 || !set.contains(c2)) continue
                    if (c1.x == c2.x && c1.y == c2.y) {
                        if (c1.z.last + 1 == c2.z.first) {
                            set.add(Cuboid(c1.x, c1.y, c1.z.first..c2.z.last))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        } else if (c2.z.last + 1 == c1.z.first) {
                            set.add(Cuboid(c1.x, c1.y, c2.z.first..c1.z.last))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        }
                    } else if (c1.x == c2.x && c1.z == c2.z) {
                        if (c1.y.last + 1 == c2.y.first) {
                            set.add(Cuboid(c1.x, c1.y.first..c2.y.last, c1.z))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        } else if (c2.y.last + 1 == c1.y.first) {
                            set.add(Cuboid(c1.x, c2.y.first..c1.y.last, c1.z))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        }
                    } else if (c1.y == c2.y && c1.z == c2.z) {
                        if (c1.x.last + 1 == c2.x.first) {
                            set.add(Cuboid(c1.x.first..c2.x.last, c1.y, c1.z))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        } else if (c2.x.last + 1 == c1.x.first) {
                            set.add(Cuboid(c2.x.first..c1.x.last, c1.y, c1.z))
                            set.remove(c1)
                            set.remove(c2)
                            done = true
                            break
                        }
                    }
                }
            }
        } while (done)
        return set.toList()
    }

    fun plus(cuboids: List<Cuboid>, cuboid: Cuboid): List<Cuboid> {
        val result = mutableListOf<Cuboid>()
        // A + B = A - B + B
        for (c in cuboids)
            result.addAll(c.minus(cuboid))
        result.add(cuboid)
        return result
    }

    fun minus(cuboids: List<Cuboid>, cuboid: Cuboid): List<Cuboid> {
        val result = mutableListOf<Cuboid>()
        for (c in cuboids)
            result.addAll(c.minus(cuboid))
        return result
    }

    fun countCubes(cuboids: List<Cuboid>): Long {
        return cuboids.sumOf {
            (it.x.last - it.x.first + 1L) * (it.y.last - it.y.first + 1L) * (it.z.last - it.z.first + 1L) }
    }

    fun part1(input: List<String>): Int {
        val cuboids = parseInput(input)
        val reactor = Array(101) { Array(101) { BooleanArray(101) } }
        for (cuboid in cuboids)
            if (cuboid.x.first in -50..50 && cuboid.x.last in -50..50 &&
                cuboid.y.first in -50..50 && cuboid.y.last in -50..50 &&
                cuboid.z.first in -50..50 && cuboid.z.last in -50..50)
                for (x in cuboid.x)
                    for (y in cuboid.y)
                        for (z in cuboid.z)
                            reactor[x + 50][y + 50][z + 50] = cuboid.on
        return reactor.sumOf { x -> x.sumOf { y -> y.count { it } } }
    }

    fun part2(input: List<String>): Long {
        val cuboids = parseInput(input)
        var onList = listOf<Cuboid>()
        for (cuboid in cuboids) {
            onList = if (cuboid.on)
                plus(onList, cuboid)
            else
                minus(onList, cuboid)
            //onList = union(onList)
        }
        return countCubes(onList)
    }

    val testInput = readInput("Day22_test")
    check(part1(testInput) == 590784)

    val testInput2 = readInput("Day22_test2")
    check(part1(testInput2) == 474140)
    check(part2(testInput2) == 2758514936282235)

    val input = readInput("Day22")
    println(part1(input))
    println(part2(input))
}
