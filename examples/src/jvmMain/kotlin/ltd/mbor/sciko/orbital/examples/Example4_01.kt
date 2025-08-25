package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.RAandDecFromR
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get

fun main() {
  val r = mk.ndarray(mk[-5368.0, -1784.0, 3691.0])
  val (ra, dec) = RAandDecFromR(r)

  println("\n -----------------------------------------------------")
  println("\n Example 4.1")
  println("\n r             = [${r[0]}  ${r[1]}  ${r[2]}] (km)")
  println(" right ascension = $ra deg")
  println(" declination     = $dec deg")
  println("\n -----------------------------------------------------")
}
