package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.monthPlanetNames
import ltd.mbor.sciko.orbital.planetElementsAndSV
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.toList

fun main() {
  // Input data
  val planetId = 3
  val year = 2003
  val month = 8
  val day = 27
  val hour = 12
  val minute = 0
  val second = 0

  // Algorithm 8.1: Calculate orbital elements and state vectors
  val (coe, r, v, jd) = planetElementsAndSV(planetId, year, month, day, hour, minute, second)

  // Convert the planetId and month numbers into names for output
  val (monthName, planetName) = monthPlanetNames(month, planetId)

  // Output the results
  println("-----------------------------------------------------")
  println("Example 8.7")
  println("\nInput data:")
  println("   Planet: $planetName")
  println("   Year  : $year")
  println("   Month : $monthName")
  println("   Day   : $day")
  println("   Hour  : $hour")
  println("   Minute: $minute")
  println("   Second: $second")
  println("\n   Julian day: ${"%.3f".format(jd)}")

  println("\nOrbital elements:")
  println("  Angular momentum (km^2/s)                   = ${coe[0]}")
  println("  Eccentricity                                = ${coe[1]}")
  println("  Right ascension of the ascending node (deg) = ${coe[2].toDegrees()}")
  println("  Inclination (deg)                           = ${coe[3].toDegrees()}")
  println("  Argument of perihelion (deg)                = ${coe[4].toDegrees()}")
  println("  True anomaly (deg)                          = ${coe[5].toDegrees()}")
  println("  Semimajor axis (km)                         = ${coe[6]}")
  println("  Longitude of perihelion (deg)               = ${coe[7].toDegrees()}")
  println("  Mean longitude (deg)                        = ${coe[8].toDegrees()}")
  println("  Mean anomaly (deg)                          = ${coe[9].toDegrees()}")
  println("  Eccentric anomaly (deg)                     = ${coe[10].toDegrees()}")

  println("\nState vectors:")
  println("  Position vector (km)   = ${r.toList()}")
  println("  Magnitude              = ${r.norm()})")
  println("  Velocity vector (km/s) = ${v.toList()}")
  println("  Magnitude              = ${v.norm()})")
}
