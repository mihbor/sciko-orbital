package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.heun
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get

fun main() {
  // System properties
  val m = 1.0
  val z = 0.03
  val wn = 1.0
  val Fo = 1.0
  val w = 0.4 * wn

  // Time range
  val t0 = 0.0
  val tf = 110.0
  val tspan = t0..tf

  // Initial conditions
  val x0 = 0.0
  val Dx0 = 0.0
  val f0 = mk.ndarray(mk[x0, Dx0])

  // Calculate and plot the solution for h = 1.0
  var h = 1.0
  val (t1, f1) = heun(::rates, tspan, f0, h)

  // Calculate and plot the solution for h = 0.1
  h = 0.1
  val (t2, f2) = heun(::rates, tspan, f0, h)

  // Plot the results
  plotScalar(Triple(t1.toList(), f1.map{ it[0] }, "h = 1.0"), Triple(t2.toList(), f2.map{ it[0] }, "h = 0.1"))
    .save("Example1_19.png")
}

private fun rates(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val m = 1.0
  val z = 0.03
  val wn = 1.0
  val Fo = 1.0
  val w = 0.4 * wn

  val x = f[0]
  val Dx = f[1]
  val D2x = Fo / m * kotlin.math.sin(w * t) - 2 * z * wn * Dx - wn * wn * x
  return mk.ndarray(mk[Dx, D2x])
}