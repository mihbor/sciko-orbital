package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.rkf45
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.*

/**
 * Kotlin port of MATLAB Example_13_03.m
 *
 * Numerically integrates Equations 13.6–13.8 for a gravity turn trajectory
 * using RKF45. Prints a summary comparable to the original MATLAB output.
 */
fun main() {
  mk.setEngine(KEEngineType)

  // Constants and settings (SI units unless noted)
  val deg = PI / 180.0              // rad/deg
  val g0 = 9.81                     // m/s^2 (sea-level gravity)
  val Re = 6378e3                   // m (Earth radius)
  val hscale = 7.5e3                // m (density scale height)
  val rho0 = 1.225                  // kg/m^3 (sea-level density)

  val diam = (196.85 / 12.0) * 0.3048      // m (vehicle diameter)
  val A = PI / 4.0 * diam.pow(2.0)         // m^2 (frontal area)
  val CD = 0.5                              // drag coefficient (assumed const)
  val m0 = 149912.0 * 0.4536                // kg (lift-off mass)
  val n = 7.0                               // mass ratio
  val T2W = 1.4                             // thrust-to-weight ratio
  val Isp = 390.0                           // s (specific impulse)

  val mfinal = m0 / n                       // kg (burnout mass)
  val Thrust = T2W * m0 * g0                // N (rocket thrust)
  val mDot = Thrust / Isp / g0              // kg/s (propellant mass flow)
  val mprop = m0 - mfinal                   // kg (propellant mass)
  val tburn = mprop / mDot                  // s (burn time)
  val hturn = 130.0                         // m (pitchover altitude)

  // Integration span
  val t0 = 0.0
  val tf = tburn

  // Initial conditions: [v, gamma, x, h, vD, vG]
  val v0 = 0.0                 // m/s
  val gamma0 = 89.85 * deg     // rad
  val x0 = 0.0                 // m
  val h0 = 0.0                 // m
  val vD0 = 0.0                // m/s (loss due to drag accumulates negative)
  val vG0 = 0.0                // m/s (loss due to gravity accumulates negative)

  val f0 = mk.ndarray(mk[
    v0, gamma0, x0, h0, vD0, vG0
  ])

  // ODE right-hand side (rates)
  fun rates(t: Double, y: MultiArray<Double, D1>): MultiArray<Double, D1> {
    val v = y[0]       // m/s
    val gamma = y[1]   // rad
    val x = y[2]       // m (unused directly in dynamics)
    val h = y[3]       // m

    // Current mass and thrust (zero after burnout)
    val (m, T) = if (t < tburn) {
      val mNow = m0 - mDot * t
      Pair(mNow, Thrust)
    } else {
      val mNow = m0 - mDot * tburn
      Pair(mNow, 0.0)
    }

    val g = g0 / (1.0 + h / Re).pow(2.0)               // m/s^2
    val rho = rho0 * exp(-h / hscale)                   // kg/m^3
    val D = 0.5 * rho * v * v * A * CD                  // N (drag)

    val vDot: Double
    val gammaDot: Double
    val xDot: Double
    val hDot: Double
    val vGdot: Double

    if (h <= hturn) {
      // Vertical flight until pitchover altitude
      gammaDot = 0.0
      vDot = T / m - D / m - g
      xDot = 0.0
      hDot = v
      vGdot = -g
    } else {
      // Gravity turn
      vDot = T / m - D / m - g * sin(gamma)
      gammaDot = -1.0 / (v + Double.MIN_VALUE) * (g - v * v / (Re + h)) * cos(gamma)
      xDot = Re / (Re + h) * v * cos(gamma)
      hDot = v * sin(gamma)
      vGdot = -g * sin(gamma)
    }

    val vDdot = -D / m

    return mk.ndarray(mk[
      vDot, gammaDot, xDot, hDot, vDdot, vGdot
    ])
  }

  // Integrate
  val (t, f) = rkf45(t0..tf, f0, tolerance = 1e-8, h0 = (tf - t0) / 2000.0) { tNow, y -> rates(tNow, y) }

  // Recover series and compute derived quantities for reporting
  val v = DoubleArray(t.size)   // km/s for reporting
  val gammaDeg = DoubleArray(t.size)
  val x = DoubleArray(t.size)   // km
  val h = DoubleArray(t.size)   // km
  val vD = DoubleArray(t.size)  // km/s (positive loss for reporting)
  val vG = DoubleArray(t.size)  // km/s (positive loss for reporting)
  val q = DoubleArray(t.size)   // Pa
  val mach = DoubleArray(t.size)

  for (i in t.indices) {
    val vi = f[i][0]           // m/s
    val gi = f[i][1]           // rad
    val xi = f[i][2]           // m
    val hi = f[i][3]           // m
    val vDi = f[i][4]          // m/s (negative)
    val vGi = f[i][5]          // m/s (negative)

    v[i] = vi / 1000.0
    gammaDeg[i] = gi / deg
    x[i] = xi / 1000.0
    h[i] = hi / 1000.0
    vD[i] = -vDi / 1000.0
    vG[i] = -vGi / 1000.0

    val rho = rho0 * exp(-(hi) / hscale)
    q[i] = 0.5 * rho * vi * vi
    val a = speedOfSoundISA(hi)
    mach[i] = if (a > 0.0) vi / a else Double.NaN
  }

  // Find max dynamic pressure and its index
  var maxQ = Double.NEGATIVE_INFINITY
  var imax = 0
  for (i in q.indices) {
    if (q[i] > maxQ) { maxQ = q[i]; imax = i }
  }

  val tQ = t[imax]
  val vQ = v[imax]
  val hQ = h[imax]
  val aQ = speedOfSoundISA(h[imax] * 1000.0)
  val MQ = if (aQ > 0.0) vQ * 1000.0 / aQ else Double.NaN

  // Output, following MATLAB formatting (no plotting here)
  printf("\n\n -----------------------------------\n")
  printf("\n Initial flight path angle = %10.3f deg ", gammaDeg.first())
  printf("\n Pitchover altitude        = %10.3f m   ", hturn)
  printf("\n Burn time                 = %10.3f s   ", tburn)
  printf("\n Maximum dynamic pressure  = %10.3f atm ", maxQ * 9.869e-6)
  printf("\n    Time                   = %10.3f min ", tQ / 60.0)
  printf("\n    Speed                  = %10.3f km/s", vQ)
  printf("\n    Altitude               = %10.3f km  ", hQ)
  printf("\n    Mach Number            = %10.3f     ", MQ)
  printf("\n At burnout:")
  printf("\n    Speed                  = %10.3f km/s", v.last())
  printf("\n    Flight path angle      = %10.3f deg ", gammaDeg.last())
  printf("\n    Altitude               = %10.3f km  ", h.last())
  printf("\n    Downrange distance     = %10.3f km  ", x.last())
  printf("\n    Drag loss              = %10.3f km/s", vD.last())
  printf("\n    Gravity loss           = %10.3f km/s", vG.last())
  printf("\n\n -----------------------------------\n")

  // Plots (similar to MATLAB subplots) using plotScalar helper
  // (a) Altitude vs Downrange Distance
  plotScalar(
    Triple(x.toList(), h.toList(), "(a) Altitude vs Downrange Distance"),
    xLabel = "Downrange Distance (km)",
    yLabel = "Altitude (km)"
  ).save("Example13_03_traj.png")

  // (b) Dynamic Pressure vs Altitude (convert Pa -> atm)
  val qAtm = q.map { it * 9.869e-6 }
  plotScalar(
    Triple(h.toList(), qAtm, "(b) Dynamic Pressure vs Altitude"),
    xLabel = "Altitude (km)",
    yLabel = "Dynamic pressure (atm)"
  ).save("Example13_03_q.png")
}

// Minimal International Standard Atmosphere speed of sound model.
// Returns speed of sound in m/s at geometric altitude (meters).
private fun speedOfSoundISA(hMeters: Double): Double {
  val gamma = 1.4
  val R = 287.05287 // J/(kg·K)
  val h = max(0.0, hMeters)

  // Piecewise temperature model sufficient for 0–20 km, adequate for Max-Q region.
  val T = when {
    h < 11000.0 -> 288.15 - 0.0065 * h
    h < 20000.0 -> 216.65
    else -> 216.65 // simple fallback; Max-Q occurs below ~20 km
  }
  return sqrt(gamma * R * T)
}

private fun printf(format: String, vararg args: Any?) {
  print(String.format(format, *args))
}
