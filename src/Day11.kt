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
        return runBlocking(Dispatchers.Default) {
            pairs.map {
                async {
                    findStepsTo(it.first, it.second, universe)
                }
            }.awaitAll().sumOf { it.size }
        }
    }

    fun part2(input: List<String>): Long {
        val (universe, planets, leaps) = parseAndExpandUniverseWithLeaps(input)
        val leapValue = 1_000_000
        val (rowsToLeap, columnsToLeap) = leaps
        val pairs = planets.asSequence().flatMap { thisIt ->
            planets.map { otherIt ->
                if (thisIt.number < otherIt.number) {
                    thisIt to otherIt
                } else {
                    otherIt to thisIt
                }
            }
        }.filter { it.first != it.second }.toSet()
        return runBlocking(Dispatchers.Default) {
            pairs.map {
                async {
                    val steps = findStepsTo(it.first, it.second, universe)
                    val columnLeaps = steps.filter { it.x in columnsToLeap }.distinctBy { it.x }
                    val rowLeaps = steps.filter { it.y in rowsToLeap }.distinctBy { it.y }
                    val stepsWithoutLeaps = steps.filter { it !in columnLeaps && it !in rowLeaps }
                    (stepsWithoutLeaps.size + columnLeaps.size * leapValue + rowLeaps.size * leapValue).toLong()
                }
            }.awaitAll().sum()
        }
    }

    val input = readInput("Day11")
    println("== Part 1 ==")
    part1(input).println()
    println("== Part 2 ==")
    part2(input).println()
}

fun parseAndExpandUniverseWithLeaps(input: List<String>): Triple<MutableList<String>, List<Planet>, Pair<List<Int>, List<Int>>> {
    val expanded = input.toMutableList()
    expanded.println()
    val planets = mutableListOf<Planet>()
    val rowsToExpand = expanded.mapIndexed { index, s -> s.all { it == '.' } to index }.filter { it.first }.map { it.second }
    val columnToExpand = (0 until input.first().length).map { row ->
        (0..expanded.lastIndex).map { column -> expanded[column][row] }.all { it == '.' }
    }.mapIndexed { index, b ->
        b to index
    }.filter { it.first }.map { it.second }

    rowsToExpand.println()
    columnToExpand.println()
    var count = 1
    expanded.forEachIndexed { y, s ->
        s.forEachIndexed { x, c ->
            if (c == '#') {
                planets.add(Planet(count++, Position(x, y)))
            }
        }
    }

    return Triple(expanded, planets.toList(), (rowsToExpand to columnToExpand))
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

fun findStepsTo(start: Planet, end: Planet, universe: List<String>): List<Position> {
    val visited = mutableMapOf(start.position to true)
    val queue: MutableList<Pair<Position, MutableList<Position>>> = mutableListOf(start.position to mutableListOf<Position>())

    while (queue.size > 0) {
        val (node, path) = queue.first()
        queue.removeAt(0)

        path.add(node)
        visited[node] = true

        if (node == end.position) {
            val result = path.slice(1 .. path.lastIndex)
            return result
        }

        val adjacentPlanets = node.getNeighbors(universe.first().lastIndex, universe.lastIndex)
        for (item in adjacentPlanets) {
            if (!visited.containsKey(item) || visited[item] == false) {
                queue.add(item to path.toMutableList())
                visited[item] = true
            }
        }
    }

    return emptyList()
}

fun Position.getNeighbors(maxX: Int, maxY: Int) = listOf(
    Position(x, y - 1),
    Position(x, y + 1),
    Position(x - 1, y),
    Position(x + 1, y),
).filter { it.x in 0 .. maxX && it.y in 0 .. maxY }
