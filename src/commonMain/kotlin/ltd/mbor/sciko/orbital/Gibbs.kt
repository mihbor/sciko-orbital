import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.muEarth
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.abs
import kotlin.math.sqrt

fun gibbs(
  R1: MultiArray<Double, D1>,
  R2: MultiArray<Double, D1>,
  R3: MultiArray<Double, D1>,
  mu: Double = muEarth
): Pair<MultiArray<Double, D1>, Boolean> {
  val tol = 1e-4
  var isError = false

  val r1 = R1.norm()
  val r2 = R2.norm()
  val r3 = R3.norm()

  val c12 = R1 cross R2
  val c23 = R2 cross R3
  val c31 = R3 cross R1

  if (abs((R1 dot c23) / r1 / c23.norm()) > tol) {
    isError = true
  }

  val N = r1*c23 + r2*c31 + r3*c12
  val D = c12 + c23 + c31
  val S = R1*(r2 - r3) + R2*(r3 - r1) + R3*(r1 - r2)

  val v2 = sqrt(mu/N.norm()/D.norm())*((D cross R2)/r2 + S)

  return Pair(v2, isError)
}