fun main() {
    fun part1(input: List<String>): Int {
        return input.mapNotNull { parseValidGame(it, 12, 13, 14) }.sum()
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { getPowerOfGame(it) }
    }

    val input = readInput("Day02")
    part1(input).println()
    part2(input).println()
}


fun parseValidGame(line: String, maxRed: Int, maxGreen: Int, maxBlue: Int): Int? {
    val (id, cubesByColor) = getCubesByColorForGame(line)

    val green = cubesByColor.getValue("green")
    val blue = cubesByColor.getValue("blue")
    val red = cubesByColor.getValue("red")

    return if (
        green.any { it.count > maxGreen } ||
        red.any { it.count > maxRed } ||
        blue.any { it.count > maxBlue }
    ) null else id.toInt()
}

fun getPowerOfGame(line: String): Int {
    val (_, cubesByColor) = getCubesByColorForGame(line)

    val green = cubesByColor.getValue("green").maxOf { it.count }
    val blue = cubesByColor.getValue("blue").maxOf { it.count }
    val red = cubesByColor.getValue("red").maxOf { it.count }

    return green * blue * red
}

private fun getCubesByColorForGame(game: String): Pair<String, Map<String, List<RevealedCubes>>> {
    val (rawId, rawRounds) = game.split(":")
    val id = rawId.removePrefix("Game ")
    val rounds = rawRounds.split(";")
    val cubesByColor = rounds.map { round ->
        round
            .split(",")
            .map { cube -> cube.trim().split(" ") }
            .map { RevealedCubes(it[1], it[0].toInt()) }
    }.flatten().groupBy { it.color }
    return Pair(id, cubesByColor)
}

data class RevealedCubes(val color: String, val count: Int)
