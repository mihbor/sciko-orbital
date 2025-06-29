package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.pow

fun main() {
  val t0 = 0.0
  val tf = 480.0

  val R1_0 = mk.ndarray(mk[0.0, 0.0, 0.0])
  val R2_0 = mk.ndarray(mk[3000.0, 0.0, 0.0])
  val V1_0 = mk.ndarray(mk[10.0, 20.0, 30.0])
  val V2_0 = mk.ndarray(mk[0.0, 40.0, 0.0])

  val y0 = R1_0 cat R2_0 cat V1_0 cat V2_0

  val (t, y) = rkf45(t0..tf, y0, odeFunction = ::rates)

  output(t, y)
}

private fun rates(t: Double, y: MultiArray<Double, D1>): MultiArray<Double, D1> {

  val m1 = 1e26
  val m2 = 1e26

  val R1 = y.slice<Double, D1, D1>(0..2)
  val R2 = y.slice<Double, D1, D1>(3..5)
  val V1 = y.slice<Double, D1, D1>(6..8)
  val V2 = y.slice<Double, D1, D1>(9..11)

  val r = (R2 - R1).norm()

  val A1 = G*m2*(R2 - R1)/r.pow(3)
  val A2 = G*m1*(R1 - R2)/r.pow(3)

  return V1 cat V2 cat A1 cat A2
}

private fun output(t: List<Double>, y: List<MultiArray<Double, D1>>) {
  // TODO: 3d plotting
}
