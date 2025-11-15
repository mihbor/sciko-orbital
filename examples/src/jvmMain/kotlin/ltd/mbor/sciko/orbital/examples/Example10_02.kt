package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Kotlin port of MATLAB Example_10_02.m
 *
 * Solves Example 10.2 using Encke's method with J2 perturbation integrated
 * by RKF45 over fixed rectification steps. Produces plots of selected
 * osculating elements variations.
 */
fun main() {
  mk.setEngine(KEEngineType)

  // Conversion factors
  val hours = 3600.0
  val days = 24.0 * hours

  // Constants
  val mu = muEarth       // km^3/s^2
  val RE = rEarth        // km
  val J2c = J2           // dimensionless

  // Initial orbital parameters (given)
  val zp0 = 300.0        // km
  val za0 = 3062.0       // km
  val RA0 = 45.0.degrees   // rad
  val i0 = 28.0.degrees    // rad
  val w0 = 30.0.degrees    // rad
  val TA0 = 40.0.degrees   // rad

  // Initial orbital parameters (inferred)
  val rp0 = RE + zp0
  val ra0 = RE + za0
  val e0 = (ra0 - rp0) / (ra0 + rp0)
  val a0 = (ra0 + rp0) / 2.0
  val h0 = sqrt(rp0 * mu * (1 + e0))
  val T0 = 2 * PI / sqrt(mu) * a0.pow(1.5)

  // Initial state vector from COE
  val (R0init, V0init) = svFromCoe(mk.ndarray(mk[h0, e0, RA0, i0, w0, TA0]), mu)

  // Encke integration parameters
  val tStart = 0.0
  val tFinal = 2.0 * days
  val delT = T0 / 100.0

  // Storage for times and states (R,V)
  val tSave = mutableListOf(tStart)
  val ySave = mutableListOf(mk.ndarray(mk[
    R0init[0], R0init[1], R0init[2],
    V0init[0], V0init[1], V0init[2]
  ]))

  // Mutable "current osculating base" state
  var R0 = R0init
  var V0 = V0init
  var t0 = tStart

  var t = t0 + delT
  while (t <= tFinal + delT / 2.0) {
    // Freeze base values for this rectification interval
    val R0s = R0
    val V0s = V0
    val t0s = t0

    // Deviation initial state
    val dy0 = mk.ndarray(mk[0.0, 0.0, 0.0, 0.0, 0.0, 0.0])

    // Integrate deviation dynamics over [t0s, t]
    val (_, yOut) = rkf45(
      t0s..t,
      dy0,
      tolerance = 1e-15,
      h0 = delT / 10.0,
    ) { ti, f -> ratesEnckeJ2(ti, f, R0s, V0s, t0s, mu, RE, J2c) }

    val z = yOut.last()

    // Compute osculating state at time t on reference (unperturbed) orbit
    val (Rosc, Vosc) = RVfromR0V0(R0s, V0s, t - t0s, mu)

    // Rectify: update base state
    R0 = Rosc + mk.ndarray(mk[z[0], z[1], z[2]])
    V0 = Vosc + mk.ndarray(mk[z[3], z[4], z[5]])
    t0 = t

    // Save
    tSave += t
    ySave += mk.ndarray(mk[
      R0[0], R0[1], R0[2],
      V0[0], V0[1], V0[2]
    ])

    // Next step
    t += delT
  }

  // Post-process: compute COEs at each saved state
  val nTimes = tSave.size
  val h = DoubleArray(nTimes)
  val e = DoubleArray(nTimes)
  val RA = DoubleArray(nTimes)
  val inc = DoubleArray(nTimes)
  val w = DoubleArray(nTimes)
  val TA = DoubleArray(nTimes)

  for (j in 0 until nTimes) {
    val y = ySave[j]
    val R = mk.ndarray(mk[y[0], y[1], y[2]])
    val V = mk.ndarray(mk[y[3], y[4], y[5]])
    val coe = coeFromSV(R, V, mu)
    h[j] = coe[0]
    e[j] = coe[1]
    RA[j] = coe[2]
    inc[j] = coe[3]
    w[j] = coe[4]
    TA[j] = coe[5]
  }

  val tHours = tSave.map { it / hours }

  // Plots similar to MATLAB (saved as separate images)
  plotScalar(
    Triple(tHours, RA.map { (it - RA0).toDegrees() }, "ΔΩ")
  , xLabel = "hours", yLabel = "deg").save("Example10_02_RA.png")

  plotScalar(
    Triple(tHours, w.map { (it - w0).toDegrees() }, "Δω")
  , xLabel = "hours", yLabel = "deg").save("Example10_02_w.png")

  plotScalar(
    Triple(tHours, h.map { it - h0 }, "Δh (km^2/s)")
  , xLabel = "hours", yLabel = "Δh (km^2/s)").save("Example10_02_h.png")

  plotScalar(
    Triple(tHours, e.map { it - e0 }, "Δe")
  , xLabel = "hours", yLabel = "Δe").save("Example10_02_e.png")

  plotScalar(
    Triple(tHours, inc.map { (it - i0).toDegrees() }, "Δi")
  , xLabel = "hours", yLabel = "deg").save("Example10_02_i.png")
}

private fun ratesEnckeJ2(
  t: Double,
  f: MultiArray<Double, D1>,
  R0: MultiArray<Double, D1>,
  V0: MultiArray<Double, D1>,
  t0: Double,
  mu: Double,
  RE: Double,
  J2: Double,
): MultiArray<Double, D1> {
  val delR = mk.ndarray(mk[f[0], f[1], f[2]])
  val delV = mk.ndarray(mk[f[3], f[4], f[5]])

  // Osculating state on the reference orbit at time t
  val (Rosc, Vosc) = RVfromR0V0(R0, V0, t - t0, mu)

  // Perturbed state components
  val Rpp = Rosc + delR
  val Vpp = Vosc + delV
  val rosc = Rosc.norm()
  val rpp = Rpp.norm()

  // J2 perturbing acceleration (Equation analogous to MATLAB 12.30)
  val xx = Rpp[0]; val yy = Rpp[1]; val zz = Rpp[2]
  val fac = 1.5 * J2 * (mu / rpp.pow(2)) * (RE / rpp).pow(2)
  val rz = zz / rpp
  val rx = xx / rpp
  val ry = yy / rpp
  val ap = mk.ndarray(mk[
    -(1 - 5 * rz.pow(2)) * rx,
    -(1 - 5 * rz.pow(2)) * ry,
    -(3 - 5 * rz.pow(2)) * rz
  ]) * fac

  // Total perturbing acceleration for Encke's equation (Equation 12.7 form)
  val F = 1.0 - (rosc / rpp).pow(3)
  val delA = -mu / rosc.pow(3) * (delR - F * Rpp) + ap

  return delV cat delA
}
