import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

fun main() {
    fun part1(input: List<String>): Int {
        val (universe, planets) = parseAndExpandUniverse(input)
        val pairs = planets.asSequence().flatMap { thisIt ->
            planets.map { otherIt ->
                if (thisIt.number < otherIt.number) {
                    thisIt to otherIt
                } else {
                    otherIt to thisIt
                }
            }
        }.filter { it.first != it.second }.toSet()
        runBlocking(Dispatchers.Default) {
            pairs.map {
                async {
                    findStepsTo(it.first, it.second, universe)
                }
            }.awaitAll().sum().println()
        }
        return input.size
    }

    fun part2(input: List<String>): Int {
        return input.size
    }

    val input = readInput("Day11")
    println("== Part 1 ==")
    part1(input).println()
    println("== Part 2 ==")
    part2(input).println()
}

fun parseAndExpandUniverse(input: List<String>): Pair<MutableList<String>, List<Planet>> {
    val expanded = input.toMutableList()
    expanded.println()
    val planets = mutableListOf<Planet>()
    val rowsToExpand = expanded.mapIndexed { index, s -> s.all { it == '.' } to index }.filter { it.first }
    val columnToExpand = (0 until input.first().length).map { row ->
        (0..expanded.lastIndex).map { column -> expanded[column][row] }.all { it == '.' }
    }.mapIndexed { index, b ->
        b to index
    }.filter { it.first }
    
    columnToExpand.forEachIndexed { index, row ->
        for (i in 0 .. expanded.lastIndex) {
            val sb = StringBuilder(expanded[i])
            sb.insert(row.second + index, '.')
            expanded[i] = sb.toString()
        }
    }
    val newRowLength = expanded.first().length


    rowsToExpand.forEachIndexed { index, pair ->
        expanded.add(pair.second + index, ".".repeat(newRowLength))
    }

    var count = 1
    expanded.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') {
                planets.add(Planet(count++, Position(x, y)))
            }
        }
    }

    expanded.forEachIndexed { index, s ->
        if (index == 0) {
            print("    ")
            (0 .. s.lastIndex).forEach {
                print(" $it")
            }
            print("\n")
        }
        println("$index".padStart(2, ' ') + " - ${s.map { it }.joinToString(" ")}")
    }

    return expanded to planets.toList()
}

data class Planet(val number: Int, val position: Position)

fun findStepsTo(start: Planet, end: Planet, universe: List<String>): Int {
    val visited = mutableMapOf(start.position to true)
    val queue: MutableList<Pair<Position, Int>> = mutableListOf(start.position to 0)

    while (queue.size > 0) {
        var (node, path) = queue.first()
        queue.removeAt(0)

        path += 1
        visited[node] = true

        if (node == end.position) {
            val result = path - 1
            return result
        }

        val adjacentPlanets = node.getNeighbors(universe.first().lastIndex, universe.lastIndex)
        for (item in adjacentPlanets) {
            if (!visited.containsKey(item) || visited[item] == false) {
                queue.add(item to path)
                visited[item] = true
            }
        }
    }

    return -1
}

fun Position.getNeighbors(maxX: Int, maxY: Int) = listOf(
    Position(x, y - 1),
    Position(x, y + 1),
    Position(x - 1, y),
    Position(x + 1, y),
).filter { it.x in 0 .. maxX && it.y in 0 .. maxY }
