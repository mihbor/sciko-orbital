import ltd.mbor.sciko.orbital.coeFromSV
import ltd.mbor.sciko.orbital.degrees
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import kotlin.math.*

const val DEG = Math.PI / 180

fun main() {
  val phi   = 40.degrees
  val t     = mk.ndarray(mk[     0.0,   118.104,  237.577])
  val ra    = mk.ndarray(mk[ 43.5365,   54.4196,  64.3178]).map { it.degrees }
  val dec   = mk.ndarray(mk[-8.78334,  -12.0739, -15.1054]).map { it.degrees }
  val theta = mk.ndarray(mk[ 44.5065,    45.000,  45.4992]).map { it.degrees }

  val Re = 6378.0 // Radius of Earth in km
  val f = 1 / 298.257 // Flattening factor
  val H = 1.0 // Altitude in km

  val fac1 = Re / sqrt(1 - (2 * f - f * f) * sin(phi).pow(2))
  val fac2 = (Re * (1 - f).pow(2) / sqrt(1 - (2 * f - f * f) * sin(phi).pow(2)) + H) * sin(phi)

  val R = mk.zeros<Double>(3, 3)
  val rho = mk.zeros<Double>(3, 3)

  for (i in 0 until 3) {
    R[i, 0] = (fac1 + H) * cos(phi) * cos(theta[i])
    R[i, 1] = (fac1 + H) * cos(phi) * sin(theta[i])
    R[i, 2] = fac2

    rho[i, 0] = cos(dec[i]) * cos(ra[i])
    rho[i, 1] = cos(dec[i]) * sin(ra[i])
    rho[i, 2] = sin(dec[i])
  }

  val (r, v, rOld, vOld) = gauss(
    Rho1 = rho[0], Rho2 = rho[1], Rho3 = rho[2],
    R1 = R[0], R2 = R[1], R3 = R[2],
    t1 = t[0], t2 = t[1], t3 = t[2]
  )

  val mu = 398600.0 // Gravitational parameter
  val coeOld = coeFromSV(rOld, vOld, mu)
  val coe = coeFromSV(r, v, mu)

  println("-----------------------------------------------------")
  println("\n Example 5.11: Orbit determination by the Gauss method\n")
  println(" Radius of earth (km)               = $Re")
  println(" Flattening factor                  = $f")
  println(" Gravitational parameter (km^3/s^2) = $mu\n")
  println(" Input data:")
  println(" Latitude (deg)                = ${phi / DEG}")
  println(" Altitude above sea level (km) = $H\n")
  println(" Observations:")
  println("   Time (s)   Ascension (deg)   Declination (deg)   Local Sidereal time (deg)")
  for (i in 0..2) {
    println(" ${t[i]}   ${ra[i] / DEG}   ${dec[i] / DEG}   ${theta[i] / DEG}")
  }
  println("\n Solution:\n")
  println(" Without iterative improvement...\n")
  println(" r (km)                          = ${rOld.data.toList()}")
  println(" v (km/s)                        = ${vOld.data.toList()}")
  println("   Angular momentum (km^2/s)     = ${coeOld[0]}")
  println("   Eccentricity                  = ${coeOld[1]}")
  println("   RA of ascending node (deg)    = ${coeOld[2] / DEG}")
  println("   Inclination (deg)             = ${coeOld[3] / DEG}")
  println("   Argument of perigee (deg)     = ${coeOld[4] / DEG}")
  println("   True anomaly (deg)            = ${coeOld[5] / DEG}")
  println("   Semimajor axis (km)           = ${coeOld[6]}")
  println("   Periapse radius (km)          = ${coeOld[0].pow(2) / mu / (1 + coeOld[1])}")
  if (coeOld[1] < 1) {
    val T = 2 * PI / sqrt(mu) * coeOld[6].pow(1.5)
    println("   Period:")
    println("     Seconds                     = $T")
    println("     Minutes                     = ${T / 60}")
    println("     Hours                       = ${T / 3600}")
    println("     Days                        = ${T / 86400}")
  }
  println("\n With iterative improvement...\n")
  println(" r (km)                          = ${r.data.toList()}")
  println(" v (km/s)                        = ${v.data.toList()}")
  println("   Angular momentum (km^2/s)     = ${coe[0]}")
  println("   Eccentricity                  = ${coe[1]}")
  println("   RA of ascending node (deg)    = ${coe[2] / DEG}")
  println("   Inclination (deg)             = ${coe[3] / DEG}")
  println("   Argument of perigee (deg)     = ${coe[4] / DEG}")
  println("   True anomaly (deg)            = ${coe[5] / DEG}")
  println("   Semimajor axis (km)           = ${coe[6]}")
  println("   Periapse radius (km)          = ${coe[0].pow(2) / mu / (1 + coe[1])}")
  if (coe[1] < 1) {
    val T = 2 * PI / sqrt(mu) * coe[6].pow(1.5)
    println("   Period:")
    println("     Seconds                     = $T")
    println("     Minutes                     = ${T / 60}")
    println("     Hours                       = ${T / 3600}")
    println("     Days                        = ${T / 86400}")
  }
  println("-----------------------------------------------------")
}
