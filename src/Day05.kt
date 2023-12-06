import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Long {
        return getDestinationForSeeds(input)
    }

    fun part2(input: List<String>): Long {
        return getDestinationForSeedRanges(input)
    }

    val input = readInput("Day05")
    part1(input).println()
    part2(input).println()

}

fun getDestinationForSeedRanges(input: List<String>): Long {
    val rawSeeds = input[0].split(": ", " ")
    val seedRanges = rawSeeds.subList(1, rawSeeds.size).map {
        it.trim().toLong()
    }.chunked(2).map { it.first() until it.first() + it[1] }

    val maps = parseMaps(input)

    var result: Long = Long.MAX_VALUE
    runBlocking(Dispatchers.Default) {
        val results = seedRanges.map { seedRange ->
            async {
                var min: Long = Long.MAX_VALUE
                for (seed in seedRange) {
                    var current: Long = seed
                    for (map in maps) {
                        val selected = map.firstOrNull { current >= it.first.min && current <= it.first.max }
                        if (selected != null) {
                            val step = current - selected.first.min
                            current = selected.second.min + step
                        }
                        min = minOf(current, min)
                    }
                }
                min
            }
        }.awaitAll()
        // TODO: Off by one
        println("Results are $results")
        result = results.min()
    }
    return result
}


data class Range(val min: Long, val max: Long, val step: Long)

fun getDestinationForSeeds(input: List<String>): Long {

    val rawSeeds = input[0].split(": ", " ")
    val seeds = rawSeeds.subList(1, rawSeeds.size).map { it.trim().toLong() }
    val maps = parseMaps(input)

    return seeds.minOf { seed -> findDestination(seed, maps) }
}

private fun parseMaps(
    input: List<String>
): List<List<Pair<Range, Range>>> {
    var tmp = mutableListOf<Pair<Range, Range>>()
    val maps = mutableListOf<List<Pair<Range, Range>>>()
    for (i in 2..input.lastIndex) {
        if (input[i].isEmpty()) {
            continue
        }

        if (input[i].endsWith(":")) {
            if (tmp.isNotEmpty()) maps.add(tmp.toList())
            tmp = mutableListOf()
            continue
        }

        val (destination, source, step) = input[i].split(" ").map { it.trim().toLong() }
        val destinationRange: Range = Range(destination, destination + step, step)
        val sourceRange: Range = Range(source, source + step, step)
        tmp.add(sourceRange to destinationRange)
    }
    if (tmp.isNotEmpty()) maps.add(tmp.toList())
    return maps.toList()
}

private fun findDestination(
    seed: Long,
    maps: List<List<Pair<Range, Range>>>,
): Long {
    var current = seed
    maps.forEach { map ->
        val range = map.firstOrNull { current >= it.first.min && current <= it.first.max }
        if (range != null) {
            val step = (current - range.first.min)
            current = range.second.min + step
        }
    }
    return current
}
