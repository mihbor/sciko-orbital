package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.bisect
import kotlin.math.abs
import kotlin.math.pow

private val m1 = 5.974e24
private val m2 = 7.348e22
private val r12 = 3.844e5
private val p = m2 / (m1 + m2)

fun main() {

  val xl = listOf(-1.1, 0.5, 1.0)
  val xu = listOf(-0.9, 1.0, 1.5)

  val x = xl.indices.map { bisect(::f, xl[it], xu[it]) }

  output(m1, m2, r12, x)
}

fun f(z: Double): Double {
  return (1 - p) * (z + p) / abs(z + p).pow(3) + p * (z + p - 1) / abs(z + p - 1).pow(3) - z
}

fun output(m1: Double, m2: Double, r12: Double, x: List<Double>) {
  println("\n\n---------------------------------------------\n")
  println("\n For\n")
  println("\n   m1 = $m1 kg")
  println("\n   m2 = $m2 kg")
  println("\n  r12 = $r12 km\n")
  println("\n the 3 colinear Lagrange points (the roots of\n")
  println(" Equation 2.204) are:\n")
  println("\n L3: x = ${x[0] * r12} km    (f(x3) = ${f(x[0])})")
  println("\n L1: x = ${x[1] * r12} km    (f(x1) = ${f(x[1])})")
  println("\n L2: x = ${x[2] * r12} km    (f(x2) = ${f(x[2])})")
  println("\n\n---------------------------------------------\n")
}
