package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.J0
import ltd.mbor.sciko.orbital.RAandDecFromR
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rEarth
import ltd.mbor.sciko.orbital.rMoon
import ltd.mbor.sciko.orbital.rkf45
import ltd.mbor.sciko.orbital.simpsonsLunarEphemeris
import ltd.mbor.sciko.orbital.degrees
import ltd.mbor.sciko.orbital.muMoon
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val days = 24 * 3600.0

private fun juliandate(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Double {
  // JD at 0h UT + fraction of day
  val jd0 = J0(year, month, day)
  val frac = (hour + minute / 60.0 + second / 3600.0) / 24.0
  return jd0 + frac
}

private fun unit(v: MultiArray<Double, D1>) = v/v.norm()

fun main() {
  mk.setEngine(KEEngineType)

  // Data declaration (from MATLAB Example 9.03)
  val year = 2020
  val month = 5
  val day = 4
  val hour = 12
  val minute = 0
  val second = 0

  val t0 = 0.0
  val z0 = 320.0 // km
  val alpha0 = 90.0.degrees // deg -> rad
  val dec0 = 15.0.degrees
  val gamma0 = 40.0.degrees
  val fac = 0.9924 // fraction of escape speed
  val ttt = 3.0 * days
  val tf = ttt + 2.667 * days

  // Moon state at arrival date/time
  val jd0 = juliandate(year, month, day, hour, minute, second)
  val (rm0, vm0) = simpsonsLunarEphemeris(jd0) // km, km/s
  val (ra, dec) = RAandDecFromR(rm0)
  val distance = rm0.norm()
  val hMoon = rm0 cross vm0
  val inclMoon = acos(hMoon[2]/hMoon.norm())

  // Initial position vector of probe in ECI using RA/Dec
  val r0mag = rEarth + z0
  val r0 = mk.ndarray(mk[
    r0mag * cos(alpha0) * cos(dec0),
    r0mag * sin(alpha0) * cos(dec0),
    r0mag * sin(dec0)
  ])

  // Initial velocity magnitude and direction
  val vEsc = sqrt(2.0 * muEarth / r0mag)
  val v0mag = fac * vEsc

  // Normal to plane defined by r0 and Moon position (ECI)
  val w0 = unit(r0 cross rm0)
  val ur = unit(r0)
  val uperpRaw = w0 cross ur
  val uperp = unit(uperpRaw)

  val vr = v0mag * sin(gamma0)
  val vperp = v0mag * cos(gamma0)
  val v0 = vr * ur + vperp * uperp

  val y0 = r0 cat v0

  // Integrate equations of motion with Earth + Moon gravity
  val (tOut, yOut) = rkf45(t0..tf, y0) { t, y -> rates(t, y, jd0, ttt) }

  // State vector and celestial position of moon when probe is at perilune:
  var distMin = Double.POSITIVE_INFINITY
  var rmPerilune: MultiArray<Double, D1> = mk.zeros(3)
  var vmPerilune: MultiArray<Double, D1> = mk.zeros(3)
  // Spacecraft at perilune:
  var rPerilune: MultiArray<Double, D1> = mk.zeros(3)
  var vPerilune: MultiArray<Double, D1> = mk.zeros(3)
  var tPerilune = 0.0

  yOut.forEachIndexed { i, state ->
    val r = mk.ndarray(mk[state[0], state[1], state[2]])
    val v = mk.ndarray(mk[state[3], state[4], state[5]])
    val jd = jd0 - (ttt - tOut[i]) / days
    val (rm, vm) = simpsonsLunarEphemeris(jd)
    val d = (r-rm).norm()
    if (d < distMin) {
      distMin = d
      rmPerilune = rm
      vmPerilune = vm
      rPerilune = r
      vPerilune = v
      tPerilune = tOut[i]
    }
  }
  // Location of the Moon at TLI:
  val (rmTLI, _) = simpsonsLunarEphemeris(jd0 - (ttt - tOut.first()) / days)
  val (RATLI, DecTLI) = RAandDecFromR(rmTLI)
  val (RAPerilune, DecPerilune) = RAandDecFromR(rmPerilune)
  val targetError = (rmPerilune - rm0).norm()
  // Speed of probe relative to Moon at perilune:
  val relSpeed = (vPerilune - vmPerilune).norm()
  // End point of trajectory:
  val rEnd = yOut.last()[0..2]
  val altEnd = rEnd.norm() - rEarth
  val (RAEnd, decEnd) = RAandDecFromR(rEnd)
  val rms = rmPerilune - rPerilune
  val aearth = -muEarth*rPerilune/rPerilune.norm().pow(3)
  val amoon = muMoon*(rms/rms.norm().pow(3) - rmPerilune/rmPerilune.norm().pow(3))
  val atot = aearth + amoon
  val binormal = unit(vPerilune cross atot)
  val binormalz = binormal[2]
  val inclPerilune = acos(binormalz).toDegrees()

  printf("\n\n%s\n\n", "Example 9.3")
  printf("Date and time of arrival at moon: ")
  printf("%s/%s/%s %s:%s:%s", month, day, year, hour, minute, second)
  printf("\nMoon's position: ")
  printf("\n Distance = %11g km", distance)
  printf("\n Right Ascension = %11g deg", ra)
  printf("\n Declination = %11g deg", dec)
  printf("\nMoon's orbital inclination = %11g deg\n", inclMoon.toDegrees())
  printf("\nThe probe at earth departure (t = %g sec):", t0)
  printf("\n Altitude = %11g km", z0)
  printf("\n Right ascension = %11g deg", alpha0.toDegrees())
  printf("\n Declination = %11g deg", dec0.toDegrees())
  printf("\n Flight path angle = %11g deg", gamma0.toDegrees())
  printf("\n Speed = %11g km/s", v0mag)
  printf("\n Escape speed = %11g km/s", vEsc)
  printf("\n v/vesc = %11g", v0mag/vEsc)
  printf("\n Inclination of translunar orbit = %11g deg\n", acos(w0[2]).toDegrees())

  printf("\nThe moon when the probe is at TLI:")
  printf("\n Distance = %11g km", rmTLI.norm())
  printf("\n Right ascension = %11g deg", RATLI)
  printf("\n Declination = %11g deg", DecTLI)
  printf("\nThe moon when the probe is at perilune: ")
  printf("\n Distance = %11g km", rmPerilune.norm())
  printf("\n Speed = %11g km/s", vmPerilune.norm())
  printf("\n Right ascension = %11g deg", RAPerilune)
  printf("\n Declination = %11g deg", DecPerilune)
  printf("\n Target error = %11g km", targetError)
  printf("\n\nThe probe at perilune:")
  printf("\n Altitude = %11g km", distMin - rMoon)
  printf("\n Speed = %11g km/s", vPerilune.norm())
  printf("\n Relative speed = %11g km/s", relSpeed)
  printf("\n Inclination of osculating plane = %11g deg", inclPerilune)
  printf("\n Time from TLI to perilune = %11g hours (%g days)", tPerilune/3600, tPerilune/3600/24)
  printf("\n\nTotal time of flight = %11g days", tOut.last()/days)
  printf("\nTime to target point = %11g days", ttt/days)
  printf("\nFinal earth altitude = %11g km", altEnd)
  printf("\nFinal right ascension = %11g deg", RAEnd)
  printf("\nFinal declination = %11g deg\n", decEnd)
}

private fun printf(format: String, vararg args: Any?) {
  System.out.printf(format, *args)
}

private fun rates(t: Double, y: MultiArray<Double, D1>, jd0: Double, ttt: Double): MultiArray<Double, D1> {
  val X = y[0]; val Y = y[1]; val Z = y[2]
  val vX = y[3]; val vY = y[4]; val vZ = y[5]

  val r = mk.ndarray(mk[X, Y, Z])
  val rmag = sqrt(X*X + Y*Y + Z*Z)

  val jd = jd0 - (ttt - t) / days
  val (rm, _) = simpsonsLunarEphemeris(jd)

  val rms = rm - r
  val rmsMag = rms.norm()
  val rmMag = rm.norm()

  val aEarth = -muEarth * r / rmag.pow(3)

  val aMoon = muMoon * (rms / rmsMag.pow(3) - rm / rmMag.pow(3))

  val aX = aEarth[0] + aMoon[0]
  val aY = aEarth[1] + aMoon[1]
  val aZ = aEarth[2] + aMoon[2]

  return mk.ndarray(mk[vX, vY, vZ, aX, aY, aZ])
}