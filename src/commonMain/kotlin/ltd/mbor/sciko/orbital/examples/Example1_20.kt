import ltd.mbor.sciko.orbital.examples.plotScalar
import ltd.mbor.sciko.orbital.rkf45
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.multiplot.plotGrid
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.pow

fun main() {
  mk.setEngine(KEEngineType)
  val minutes = 60.0  // Conversion from minutes to seconds

  val x0 = 6500.0
  val v0 = 7.8
  val y0 = mk.ndarray(mk[x0, v0])
  val t0 = 0.0
  val tf = 70 * minutes

  val (t, f) = rkf45(::rates, t0..tf, y0)
  plotGrid(
    listOf(
      plotScalar(Triple(t.map{it/minutes}, f.map { it[0] }, "position, km")),
      plotScalar(Triple(t.map{it/minutes}, f.map { it[1] }, "velocity, km/s"))
    )
  ).save("Example1_20.png")
}

fun rates(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val mu = 398600.0
  val x = f[0]
  val Dx = f[1]
  val D2x = -mu / x.pow(2)
  return mk.ndarray(mk[Dx, D2x])
}
