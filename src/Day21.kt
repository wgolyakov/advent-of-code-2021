import java.lang.Long.max
import java.lang.Long.sum

fun main() {
    fun parseInput(input: List<String>): Pair<Int, Int> {
        val p1 = input[0].substringAfter("Player 1 starting position: ").toInt()
        val p2 = input[1].substringAfter("Player 2 starting position: ").toInt()
        return Pair(p1, p2)
    }

    data class DeterministicDie(var value: Int = 100, var rollCount: Int = 0) {
        fun roll(): Int {
            rollCount++
            value = if (value < 100) value + 1 else 1
            return value
        }

        fun roll3(): Int {
            var step = roll()
            step += roll()
            step += roll()
            return step
        }
    }

    class DiracDie {
        val stepToUniverses: Map<Int, Int>
        init {
            stepToUniverses = mutableMapOf()
            for (n1 in 1..3)
                for (n2 in 1..3)
                    for (n3 in 1..3)
                        stepToUniverses.merge(n1 + n2 + n3, 1, Integer::sum)
        }

        fun roll3() = stepToUniverses
    }

    data class Player(var position: Int, var score: Int = 0) {
        fun move(die: DeterministicDie) {
            val step = die.roll3()
            move(step)
        }

        fun move(step: Int) {
            position = (position + step - 1) % 10 + 1
            score += position
        }
    }

    data class GameState(val player1: Player, val player2: Player) {
        fun copy() = GameState(player1.copy(), player2.copy())
    }

    class DiracGame(position1: Int, position2: Int) {
        var stateToUniverses = mutableMapOf(GameState(Player(position1, 0), Player(position2, 0)) to 1L)
        var win1Universes = 0L
        var win2Universes = 0L

        fun move(die: DiracDie, playerNum: Int) {
            val stateToUniversesNext = mutableMapOf<GameState, Long>()
            for ((state, stateUniverses) in stateToUniverses) {
                for ((step, stepUniverses) in die.roll3()) {
                    val st = state.copy()
                    if (playerNum == 1)
                        st.player1.move(step)
                    else
                        st.player2.move(step)
                    stateToUniversesNext.merge(st, stateUniverses * stepUniverses, ::sum)
                }
            }
            stateToUniverses = stateToUniversesNext
            checkWin(playerNum)
        }

        fun checkWin(playerNum: Int) {
            stateToUniverses.entries.removeAll { (state, universes) ->
                if (playerNum == 1 && state.player1.score >= 21) {
                    win1Universes += universes
                    true
                } else if (playerNum == 2 && state.player2.score >= 21) {
                    win2Universes += universes
                    true
                } else {
                    false
                }
            }
        }

        fun over(): Boolean {
            return stateToUniverses.isEmpty()
        }
    }

    fun part1(input: List<String>): Int {
        val (p1, p2) = parseInput(input)
        val player1 = Player(p1)
        val player2 = Player(p2)
        val die = DeterministicDie()
        while (true) {
            player1.move(die)
            if (player1.score >= 1000) return player2.score * die.rollCount
            player2.move(die)
            if (player2.score >= 1000) return player1.score * die.rollCount
        }
    }

    fun part2(input: List<String>): Long {
        val (p1, p2) = parseInput(input)
        val game = DiracGame(p1, p2)
        val die = DiracDie()
        while (!game.over()) {
            game.move(die, 1)
            game.move(die, 2)
        }
        return max(game.win1Universes, game.win2Universes)
    }

    val testInput = readInput("Day21_test")
    check(part1(testInput) == 739785)
    check(part2(testInput) == 444356092776315)

    val input = readInput("Day21")
    println(part1(input))
    println(part2(input))
}
