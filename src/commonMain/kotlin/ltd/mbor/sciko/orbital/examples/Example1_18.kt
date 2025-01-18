package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.rk
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.plotGrid
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.sin

fun main() {
  // Input data
  val m = 1.0
  val z = 0.03
  val wn = 1.0
  val Fo = 1.0
  val w = 0.4 * wn

  val x0 = 0.0
  val xDot0 = 0.0
  val f0 = mk.ndarray(mk[x0, xDot0])

  val t0 = 0.0
  val tf = 110.0
  val tspan = t0 to tf

  // Solve using RK1 through RK4, using the same and a larger time step for each method
  var rk = 1
  var h = 0.01
  var (t1, f1) = rk(::rates, tspan, f0, h, rk)
  h = 0.1
  var (t11, f11) = rk(::rates, tspan, f0, h, rk)

  rk = 2
  h = 0.1
  var (t2, f2) = rk(::rates, tspan, f0, h, rk)
  h = 0.5
  var (t21, f21) = rk(::rates, tspan, f0, h, rk)

  rk = 3
  h = 0.5
  var (t3, f3) = rk(::rates, tspan, f0, h, rk)
  h = 1.0
  var (t31, f31) = rk(::rates, tspan, f0, h, rk)

  rk = 4
  h = 1.0
  var (t4, f4) = rk(::rates, tspan, f0, h, rk)
  h = 2.0
  var (t41, f41) = rk(::rates, tspan, f0, h, rk)

  plotGrid(
    listOf(
      plotVectors(Triple(t1, f1.map{ it[0] }, "h = 0.01"), Triple(t11, f11.map{ it[0] }, "h = 0.1")),
      plotVectors(Triple(t2, f2.map{ it[0] }, "h = 0.1"), Triple(t21, f21.map{ it[0] }, "h = 0.5")),
      plotVectors(Triple(t3, f3.map{ it[0] }, "h = 0.5"), Triple(t31, f31.map{ it[0] }, "h = 1.0")),
      plotVectors(Triple(t4, f4.map{ it[0] }, "h = 1.0"), Triple(t41, f41.map{ it[0] }, "h = 2.0")),
    ),
    nCol = 1
  ).save("Example1_18.png")
}

fun rates(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
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

fun plotVectors(vararg histories: Triple<List<Double>, List<Double>, String>): Plot {
  val map = mapOf(
    "t" to histories.flatMap { it.first },
    "values" to histories.flatMap { it.second },
    "labels" to histories.flatMap { it.first.map { _ -> it.third } },
  )
  return map.plot {
    line {
      x("t")
      y("values")
      color("labels")
    }
  }
}