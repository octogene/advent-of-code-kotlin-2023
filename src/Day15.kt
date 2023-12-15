fun main() {
    fun part1(input: List<String>): Int {
        return input.first().split(",").sumOf { hashAlgorithm(it) }
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day15")
    println("== Part I ==")
    part1(input).println()
    println("== Part II ==")
    part2(input).println()
}

fun hashAlgorithm(input: String): Int {
    return input.fold(0) { acc, char -> ((acc + char.code) * 17) % 256 }
}
