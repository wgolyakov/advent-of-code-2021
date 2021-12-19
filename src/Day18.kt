fun main() {
    class SnailNumber() {
        var xInt: Int? = null
        var yInt: Int? = null
        var xPair: SnailNumber? = null
        var yPair: SnailNumber? = null
        var parent: SnailNumber? = null

        constructor(strValue: String) : this() {
            var openCount = 0
            for ((i, c) in strValue.withIndex()) {
                if (c == '[')
                    openCount++
                else if (c == ']')
                    openCount--
                else if (c == ',' && openCount == 1) {
                    val xStr = strValue.substring(1, i)
                    val yStr = strValue.substring(i + 1, strValue.length - 1)
                    if (xStr[0] == '[') {
                        xPair = SnailNumber(xStr)
                        xPair!!.parent = this
                    } else
                        xInt = xStr.toInt()
                    if (yStr[0] == '[') {
                        yPair = SnailNumber(yStr)
                        yPair!!.parent = this
                    } else
                        yInt = yStr.toInt()
                    break
                }
            }
        }

        constructor(xPair: SnailNumber, yPair: SnailNumber) : this() {
            this.xPair = xPair
            this.yPair = yPair
            xPair.parent = this
            yPair.parent = this
        }

        constructor(xInt: Int, yInt: Int) : this() {
            this.xInt = xInt
            this.yInt = yInt
        }

        override fun toString(): String = "[${xPair?.toString() ?: xInt},${yPair?.toString() ?: yInt}]"

        operator fun plus(a: SnailNumber) = SnailNumber(this.copy(), a.copy()).reduce()

        fun magnitude(): Int = 3 * (xPair?.magnitude() ?: xInt ?: 0) + 2 * (yPair?.magnitude() ?: yInt ?: 0)

        fun copy(): SnailNumber {
            val clone = SnailNumber()
            clone.xInt = xInt
            clone.yInt = yInt
            clone.xPair = xPair?.copy()
            clone.yPair = yPair?.copy()
            clone.xPair?.parent = clone
            clone.yPair?.parent = clone
            return clone
        }

        fun root(): SnailNumber {
            var node = this
            while (node.parent != null)
                node = node.parent!!
            return node
        }

        private fun explode(level: Int): Boolean {
            if (xInt != null && yInt != null && level >= 5) {
                root().addExploded(this, arrayOf(null, false, false))
                if (this == parent!!.xPair) {
                    parent!!.xPair = null
                    parent!!.xInt = 0
                } else {
                    parent!!.yPair = null
                    parent!!.yInt = 0
                }
                return true
            }
            if (xPair != null) {
                val exploded = xPair!!.explode(level + 1)
                if (exploded) return true
            }
            if (yPair != null) {
                val exploded = yPair!!.explode(level + 1)
                if (exploded) return true
            }
            return false
        }

        private fun addExploded(explodedNumber: SnailNumber, params: Array<Any?>): Boolean {
            if (explodedNumber == this) {
                val lastRegularNumber = params[0] as SnailNumber?
                if (lastRegularNumber != null) {
                    if (params[1] as Boolean)
                        lastRegularNumber.xInt = lastRegularNumber.xInt!! + xInt!!
                    else
                        lastRegularNumber.yInt = lastRegularNumber.yInt!! + xInt!!
                }
                params[2] = true
                return false
            }
            if (xInt != null) {
                if (params[2] as Boolean) {
                    xInt = xInt!! + explodedNumber.yInt!!
                    return true
                }
                params[0] = this
                params[1] = true
            } else {
                val done = xPair!!.addExploded(explodedNumber, params)
                if (done) return true
            }
            if (yInt != null) {
                if (params[2] as Boolean) {
                    yInt = yInt!! + explodedNumber.yInt!!
                    return true
                }
                params[0] = this
                params[1] = false
            } else {
                val done = yPair!!.addExploded(explodedNumber, params)
                if (done) return true
            }
            return false
        }

        private fun split(): Boolean {
            val x = xInt
            if (x != null && x >= 10) {
                xPair = SnailNumber( x / 2, x / 2 + x % 2)
                xPair!!.parent = this
                xInt = null
                return true
            }
            if (xPair != null) {
                val splited = xPair!!.split()
                if (splited) return true
            }
            val y = yInt
            if (y != null && y >= 10) {
                yPair = SnailNumber( y / 2, y / 2 + y % 2)
                yPair!!.parent = this
                yInt = null
                return true
            }
            if (yPair != null) {
                val splited = yPair!!.split()
                if (splited) return true
            }
            return false
        }

        fun reduce(): SnailNumber {
            var exploded: Boolean
            var splited: Boolean
            do {
                do {
                    exploded = explode(1)
                } while (exploded)
                splited = split()
            } while (splited)
            return this
        }
    }

    fun parseInput(input: List<String>): List<SnailNumber> {
        val result = mutableListOf<SnailNumber>()
        for (line in input)
            result.add(SnailNumber(line))
        return result
    }

    fun part1(input: List<String>): Int {
        val numbers = parseInput(input)
        val sum = numbers.reduce { a, b -> a + b }
        return sum.magnitude()
    }

    fun part2(input: List<String>): Int {
        val numbers = parseInput(input)
        var maxMagnitude = -1
        for (n1 in numbers) {
            for (n2 in numbers) {
                if (n1 != n2) {
                    val magnitude = (n1 + n2).magnitude()
                    if (magnitude > maxMagnitude)
                        maxMagnitude = magnitude
                }
            }
        }
        return maxMagnitude
    }

    check(SnailNumber("[[[[[9,8],1],2],3],4]").reduce().toString() == "[[[[0,9],2],3],4]")
    check(SnailNumber("[7,[6,[5,[4,[3,2]]]]]").reduce().toString() == "[7,[6,[5,[7,0]]]]")
    check(SnailNumber("[[6,[5,[4,[3,2]]]],1]").reduce().toString() == "[[6,[5,[7,0]]],3]")
    check(SnailNumber("[[3,[2,[1,[7,3]]]],[6,[5,[4,[3,2]]]]]").reduce().toString() == "[[3,[2,[8,0]]],[9,[5,[7,0]]]]")
    check((SnailNumber("[[[[4,3],4],4],[7,[[8,4],9]]]") +
            SnailNumber("[1,1]")).toString() == "[[[[0,7],4],[[7,8],[6,0]]],[8,1]]")
    check((SnailNumber("[1,1]") +
            SnailNumber("[2,2]") +
            SnailNumber("[3,3]") +
            SnailNumber("[4,4]")).toString() == "[[[[1,1],[2,2]],[3,3]],[4,4]]")
    check((SnailNumber("[1,1]") +
            SnailNumber("[2,2]") +
            SnailNumber("[3,3]") +
            SnailNumber("[4,4]") +
            SnailNumber("[5,5]")).toString() == "[[[[3,0],[5,3]],[4,4]],[5,5]]")
    check((SnailNumber("[1,1]") +
            SnailNumber("[2,2]") +
            SnailNumber("[3,3]") +
            SnailNumber("[4,4]") +
            SnailNumber("[5,5]") +
            SnailNumber("[6,6]")).toString() == "[[[[5,0],[7,4]],[5,5]],[6,6]]")
    check((SnailNumber("[[[0,[4,5]],[0,0]],[[[4,5],[2,6]],[9,5]]]") +
            SnailNumber("[7,[[[3,7],[4,3]],[[6,3],[8,8]]]]") +
            SnailNumber("[[2,[[0,8],[3,4]]],[[[6,7],1],[7,[1,6]]]]") +
            SnailNumber("[[[[2,4],7],[6,[0,5]]],[[[6,8],[2,8]],[[2,1],[4,5]]]]") +
            SnailNumber("[7,[5,[[3,8],[1,4]]]]") +
            SnailNumber("[[2,[2,2]],[8,[8,1]]]") +
            SnailNumber("[2,9]") +
            SnailNumber("[1,[[[9,3],9],[[9,0],[0,7]]]]") +
            SnailNumber("[[[5,[7,4]],7],1]") +
            SnailNumber("[[[[4,2],2],6],[8,7]]")
            ).toString() == "[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]")

    check(SnailNumber("[[1,2],[[3,4],5]]").magnitude() == 143)
    check(SnailNumber("[[[[0,7],4],[[7,8],[6,0]]],[8,1]]").magnitude() == 1384)
    check(SnailNumber("[[[[1,1],[2,2]],[3,3]],[4,4]]").magnitude() == 445)
    check(SnailNumber("[[[[3,0],[5,3]],[4,4]],[5,5]]").magnitude() == 791)
    check(SnailNumber("[[[[5,0],[7,4]],[5,5]],[6,6]]").magnitude() == 1137)
    check(SnailNumber("[[[[8,7],[7,7]],[[8,6],[7,7]]],[[[0,7],[6,6]],[8,7]]]").magnitude() == 3488)

    val testInput = readInput("Day18_test")
    check(part1(testInput) == 4140)
    check(part2(testInput) == 3993)

    val input = readInput("Day18")
    println(part1(input))
    println(part2(input))
}
