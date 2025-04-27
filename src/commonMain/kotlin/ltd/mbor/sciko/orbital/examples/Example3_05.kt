package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.keplerH

fun main() {
  // Data declaration for Example 3.5
  val e = 2.7696
  val M = 40.69

  // Pass the input data to the function keplerH, which returns F
  val F = keplerH(e, M)

  // Echo the input data and output to the console
  println("-----------------------------------------------------")
  println(" Example 3.5")
  println(" Eccentricity                 = $e")
  println(" Hyperbolic mean anomaly      = $M")
  println(" Hyperbolic eccentric anomaly = $F")
  println("-----------------------------------------------------")
}
