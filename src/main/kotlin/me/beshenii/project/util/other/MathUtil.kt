package me.beshenii.project.util.other

fun rand(start: Int, end: Int): Int {
    require(start <= end) { "Illegal Argument" }
    return (start..end).random()
}

fun clamp(number: Double, min: Double, max: Double): Double {
    return if (number > max) max
    else if (number < min) min
    else number
}

fun clamp(number: Int, min: Int, max: Int): Int {
    return if (number > max) max
    else if (number < min) min
    else number
}