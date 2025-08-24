package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import java.lang.Math.PI
import kotlin.math.*

fun rvFromObserve(
  omega: MultiArray<Double, D1>, // 3D vector
  phi: Double,        // latitude (rad)
  A: Double,          // azimuth (rad)
  a: Double,          // altitude (rad)
  H: Double,          // elevation of site (km)
  aDot: Double,       // altitude rate (rad/s)
  ADot: Double,       // azimuth rate (rad/s)
  theta: Double,      // local sidereal time (rad)
  wE: Double,         // Earth's rotation rate (rad/s)
  rho: Double,        // slant range of object (km)
  rhoDot: Double,     // range rate (km/s)
  Re: Double = rEarth,// equatorial radius of the earth (km)
  f: Double,          // earth's flattening factor
): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {

  //...Equation 5.56:
  val R = mk.ndarray(mk[(Re/sqrt(1 - (2*f - f*f)*sin(phi).pow(2)) + H)*cos(phi)*cos(theta),
    (Re/sqrt(1 - (2*f - f*f)*sin(phi).pow(2))+H)*cos(phi)*sin(theta),
    (Re*(1 - f).pow(2)/sqrt(1-(2*f-f*f)*sin(phi).pow(2))+H)*sin(phi)])

  val Rdot = omega cross R

  // Equation 5.83a
  val dec = asin(cos(phi)*cos(A)*cos(a) + sin(phi)*sin(a))

  // Equation 5.83b
  var h = acos((cos(phi)*sin(a) - sin(phi)*cos(A)*cos(a))/cos(dec))
  if (A > 0 && A < PI) {
    h = 2*PI - h
  }

  // Equation 5.83c
  val RA = theta - h

  // Equation 5.57
  val Rho = mk.ndarray(mk[
    cos(RA)*cos(dec),
    sin(RA)*cos(dec),
    sin(dec)
  ])

  // Equation 5.63
  val r = R + rho*Rho

  // Equation 5.84
  val decdot = (-ADot*cos(phi)*sin(A)*cos(a) + aDot*(sin(phi)*cos(a) - cos(phi)*cos(A)*sin(a)))/cos(dec)

  // Equation 5.85
  val RAdot = wE +
    (ADot*cos(A)*cos(a) - aDot*sin(A)*sin(a) + decdot*sin(A)*cos(a)*tan(dec))/
    (cos(phi)*sin(a) - sin(phi)*cos(A)*cos(a))

  // Equations 5.69 and 5.72
  val Rhodot = mk.ndarray(mk[
    -RAdot*sin(RA)*cos(dec) - decdot*cos(RA)*sin(dec),
    RAdot*cos(RA)*cos(dec) - decdot*sin(RA)*sin(dec),
    decdot*cos(dec)
  ])

  // Equation 5.64
  val v = Rdot + rhoDot*Rho + rho*Rhodot

  return Pair(r, v)
}


