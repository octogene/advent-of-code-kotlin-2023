fun main() {
    fun part1(input: List<String>): Int {
        val histories = parseInput(input)
        val steps = mutableListOf<List<Int>>()
        histories.forEach { ints ->
            val lastValues = mutableListOf(ints.last())
            var intermediate = ints
            while (!intermediate.all { it == 0 }) {
                intermediate = intermediate.windowed(2).map { it[1] - it[0] }
                lastValues.add(intermediate.last())
            }
            steps.add(lastValues)
        }
        return steps.sumOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        val histories = parseInput(input)
        val steps = mutableListOf<List<Int>>()
        histories.forEach { ints ->
            val firstValues = mutableListOf(ints.first())
            var intermediate = ints
            while (!intermediate.all { it == 0 }) {
                // TODO: Avoid processing full row
                intermediate = intermediate.windowed(2).map { it[1] - it[0] }
                firstValues.add(intermediate.first())
            }
            steps.add(firstValues.reversed())
        }
        return steps.sumOf { it.reduce { acc, i -> i - acc } }
    }

    val input = readInput("Day09")
    part1(input).println()
    part2(input).println()
}

fun parseInput(input: List<String>): List<List<Int>> {
    return input.map { it.split(" ").map { it.toInt() } }
}
