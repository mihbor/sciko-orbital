package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.`RV from R0V0 ta`
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI

val Number.degrees get() = this.toDouble() * PI/180

fun main() {

  val mu = 398600.0

  val R0 = mk.ndarray(mk[8182.4, -6865.9, 0.0])
  val V0 = mk.ndarray(mk[0.47572, 8.8116, 0.0])
  val dta = 120.degrees

  val (R, V) = `RV from R0V0 ta`(R0, V0, dta, mu)

  val r  = R.norm()
  val v  = V.norm()
  val r0 = R0.norm()
  val v0 = V0.norm()

  println("-----------------------------------------------------------")
  println("\n Example 2.13 \n")
  println("\n Initial state vector:\n")
  println("\n   r = [${R0[0]}, ${R0[1]}, ${R0[2]}] (km)")
  println("\n     magnitude = ${r0}\n")

  println("\n   v = [${V0[0]}, ${V0[1]}, ${V0[2]}] (km/s)")
  println("\n     magnitude = ${v0}")

  println("\n\n State vector after ${dta} degree change in true anomaly:\n")
  println("\n   r = [${R[0]}, ${R[1]}, ${R[2]}] (km)")
  println("\n     magnitude = ${r}\n")
  println("\n   v = [${V[0]}, ${V[1]}, ${V[2]}] (km/s)")
  println("\n     magnitude = ${v}")
  println("\n-----------------------------------------------------------\n")
}