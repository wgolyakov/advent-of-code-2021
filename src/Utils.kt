import java.io.File

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src", "$name.txt").readLines()

/**
 * Reads int numbers from the given input txt file.
 */
fun readInts(name: String) = readInput(name).map { it.toInt() }

/**
 * Reads int numbers comma separated from the given input txt file.
 */
fun readIntsCommaSeparated(name: String) = readInput(name)[0].split(',').map { it.toInt() }
