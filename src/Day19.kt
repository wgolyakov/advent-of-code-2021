import javafx.geometry.Point3D
import javafx.scene.transform.Affine
import javafx.scene.transform.Rotate
import javafx.scene.transform.Transform
import javafx.scene.transform.Translate
import kotlin.math.abs
import kotlin.math.roundToInt

fun main() {
    data class Beacon(var x: Int, var y: Int, var z: Int)
    class Scanner(var x: Int, var y: Int, var z: Int) {
        val beacons = mutableSetOf<Beacon>()
        val overlappedScanners = mutableMapOf<Scanner, Transform>()

        fun getTransformTo(scanner: Scanner, path: Set<Scanner>): Transform? {
            var transform = overlappedScanners[scanner]
            if (transform != null)
                return transform
            for ((s, tr) in overlappedScanners) {
                if (path.contains(s)) continue
                transform = s.getTransformTo(scanner, path + setOf(this))
                if (transform != null) {
                    val sumTransform = Affine(transform)
                    sumTransform.append(tr)
                    return sumTransform
                }
            }
            return null
        }
    }

    val p0 = Point3D(0.0, 0.0, 0.0)
    val allRotations = listOf<Transform>(
        Affine(Rotate(0.0, Rotate.Z_AXIS)).apply { appendRotation(0.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(90.0, Rotate.Z_AXIS)).apply { appendRotation(0.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(180.0, Rotate.Z_AXIS)).apply { appendRotation(0.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(270.0, Rotate.Z_AXIS)).apply { appendRotation(0.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(0.0, Rotate.Y_AXIS)).apply { appendRotation(90.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(90.0, Rotate.Y_AXIS)).apply { appendRotation(90.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(180.0, Rotate.Y_AXIS)).apply { appendRotation(90.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(270.0, Rotate.Y_AXIS)).apply { appendRotation(90.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(0.0, Rotate.Z_AXIS)).apply { appendRotation(180.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(90.0, Rotate.Z_AXIS)).apply { appendRotation(180.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(180.0, Rotate.Z_AXIS)).apply { appendRotation(180.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(270.0, Rotate.Z_AXIS)).apply { appendRotation(180.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(0.0, Rotate.Y_AXIS)).apply { appendRotation(270.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(90.0, Rotate.Y_AXIS)).apply { appendRotation(270.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(180.0, Rotate.Y_AXIS)).apply { appendRotation(270.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(270.0, Rotate.Y_AXIS)).apply { appendRotation(270.0, p0, Rotate.X_AXIS) },
        Affine(Rotate(0.0, Rotate.X_AXIS)).apply { appendRotation(90.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(90.0, Rotate.X_AXIS)).apply { appendRotation(90.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(180.0, Rotate.X_AXIS)).apply { appendRotation(90.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(270.0, Rotate.X_AXIS)).apply { appendRotation(90.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(0.0, Rotate.X_AXIS)).apply { appendRotation(270.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(90.0, Rotate.X_AXIS)).apply { appendRotation(270.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(180.0, Rotate.X_AXIS)).apply { appendRotation(270.0, p0, Rotate.Y_AXIS) },
        Affine(Rotate(270.0, Rotate.X_AXIS)).apply { appendRotation(270.0, p0, Rotate.Y_AXIS) },
    )

    fun parseInput(input: List<String>): List<Scanner> {
        val scanners = mutableListOf<Scanner>()
        var scanner: Scanner? = null
        for (line in input) {
            if (line.startsWith("---")) {
                scanner = Scanner(0, 0, 0)
                scanners.add(scanner)
                continue
            } else if (line.isNotEmpty()) {
                val (x, y, z) = line.split(',').map { it.toInt() }
                val beacon = Beacon(x, y, z)
                scanner?.beacons?.add(beacon)
            }
        }
        return scanners
    }

    fun transformedScanner(scanner: Scanner, transform: Transform): Scanner {
        val result = Scanner(0, 0 , 0)
        for (beacon in scanner.beacons) {
            val point = transform.transform(beacon.x.toDouble(), beacon.y.toDouble(), beacon.z.toDouble())
            val transformedBeacon = Beacon(point.x.roundToInt(), point.y.roundToInt(), point.z.roundToInt())
            result.beacons.add(transformedBeacon)
        }
        return result
    }

    fun getDelta(scanner: Scanner, beacon0: Beacon): Set<Triple<Int, Int, Int>> {
        val delta = mutableSetOf<Triple<Int, Int, Int>>()
        for (beacon in scanner.beacons) {
            val dx = beacon.x - beacon0.x
            val dy = beacon.y - beacon0.y
            val dz = beacon.z - beacon0.z
            delta.add(Triple(dx, dy ,dz))
        }
        return delta
    }

    fun findOverlap(scanner1: Scanner, scanner2: Scanner): Translate? {
        for (beacon1 in scanner1.beacons) {
            val delta1 = getDelta(scanner1, beacon1)
            for (beacon2 in scanner2.beacons) {
                val delta2 = getDelta(scanner2, beacon2)
                if (delta1.intersect(delta2).size >= 12) {
                    val dx = beacon1.x - beacon2.x
                    val dy = beacon1.y - beacon2.y
                    val dz = beacon1.z - beacon2.z
                    return Translate(dx.toDouble() ,dy.toDouble(), dz.toDouble())
                }
            }
        }
        return null
    }

    fun fillOverlaps(scanners: List<Scanner>) {
        for (scanner1 in scanners) {
            for (scanner2 in scanners) {
                if (scanner2 == scanner1 || scanner2.overlappedScanners.contains(scanner1)) continue
                for (rotation in allRotations) {
                    val s2 = transformedScanner(scanner2, rotation)
                    val translation = findOverlap(scanner1, s2)
                    if (translation != null) {
                        val transform = Affine(translation)
                        transform.append(rotation)
                        scanner2.overlappedScanners[scanner1] = transform
                        scanner1.overlappedScanners[scanner2] = transform.createInverse()
                        break
                    }
                }
            }
        }
    }

    fun part1(input: List<String>): Int {
        val scanners = parseInput(input)
        fillOverlaps(scanners)
        val firstScanner = scanners[0]
        val beaconsMap = mutableSetOf<Beacon>()
        beaconsMap.addAll(firstScanner.beacons)
        for (scanner in scanners) {
            if (scanner == firstScanner) continue
            val transform = scanner.getTransformTo(firstScanner, emptySet())!!
            val s = transformedScanner(scanner, transform)
            beaconsMap.addAll(s.beacons)
        }
        return beaconsMap.size
    }

    fun part2(input: List<String>): Int {
        val scanners = parseInput(input)
        fillOverlaps(scanners)
        val firstScanner = scanners[0]
        for (scanner in scanners) {
            if (scanner == firstScanner) continue
            val transform = scanner.getTransformTo(firstScanner, emptySet())!!
            val point = transform.transform(0.0, 0.0, 0.0)
            scanner.x = point.x.roundToInt()
            scanner.y = point.y.roundToInt()
            scanner.z = point.z.roundToInt()
        }
        var maxDistance = 0
        for (scanner1 in scanners) {
            for (scanner2 in scanners) {
                if (scanner1 == scanner2) continue
                val distance = abs(scanner1.x - scanner2.x) +
                        abs(scanner1.y - scanner2.y) +
                        abs(scanner1.z - scanner2.z)
                if (distance > maxDistance)
                    maxDistance = distance
            }
        }
        return maxDistance
    }

    val testInput = readInput("Day19_test")
    check(part1(testInput) == 79)
    check(part2(testInput) == 3621)

    val input = readInput("Day19")
    println(part1(input))
    println(part2(input))
}
