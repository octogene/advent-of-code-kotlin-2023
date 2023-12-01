fun main() {
    fun part1(input: List<String>): Int {
        return input.sumOf { getCalibrationFromLine(it) }
    }

    fun part2(input: List<String>): Int {
        return input.sumOf { getCalibrationFromLineWithWords(it) }
    }

    val input = readInput("Day01")
    part1(input).println()
    part2(input).println()
}

fun getCalibrationFromLine(line: String): Int {
    var firstDigit: Int? = null
    var lastDigit: Int? = null
    var result = 0
    for (idx in 0..line.lastIndex) {
        if (firstDigit == null && line[idx].isDigit()) {
            firstDigit = line[idx].digitToInt()
        }
        if (lastDigit == null && line[line.lastIndex - idx].isDigit()) {
            lastDigit = line[line.lastIndex - idx].digitToInt()
        }
        if (firstDigit != null && lastDigit != null) {
            result = firstDigit * 10 + lastDigit
        }
    }
    return result
}

fun getCalibrationFromLineWithWords(line: String): Int {
    var firstDigit: Int? = null
    var lastDigit: Int? = null
    var result = 0
    for (idx in 0..line.lastIndex) {
        if (firstDigit == null) {
            if (line[idx].isDigit()) {
                firstDigit = line[idx].digitToInt()
            }
            val firstWordDigit = line.slice(idx..line.lastIndex).isWordDigit(false)
            if (firstWordDigit != null) {
                firstDigit = firstWordDigit
            }
        }
        if (lastDigit == null) {
            if (line[line.lastIndex - idx].isDigit()) {
                lastDigit = line[line.lastIndex - idx].digitToInt()
            }
            val lastWordDigit = line.slice(0..line.lastIndex - idx).isWordDigit(true)
            if (lastWordDigit != null) {
                lastDigit = lastWordDigit
            }
        }
        if (firstDigit != null && lastDigit != null) {
            result = firstDigit * 10 + lastDigit
        }
    }
    return result
}

val wordDigits = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine")
fun String.isWordDigit(reverse: Boolean): Int? {
    val digit = if (reverse) {
        wordDigits.firstOrNull { this.endsWith(it) }
    } else {
        wordDigits.firstOrNull { this.startsWith(it) }
    }

    return (wordDigits.indexOf(digit) + 1).takeIf { digit != null }
}
