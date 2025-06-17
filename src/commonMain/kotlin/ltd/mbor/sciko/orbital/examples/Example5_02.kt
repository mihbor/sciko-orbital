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
  // Data declaration for Example 5.2
  val mu = muEarth
  val r1 = mk.ndarray(mk[5000.0, 10000.0, 2100.0])
  val r2 = mk.ndarray(mk[-14600.0, 2500.0, 7000.0])
  val dt = 3600.0
  val traj = Trajectory.PROGRADE

  println("-----------------------------------------------------")
  println(" Example 5.2: Lambert's Problem\n")
  println("\n Input data:")
  println("   Gravitational parameter (km^3/s^2) = $mu")
  println("   r1 (km)                       = [${r1[0]}  ${r1[1]}  ${r1[2]}]")
  println("   r2 (km)                       = [${r2[0]}  ${r2[1]}  ${r2[2]}]")
  println("   Elapsed time (s)              = $dt")
  println("\n Solution:")

  // Algorithm 5.2: Lambert's problem
  val (v1, v2) = lambert(r1, r2, dt, traj, mu)

  println("   v1 (km/s)                     = [${v1[0]}  ${v1[1]}  ${v1[2]}]")
  println("   v2 (km/s)                     = [${v2[0]}  ${v2[1]}  ${v2[2]}]")

  // Algorithm 4.1: Orbital elements from state vector (initial)
  val coe1 = coeFromSV(r1, v1, mu)
  val TA1 = coe1[5]

  // Algorithm 4.1: Orbital elements from state vector (final)
  val coe2 = coeFromSV(r2, v2, mu)
  val TA2 = coe2[5]

  println("\n Orbital elements:")
  println("   Angular momentum (km^2/s)     = ${coe2[0]}")
  println("   Eccentricity                  = ${coe2[1]}")
  println("   Inclination (deg)             = ${coe2[3].toDegrees()}")
  println("   RA of ascending node (deg)    = ${coe2[2].toDegrees()}")
  println("   Argument of perigee (deg)     = ${coe2[4].toDegrees()}")
  println("   True anomaly initial (deg)    = ${TA1.toDegrees()}")
  println("   True anomaly final   (deg)    = ${TA2.toDegrees()}")
  println("   Semimajor axis (km)           = ${coe2[6]}")
  println("   Periapse radius (km)          = ${coe2[0].pow(2) / mu / (1 + coe2[1])}")

  // If the orbit is an ellipse, output its period
  if (coe2[1] < 1) {
    val T = 2 * Math.PI / sqrt(mu) * coe2[6].pow(1.5)
    println("   Period:")
    println("     Seconds                     = $T")
    println("     Minutes                     = ${T / 60}")
    println("     Hours                       = ${T / 3600}")
    println("     Days                        = ${T / 86400}")
  }
  println("-----------------------------------------------------")
}

// You must implement or import lambert and coeFromSv using MultiArray<Double, D1>