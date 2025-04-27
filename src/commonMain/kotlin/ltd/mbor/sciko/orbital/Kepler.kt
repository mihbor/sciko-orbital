package ltd.mbor.sciko.orbital

import kotlin.math.*

const val mu = 398600.0

fun keplerU(dt: Double, ro: Double, vro: Double, a: Double): Double {
  val error = 1e-8
  val nMax = 1000

  var x = sqrt(mu) * abs(a) * dt
  var n = 0
  var ratio = 1.0

  while (abs(ratio) > error && n <= nMax) {
    n += 1
    val C = stumpC(a * x.pow(2))
    val S = stumpS(a * x.pow(2))
    val F = ro * vro / sqrt(mu) * x.pow(2) * C + (1 - a * ro) * x.pow(3) * S + ro * x - sqrt(mu) * dt
    val dFdx = ro * vro / sqrt(mu) * x * (1 - a * x.pow(2) * S) + (1 - a * ro) * x.pow(2) * C + ro
    ratio = F / dFdx
    x -= ratio
  }

  if (n > nMax) {
    println("\n **No. iterations of Kepler's equation = $n")
    println("\n   F/dFdx                              = $ratio\n")
  }

  return x
}

fun keplerE(e: Double, M: Double): Double {
  val error = 1.0e-8
  var E = if (M < PI) M + e/2 else M - e/2
  var ratio = 1.0
  while (abs(ratio) > error) {
    ratio = (E - e*sin(E) - M)/(1 - e* cos(E))
    E = E - ratio
  }
  return E
}
