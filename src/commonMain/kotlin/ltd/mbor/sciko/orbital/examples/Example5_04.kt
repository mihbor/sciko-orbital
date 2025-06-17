package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.J0

/**
 * Example 5.4: Julian day calculation (converted from MATLAB)
 */
fun main() {
    val year = 2004
    val month = 5
    val day = 12
    val hour = 14
    val minute = 45
    val second = 30

    val ut = hour + minute / 60.0 + second / 3600.0
    val j0 = J0(year, month, day)
    val jd = j0 + ut / 24.0

    println("-----------------------------------------------------")
    println("Example 5.4: Julian day calculation")
    println("Input data:")
    println("  Year            = $year")
    println("  Month           = $month")
    println("  Day             = $day")
    println("  Hour            = $hour")
    println("  Minute          = $minute")
    println("  Second          = $second")
    println("Julian day number = %11.3f".format(jd))
    println("-----------------------------------------------------")
}
