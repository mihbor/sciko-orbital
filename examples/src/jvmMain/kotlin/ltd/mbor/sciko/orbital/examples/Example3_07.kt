package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.RVfromR0V0
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get

fun main() {
  // Gravitational parameter (km^3/s^2)
  val mu = 398600.0

  // Data declaration for Example 3.7
  val R0 = mk.ndarray(mk[7000.0, -12124.0, 0.0])
  val V0 = mk.ndarray(mk[2.6679, 4.6210, 0.0])
  val t = 3600.0

  // Algorithm 3.4: Compute the final state vector (R, V)
  val (R, V) = RVfromR0V0(R0, V0, t, mu)

  // Echo the input data and output the results to the console
  println("-----------------------------------------------------")
  println(" Example 3.7")
  println(" Initial position vector (km):")
  println("   r0 = (${R0[0]}, ${R0[1]}, ${R0[2]})")
  println(" Initial velocity vector (km/s):")
  println("   v0 = (${V0[0]}, ${V0[1]}, ${V0[2]})")
  println("\n Elapsed time = $t s")
  println(" Final position vector (km):")
  println("   r = (${R[0]}, ${R[1]}, ${R[2]})")
  println(" Final velocity vector (km/s):")
  println("   v = (${V[0]}, ${V[1]}, ${V[2]})")
  println("-----------------------------------------------------")
}
