package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.pow

fun rvaRelative(
  rA: MultiArray<Double, D1>,
  vA: MultiArray<Double, D1>,
  rB: MultiArray<Double, D1>,
  vB: MultiArray<Double, D1>,
  mu: Double = muEarth
): Triple<NDArray<Double, D1>, NDArray<Double, D1>, NDArray<Double, D1>> {
  // Calculate the vector hA
  val hA = rA cross vA

  // QXx maps ECI components into the RIC frame. Keep this convention shared
  // with ricToEciRotation, whose columns map RIC components into ECI.
  val QXx = ricToEciRotation(rA.cat(vA)).transpose()

  // Calculate Omega and Omega_dot
  val Omega = hA/rA.norm().pow(2.0)
  val OmegaDot = -2.0*(rA dot vA)/rA.norm().pow(2.0)*Omega

  // Calculate the accelerations aA and aB
  val aA = -mu*rA/rA.norm().pow(3.0)
  val aB = -mu*rB/rB.norm().pow(3.0)

  // Calculate r_rel
  val rRel = rB - rA

  // Calculate v_rel
  val vRel = vB - vA - (Omega cross rRel)

  // Calculate a_rel
  val aRel = aB - aA - (OmegaDot cross rRel) - (Omega cross (Omega cross rRel)) - 2.0*(Omega cross vRel)

  // Calculate r_rel_x, v_rel_x, and a_rel_x
  val rRelX = QXx dot rRel
  val vRelX = QXx dot vRel
  val aRelX = QXx dot aRel

  return Triple(rRelX, vRelX, aRelX)
}
