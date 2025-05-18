package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.degrees
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.svFromCoe
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get

fun main() {

  val mu = muEarth

  // Data declaration for Example 4.5 (angles in degrees)
  val h = 80000.0
  val e = 1.4
  val RA = 40.0
  val incl = 30.0
  val w = 60.0
  val TA = 30.0

  // Orbital elements: [h, e, RA, incl, w, TA] (angles in radians)
  val coe = mk.ndarray(
    doubleArrayOf(
      h,
      e,
      RA.degrees,
      incl.degrees,
      w.degrees,
      TA.degrees
    )
  )

  // Algorithm 4.5 (requires angular elements in radians)
  val (r, v) = svFromCoe(coe, mu)

  // Echo the input data and output the results
  println("-----------------------------------------------------")
  println(" Example 4.7")
  println("\n Gravitational parameter (km^3/s^2)  = $mu")
  println(" Angular momentum (km^2/s)           = $h")
  println(" Eccentricity                        = $e")
  println(" Right ascension (deg)               = $RA")
  println(" Argument of perigee (deg)           = $w")
  println(" True anomaly (deg)                  = $TA")
  println("\n State vector:")
  println("   r (km)   = [${r[0]}  ${r[1]}  ${r[2]}]")
  println("   v (km/s) = [${v[0]}  ${v[1]}  ${v[2]}]")
  println("-----------------------------------------------------")
}
