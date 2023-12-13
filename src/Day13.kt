fun main() {
    fun part1(input: List<String>): Int {
        val inputs = parseMirrorInput(input)
        val result = inputs.withIndex().sumOf { (index, input) ->
            val verticalReflection = findVerticalReflectionForLines(input)
            if (verticalReflection == null) {
                val horizontalReflection = findReflectionForLines(input)
                horizontalReflection.second * 100
            } else {
                verticalReflection.second
            }
        }
        return result
    }

    fun part2(input: List<String>): Int {
        val inputs = parseMirrorInput(input)

        return inputs.sumOf { data ->
            finReflection2(data)
        }
    }

    val input = readInput("Day13")
    println("== Part I ==")
    part1(input).println()
    println("== Part II ==")
    part2(input).println()
}

fun parseMirrorInput(input: List<String>): List<List<String>> {
    val groups = mutableListOf<MutableList<String>>(mutableListOf())
    input.forEach { s ->
        if (s.isEmpty()) {
            groups.add(mutableListOf())
        } else {
            groups.last().add(s)
        }
    }
    return groups
}

fun findVerticalReflectionForLines(lines: List<String>): Pair<Int, Int>? {
    val result = mutableListOf<List<Pair<Int, Pair<Int, Int>>>>()
    lines.forEach { line ->
        var index1 = 0
        var index2 = 1
        val lineRange = 0..line.lastIndex
        val candidates = mutableListOf<Pair<Int, Pair<Int, Int>>>()
        while (index2 != line.length) {
            var currentCount = 0
            for (i in 0..line.lastIndex) {
                val leftIndex = index1 - i
                val rightIndex = index2 + i
                if (leftIndex !in lineRange || rightIndex !in lineRange) break
                if (line[index1 - i] == line[index2 + i]) {
                    currentCount++
                } else break
            }
            if (currentCount > 0) {
                candidates.add(currentCount to (index1 to index2))
            }
            index1++
            index2++
        }
        result.add(candidates)
    }
    return result.firstOrNull()?.firstOrNull { a -> result.all { r -> r.contains(a) } }?.second
}


fun findReflectionForLines(lines: List<String>): Pair<Int, Int> {
    val possibleSymmetry = lines
        .windowed(2)
        .withIndex()
        .filter { (idx, s) -> s[0] == s[1] }.map {
            it.index to it.index + 1
        }

    val candidate = possibleSymmetry.filter { p ->
        val top = lines.slice(0..p.first)
        val bottom = lines.slice(p.second..lines.lastIndex)
        if (top.size > bottom.size) {
            bottom.withIndex().all { top.reversed()[it.index] == it.value }
        } else {
            top.reversed().withIndex().all { bottom[it.index] == it.value }
        }
    }
    return candidate.first()
}


fun finReflection2(lines: List<String>): Int {
    val possibleHSymmetry = lines
        .windowed(2)
        .withIndex()
        .filter { (idx, s) -> s[0] diffCount s[1] <= 1 }.map {
            it.index to it.index + 1
        }
    val possibleVSymmetry = findVerticalReflectionForLines2(lines)

    val hCandidate = possibleHSymmetry.filter { p ->
        val top = lines.slice(0..p.first)
        val bottom = lines.slice(p.second..lines.lastIndex)
        if (top.size > bottom.size) {
            bottom.maxDiffCount(top.reversed()) == 1
        } else {
            top.reversed().maxDiffCount(bottom) == 1
        }
    }
    val vCandidate: List<Pair<Int, Int>> = possibleVSymmetry.filter {
        maxDiffCount(lines, it.first .. it.second) == 1
    }

    return if (vCandidate.isEmpty()) hCandidate.first().second * 100 else vCandidate.first().second
}

infix fun String.diffCount(value: String): Int {
    require(this.length == value.length)
    var count = 0
    if (this == value) return count
    for (i in 0 .. lastIndex) {
        if (this[i] != value[i]) count++
    }
    return count
}

fun List<String>.maxDiffCount(value: List<String>): Int {
    var count = 0
    for (i in 0 .. lastIndex) {
        count += this[i] diffCount value[i]
    }
    return count
}

fun findVerticalReflectionForLines2(lines: List<String>): List<Pair<Int, Int>> {
    val maxLength = lines.first().lastIndex
    val candidates = mutableListOf<Pair<Int, Int>>()
    for (c in 0 until maxLength) {
        var diffCount = 0
        for (r in 0 .. lines.lastIndex) {
            val left = lines[r][c]
            val right = lines[r][c + 1]
            if (left != right) diffCount++
        }
        if (diffCount <= 1) {
            candidates.add(c to c + 1)
        }
    }
    return candidates
}

fun maxDiffCount(lines: List<String>, range: IntRange): Int {
    val maxLength = lines.first().lastIndex
    var diffCount = 0
    val slices = lines.map {
        it.slice(0 .. range.first) to it.slice(range.last .. maxLength)
    }
    slices.forEach { (left, right) ->
        if (left.length > right.length) {
            right.withIndex().forEach { (idx, n) ->
                if (n != left.reversed()[idx]) diffCount++
            }
        } else {
            left.reversed().withIndex().forEach { (idx, n) ->
                if (n != right[idx]) diffCount++
            }
        }
    }
    return diffCount
}

