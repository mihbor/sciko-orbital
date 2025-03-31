package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

private val a = mk.ndarray(mk[0.0, 1.0 / 4, 3.0 / 8, 12.0 / 13, 1.0, 1.0 / 2])
private val b = mk.ndarray(mk[
  mk[          0.0,            0.0,            0.0,           0.0,        0.0],
  mk[      1.0 / 4,            0.0,            0.0,           0.0,        0.0],
  mk[     3.0 / 32,       9.0 / 32,            0.0,           0.0,        0.0],
  mk[1932.0 / 2197, -7200.0 / 2197,  7296.0 / 2197,           0.0,        0.0],
  mk[  439.0 / 216,           -8.0,   3680.0 / 513, -845.0 / 4104,        0.0],
  mk[    -8.0 / 27,            2.0, -3544.0 / 2565, 1859.0 / 4104, -11.0 / 40]
])
private val c4 = mk.ndarray(mk[25.0 / 216, 0.0,  1408.0 / 2565,   2197.0 / 4104,  -1.0 / 5,      0.0])
private val c5 = mk.ndarray(mk[16.0 / 135, 0.0, 6656.0 / 12825, 28561.0 / 56430, -9.0 / 50, 2.0 / 55])

fun rkf45(
  tspan: ClosedRange<Double>,
  y0: MultiArray<Double, D1>,
  tolerance: Double = 1e-8,
  outerFunction: (Double, MultiArray<Double, D1>) -> MultiArray<Double, D1> = { _, y -> y },
  odeFunction: (Double, MultiArray<Double, D1>) -> MultiArray<Double, D1>,
): Pair<List<Double>, List<MultiArray<Double, D1>>> {

  val t0 = tspan.start
  val tf = tspan.endInclusive
  var t = t0
  var y = y0
  val tOut = mutableListOf(t)
  val yOut = mutableListOf(y)
  var h = (tf - t0) / 100

  while (t < tf) {
    val hmin = 16 * Double.MIN_VALUE
    val ti = t
    val yi = outerFunction(t, y)
    val f = mk.zeros<Double>(6, y0.size)

    for (i in 0..<6) {
      val tInner = ti + a[i] * h
      var yInner = yi
      for (j in 0..<i) {
        yInner += h * b[i, j] * f[j]
      }
      f[i] = odeFunction(tInner, yInner)
    }

    val te = h * (f.transpose() dot (c4 - c5))
    val teMax = te.map { abs(it) }.max()!!
    val ymax = y.map { abs(it) }.max()!!
    val teAllowed = tolerance * max(ymax, 1.0)
    val delta = (teAllowed / (teMax + Double.MIN_VALUE)).pow(1.0 / 5)

    if (teMax <= teAllowed) {
      h = min(h, tf - t)
      t += h
      y += h * (f.transpose() dot c5)
      tOut.add(t)
      yOut.add(y)
    }

    h = min(delta * h, 4 * h)
    if (h < hmin) {
      println("\n\n Warning: Step size fell below its minimum allowable value ($hmin) at time $t.\n\n")
      return Pair(tOut, yOut)
    }
  }

  return Pair(tOut, yOut)
}
