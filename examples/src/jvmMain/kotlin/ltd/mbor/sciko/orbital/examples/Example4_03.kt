package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.coeFromSV
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
  mk.setEngine(KEEngineType)
  val r = mk.ndarray(mk[-6045.0, -3490.0, 2500.0])
  val v = mk.ndarray(mk[-3.457, 6.618, 2.533])
  val coe = coeFromSV(r, v, muEarth)

  println(" -----------------------------------------------------")
  println(" Example 4.3")
  println(" Gravitational parameter (k^3/s^2) = ${muEarth}")
  println(" State vector:")
  println(" r (km)        = [${r[0]} ${r[1]} ${r[2]}])")
  println(" v (km)        = [${v[0]}   ${v[1]}  ${v[2]} ])")
  println()
  println(" Angular momentum (km^2/s) = ${coe[0]}")
  println(" Eccentricity              = ${coe[1]}")
  println(" Right ascension (deg)     = ${coe[2].toDegrees()}")
  println(" Inclination (deg)         = ${coe[3].toDegrees()}")
  println(" Argument of perigee (deg) = ${coe[4].toDegrees()}")
  println(" True anomaly (deg)        = ${coe[5].toDegrees()}")
  println(" Semimajor axis (km)       = ${coe[6]}")

  if (coe[1] < 1) {
    val T = 2*PI/sqrt(muEarth)*coe[6].pow(1.5)
    println(" Period:")
    println("   Seconds = $T")
    println("   Minutes = ${T/60}")
    println("   Hours = ${T/3600}")
    println("   Days = ${T/24/3600}")
  }
  println(" -----------------------------------------------------")
}
