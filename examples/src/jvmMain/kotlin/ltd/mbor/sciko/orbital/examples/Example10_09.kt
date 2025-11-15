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
import kotlin.math.*

/**
 * Kotlin port of MATLAB Example_10_09.m
 *
 * Solves Gauss planetary equations with solar radiation pressure (SRP)
 * over 3 years, including Earth eclipse (line-of-sight) shadow function.
 */
fun main() {
  mk.setEngine(KEEngineType)

  // Conversion factors
  val hours = 3600.0
  val days = 24.0 * hours

  // Constants
  val mu = muEarth          // km^3/s^2
  val c = 2.998e8           // m/s
  val S = 1367.0            // W/m^2
  val Psr = S / c           // Pa = N/m^2

  // Spacecraft properties
  val CR = 2.0              // radiation pressure coefficient
  val m = 100.0             // kg
  val As = 200.0            // m^2

  // Initial orbital parameters (given)
  val a0 = 10085.44         // km
  val e0 = 0.025422
  val i0 = 88.3924.degrees
  val RA0 = 45.38124.degrees
  val TA0 = 343.4268.degrees
  val w0 = 227.493.degrees

  // Inferred parameters
  val h0 = sqrt(mu * a0 * (1 - e0.pow(2)))
  val T0 = 2 * PI / sqrt(mu) * a0.pow(1.5)

  // Initial element state vector
  val coe0 = mk.ndarray(mk[h0, e0, RA0, i0, w0, TA0])

  // Time span
  val JD0 = 2438400.5 // 6 January 1964 0 UT
  val t0 = 0.0
  val tf = 3.0 * 365.0 * days

  val (tOut, yOut) = rkf45(
    t0..tf,
    coe0,
    tolerance = 1e-10,
    h0 = T0 / 1000.0,
  ) { t, y ->
    ratesSRP(t, y, mu, JD0, days, Psr, CR, As, m)
  }

  val h = yOut.map { it[0] }
  val e = yOut.map { it[1] }
  val RA = yOut.map { it[2] }
  val inc = yOut.map { it[3] }
  val w = yOut.map { it[4] }
  val TA = yOut.map { it[5] }

  // a = h^2 / mu / (1 - e^2)
  val a = yOut.map { it[0].pow(2) / mu / (1 - it[1].pow(2)) }

  val tDays = tOut.map { it / days }

  // Plots similar to MATLAB example
  plotScalar(
    Triple(tDays, h.map { it - h0 }, "Δh (km^2/s)")
  , xLabel = "days", yLabel = "Δh (km^2/s)").save("Example10_09_h.png")

  plotScalar(
    Triple(tDays, e.map { it - e0 }, "Δe")
  , xLabel = "days", yLabel = "Δe").save("Example10_09_e.png")

  plotScalar(
    Triple(tDays, RA.map { (it - RA0).toDegrees() }, "ΔΩ (deg)")
  , xLabel = "days", yLabel = "deg").save("Example10_09_RA.png")

  plotScalar(
    Triple(tDays, inc.map { (it - i0).toDegrees() }, "Δi (deg)")
  , xLabel = "days", yLabel = "deg").save("Example10_09_i.png")

  plotScalar(
    Triple(tDays, w.map { (it - w0).toDegrees() }, "Δω (deg)")
  , xLabel = "days", yLabel = "deg").save("Example10_09_w.png")

  plotScalar(
    Triple(tDays, a.map { it - a0 }, "Δa (km)")
  , xLabel = "days", yLabel = "km").save("Example10_09_a.png")
}

private fun ratesSRP(
  t: Double,
  f: MultiArray<Double, D1>,
  mu: Double,
  JD0: Double,
  days: Double,
  Psr: Double,
  CR: Double,
  As: Double,
  m: Double,
): MultiArray<Double, D1> {
  val h = f[0]
  val e = f[1]
  val RA = f[2]
  val i = f[3]
  val w = f[4]
  val TA = f[5]

  val u = w + TA // argument of latitude

  // State vectors from COEs at current time
  val coe = f
  val (R, V) = svFromCoe(coe, mu)
  val r = R.norm()

  // Sun position at current Julian date
  val JD = JD0 + t / days
  val (lambda, eps, rSun) = solarPosition(JD) // km, earth->sun

  // Eclipse shadow function (1 in sunlight, 0 in shadow)
  val nu = if (los(R, rSun)) 0.0 else 1.0

  // Solar radiation pressure acceleration magnitude (km/s^2)
  val pSR = nu * (Psr * CR * As / m) / 1000.0

//  // Compute RSW frame unit vectors
//  val rHat = R / r
//  val hVec = R cross V
//  val wHat = hVec / hVec.norm()
//  val sHat = wHat cross rHat
//
//  // Unit vector from Earth to Sun
//  val uSun = rSun / rSun.norm()
//
//  // Components of sun direction in RSW frame
//  val ur = (uSun dot rHat)
//  val us = (uSun dot sHat)
//  val uw = (uSun dot wHat)
//
//  val si = sin(i)

  val sl = sin(lambda); val cl = cos(lambda)
  val se = sin(eps);    val ce = cos(eps)
  val sW = sin(RA);     val cW = cos(RA)
  val si = sin(i);      val ci = cos(i)
  val su = sin(u);      val cu = cos(u)
  val sT = sin(TA);     val cT = cos(TA)

  val ur     =   sl*ce*cW*ci*su + sl*ce*sW*cu - cl*sW*ci*su + cl*cW*cu + sl*se*si*su

  val us     =   sl*ce*cW*ci*cu - sl*ce*sW*su - cl*sW*ci*cu - cl*cW*su + sl*se*si*cu

  val uw     = - sl*ce*cW*si + cl*sW*si + sl*se*ci

  // Gauss planetary equations with SRP (matching MATLAB lines 178-191)
  val hdot = -pSR * r * us

  val edot = -pSR * (
    (h / mu) * sT * ur + (1.0 / (mu * h)) * ((h.pow(2) + mu * r) * cT + mu * e * r) * us
  )

  val TAdot = h / r.pow(2) - pSR / (e * h) * (
    h.pow(2) / mu * cT * ur - (r + h.pow(2) / mu) * sT * us
  )

  val RAdot = -pSR * r / h / si * su * uw

  val idot = -pSR * r / h * cu * uw

  val wdot = -pSR * (
    -1.0 / (e * h) * ((h * h / mu) * cT * ur - (r + h * h / mu) * sT * us) -
      r * sin(u) / h / si * cos(i) * uw
  )

  return mk.ndarray(mk[hdot, edot, RAdot, idot, wdot, TAdot])
}
