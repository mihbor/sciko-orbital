package ltd.mbor.sciko.orbital

/**
 * Calculates the local sidereal time (LST) in degrees.
 * @param y Year
 * @param m Month
 * @param d Day
 * @param ut Universal Time (hours)
 * @param EL East longitude (degrees)
 * @return Local sidereal time (degrees)
 */
fun LST(y: Int, m: Int, d: Int, ut: Double, EL: Double): Double {
    val j0 = J0(y, m, d)
    val j = (j0 - 2451545) / 36525.0
    val g0 = 100.4606184 + 36000.77004 * j + 0.000387933 * j * j - 2.583e-8 * j * j * j
    val gst = g0 + 360.98564724 * ut / 24.0
    return zeroTo360(gst + EL)
}

/**
 * Reduces an angle to the range [0, 360) degrees.
 */
fun zeroTo360(angle: Double): Double {
    var a = angle % 360.0
    if (a < 0) a += 360.0
    return a
}
