fun main() {
    fun part1(input: List<String>): Int {
        return input.first().split(",").sumOf { hashAlgorithm(it) }
    }

    fun part2(input: List<String>): Int {
        val regex = "^(\\w+)(=|-)(\\d+)?".toRegex()
        val boxes = List<LinkedHashMap<String, Int>>(256) { LinkedHashMap() }
        input.first().split(",").mapNotNull {
            regex.find(it)?.groupValues?.drop(1)
        }.forEach {
            val (label, instruction, value) = it
            if (instruction == "=") {
                val boxNumber = hashAlgorithm(label)
                boxes[boxNumber][label] = value.toInt()
            } else {
                val boxNumber = hashAlgorithm(label)
                boxes[boxNumber].remove(label)
            }
        }
        return boxes.mapIndexed { index, linkedHashMap ->
            val boxFactor = index + 1
            linkedHashMap.keys.mapIndexed { slot, s ->
                boxFactor * (slot + 1) * linkedHashMap.getValue(s)
            }.sum()
        }.sum()
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
