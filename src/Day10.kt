fun main() {
    fun part1(input: List<String>): Int {
        val (initialPosition, map) = parsePipeInput(input)
        val connections = buildMapFromPosition(map, initialPosition)
        return (connections.size / 2) + 1
    }

    fun part2(input: List<String>): Int {
        // Using https://en.wikipedia.org/wiki/Point_in_polygon
        val (initialPosition, map) = parsePipeInput(input)
        val connections = buildMapFromPosition(map, initialPosition)
        val connectionsWithInitialPosition = connections + initialPosition
        val areaMaxY = connectionsWithInitialPosition.maxOf { it.position.y }
        val areaMinY = connectionsWithInitialPosition.minOf { it.position.y }
        val areaMaxX = connectionsWithInitialPosition.maxOf { it.position.x }
        val areaMinX = connectionsWithInitialPosition.minOf { it.position.x }

        val connectionsByPosition = connectionsWithInitialPosition.associateBy { it.position }
        check(connectionsByPosition.count { it.value.type == '.' } == 0)

        val boundedMap = map.slice(areaMinY..areaMaxY).map {
            it.slice(areaMinX..areaMaxX).filter { !connectionsByPosition.containsKey(it.position) }
        }.flatten()

        val inside =  boundedMap.map { tile ->
            val intersectionCount = (tile.position.x + 1 ..areaMaxX).count { connectionsByPosition.containsKey(Position(it, tile.position.y)) }
            tile to intersectionCount
        }.filter { it.second % 2 != 0 }

        inside.toList().forEach { println(it) }

        return inside.count()

    }

    val input = readInput("Day10test")
    println("== Part 1 ==")
    part1(input).println()
    println("== Part 2 ==")
    part2(input).println()
}

fun parsePipeInput(input: List<String>): Pair<Tile, List<List<Tile>>> {
    lateinit var initialPosition: Tile
    val map = input.mapIndexed { y, line ->
        line.mapIndexed { x, c ->
            val tile = Tile(c, Position(x, y))
            if (tile.type == 'S') initialPosition = tile
            tile
        }
    }
    return initialPosition to map
}

fun buildMapFromPosition(
    map: List<List<Tile>>,
    initialPosition: Tile,
): MutableList<Tile> {
    val xBound = map.first().lastIndex
    val yBound = map.lastIndex

    val first = Direction.entries.mapNotNull {
        val (x, y) = initialPosition.position.to(it)
        if (x in 0..xBound && y in 0..yBound) {
            val pipe = map[y][x]
            pipe
        } else null
    }.first { pipe ->
        pipe.connectedTo()?.first?.let {
            pipe.position.to(it) == initialPosition.position
        } == true
    }

    val connections = mutableListOf(first)
    var current = connections.last()

    while (true) {
        val direction = current.connectedTo()?.second
        val (x, y) = current.position.to(direction!!)
        val pipe = map[y][x]
        if (pipe !in connections) {
            current = pipe
        } else {
            val direction = current.connectedTo()?.first
            val (x, y) = current.position.to(direction!!)
            val pipe = map[y][x]
            current = pipe
        }
        if (current.type == 'S') break
        connections.add(current)
    }
    return connections
}


/**
 *     | is a vertical pipe connecting north and south.
 *     - is a horizontal pipe connecting east and west.
 *     L is a 90-degree bend connecting north and east.
 *     J is a 90-degree bend connecting north and west.
 *     7 is a 90-degree bend connecting south and west.
 *     F is a 90-degree bend connecting south and east.
 *     . is ground; there is no pipe in this tile.
 *     S is the starting position of the animal; there is a pipe on this tile, but your sketch doesn't show what shape the pipe has.
 */

fun Tile.connectedTo(): Pair<Direction, Direction>? = when (type) {
    '|' -> Direction.NORTH to Direction.SOUTH
    '-' -> Direction.EAST to Direction.WEST
    'L' -> Direction.NORTH to Direction.EAST
    'J' -> Direction.NORTH to Direction.WEST
    '7' -> Direction.SOUTH to Direction.WEST
    'F' -> Direction.SOUTH to Direction.EAST
    else -> null
}


fun Position.to(direction: Direction): Position = when (direction) {
    Direction.SOUTH -> copy(y = y + 1)
    Direction.NORTH -> copy(y = y - 1)
    Direction.EAST -> copy(x = x + 1)
    Direction.WEST -> copy(x = x - 1)
}

enum class Direction {
    SOUTH,
    NORTH,
    EAST,
    WEST
}

data class Tile(val type: Char, val position: Position)
data class Position(val x: Int, val y: Int)
