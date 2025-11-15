package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.*

/**
 * Kotlin port of MATLAB Example_10_12.m
 *
 * Integrates Gauss variational equations with solar gravitational perturbation
 * for three orbits (GEO, HEO, LEO) over 720 days and plots ΔΩ, Δi, Δω.
 */
fun main() {
  mk.setEngine(KEEngineType)

  val hours = 3600.0
  val days = 24.0 * hours

  val mu = muEarth
  val mu3 = muSun

  // Common start Julian Day
  val JD0 = 2454283.0

  data class Case(val name: String, val a0: Double, val e0: Double, val i0: Double, val RA0: Double, val w0: Double, val TA0: Double)

  val cases = listOf(
    Case("GEO", a0 = 42164.0, e0 = 0.0001, i0 = 1.0.degrees, RA0 = 0.0, w0 = 0.0, TA0 = 0.0),
    Case("HEO", a0 = 26553.4, e0 = 0.741, i0 = 63.4.degrees, RA0 = 0.0, w0 = 270.0.degrees, TA0 = 0.0),
    Case("LEO", a0 = 6678.136, e0 = 0.01, i0 = 28.5.degrees, RA0 = 0.0, w0 = 0.0, TA0 = 0.0),
  )

  for (c in cases) {
    // Derived quantities
    val h0 = sqrt(mu * c.a0 * (1 - c.e0 * c.e0))

    val coe0 = mk.ndarray(mk[h0, c.e0, c.RA0, c.i0, c.w0, c.TA0])

    val t0 = 0.0
    val tf = 720.0 * days

    val (tOut, yOut) = rkf45(
      t0..tf,
      coe0,
      tolerance = 1e-10,
    ) { t, y ->
      ratesSolar(t, y, mu, mu3, JD0, days)
    }

    val RA = yOut.map { it[2] }
    val inc = yOut.map { it[3] }
    val w = yOut.map { it[4] }
    val tDays = tOut.map { it / days }

    plotScalar(
      Triple(tDays, RA.map { (it - c.RA0).toDegrees() }, "ΔΩ (deg) ${c.name}"),
      xLabel = "days", yLabel = "deg"
    ).save("Example10_12_RA_${c.name}.png")

    plotScalar(
      Triple(tDays, inc.map { (it - c.i0).toDegrees() }, "Δi (deg) ${c.name}"),
      xLabel = "days", yLabel = "deg"
    ).save("Example10_12_i_${c.name}.png")

    plotScalar(
      Triple(tDays, w.map { (it - c.w0).toDegrees() }, "Δω (deg) ${c.name}"),
      xLabel = "days", yLabel = "deg"
    ).save("Example10_12_w_${c.name}.png")
  }
}

private fun ratesSolar(
  t: Double,
  f: MultiArray<Double, D1>,
  mu: Double,
  mu3: Double,
  JD0: Double,
  days: Double,
): MultiArray<Double, D1> {
  val h = f[0]
  val e = f[1]
  val RA = f[2]
  val i = f[3]
  val w = f[4]
  val TA = f[5]
  val phi = w + TA

  val coe = f
  val (R, V) = svFromCoe(coe, mu)
  val r = R.norm()

  // RSW frame unit vectors
  val ur = R / r
  val H = R cross V
  val uh = H / H.norm()
  val s = uh cross ur
  val us = s / s.norm()

  // Time update
  val JD = JD0 + t / days

  // Sun position (geocentric, km)
  val (_, _, RS) = solarPosition(JD)
  val rS = RS.norm()

  val Rrel = RS - R
  val rrel = Rrel.norm()

  // Appendix F correction factor
  val q = (R dot (RS * 2.0 - R)) / (rS * rS)
  val Fq = ((q * q - 3.0 * q + 3.0) * q) / (1.0 + (1.0 - q).pow(1.5))

  // Solar gravitational perturbation acceleration (km/s^2)
  val ap = (mu3 / rrel.pow(3)) * (RS * Fq - R)

  // Components in RSW
  val apr = ap dot ur
  val aps = ap dot us
  val aph = ap dot uh

  // Gauss variational equations
  val hdot = r * aps

  val edot = (h / mu) * sin(TA) * apr + (1.0 / (mu * h)) * ((h * h + mu * r) * cos(TA) + mu * e * r) * aps

  val RAdot = r / h / sin(i) * sin(phi) * aph

  val idot = r / h * cos(phi) * aph

  val wdot = -h * cos(TA) / mu / e * apr + (h * h + mu * r) / mu / e / h * sin(TA) * aps - r * sin(phi) / h / tan(i) * aph

  val TAdot = h / r.pow(2) + (1.0 / (e * h)) * (h * h / mu * cos(TA) * apr - (r + h * h / mu) * sin(TA) * aps)

  return mk.ndarray(mk[hdot, edot, RAdot, idot, wdot, TAdot])
}
