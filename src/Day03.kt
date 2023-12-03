fun main() {
    fun part1(input: List<String>): Int {
        return getValidParts(input)
    }

    fun part2(input: List<String>): Int {
        return computeGearRatio(input)
    }

    val input = readInput("Day03")
    part1(input).println()
    part2(input).println()
}

private fun getAllPartsByPosition(
    input: List<String>,
    rowLength: Int,
): Pair<MutableMap<Pair<Int, Int>, Part>, MutableList<Symbol>> {
    val possibleParts = mutableListOf<Part>()
    val currentPositions = mutableListOf<Pair<Int, Int>>()
    var currentDigits = ""
    val symbols = mutableListOf<Symbol>()
    for (row in 0..input.lastIndex) {
        for (col in 0 until rowLength) {
            val char = input[row][col]
            if (char.isDigit()) {
                currentDigits += char
                currentPositions.add(row to col)
            } else {
                if (char.isNotDot()) {
                    symbols.add(Symbol(char, row to col))
                }
                if (currentDigits.isNotEmpty()) {
                    possibleParts.add(Part(currentDigits, currentPositions.toList()))
                    currentPositions.clear()
                    currentDigits = ""
                }
            }
        }
        if (currentDigits.isNotEmpty()) {
            possibleParts.add(Part(currentDigits, currentPositions.toList()))
            currentPositions.clear()
            currentDigits = ""
        }
    }

    val partsByPosition = buildMap {
        possibleParts.forEach { part ->
            part.positions.forEach { position ->
                this[position] = part
            }
        }
    }.toMutableMap()
    return partsByPosition to symbols
}

fun getValidParts(input: List<String>): Int {

    val rowLength = input[0].length
    val validParts = mutableListOf<Int>()
    val (partsByPosition, symbols) = getAllPartsByPosition(input, rowLength)

    symbols.forEach {symbol ->
        val positions = getAdjacentPositions(symbol.position.first, symbol.position.second)
        positions.forEach { position ->
            if (partsByPosition.containsKey(position)) {
                val part = partsByPosition.getValue(position)
                validParts.add(part.value.toInt())
                part.positions.forEach { position ->
                    partsByPosition.remove(position)
                }
            }
        }
    }

    return validParts.sum()
}

fun computeGearRatio(input: List<String>): Int {

    var gearRatio = 0
    val rowLength = input[0].length
    val (partsByPosition, symbols) = getAllPartsByPosition(input, rowLength)

    symbols.forEach {symbol ->
        val positions = getAdjacentPositions(symbol.position.first, symbol.position.second)
        val gearAdjacentParts = mutableListOf<Int>()
        positions.forEach { position ->
            if (partsByPosition.containsKey(position)) {
                val part = partsByPosition.getValue(position)
                part.positions.forEach { position ->
                    partsByPosition.remove(position)
                }
                if (input[symbol.position.first][symbol.position.second] == '*') {
                    gearAdjacentParts.add(part.value.toInt())
                }
            }
        }
        if (gearAdjacentParts.size == 2) {
            gearRatio += gearAdjacentParts[0] * gearAdjacentParts[1]
        }
    }

    return gearRatio
}

private fun getAdjacentPositions(row: Int, col: Int): List<Pair<Int, Int>> {
    return listOf(
        row - 1 to col,
        row + 1 to col,
        row to col - 1,
        row to col + 1,
        row - 1 to col - 1,
        row + 1 to col + 1,
        row + 1 to col - 1,
        row - 1 to col + 1
    )
}

fun Char.isNotDot() = this != '.'
data class Part(val value: String, val positions: List<Pair<Int, Int>>)
data class Symbol(val value: Char, val position: Pair<Int, Int>)
