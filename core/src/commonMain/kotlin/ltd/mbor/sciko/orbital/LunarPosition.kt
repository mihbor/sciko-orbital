package ltd.mbor.sciko.orbital
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.times

fun lunarPosition(jd: Double): MultiArray<Double, D1> {
  // Earth's radius (km)
  val RE = rEarth

  // Time in centuries since J2000
  val T = (jd - 2451545) / 36525.0

  // Ecliptic longitude (deg)
  var e_long = 218.32 + 481267.881 * T +
     6.29 * sind(135.0 + 477198.87 * T) - 1.27 * sind(259.3 - 413335.36 * T) +
     0.66 * sind(235.7 + 890534.22 * T) + 0.21 * sind(269.9 + 954397.74 * T) +
    -0.19 * sind(357.5 +  35999.05 * T) - 0.11 * sind(186.5 + 966404.03 * T)
  e_long = e_long.mod(360.0)

  // Ecliptic latitude (deg)
  var e_lat = 5.13 * sind( 93.3 + 483202.02 * T) + 0.28 * sind(228.2 + 960400.89 * T) +
             -0.28 * sind(318.3 +   6003.15 * T) - 0.17 * sind(217.6 - 407332.21 * T)
  e_lat = e_lat.mod(360.0)

  // Horizontal parallax (deg)
  var h_par = 0.9508 + 
    0.0518 * cosd(135.0 + 477198.87 * T) + 0.0095 * cosd(259.3 - 413335.36 * T) +
    0.0078 * cosd(235.7 + 890534.22 * T) + 0.0028 * cosd(269.9 + 954397.74 * T)
  h_par = h_par.mod(360.0)

  // Angle between earth's orbit and its equator (deg)
  val obliquity = 23.439291 - 0.0130042 * T

  // Direction cosines of the moon's geocentric equatorial position vector
  val l = cosd(e_lat) * cosd(e_long)
  val m = cosd(obliquity) * cosd(e_lat) * sind(e_long) - sind(obliquity) * sind(e_lat)
  val n = sind(obliquity) * cosd(e_lat) * sind(e_long) + cosd(obliquity) * sind(e_lat)

  // Earth-moon distance (km)
  val dist = RE / sind(h_par)

  // Moon's geocentric equatorial position vector (km)
  return dist * mk.ndarray(mk[l, m, n])
}
