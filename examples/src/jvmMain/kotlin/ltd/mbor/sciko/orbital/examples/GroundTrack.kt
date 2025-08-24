package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.`RA and Dec from R`
import ltd.mbor.sciko.orbital.degrees
import ltd.mbor.sciko.orbital.keplerE
import ltd.mbor.sciko.orbital.svFromCoe
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.letsplot.layers.points
import org.jetbrains.kotlinx.kandy.util.color.Color
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.*

fun main() {
  val mu = 398600.0
  val J2 = 0.00108263
  val Re = 6378.0
  val we = (2 * Math.PI + 2 * Math.PI / 365.26) / (24 * 3600)

  // Example 4.12 data
  val rP = 6700.0
  val rA = 10000.0
  val TAo = 230.degrees
  val Wo = 270.degrees
  val incl = 60.degrees
  val wpo = 45.degrees
  val nPeriods = 3.25

  // Orbital elements and derived quantities
  val a = (rA + rP) / 2
  val T = 2 * Math.PI / sqrt(mu) * a.pow(1.5)
  val e = (rA - rP) / (rA + rP)
  val h = sqrt(mu * a * (1 - e * e))
  val Eo = 2 * atan(tan(TAo / 2) * sqrt((1 - e) / (1 + e)))
  val Mo = Eo - e * sin(Eo)
  val to = Mo * (T / (2 * Math.PI))
  val tf = to + nPeriods * T
  val fac = -1.5 * sqrt(mu) * J2 * Re * Re / (1 - e * e).pow(2) / a.pow(3.5)
  val Wdot = fac * cos(incl)
  val wpdot = fac * (2.5 * sin(incl).pow(2) - 2)

  val (raList, decList) = findRaAndDec(
    to,
    tf,
    h,
    e,
    T,
    mu,
    Wo,
    Wdot,
    wpo,
    wpdot,
    incl,
    we,
    TAo
  )
  println("Ground track points (longitude, latitude):")
  for (i in raList.indices) {
    println("[${raList[i]}, ${decList[i]}]")
  }
  val curves = formSeparateCurves(raList, decList)
  plotGroundTrack(curves, raList, decList).save("ground_track.png")

  printOrbitalData(
    h,
    e,
    a,
    rP,
    rA,
    T,
    incl,
    TAo,
    to,
    Wo,
    Wdot,
    wpo,
    wpdot,
    mu,
  )
}

fun findRaAndDec(
  to: Double,
  tf: Double,
  h: Double,
  e: Double,
  T: Double,
  mu: Double,
  Wo: Double,
  Wdot: Double,
  wpo: Double,
  wpdot: Double,
  incl: Double,
  we: Double,
  TAo: Double,
): Pair<List<Double>, List<Double>> {
  val times = DoubleArray(1000) { i -> to + (tf - to) * i / 999 }
  val ra = mutableListOf<Double>()
  val dec = mutableListOf<Double>()

  for (t in times) {
    val M = 2 * Math.PI / T * t
    val E = keplerE(e, M)
    val TA = 2 * atan(tan(E / 2) * sqrt((1 + e) / (1 - e)))
    val rPerifocal = mk.ndarray(mk[
      h * h / mu / (1 + e * cos(TA)) * cos(TA),
      h * h / mu / (1 + e * cos(TA)) * sin(TA),
      0.0
    ])

    val W = Wo + Wdot * t
    val wp = wpo + wpdot * t

    val R1 = rotationZ(W)
    val R2 = rotationX(incl)
    val R3 = rotationZ(wp)
    val QxX = (R3 dot (R2 dot R1)).transpose()
    val R = QxX dot rPerifocal

    val theta = we * (t - to)
    val Q = rotationZ(theta)
    val rRel = Q dot R

    val (alpha, delta) = `RA and Dec from R`(rRel)
    ra.add(alpha)
    dec.add(delta)
  }

  return Pair(ra, dec)
}

fun formSeparateCurves(
  ra: List<Double>,
  dec: List<Double>,
  tol: Double = 100.0
): List<Pair<List<Double>, List<Double>>> {
  val curves = mutableListOf<Pair<MutableList<Double>, MutableList<Double>>>()
  var currentRa = mutableListOf<Double>()
  var currentDec = mutableListOf<Double>()
  var raPrev = ra.firstOrNull() ?: return emptyList()

  for (i in ra.indices) {
    if (abs(ra[i] - raPrev) > tol && currentRa.isNotEmpty()) {
      curves.add(Pair(currentRa, currentDec))
      currentRa = mutableListOf()
      currentDec = mutableListOf()
    }
    currentRa.add(ra[i])
    currentDec.add(dec[i])
    raPrev = ra[i]
  }
  if (currentRa.isNotEmpty()) {
    curves.add(Pair(currentRa, currentDec))
  }
  return curves
}

fun plotGroundTrack(
  curves: List<Pair<List<Double>, List<Double>>>,
  allRa: List<Double>,
  allDec: List<Double>
): Plot {
  return plot {
    // Plot each ground track curve as a line
    for ((i, curve) in curves.withIndex()) {
      val (ra, dec) = curve
      line {
        x(ra)
        y(dec)
        color = Color.BLUE
        width = 2.0
//        name = "Track ${i + 1}"
      }
    }

    // Plot the equator as a black line
    line {
      x(listOf(0.0, 360.0))
      y(listOf(0.0, 0.0))
      color = Color.BLACK
      width = 1.5
//      name = "Equator"
    }

    // Mark the start point
    points {
      x(listOf(allRa.first()))
      y(listOf(allDec.first()))
      color = Color.GREEN
      size = 8.0
//      name = "Start"
    }

    // Mark the finish point
    points {
      x(listOf(allRa.last()))
      y(listOf(allDec.last()))
      color = Color.RED
      size = 8.0
//      name = "Finish"
    }

    layout {
      title = "Ground Track"
      xAxisLabel = "East longitude (degrees)"
//        size = 0.0..360.0
//      )
      yAxisLabel = "Latitude (degrees)"
//        size = -90.0..90.0
//      )
    }
  }
}

// Helper functions for rotation matrices
fun rotationZ(angle: Double): MultiArray<Double, D2> {
  val c = cos(angle)
  val s = sin(angle)
  return mk.ndarray(mk[
    mk[  c,   s, 0.0],
    mk[ -s,   c, 0.0],
    mk[0.0, 0.0, 1.0]
  ])
}

fun rotationX(angle: Double): MultiArray<Double, D2> {
  val c = cos(angle)
  val s = sin(angle)
  return mk.ndarray(mk[
    mk[1.0, 0.0, 0.0],
    mk[0.0,   c,   s],
    mk[0.0,  -s,   c]
  ])
}

fun printOrbitalData(
  h: Double,
  e: Double,
  a: Double,
  rP: Double,
  rA: Double,
  T: Double,
  incl: Double,
  TAo: Double,
  to: Double,
  Wo: Double,
  Wdot: Double,
  wpo: Double,
  wpdot: Double,
  mu: Double,
) {
  // Orbital elements: [h, e, Wo, incl, wpo, TAo]
  val coe = mk.ndarray(mk[h, e, Wo, incl, wpo, TAo])
  val (ro, vo) = svFromCoe(coe, mu)

  println("\n ----------------------------------------------------")
  println("\n Angular momentum     = $h km^2/s")
  println(" Eccentricity         = $e")
  println(" Semimajor axis       = $a km")
  println(" Perigee radius       = $rP km")
  println(" Apogee radius        = $rA km")
  println(" Period               = ${T / 3600} hours")
  println(" Inclination          = ${incl.toDegrees()} deg")
  println(" Initial true anomaly = ${TAo.toDegrees()} deg")
  println(" Time since perigee   = ${to / 3600} hours")
  println(" Initial RA           = ${Wo.toDegrees()} deg")
  println(" RA_dot               = ${Wdot.toDegrees() * T} deg/period")
  println(" Initial wp           = ${wpo.toDegrees()} deg")
  println(" wp_dot               = ${wpdot.toDegrees() * T} deg/period")
  println()
  println(" r0 = [${ro[0]}, ${ro[1]}, ${ro[2]}] (km)")
  println(" magnitude = ${sqrt(ro[0] * ro[0] + ro[1] * ro[1] + ro[2] * ro[2])} km")
  println(" v0 = [${vo[0]}, ${vo[1]}, ${vo[2]}] (km)")
  println(" magnitude = ${sqrt(vo[0] * vo[0] + vo[1] * vo[1] + vo[2] * vo[2])} km")
  println(" ----------------------------------------------------\n")
}
