fun main() {
    fun part1(input: List<String>): Int {
        val histories = parseInput(input)
        val steps = mutableListOf<List<Int>>()
        histories.forEach { ints ->
            val lastValues = mutableListOf<Int>()
            var intermediate = ints
            lastValues.add(ints.last())
            while (!intermediate.all { it == 0 }) {
                val current = intermediate.foldIndexed(emptyList<Int>()) { index, acc, i ->
                    if (index + 1 <= intermediate.lastIndex) {
                        acc + (intermediate[index + 1] - i)
                    } else acc
                }
                lastValues.add(current.last())
                intermediate = current
            }
            steps.add(lastValues)
        }
        return steps.sumOf { it.sum() }
    }

    fun part2(input: List<String>): Int {
        val histories = parseInput(input)
        val steps = mutableListOf<List<Int>>()
        histories.forEach { ints ->
            val firstValues = mutableListOf<Int>()
            var intermediate = ints
            firstValues.add(ints.first())
            while (!intermediate.all { it == 0 }) {
                // TODO: Avoid processing full row
                val current = intermediate.foldIndexed(emptyList<Int>()) { index, acc, i ->
                    if (index + 1 <= intermediate.lastIndex) {
                        acc + (intermediate[index + 1] - i)
                    } else acc
                }
                firstValues.add(current.first())
                intermediate = current
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
