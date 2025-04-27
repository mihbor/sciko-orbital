package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.pow

fun `RV from R0V0`(R0: MultiArray<Double, D1>, V0: MultiArray<Double, D1>, t: Double, mu: Double): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val r0 = R0.norm()
  val v0 = V0.norm()
  val vr0 = (R0 dot V0) / r0
  val alpha = 2 / r0 - v0.pow(2) / mu
  val x = keplerU(t, r0, vr0, alpha)
  val (f, g) = `f and g`(x, t, r0, alpha, mu)
  val R = f * R0 + g * V0
  val r = R.norm()
  val (fDot, gDot) = `fDot and gDot`(x, r, r0, alpha, mu)
  val V = fDot * R0 + gDot * V0
  return R to V
}

fun `RV from R0V0 ta`(R0: MultiArray<Double, D1>, V0: MultiArray<Double, D1>, dta: Double, mu: Double): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val (f, g) = `f and g ta`(R0, V0, dta, mu)
  val (fDot, gDot) = `fDot and gDot ta`(R0, V0, dta, mu)
  val R = f * R0 + g * V0
  val V = fDot * R0 + gDot * V0
  return R to V
}
