package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.analysis.solvers.LaguerreSolver
import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.complex.ComplexDouble
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.abs
import kotlin.math.pow

data class GaussResult(
  val r: MultiArray<Double, D1>,
  val v: MultiArray<Double, D1>,
  val rOld: MultiArray<Double, D1>,
  val vOld: NDArray<Double, D1>
)

fun gauss(
  Rho1: MultiArray<Double, D1>,
  Rho2: MultiArray<Double, D1>,
  Rho3: MultiArray<Double, D1>,
  R1: MultiArray<Double, D1>,
  R2: MultiArray<Double, D1>,
  R3: MultiArray<Double, D1>,
  t1: Double,
  t2: Double,
  t3: Double,
  mu: Double = muEarth
): GaussResult {
  val tau1 = t1 - t2
  val tau3 = t3 - t2
  val tau = tau3 - tau1

  val p1 = Rho2 cross Rho3
  val p2 = Rho1 cross Rho3
  val p3 = Rho1 cross Rho2

  val Do = Rho1 dot p1

  val D = mk.ndarray(
    doubleArrayOf(
      R1 dot p1, R1 dot p2, R1 dot p3,
      R2 dot p1, R2 dot p2, R2 dot p3,
      R3 dot p1, R3 dot p2, R3 dot p3
    ),
    3, 3
  )

  val E = R2 dot Rho2

  val A = 1.0 / Do * (-D[0, 1] * tau3 / tau + D[1, 1] + D[2, 1] * tau1 / tau)
  val B = 1.0 / 6.0 / Do * (
    D[0, 1] * (tau3 * tau3 - tau * tau) * tau3 / tau +
      D[2, 1] * (tau * tau - tau1 * tau1) * tau1 / tau
    )

  val a = -(A * A + 2 * A * E + R2.norm().pow(2))
  val b = -2 * mu * B * (A + E)
  val c = -(mu * B).pow(2)

  // Find roots of 8th order polynomial: x^8 + a x^6 + b x^3 + c = 0
  val coeffs = mk.ndarray(mk[1.0, 0.0, a, 0.0, 0.0, b, 0.0, 0.0, c])
  val roots = roots(coeffs, mu)
  val x = posRoot(roots)

  val f1 = 1 - 0.5 * mu * tau1 * tau1 / x.pow(3)
  val f3 = 1 - 0.5 * mu * tau3 * tau3 / x.pow(3)
  val g1 = tau1 - 1.0 / 6.0 * mu * (tau1 / x).pow(3)
  val g3 = tau3 - 1.0 / 6.0 * mu * (tau3 / x).pow(3)

  val rho2 = A + mu * B / x.pow(3)
  val rho1 = 1.0 / Do * (
    (6 * (D[2, 0] * tau1 / tau3 + D[1, 0] * tau / tau3) * x.pow(3) +
      mu * D[2, 0] * (tau * tau - tau1 * tau1) * tau1 / tau3) /
      (6 * x.pow(3) + mu * (tau * tau - tau3 * tau3)) - D[0, 0]
    )
  val rho3 = 1.0 / Do * (
    (6 * (D[0, 2] * tau3 / tau1 - D[1, 2] * tau / tau1) * x.pow(3) +
      mu * D[0, 2] * (tau * tau - tau3 * tau3) * tau3 / tau1) /
      (6 * x.pow(3) + mu * (tau * tau - tau1 * tau1)) - D[2, 2]
    )

  var r1 = R1 + rho1 * Rho1
  var r2 = R2 + rho2 * Rho2
  var r3 = R3 + rho3 * Rho3

  var v2 = (-f3 * r1 + f1 * r3) / (f1 * g3 - f3 * g1)

  val rOld = r2
  val vOld = v2

  // Iterative improvement (Algorithm 5.6)
  var rho1Old = rho1
  var rho2Old = rho2
  var rho3Old = rho3
  var diff1 = 1.0
  var diff2 = 1.0
  var diff3 = 1.0
  var n = 0
  val nmax = 1000
  val tol = 1e-8

  while ((diff1 > tol || diff2 > tol || diff3 > tol) && n < nmax) {
    n += 1
    val ro = r2.norm()
    val vo = v2.norm()
    val vro = (v2 dot r2) / ro
    val aRecip = 2 / ro - vo * vo / mu

    val x1 = keplerU(tau1, ro, vro, aRecip, mu)
    val x3 = keplerU(tau3, ro, vro, aRecip, mu)

    val (ff1, gg1) = fg(x1, tau1, ro, aRecip, mu)
    val (ff3, gg3) = fg(x3, tau3, ro, aRecip, mu)

    val f1New = (f1 + ff1) / 2
    val f3New = (f3 + ff3) / 2
    val g1New = (g1 + gg1) / 2
    val g3New = (g3 + gg3) / 2

    val c1 = g3New / (f1New * g3New - f3New * g1New)
    val c3 = -g1New / (f1New * g3New - f3New * g1New)

    val rho1New = 1.0 / Do * (-D[0, 0] + 1 / c1 * D[1, 0] - c3 / c1 * D[2, 0])
    val rho2New = 1.0 / Do * (-c1 * D[0, 1] + D[1, 1] - c3 * D[2, 1])
    val rho3New = 1.0 / Do * (-c1 / c3 * D[0, 2] + 1 / c3 * D[1, 2] - D[2, 2])

    r1 = R1 + rho1New * Rho1
    r2 = R2 + rho2New * Rho2
    r3 = R3 + rho3New * Rho3

    v2 = (-f3New * r1 + f1New * r3) / (f1New * g3New - f3New * g1New)

    diff1 = abs(rho1New - rho1Old)
    diff2 = abs(rho2New - rho2Old)
    diff3 = abs(rho3New - rho3Old)

    rho1Old = rho1New
    rho2Old = rho2New
    rho3Old = rho3New
  }

  if (n >= nmax) {
    println("** Number of iterations exceeds $nmax")
  }

  return GaussResult(r2, v2, rOld, vOld)
}

// Find the positive real root from a list of roots
fun posRoot(roots: MultiArray<ComplexDouble, D1>): Double {
  val posroots = roots.filter { it.re > 0 && it.im == 0.0 }
  if (posroots.isEmpty()) error("No positive real roots found.")
  return posroots.first().re
}

// Placeholder for polynomial root finding (replace with a proper implementation/library)
fun roots(coeffs: MultiArray<Double, D1>, mu: Double): MultiArray<ComplexDouble, D1> {
  return LaguerreSolver().solveAllComplex(coeffs.reversed(), mu)
}
