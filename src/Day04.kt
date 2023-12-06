import kotlin.math.pow

fun main() {
    fun part1(input: List<String>): Int {
        return input
            .map { parseCard(it) }
            .sumOf { it.value }
    }

    fun part2(input: List<String>): Int {
        val cards = input.map { parseCard(it) }
        val allCards = getAllAdditionalCardsValue(cards, cards)
        return allCards.size
    }

    val input = readInput("Day04")
    part1(input).println()
    part2(input).println()
}

fun parseCard(input: String): Card {
    val (cardNumber, numbers) = input.replace("Card\\s+".toRegex(), "").split(":")
    val (rawWinningNumbers, rawCardNumbers) = numbers.split("|").map { it.trim() }
    val cardNumbers = rawCardNumbers.split("\\s+".toRegex()).map { it.toInt() }
    val winningNumbers = rawWinningNumbers.split("\\s+".toRegex()).map { it.toInt() }

    val cardWinnings = winningNumbers.sumOf { winningNumber ->
        cardNumbers.count { it == winningNumber }
    }

    return Card(
        id = cardNumber.toInt(),
        winningNumbers = winningNumbers,
        cardNumbers = cardNumbers,
        winnings = cardWinnings,
        value = 2.0.pow(maxOf(0,cardWinnings - 1)).toInt()
    )
}

fun getAdditionalCardsValue(card: Card, cards: List<Card>): List<Card> {
    return if (card.winnings == 0) {
        emptyList()
    } else {
        cards.slice(card.id until card.id + card.winnings)
    }
}

fun getAllAdditionalCardsValue(cards: List<Card>, allCards: List<Card>, id: Int = 0): List<Card> {
    return if (cards.isEmpty()) {
        cards
    } else {
        cards + cards.flatMap { getAllAdditionalCardsValue(getAdditionalCardsValue(it, allCards), allCards) }
    }
}


data class Card(
    val id: Int,
    val winningNumbers: List<Int>,
    val cardNumbers: List<Int>,
    val winnings: Int = 0,
    val value: Int = 0
)
