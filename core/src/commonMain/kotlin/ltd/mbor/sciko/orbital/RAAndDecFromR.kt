package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos

fun `RA and Dec from R`(r: MultiArray<Double, D1>): Pair<Double, Double> {
  // Normalize the position vector components
  val rNorm = r.norm()
  val l = r[0] / rNorm
  val m = r[1] / rNorm
  val n = r[2] / rNorm

  // Calculate declination in degrees
  val dec = asin(n).toDegrees()

  // Calculate right ascension in degrees
  val ra = if (m > 0) {
    acos(l / cos(Math.toRadians(dec))).toDegrees()
  } else {
    360 - acos(l / cos(Math.toRadians(dec))).toDegrees()
  }

  return ra to dec
}
