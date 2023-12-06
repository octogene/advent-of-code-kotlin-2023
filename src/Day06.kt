fun main() {
    fun part1(input: List<String>): Long {
        val (times, distances) = parse(input)

        val successPerRace = mutableListOf<Long>()
        for (race in 0 .. times.lastIndex) {
            val count = getRaceSuccessCount(times[race], distances[race])
            successPerRace.add(count)
        }
        check(successPerRace.size == times.size)

        return successPerRace.reduce { acc, i -> acc * i }
    }

    fun part2(input: List<String>): Long {
        val (time, distance) = parse2(input)
        return getRaceSuccessCount(time, distance)
    }

    val input = readInput("Day06")
    part1(input).println()
    part2(input).println()
}

fun parse(input: List<String>): Pair<List<Long>, List<Long>> {
    val times =
        input[0].split(":").drop(1).flatMap { it.split(" ").filter { it.isNotEmpty() }.map { it.trim().toLong() } }
    val distances =
        input[1].split(":").drop(1).flatMap { it.split(" ").filter { it.isNotEmpty() }.map { it.trim().toLong() } }
    return times to distances
}

fun parse2(input: List<String>): Pair<Long, Long> {
    val time =
        input[0].split(":")[1].replace(" ", "").toLong()
    val distance =
        input[1].split(":")[1].replace(" ", "").toLong()
    return time to distance
}

fun getRaceSuccessCount(
    time: Long,
    recordDistance: Long,
): Long {
    var count = 0L
    for (speed in 0..time) {
        val distance = (time - speed) * speed
        if (distance > recordDistance) count++
    }
    return count
}
