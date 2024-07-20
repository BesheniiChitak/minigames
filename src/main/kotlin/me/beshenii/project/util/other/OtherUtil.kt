package me.beshenii.project.util.other

fun intArrayOf(range: IntRange) =
    IntArray(range.last - range.first + 1) { range.first + it }

fun Map<*, *>.mapToString(): String {
    var result = ""
    val first = this.keys.first()
    this.forEach { (key, value) ->
        var add = "$key — $value"
        if (key == first) add = " | $add"
        result += add
    }
    return result
}

fun String.stringToMap(): MutableMap<String, Int> {
    val result = mutableMapOf<String, Int>()
    val arrays = this.split(" | ")
    arrays.forEach {
        val array = it.split(" — ")
        result[array[0]] = array[1].toInt()
    }
    return result
}
