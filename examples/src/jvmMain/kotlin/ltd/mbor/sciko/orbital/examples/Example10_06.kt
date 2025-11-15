package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.*

/**
 * Kotlin port of MATLAB Example_10_6.m
 *
 * Integrates Gauss planetary equations (with J2 perturbation) using RKF45
 * to compute the time histories of the osculating elements.
 */
fun main() {
  mk.setEngine(KEEngineType)

  // Conversion factors
  val hours = 3600.0
  val days = 24.0 * hours

  // Constants
  val mu = muEarth          // km^3/s^2
  val RE = rEarth           // km
  val J2c = J2              // dimensionless

  // Initial orbital parameters (given)
  val rp0 = RE + 300.0      // km
  val ra0 = RE + 3062.0     // km
  val RA0 = 45.0.degrees    // rad
  val i0 = 28.0.degrees     // rad
  val w0 = 30.0.degrees     // rad
  val TA0 = 40.0.degrees    // rad

  // Initial orbital parameters (inferred)
  val e0 = (ra0 - rp0) / (ra0 + rp0)
  val h0 = sqrt(rp0 * mu * (1 + e0))
  val a0 = (rp0 + ra0) / 2.0
  val T0 = 2 * PI / sqrt(mu) * a0.pow(1.5)

  // Initial state vector of elements
  val y0 = mk.ndarray(mk[h0, e0, RA0, i0, w0, TA0])

  // Integration span and parameters
  val t0 = 0.0
  val tf = 2.0 * days

  val (tOut, yOut) = rkf45(
    t0..tf,
    y0,
    tolerance = 1e-12,
    h0 = T0 / 1000.0,
    odeFunction = { t, y -> ratesGaussJ2(t, y, mu, RE, J2c) }
  )

  // Extract element histories
  val h = yOut.map { it[0] }
  val e = yOut.map { it[1] }
  val RA = yOut.map { it[2] }
  val inc = yOut.map { it[3] }
  val w = yOut.map { it[4] }
  val TA = yOut.map { it[5] }

  val tHours = tOut.map { it / hours }

  // Plots similar to MATLAB; save to files
  plotScalar(
    Triple(tHours, RA.map { (it - RA0).toDegrees() }, "ΔΩ")
  , xLabel = "hours", yLabel = "deg").save("Example10_06_RA.png")

  plotScalar(
    Triple(tHours, w.map { (it - w0).toDegrees() }, "Δω")
  , xLabel = "hours", yLabel = "deg").save("Example10_06_w.png")

  plotScalar(
    Triple(tHours, h.map { it - h0 }, "Δh (km^2/s)")
  , xLabel = "hours", yLabel = "Δh (km^2/s)").save("Example10_06_h.png")

  plotScalar(
    Triple(tHours, e.map { it - e0 }, "Δe")
  , xLabel = "hours", yLabel = "Δe").save("Example10_06_e.png")

  plotScalar(
    Triple(tHours, inc.map { (it - i0).toDegrees() }, "Δi")
  , xLabel = "hours", yLabel = "deg").save("Example10_06_i.png")
}

private fun ratesGaussJ2(
  t: Double,
  f: MultiArray<Double, D1>,
  mu: Double,
  RE: Double,
  J2: Double,
): MultiArray<Double, D1> {
  val h = f[0]
  val e = f[1]
  val RA = f[2]
  val i = f[3]
  val w = f[4]
  val TA = f[5]

  val r = h * h / mu / (1 + e * cos(TA))
  val u = w + TA

  // Guard small eccentricity to avoid division by zero in terms with /e
  val eSafe = if (abs(e) < 1e-12) 1e-12 else e

  // Direct port of MATLAB equations (lines 127-151)
  val hdot = -1.5 * J2 * mu * RE * RE / r.pow(3) * sin(i).pow(2) * sin(2.0 * u)

  // The MATLAB file shows two expressions for edot; the latter is the refined one
  val edot = 1.5 * J2 * mu * RE * RE / h / r.pow(3) * (
    (h * h / mu / r * sin(TA) * (3 * sin(i).pow(2) * sin(u).pow(2) - 1)) -
      (sin(2.0 * u) * sin(i).pow(2) * ((2 + e * cos(TA)) * cos(TA) + e))
    )

  val TAdot = h / r.pow(2) + 1.5 * J2 * mu * RE * RE / eSafe / h / r.pow(3) * (
    (h * h / mu / r * cos(TA) * (3 * sin(i).pow(2) * sin(u).pow(2) - 1)) +
      (sin(2.0 * u) * sin(i).pow(2) * sin(TA) * (h * h / mu / r + 1))
    )

  val RAdot = -3.0 * J2 * mu * RE * RE / h / r.pow(3) * sin(u).pow(2) * cos(i)

  val idot = -0.75 * J2 * mu * RE * RE / h / r.pow(3) * sin(2.0 * u) * sin(2.0 * i)

  val wdot = 1.5 * J2 * mu * RE * RE / eSafe / h / r.pow(3) * (
    -(h * h / mu / r * cos(TA) * (3 * sin(i).pow(2) * sin(u).pow(2) - 1)) -
      (sin(2.0 * u) * sin(i).pow(2) * sin(TA) * (2 + e * cos(TA))) +
      (2 * e * cos(i).pow(2) * sin(u).pow(2))
    )

  return mk.ndarray(mk[hdot, edot, RAdot, idot, wdot, TAdot])
}
