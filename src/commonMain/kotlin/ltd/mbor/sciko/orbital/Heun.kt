package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.abs
import kotlin.math.min

fun heun(
  odeFunction: (Double, MultiArray<Double, D1>) -> MultiArray<Double, D1>,
  tspan: ClosedRange<Double>,
  y0: MultiArray<Double, D1>,
  h: Double,
  outerFunction: (Double, MultiArray<Double, D1>) -> MultiArray<Double, D1> = { _, y -> y },
): Pair<List<Double>, List<MultiArray<Double, D1>>> {
  val tol = 1e-6
  val itermax = 100

  val t0 = tspan.start
  val tf = tspan.endInclusive
  var t = t0
  var y = y0
  var tout = listOf(t)
  var yout = listOf(y.copy())

  while (t < tf) {
    val hStep = min(h, tf - t)
    val t1 = t
    val y1 = outerFunction(t, y)
    val f1 = odeFunction(t1, y1)
    var y2 = y1 + f1 * hStep
    val t2 = t1 + hStep
    var err = tol + 1
    var iter = 0

    while (err > tol && iter <= itermax) {
      val y2p = y2
      val f2 = odeFunction(t2, y2p)
      val favg = (f1 + f2) / 2.0
      y2 = y1 + favg * hStep
      err = (y2 - y2p).map{ abs(it) }.max()!! / (y2 + Double.MIN_VALUE).map{abs(it)}.max()!!
      iter++
    }

    if (iter > itermax) {
      println("\n Maximum no. of iterations ($itermax) exceeded at time = $t in function 'heun.'\n\n")
      return Pair(tout, yout)
    }

    t += hStep
    y = y2
    tout += t
    yout += y.copy()
  }

  return Pair(tout, yout)
}