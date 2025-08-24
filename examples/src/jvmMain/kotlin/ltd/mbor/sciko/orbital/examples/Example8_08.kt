package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.coeFromSV
import ltd.mbor.sciko.orbital.interplanetary
import ltd.mbor.sciko.orbital.monthPlanetNames
import ltd.mbor.sciko.orbital.muSun
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.toList
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
  mk.setEngine(KEEngineType)
  // Input data
  val depart = listOf(3, 1996, 11, 7, 0, 0, 0) // Earth departure
  val arrive = listOf(4, 1997, 9, 12, 0, 0, 0) // Mars arrival

  // Calculate interplanetary trajectory
  val (planet1, planet2, V1, V2) = interplanetary(depart, arrive)

  // Extract data
  val (_, Rp1, Vp1, jd1) = planet1
  val (_, Rp2, Vp2, jd2) = planet2

  // Compute orbital elements
  val coe1 = coeFromSV(Rp1, V1, muSun)
  val coe2 = coeFromSV(Rp2, V2, muSun)

  // Compute excess velocities
  val vinf1 = V1 - Vp1
  val vinf2 = V2 - Vp2

  // Convert the planetId and month numbers into names for output
  val (monthNameDepart, planetNameDepart) = monthPlanetNames(depart[2], depart[0])
  val (monthNameArrive, planetNameArrive) = monthPlanetNames(arrive[2], arrive[0])

  // Output the results
  println("-----------------------------------------------------")
  println("Example 8.8")

  println("\nDeparture:")
  println("   Planet: $planetNameDepart")
  println("   Year  : ${depart[1]}")
  println("   Month : $monthNameDepart")
  println("   Day   : ${depart[3]}")
  println("   Hour  : ${depart[4]}")
  println("   Minute: ${depart[5]}")
  println("   Second: ${depart[6]}")
  println("\n   Julian day: ${"%.3f".format(jd1)}")
  println("\n   Planet position vector (km): ${Rp1.toList()}")
  println("   Magnitude: ${Rp1.norm()}")
  println("\n   Planet velocity (km/s): ${Vp1.toList()}")
  println("   Magnitude: ${Vp1.norm()}")
  println("\n   Spacecraft velocity (km/s): ${V1.toList()}")
  println("   Magnitude: ${V1.norm()}")
  println("\n   v-infinity at departure (km/s): ${vinf1.toList()}")
  println("   Magnitude: ${vinf1.norm()}")

  println("\nArrival:")
  println("   Planet: $planetNameArrive")
  println("   Year  : ${arrive[1]}")
  println("   Month : $monthNameArrive")
  println("   Day   : ${arrive[3]}")
  println("   Hour  : ${arrive[4]}")
  println("   Minute: ${arrive[5]}")
  println("   Second: ${arrive[6]}")
  println("\n   Julian day: ${"%.3f".format(jd2)}")
  println("\n   Planet position vector (km): ${Rp2.toList()}")
  println("   Magnitude: ${Rp2.norm()}")
  println("\n   Planet velocity (km/s): ${Vp2.toList()}")
  println("   Magnitude: ${Vp2.norm()}")
  println("\n   Spacecraft velocity (km/s): ${V2.toList()}")
  println("   Magnitude: ${V2.norm()}")
  println("\n   v-infinity at departure (km/s): ${vinf2.toList()}")
  println("   Magnitude: ${vinf2.norm()}")

  printf("\n\n\n Orbital elements of flight trajectory:\n")

  printf("\n  Angular momentum (km^2/s)                   = %g", coe1[0])
  printf("\n  Eccentricity                                = %g", coe1[1])
  printf("\n  Right ascension of the ascending node (deg) = %g", coe1[2].toDegrees())
  printf("\n  Inclination to the ecliptic (deg)           = %g", coe1[3].toDegrees())
  printf("\n  Argument of perihelion (deg)                = %g", coe1[4].toDegrees())
  printf("\n  True anomaly at departure (deg)             = %g", coe1[5].toDegrees())
  printf("\n  True anomaly at arrival (deg)               = %g\n", coe2[5].toDegrees())
  printf("\n  Semimajor axis (km)                         = %g", coe1[6])
  // If the orbit is an ellipse, output the period:
  if (coe1[1] < 1) {
    printf("\n  Period (days)                               = %g\n", 2*PI/sqrt(muSun)*coe1[6].pow(1.5)/24/3600)
  }

}

private fun printf(format: String, vararg args: Any?) {
  System.out.printf(format, *args)
}
