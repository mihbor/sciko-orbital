package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.keplerU

fun main() {
  // Gravitational parameter (km^3/s^2)
  val mu = 398600.0

  // Data declaration for Example 3.6
  val ro = 10000.0
  val vro = 3.0752
  val dt = 3600.0
  val a = -19655.0

  // Pass the input data to the function keplerU, which returns x
  // (Universal Kepler's requires the reciprocal of semimajor axis)
  val x = keplerU(dt, ro, vro, 1 / a)

  // Echo the input data and output the results to the console
  println("-----------------------------------------------------")
  println(" Example 3.6")
  println(" Initial radial coordinate (km) = $ro")
  println(" Initial radial velocity (km/s) = $vro")
  println(" Elapsed time (seconds)         = $dt")
  println(" Semimajor axis (km)            = $a")
  println(" Universal anomaly (km^0.5)     = $x")
  println("-----------------------------------------------------")
}
