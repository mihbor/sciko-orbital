import ltd.mbor.sciko.orbital.coeFromSV
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {

  mk.setEngine(KEEngineType)

  // Data declaration for Example 5.1
  val r1 = mk.ndarray(doubleArrayOf(-294.32, 4265.1, 5986.7))
  val r2 = mk.ndarray(doubleArrayOf(-1365.5, 3637.6, 6346.8))
  val r3 = mk.ndarray(doubleArrayOf(-2940.3, 2473.7, 6555.8))

  println("-----------------------------------------------------")
  println(" Example 5.1: Gibbs Method\n")
  println(" Input data:")
  println("  Gravitational parameter (km^3/s^2)  = $muEarth")
  println("  r1 (km) = [${r1[0]}  ${r1[1]}  ${r1[2]}]")
  println("  r2 (km) = [${r2[0]}  ${r2[1]}  ${r2[2]}]")
  println("  r3 (km) = [${r3[0]}  ${r3[1]}  ${r3[2]}]\n")

  // Algorithm 5.1: Gibbs method
  val (v2, ierr) = gibbs(r1, r2, r3, muEarth)

  if (ierr) {
    println("  These vectors are not coplanar.\n")
    return
  }

  // Algorithm 4.2: Orbital elements from state vector
  val coe = coeFromSV(r2, v2, muEarth)
  val h = coe[0]
  val e = coe[1]
  val RA = coe[2]
  val incl = coe[3]
  val w = coe[4]
  val TA = coe[5]
  val a = coe[6]

  println(" Solution:\n")
  println("  v2 (km/s) = [${v2[0]}  ${v2[1]}  ${v2[2]}]\n")
  println("  Orbital elements:")
  println("    Angular momentum (km^2/s)  = $h")
  println("    Eccentricity               = $e")
  println("    Inclination (deg)          = ${incl.toDegrees()}")
  println("    RA of ascending node (deg) = ${RA.toDegrees()}")
  println("    Argument of perigee (deg)  = ${w.toDegrees()}")
  println("    True anomaly (deg)         = ${TA.toDegrees()}")
  println("    Semimajor axis (km)        = $a")
  if (e < 1) {
    val T = 2 * Math.PI / sqrt(muEarth) * a.pow(1.5)
    println("    Period (s)                 = $T")
  }
  println("-----------------------------------------------------")
}
