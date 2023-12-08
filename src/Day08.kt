import java.math.BigInteger


fun main() {
    fun part1(input: List<String>): Int {
        val (instructions, map) = parseMap(input)
        return countStepsToDestination(instructions, map, "AAA", "..Z".toRegex())
    }

    fun part2(input: List<String>): Long {
        val (instructions, map) = parseMap(input)
        return countGhostStepsToDestination(instructions, map)
    }

    val input = readInput("Day08")
    part1(input).println()
    part2(input).println()
}

fun parseMap(input: List<String>): Pair<List<Int>, Map<String, List<String>>> {
    val instructions = input[0].map { if (it == 'L') 0 else 1 }
    val map = input.slice(2..input.lastIndex).associate {
        val (location, paths) = it.split(" = ")
        location.trim() to paths.trim().split(",").map { it.filter { it.isLetter() || it.isDigit() } }
    }
    return instructions to map
}

private fun countStepsToDestination(
    instructions: List<Int>,
    map: Map<String, List<String>>,
    start: String = "AAA",
    destination: Regex = "ZZZ".toRegex(),
): Int {
    var currentKey = start
    var steps = 0

    while (!destination.matches(currentKey)) {
        val instructionIndex = steps % instructions.size
        val instruction = instructions[instructionIndex]
        currentKey = map.getValue(currentKey)[instruction]
        steps += 1
    }
    return steps
}

private fun countGhostStepsToDestination(
    instructions: List<Int>,
    map: Map<String, List<String>>,
    destination: Regex = "..Z".toRegex(),
): Long {
    val startInstructions = map.filter { it.key.endsWith('A') }
    val currentKeys = startInstructions.keys.toMutableList()
    val stepsForEach = currentKeys.map { countStepsToDestination(instructions, map, it, destination) }
    return stepsForEach.map { it.toBigInteger() }.reduce { acc, bigInteger -> lcm(acc, bigInteger) }.toLong()
}

fun lcm(number1: BigInteger, number2: BigInteger?): BigInteger {
    val gcd = number1.gcd(number2)
    val absProduct = number1.multiply(number2).abs()
    return absProduct.divide(gcd)
}
