package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.LST

fun main() {
    // Data declaration for Example 5.6:
    // East longitude:
    var degrees = 139
    val minutes = 47
    val seconds = 0

    // Date:
    val year = 2004
    val month = 3
    val day = 3

    // Universal time:
    val hour = 4
    val minute = 30
    val second = 0

    // Convert negative (west) longitude to east longitude:
    if (degrees < 0) {
        degrees += 360
    }

    // Express the longitudes as decimal numbers:
    val EL = degrees + minutes / 60.0 + seconds / 3600.0
    val WL = 360 - EL

    // Express universal time as a decimal number:
    val ut = hour + minute / 60.0 + second / 3600.0

    // Algorithm 5.3: Local Sidereal Time
    val lstDeg = LST(year, month, day, ut, EL)

    // Output
    println("-----------------------------------------------------")
    println(" Example 5.6: Local sidereal time calculation\n")
    println(" Input data:")
    println("   Year                      = $year")
    println("   Month                     = $month")
    println("   Day                       = $day")
    println("   UT (hr)                   = $ut")
    println("   West Longitude (deg)      = %3.3f".format(WL))
    println("   East Longitude (deg)      = %3.3f".format(EL))
    println(" Solution:")
    println("  Local Sidereal Time (deg)  = %3.5f".format(lstDeg))
    println("  Local Sidereal Time (hr)   = %3.6f".format(lstDeg / 15.0))
    println("-----------------------------------------------------")
}
