fun main() {
    fun part1(input: List<String>): Int {
        val items = parseRockBeamInput(input)
        val columns = items.groupBy { it.x }
        for (i in 0 until columns.keys.size) {
            val top = columns.getValue(i)
            val toMove = top.filter { it.type == 'O' }.sortedBy { it.y }
            for (i in 0..toMove.lastIndex) {
                val item = toMove[i]
                if (item.y > 0) {
                    val topRange = 0 until item.y
                    val obstacle = top.lastOrNull { it.y in topRange }
                    if (obstacle == null) {
                        item.y = 0
                    } else if (obstacle.y == item.y - 1) {
                        continue
                    } else {
                        item.y = obstacle.y + 1
                    }
                }
            }
        }
        return columns
            .asSequence()
            .flatMap { it.value }
            .filter { it.type == 'O' }
            .sumOf { columns.size - it.y }
    }

    fun part2(input: List<String>): Int {
        return input.size
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
