package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.keplerE

fun main() {
  // Data declaration for Example 3.2
  val e = 0.37255
  val M = 3.6029

  // Pass the input data to the function keplerE, which returns E
  val E = keplerE(e, M)

  // Echo the input data and output to the console
  println("-----------------------------------------------------")
  println(" Example 3.2")
  println(" Eccentricity                = $e")
  println(" Mean anomaly (radians)      = $M")
  println(" Eccentric anomaly (radians) = $E")
  println("-----------------------------------------------------")
}
