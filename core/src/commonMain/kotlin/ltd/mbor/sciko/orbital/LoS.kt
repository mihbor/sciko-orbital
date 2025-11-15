package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

fun los(rSat: MultiArray<Double, D1>, rSun: MultiArray<Double, D1>, rPlanet: Double = rEarth): Boolean {

  val theta = acos((rSat dot rSun) / rSat.norm() / rSun.norm())
  val thetaSat = acos(rPlanet / rSat.norm())
  val thetaSun = acos(rPlanet / rSun.norm())

  return thetaSat + thetaSun <= theta
}
