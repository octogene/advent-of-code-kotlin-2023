
fun main() {
    fun part1(input: List<String>): Int {
        val records = parseRecords(input)
        return records.sumOf { it.generateArrangements().size }
    }

    fun part2(input: List<String>): Int {
        val records = parseRecords(input).map { record ->
            val newEntry = (0 until 5).joinToString("?") { record.entry }
            val newGroups = (0 until 5).flatMap { record.groups }
            Record(newEntry, newGroups)
        }
        return input.size
    }

    val input = readInput("Day12")
    part1(input).println()
    part2(input).println()
}

fun parseRecords(input: List<String>): List<Record> {
    val records = input.map {
        val (record, condition) = it.split(" ")
        Record(record, condition.split(",").map { it.toInt() })
    }
    return records
}

fun Record.isValid(candidate: String): Boolean {
    val nonFunctional = candidate.split("\\.+".toRegex()).filter {
        it.all { it == '#' }
    }.filter { it.isNotEmpty() }
    if (nonFunctional.size != groups.size) return false
    for (i in 0..groups.lastIndex) {
        if (groups[i] != nonFunctional[i].length) return false
    }
    return true
}

fun Record.generateArrangements(): List<String> {
    val unknownIndexes = entry.mapIndexedNotNull { index, char -> if (char == '?') index else null }
    val chars = listOf('#', '.')
    return generateAllArrangements(chars, "", chars.size, unknownIndexes.size, this, unknownIndexes)
}

private fun Record.candidate(it: String, unknownIndexes: List<Int>): String {
    val candidates = it.mapIndexed { index, c -> unknownIndexes[index] to c }
    check(candidates.size == unknownIndexes.size)
    val candidate = entry.toMutableList()
    candidates.forEach { candidate[it.first] = it.second }
    return candidate.joinToString("")
}

fun generateAllArrangements(
    set: List<Char>,
    prefix: String,
    n: Int, k: Int,
    record: Record,
    unknownIndexes: List<Int>
): List<String> {

    val permutations = mutableListOf<String>()

    fun generate(
        set: List<Char>,
        prefix: String,
        n: Int, k: Int
    ) {
        if (k == 0) {
            if (record.isValid(record.candidate(prefix, unknownIndexes))) permutations.add(prefix)
            return
        }

        for (i in 0 until n) {
            val newPrefix = prefix + set[i]

            generate(set, newPrefix, n, k - 1)
        }
    }

    generate(set, prefix, n, k)
    return permutations
}

data class Record(val entry: String, val groups: List<Int>)
