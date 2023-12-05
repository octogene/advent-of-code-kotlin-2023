import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Int {
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day05test")
    parseInput(input)
//    part1(input).println()
//    part2(input).println()
}

fun parseInput(input: List<String>) {

    val maps = mutableListOf<List<Pair<LongRange, LongRange>>>()
    val rawSeeds = input[0].split(": ", " ")
    val seeds = rawSeeds.subList(1, rawSeeds.size).map { it.trim().toLong() }
    println(seeds)

    var tmp = mutableListOf<Pair<LongRange, LongRange>>()

    for (i in 2 .. input.lastIndex) {
        if (input[i].isEmpty()) {
//            println("Empty line")
            continue
        }

        if (input[i].endsWith(":")) {
//            println(tmp)
//            println("New map ${input[i]}")
            if (tmp.isNotEmpty()) maps.add(tmp.toList())
            tmp = mutableListOf()
            continue
        }

        val (destination, source, step) = input[i].split(" ").map { it.trim().toLong() }
        val destinationRange: LongRange = destination until destination + step
        val sourceRange: LongRange = source until source + step
        tmp.add(sourceRange to destinationRange)
    }
    if (tmp.isNotEmpty()) maps.add(tmp.toList())

    runBlocking {
        seeds.map { seed ->
            async(Dispatchers.Default) {
                findDestination(seed, maps)
            }
        }.awaitAll().min().println()
    }
}

private fun findDestination(
    seed: Long,
    maps: MutableList<List<Pair<LongRange, LongRange>>>,
): Long {
    var current = seed
    println("Seed -> $seed")
    maps.forEach { map ->
        val range = map.firstOrNull { current in it.first }
        if (range != null) {
            val step = (current - range.first.min())
            current = range.second.min() + step
        }
    }
    return current
}
