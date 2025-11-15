package ltd.mbor.sciko.orbital
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun solarPosition(jd: Double): Triple<Double, Double, MultiArray<Double, D1>> {
  // Astronomical unit (km)
  val au = 149597870.691

  // Julian days since J2000
  val n = jd - 2451545

  // Julian centuries since J2000
//  val cy = n / 36525

  // Mean anomaly (radians)
  val m = (357.528 + 0.9856003 * n).degrees.mod(2*PI)

  // Mean longitude (radians)
  val l = (280.460 + 0.98564736 * n).degrees.mod(2*PI)

  // Apparent ecliptic longitude (radians)
  val lambda = (l + 1.915.degrees * sin(m) + 0.020.degrees * sin(2 * m)).mod(2*PI)

  // Obliquity of the ecliptic (radians)
  val eps = (23.439 - 0.0000004 * n).degrees.mod(2*PI)

  // Unit vector from earth to sun
  val u = mk.ndarray(mk[cos(lambda), sin(lambda) * cos(eps), sin(lambda) * sin(eps)])

  // Distance from earth to sun (km)
  val rS = (1.00014 - 0.01671 * cos(m) - 0.000140 * cos(2 * m)) * au

  // Geocentric position vector (km)
  val rSVector = rS * u

  return Triple(lambda, eps, rSVector)
}
