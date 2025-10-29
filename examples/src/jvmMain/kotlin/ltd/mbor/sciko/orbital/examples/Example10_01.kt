package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.pow

/**
 * Kotlin port of MATLAB Example_10_01.m
 *
 * Integrates orbital motion with atmospheric drag using RKF45 and the
 * USSA76-based `atmosphere(z)` density model. Terminates when the
 * altitude decreases to 100 km.
 */
fun main() {
  mk.setEngine(KEEngineType)

  // Conversion factors
  val days = 24.0 * 3600.0
  val deg = PI / 180.0

  // Constants
  val mu = muEarth // km^3/s^2
  val RE = rEarth  // km
  val wE = mk.ndarray(mk[0.0, 0.0, 7.2921159e-5]) // rad/s

  // Satellite data
  val CD = 2.2            // Drag coefficient
  val m = 100.0           // Mass (kg)
  val A = PI / 4.0 * (1.0.pow(2.0)) // Frontal area (m^2) for 1 m diameter

  // Initial orbital parameters (given)
  val rp = RE + 215.0 // km
  val ra = RE + 939.0 // km
  val RA = 339.94 * deg
  val i = 65.1 * deg
  val w = 58.0 * deg
  val TA = 332.0 * deg

  // Initial orbital parameters (inferred)
  val e = (ra - rp) / (ra + rp)
  val a = (rp + ra) / 2.0
  val h = kotlin.math.sqrt(mu * a * (1 - e * e))
  val T = 2 * PI / kotlin.math.sqrt(mu) * a.pow(1.5)

  // Initial state vector from classical orbital elements
  val (R0, V0) = svFromCoe(mk.ndarray(mk[h, e, RA, i, w, TA]), mu)
  val y0 = mk.ndarray(mk[
    R0[0], R0[1], R0[2],
    V0[0], V0[1], V0[2]
  ])

  // Time span and integration settings
  val t0 = 0.0
  val tf = 120.0 * days
  val tolerance = 1e-10
  val initialStep = T / 10000.0

  // Terminate when altitude <= 100 km (approximate downward crossing)
  val terminate = TerminateFunction { _, y, _ ->
    val rmag = mk.ndarray(mk[y[0], y[1], y[2]]).norm()
    val alt = rmag - RE
    alt <= 100.0
  }

  val (tOut, yOut) = rkf45(
    t0..tf,
    y0,
    tolerance = tolerance,
    initialStep = initialStep,
    terminateFunction = terminate,
  ) { t, y -> rates(t, y, mu, RE, wE, CD, m, A) }

  // Simple summary output similar in spirit (no plotting)
  val r0mag = R0.norm()
  val alt0 = r0mag - RE
  val rEnd = mk.ndarray(mk[yOut.last()[0], yOut.last()[1], yOut.last()[2]])
  val altEnd = rEnd.norm() - RE

  printf("\n\n%s\n\n", "Example 10.1 — Atmospheric drag with RKF45")
  printf("Initial altitude = %g km\n", alt0)
  printf("Final altitude   = %g km\n", altEnd)
  printf("Integration steps = %d\n", tOut.size)
  printf("Integration time  = %.3f days\n", tOut.last() / days)

  // Build altitude time series
  val altitudes = yOut.map { state ->
    val r = mk.ndarray(mk[state[0], state[1], state[2]]).norm()
    r - RE
  }
  val tDays = tOut.map { it / days }

  // Detect local extrema for apogee (max) and perigee (min)
  val apogeeT = mutableListOf<Double>()
  val apogeeAlt = mutableListOf<Double>()
  val perigeeT = mutableListOf<Double>()
  val perigeeAlt = mutableListOf<Double>()
  for (k in 1 until altitudes.size - 1) {
    val aPrev = altitudes[k - 1]
    val aCurr = altitudes[k]
    val aNext = altitudes[k + 1]
    if (aCurr > aPrev && aCurr > aNext) {
      apogeeT += tDays[k]
      apogeeAlt += aCurr
    }
    if (aCurr < aPrev && aCurr < aNext) {
      perigeeT += tDays[k]
      perigeeAlt += aCurr
    }
  }
  // Match MATLAB behavior: drop first apogee point in plot by setting to NaN
  if (apogeeAlt.isNotEmpty()) apogeeAlt[0] = Double.NaN

  // Plot perigee (red) and apogee (blue) history on the same figure
  plotScalar(
    Triple(apogeeT, apogeeAlt, "apogee"),
    Triple(perigeeT, perigeeAlt, "perigee"),
    xLabel = "Time (days)",
    yLabel = "Altitude (km)"
  ).save("Example10_01.png")
}

private fun rates(
  t: Double,
  f: MultiArray<Double, D1>,
  mu: Double,
  RE: Double,
  wE: MultiArray<Double, D1>,
  CD: Double,
  m: Double,
  A: Double,
): MultiArray<Double, D1> {
  val R = mk.ndarray(mk[f[0], f[1], f[2]]) // km
  val r = R.norm() // km
  val alt = r - RE // km
  val rho = atmosphere(alt) // kg/m^3

  val V = mk.ndarray(mk[f[3], f[4], f[5]]) // km/s
  val Vrel = V - (wE cross R) // km/s
  val vrel = Vrel.norm() // km/s
  val uv = Vrel / (vrel + Double.MIN_VALUE) // unit vector

  // Drag acceleration (m/s^2), convert to km/s^2 at the end
  val ap_mps2 = -CD * A / m * rho * (1000.0 * vrel).pow(2) / 2.0 * uv // m/s^2
  val a0 = -mu * R / r.pow(3) // km/s^2
  val a = a0 + ap_mps2 / 1000.0 // km/s^2

  return mk.ndarray(mk[
    V[0], V[1], V[2],
    a[0], a[1], a[2]
  ])
}

private fun printf(format: String, vararg args: Any?) {
  print(String.format(format, *args))
}
