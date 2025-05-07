package me.baldo.rootlink.utils

fun parseCoordinate(coordinate: String): Double {
    val regex = """(\d+)° (\d+)' ([\d.]+)''""".toRegex()
    val matchResult = regex.matchEntire(coordinate)
    requireNotNull(matchResult) { "Invalid coordinate format: $coordinate" }

    val (degrees, minutes, seconds) = matchResult.destructured
    return degrees.toDouble() + (minutes.toDouble() / 60) + (seconds.toDouble() / 3600)
}