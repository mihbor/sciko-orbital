import Trajectory.PROGRADE
import Trajectory.RETROGRADE
import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.pow
import ltd.mbor.sciko.orbital.sqrt
import ltd.mbor.sciko.orbital.stumpC
import ltd.mbor.sciko.orbital.stumpS
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.complex.ComplexDouble
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.*

enum class Trajectory {
  PROGRADE,
  RETROGRADE
}

fun lambert(
  R1: MultiArray<Double, D1>,
  R2: MultiArray<Double, D1>,
  t: Double,
  traj: Trajectory = PROGRADE,
  mu: Double = muEarth,
): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val r1 = R1.norm()
  val r2 = R2.norm()
  val c12 = R1 cross R2
  var theta = acos((R1 dot R2) / r1 / r2)

  when (traj) {
    PROGRADE -> if (c12[2] <= 0.0) theta = 2*Math.PI - theta
    RETROGRADE -> if (c12[2] >= 0.0) theta = 2*Math.PI - theta
  }

  val A = sin(theta) * sqrt(r1 * r2 / (1 - cos(theta)))

  // Find initial z where F(z, t) changes sign
  var z = -100.0
  while (F(z, t, r1, r2, A) < 0) {
    z += 0.1
  }

  val tol = 1e-8
  val nmax = 5000
  var ratio: Double
  var n = 0
  do {
    n += 1
    ratio = F(z, t, r1, r2, A) / dFdz(z, r1, r2, A)
    z -= ratio
  } while (abs(ratio) > tol && n <= nmax)

  if (n >= nmax) {
    println("** Number of iterations exceeds $nmax")
  }

  val y = y(z, r1, r2, A)
  val f = 1 - y / r1
  val g = A * sqrt(y / mu)
  val gdot = 1 - y / r2

  val V1 = (R2 - f * R1) / g
  val V2 = (gdot * R2 - R1) / g

  return Pair(V1, V2)
}

private fun y(z: Double, r1: Double, r2: Double, A: Double): Double =
  r1 + r2 + A * (z * S(z) - 1) / sqrt(C(z))

private fun F(
  z: Double,
  t: Double,
  r1: Double,
  r2: Double,
  A: Double,
  mu: Double = muEarth,
): Double {
  val y = ComplexDouble(re = y(z, r1, r2, A))
  return ((y/C(z)).pow(1.5)*S(z) + sqrt(y)*A - sqrt(mu)*t).re
}

private fun dFdz(z: Double, r1: Double, r2: Double, A: Double): Double {
  val y = y(z, r1, r2, A)
  val C = C(z)
  val S = S(z)
  return if (z == 0.0) {
    sqrt(2.0) / 40.0 * y.pow(1.5) + A / 8.0 * (sqrt(y) + A * sqrt(1.0 / 2.0 / y))
  } else {
    (y / C).pow(1.5) * (0.5 / z * (C - 1.5 * S / C) + 0.75 * S.pow(2) / C) +
      A / 8.0 * (3.0 * S / C * sqrt(y) + A * sqrt(C / y))
  }
}

fun C(z: Double) = stumpC(z)

fun S(z: Double) = stumpS(z)
