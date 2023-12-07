fun main() {
    fun part1(input: List<String>): Int {
        return parseHands(input)
    }

    fun part2(input: List<String>): Int {
        return parseHandsWithJoker(input)
    }

    val input = readInput("Day07test")
    part1(input).println()
    part2(input).println()
}

fun parseHands(input: List<String>): Int {
    val hands = input.map {
        val (value, bid) = it.split(" ")
        val valuesCount = value.groupingBy { it }.eachCount()
        val type = assignHandType(valuesCount)
        Hand(value, bid.toInt(), type)
    }.sorted()
    println("Hands are $hands")
    return hands.mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
}

private fun assignHandType(valuesCount: Map<Char, Int>): Int {
    val type = if (valuesCount.size == 1) {
        7
    } else if (valuesCount.size == 2) {
        if (valuesCount.values.containsAll(listOf(4, 1))) 6 else 5
    } else if (valuesCount.size == 3 && valuesCount.any { it.value == 3 }) {
        4
    } else if (valuesCount.size == 3 && valuesCount.count { it.value == 2 } == 2) {
        3
    } else if (valuesCount.size == 4 && valuesCount.any { it.value == 2 }) {
        2
    } else {
        1
    }
    return type
}

fun parseHandsWithJoker(input: List<String>): Int {
    val hands = input.map {
        val (value, bid) = it.split(" ")
        val valuesCount = value.groupingBy { it }.eachCount()
        val jokers = valuesCount.getOrDefault('J', 0)
        val handWithoutJokers = if (jokers in 1..4) {
            val handWithoutJokers = valuesCount.filter { it.key != 'J' }.toMutableMap()
            val max = handWithoutJokers.maxOf { it.value }
            val cardToUseJokerOn = handWithoutJokers.entries.firstOrNull { it.value == max }?.key
            cardToUseJokerOn?.let { card ->
                val newMaxValue = handWithoutJokers.getValue(card) + jokers
                handWithoutJokers[card] = newMaxValue
            }
            handWithoutJokers
        } else valuesCount
        
        val type = assignHandType(handWithoutJokers)
        HandWithJoker(value, bid.toInt(), type)
    }.sorted()
    println("Hands are $hands")
    return hands.mapIndexed { index, hand -> (index + 1) * hand.bid }.sum()
}


data class Hand(val value: String, val bid: Int, val type: Int): Comparable<Hand> {
    companion object {
        private val ordering = listOf('A', 'K', 'Q', 'J', 'T', '9', '8', '7', '6', '5', '4', '3', '2').reversed()
    }
    override fun compareTo(other: Hand): Int = when {
        type > other.type -> 1
        type < other.type -> -1
        else -> {
            var equality = 0
            for (i in 0 .. value.lastIndex) {
                val currentValue = ordering.indexOf(value[i])
                val otherValue = ordering.indexOf(other.value[i])
                if (currentValue > otherValue) {
                    equality = 1
                    break
                } else if (otherValue > currentValue) {
                    equality = -1
                    break
                } else {
                    continue
                }
            }
            equality
        }
    }
}

data class HandWithJoker(val value: String, val bid: Int, val type: Int): Comparable<HandWithJoker> {
    companion object {
        private val ordering = listOf('A', 'K', 'Q', 'T', '9', '8', '7', '6', '5', '4', '3', '2', 'J').reversed()
    }
    override fun compareTo(other: HandWithJoker): Int = when {
        type > other.type -> 1
        type < other.type -> -1
        else -> {
            var equality = 0
            for (i in 0 .. value.lastIndex) {
                val currentValue = ordering.indexOf(value[i])
                val otherValue = ordering.indexOf(other.value[i])
                if (currentValue > otherValue) {
                    equality = 1
                    break
                } else if (otherValue > currentValue) {
                    equality = -1
                    break
                } else {
                    continue
                }
            }
            equality
        }
    }
}


