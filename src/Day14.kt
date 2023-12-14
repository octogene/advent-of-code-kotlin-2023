fun main() {
    fun part1(input: List<String>): Int {
        val items = parseRockBeamInput(input)
        val width = input.first().lastIndex
        val height = input.lastIndex
        items.printMap(width, height)
        val columns = moveItems(items, Direction.NORTH, width, height)
        return columns
            .asSequence()
            .flatMap { it.value }
            .filter { it.type == 'O' }
            .sumOf { columns.size - it.y }
    }

    fun part2(input: List<String>): Int {
        val items = parseRockBeamInput(input)
        val width = input.first().lastIndex
        val height = input.lastIndex
        listOf(Direction.NORTH, Direction.WEST, Direction.SOUTH, Direction.EAST)
        var columns = items.map { it.copy() }
        val cycles = (0..1000).map { repeatCount ->
            columns = moveItems(columns.toList(), Direction.NORTH, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.WEST, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.SOUTH, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.EAST, width, height).flatMap { it.value }
            columns
                .asSequence()
                .filter { it.type == 'O' }
                .sumOf { (height + 1) - it.y }
        }

        val initializationValuesCount = cycles.groupingBy { it }.eachCount().filter { it.value == 1 }.size
        var startCycle = -1
        for (i in initializationValuesCount until cycles.lastIndex - 5) {
            if (cycles.count { it == cycles[i] } > 2) {
                val pattern = cycles.slice(i .. i + 5)
                val count = cycles
                    .slice(i + 1 .. cycles.lastIndex)
                    .windowed(6)
                    .count { it == pattern }
                if (count > 1) {
                    startCycle = i
                    break
                }
            }
        }

        val firstCycleNumber = cycles[startCycle]
        val cycleCount = cycles.slice(startCycle + 5 .. cycles.lastIndex).indexOf(firstCycleNumber) + 5
        columns = items
        repeat(((1_000_000_000 - startCycle) % cycleCount) + startCycle) {
            columns = moveItems(columns.toList(), Direction.NORTH, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.WEST, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.SOUTH, width, height).flatMap { it.value }
            columns = moveItems(columns.toList(), Direction.EAST, width, height).flatMap { it.value }
        }

        return columns
            .asSequence()
            .filter { it.type == 'O' }
            .sumOf { (height + 1) - it.y }
    }

    val input = readInput("Day14")
    println("== Part I ==")
    part1(input).println()
    println("== Part II ==")
    part2(input).println()
}

fun parseRockBeamInput(inputs: List<String>): List<Item> {
    val items = inputs.flatMapIndexed { y: Int, s: String ->
        s.mapIndexedNotNull { x, c -> if (c != '.') Item(c, x, y) else null }
    }
    return items
}

data class Item(val type: Char, var x: Int, var y: Int)

fun moveItems(items: List<Item>, direction: Direction, width: Int, height: Int): Map<Int, List<Item>> {
    val rowKeySelector: (Item) -> Int = when (direction) {
        Direction.SOUTH, Direction.NORTH -> {
            { it.y }
        }

        Direction.EAST, Direction.WEST -> {
            { it.x }
        }
    }

    val rowSimpleKeySetter: (item: Item, value: Int) -> Unit = when (direction) {
        Direction.SOUTH, Direction.NORTH -> {
            { item, value -> item.y = value }
        }

        Direction.EAST, Direction.WEST -> {
            { item, value -> item.x = value }
        }
    }

    val rowKeySetter: (item: Item, obstacle: Item) -> Unit = when (direction) {
        Direction.SOUTH -> {
            { item, obstacle -> item.y = obstacle.y - 1 }
        }

        Direction.NORTH -> {
            { item, obstacle -> item.y = obstacle.y + 1 }
        }

        Direction.EAST -> {
            { item, obstacle -> item.x = obstacle.x - 1 }
        }

        Direction.WEST -> {
            { item, obstacle -> item.x = obstacle.x + 1 }
        }
    }

    val range: (Item) -> IntRange = when (direction) {
        Direction.SOUTH -> {
            { it -> it.y + 1..height }
        }

        Direction.NORTH -> {
            { it-> 0 until it.y }
        }

        Direction.EAST -> {
            { it -> it.x + 1 .. width }
        }

        Direction.WEST -> {
            { it  -> 0 until it.x }
        }
    }

    val rowIterationRange: (List<Item>) -> IntProgression = when (direction) {
        Direction.SOUTH -> {
            { it.lastIndex downTo 0 }
        }

        Direction.NORTH -> {
            { 0..it.lastIndex }
        }

        Direction.EAST -> {
            { it.lastIndex downTo 0 }
        }

        Direction.WEST -> {
            { 0..it.lastIndex }
        }
    }

    val positionComparator: (obstacle: Item, item: Item) -> Boolean = when (direction) {
        Direction.SOUTH -> {
            { obstacle, item -> obstacle.y == item.y + 1 }
        }

        Direction.NORTH -> {
            { obstacle, item -> obstacle.y == item.y - 1 }
        }

        Direction.EAST -> {
            { obstacle, item -> obstacle.x == item.x - 1 }
        }

        Direction.WEST -> {
            { obstacle, item -> obstacle.x == item.x + 1 }
        }
    }

    val obstacleSelector: (List<Item>, IntRange) -> Item? = when (direction) {
        Direction.SOUTH -> {
            { list, range -> list.firstOrNull { rowKeySelector(it) in range } }
        }

        Direction.NORTH -> {
            { list, range -> list.lastOrNull { rowKeySelector(it) in range } }
        }

        Direction.EAST -> {
            { list, range -> list.firstOrNull { rowKeySelector(it) in range } }
        }

        Direction.WEST -> {
            { list, range -> list.lastOrNull { rowKeySelector(it) in range } }
        }
    }

    val endSelector: Int = when (direction) {
        Direction.SOUTH -> height
        Direction.NORTH -> 0
        Direction.EAST -> width
        Direction.WEST -> 0
    }

    val edgeCondition: (Item) -> Boolean = when (direction) {
        Direction.SOUTH -> {
            { item -> rowKeySelector(item) < endSelector }
        }

        Direction.NORTH -> {
            { item -> rowKeySelector(item) > endSelector }
        }

        Direction.EAST -> {
            { item -> rowKeySelector(item) < endSelector }
        }

        Direction.WEST -> {
            { item -> rowKeySelector(item) > endSelector }
        }
    }

    val columns = when (direction) {
        Direction.SOUTH, Direction.NORTH -> items.groupBy { it.x }.mapValues { it.value.sortedBy { it.y } }
        Direction.EAST, Direction.WEST -> items.groupBy { it.y }.mapValues { it.value.sortedBy { it.x } }
    }

    for (i in 0 until columns.keys.size) {
        columns[i]?.let { top ->
            val toMove = top.filter { it.type == 'O' }.sortedBy { rowKeySelector(it) }
            for (j in rowIterationRange(toMove)) {
                val item = toMove[j]
                if (edgeCondition(item)) {
                    val topRange = range(item)
                    val obstacle = obstacleSelector(top, topRange)
                    if (obstacle == null) {
                        rowSimpleKeySetter(item, endSelector)
                    } else if (positionComparator(obstacle, item)) {
                        continue
                    } else {
                        rowKeySetter(item, obstacle)
                    }
                }
            }
        }
    }
    return columns
}

fun List<Item>.printMap(width: Int, height: Int) {
//    val rows = groupBy { it.y }
//    (0..height).forEach { h ->
//        for (i in 0 .. width) {
//            val item = rows[h]?.firstOrNull() { it.x == i }
//            print(item?.type ?: '.')
//        }
//        println("")
//    }
}
