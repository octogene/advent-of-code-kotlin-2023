fun main() {
    fun part1(input: List<String>): Int {
        val map = parseBeamInput(input)
        val mapByPosition: Map<Position, Tile> = map.associateBy { it.position }
        return moveBeam(mapByPosition)
    }

    fun part2(input: List<String>): Int {
        val map = parseBeamInput(input)
        val width = input.first().lastIndex
        val height = input.lastIndex
        val mapByPosition: Map<Position, Tile> = map.associateBy { it.position }
        val startingBeams = Direction.entries.flatMap { direction ->
            when (direction) {
                Direction.SOUTH -> {
                    (0..width).map { Beam(Position(it, -1), direction) }
                }

                Direction.NORTH -> {
                    (0..width).map { Beam(Position(it, height + 1), direction) }
                }

                Direction.EAST -> {
                    (0..height).map { Beam(Position(-1, it), direction) }
                }

                Direction.WEST -> {
                    (0..height).map { Beam(Position(width + 1, it), direction) }
                }
            }
        }
        startingBeams.maxOfOrNull { moveBeam(mapByPosition, it) }.println()

        return input.size
    }

    val input = readInput("Day16")
    println("== Part I ==")
    part1(input).println()
    println("== Part II ==")
    part2(input).println()
}

fun parseBeamInput(input: List<String>): List<Tile> {
    return input.flatMapIndexed { y: Int, s: String ->
        s.mapIndexed { x, c -> Tile(c, Position(x, y)) }
    }
}

fun moveBeam(map: Map<Position, Tile>, initialBeam: Beam = Beam(Position(-1, 0), Direction.EAST)): Int {
    val blockedBeams = mutableListOf<Beam>()
    val beams = mutableListOf<Beam>(initialBeam)
    val knownPosition = mutableMapOf<Direction, MutableList<Position>>()
    while (beams.size > 0) {
        val beam = beams.last()
        while (true) {
            val nextPosition = beam.move()
            val nextTile = map[nextPosition]
            if (nextTile != null) {
                beam.tiles.add(nextTile)
                when (nextTile.type) {
                    '-' -> {
                        when (beam.direction) {
                            Direction.NORTH,
                            Direction.SOUTH,
                            -> {
                                beam.direction = Direction.EAST
                                beams.add(Beam(nextTile.position, Direction.WEST))
                            }

                            else -> {}
                        }
                    }

                    '|' -> {
                        when (beam.direction) {
                            Direction.EAST,
                            Direction.WEST,
                            -> {
                                beam.direction = Direction.NORTH
                                beams.add(Beam(nextTile.position, Direction.SOUTH))
                            }

                            else -> {}
                        }
                    }

                    '/' -> {
                        when (beam.direction) {
                            Direction.SOUTH -> beam.direction = Direction.WEST
                            Direction.NORTH -> beam.direction = Direction.EAST
                            Direction.EAST -> beam.direction = Direction.NORTH
                            Direction.WEST -> beam.direction = Direction.SOUTH
                        }
                    }

                    '\\' -> {
                        when (beam.direction) {
                            Direction.SOUTH -> beam.direction = Direction.EAST
                            Direction.NORTH -> beam.direction = Direction.WEST
                            Direction.EAST -> beam.direction = Direction.SOUTH
                            Direction.WEST -> beam.direction = Direction.NORTH
                        }
                    }
                    else -> Unit
                }
                beam.position = nextPosition
                if (knownPosition.containsKey(beam.direction)) {
                    if (knownPosition[beam.direction]?.contains(beam.position) == true) {
                        blockedBeams.add(beam)
                        beams.remove(beam)
                        break
                    }
                    knownPosition[beam.direction]?.add(beam.position)
                } else {
                    knownPosition[beam.direction] = mutableListOf(beam.position)
                }
            } else {
                blockedBeams.add(beam.copy())
                beams.remove(beam)
                break
            }
        }
    }
    return blockedBeams.flatMap { it.tiles }.toSet().size
}

data class Beam(var position: Position, var direction: Direction, val tiles: MutableList<Tile> = mutableListOf())

fun Beam.move(): Position = when (direction) {
    Direction.SOUTH -> Position(position.x, position.y + 1)
    Direction.NORTH -> Position(position.x, position.y - 1)
    Direction.EAST -> Position(position.x + 1, position.y)
    Direction.WEST -> Position(position.x - 1, position.y)
}


