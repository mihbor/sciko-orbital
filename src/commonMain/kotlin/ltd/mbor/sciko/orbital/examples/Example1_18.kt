package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.rk
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.plotGrid
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.sin

fun main() {
  mk.setEngine(KEEngineType)

  val x0 = 0.0
  val xDot0 = 0.0
  val f0 = mk.ndarray(mk[x0, xDot0])

  val t0 = 0.0
  val tf = 110.0
  val tspan = t0..tf

  // Solve using RK1 through RK4, using the same and a larger time step for each method
  var rk = 1
  var h = 0.01
  var (t1, f1) = rk(tspan, f0, h, rk, odeFunction = ::rates)
  h = 0.1
  var (t11, f11) = rk(tspan, f0, h, rk, odeFunction = ::rates)

  rk = 2
  h = 0.1
  var (t2, f2) = rk(tspan, f0, h, rk, odeFunction = ::rates)
  h = 0.5
  var (t21, f21) = rk(tspan, f0, h, rk, odeFunction = ::rates)

  rk = 3
  h = 0.5
  var (t3, f3) = rk(tspan, f0, h, rk, odeFunction = ::rates)
  h = 1.0
  var (t31, f31) = rk(tspan, f0, h, rk, odeFunction = ::rates)

  rk = 4
  h = 1.0
  var (t4, f4) = rk(tspan, f0, h, rk, odeFunction = ::rates)
  h = 2.0
  var (t41, f41) = rk(tspan, f0, h, rk, odeFunction = ::rates)

  plotGrid(
    listOf(
      plotScalar(Triple(t1, f1.map{ it[0] }, "h = 0.01"), Triple(t11, f11.map{ it[0] }, "h = 0.1")),
      plotScalar(Triple(t2, f2.map{ it[0] }, "h = 0.1"), Triple(t21, f21.map{ it[0] }, "h = 0.5")),
      plotScalar(Triple(t3, f3.map{ it[0] }, "h = 0.5"), Triple(t31, f31.map{ it[0] }, "h = 1.0")),
      plotScalar(Triple(t4, f4.map{ it[0] }, "h = 1.0"), Triple(t41, f41.map{ it[0] }, "h = 2.0")),
    ),
    nCol = 1
  ).save("Example1_18.png")
}

private fun rates(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val m = 1.0
  val z = 0.03
  val wn = 1.0
  val Fo = 1.0
  val w = 0.4 * wn

  val x = f[0]
  val Dx = f[1]
  val D2x = Fo / m * sin(w * t) - 2 * z * wn * Dx - wn * wn * x
  return mk.ndarray(mk[Dx, D2x])
}
