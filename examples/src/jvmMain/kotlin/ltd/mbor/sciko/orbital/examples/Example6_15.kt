package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.pow

fun main() {
  mk.setEngine(KEEngineType)
  val mu = muEarth
  val g0 = 9.807
  val RE = rEarth
  val r0 = mk.ndarray(mk[RE + 480, 0.0, 0.0])
  val v0 = mk.ndarray(mk[0.0, 7.7102, 0.0])
  val t0 = 0.0
  val tBurn = 261.1127

  fun rates(t: Double, y: MultiArray<Double, D1>, T: Double, Isp: Double): MultiArray<Double, D1> {
    val r = mk.ndarray(mk[y[0], y[1], y[2]])
    val v = mk.ndarray(mk[y[3], y[4], y[5]])
    val m = y[6]

    val rNorm = r.norm()
    val aGrav = (-mu/rNorm.pow(3))*r
    val aThrust = if (m > 0) (T/m)*v/v.norm() else mk.zeros<Double>(3)
    val dm = if (m > 0) -T*1000.0/(Isp*g0) else 0.0

    return mk.ndarray(
      mk[
        v[0], v[1], v[2],
        aGrav[0] + aThrust[0], aGrav[1] + aThrust[1], aGrav[2] + aThrust[2],
        dm
      ]
    )
  }

  val m0 = 2000.0
  val T = 10.0 //kN
  val Isp = 300.0

  val y0 = mk.ndarray(mk[r0[0], r0[1], r0[2], v0[0], v0[1], v0[2], m0])

  val (tOut, yOut) = rkf45(
    tspan = t0..tBurn,
    y0 = y0,
    tolerance = 1e-16,
    odeFunction = { t, y -> rates(t, y, T, Isp) }
  )

  val r1 = mk.ndarray(mk[yOut.last()[0], yOut.last()[1], yOut.last()[2]])
  val v1 = mk.ndarray(mk[yOut.last()[3], yOut.last()[4], yOut.last()[5]])
  val m1 = yOut.last()[6]

  val coe = coeFromSV(r1, v1, mu)
  val e = coe[1]
  val TA = coe[5]
  val a = coe[6]

  val dtheta = if (TA <= PI) PI - TA else 3*PI - TA
  val (ra, va) = RVfromR0V0TA(r1, v1, dtheta, mu)
  val rmax = ra.norm()

  printf("\n\n--------------------------------------------------------\n")
  printf("\nBefore ignition:")
  printf("\n  Mass = %g kg", m0)
  printf("\n  State vector:")
  printf("\n    r = [%10g, %10g, %10g] (km)", r0[0], r0[1], r0[2])
  printf("\n      Radius = %g", r0.norm())
  printf("\n    v = [%10g, %10g, %10g] (km/s)", v0[0], v0[1], v0[2])
  printf("\n      Speed = %g\n", v0.norm())
  printf("\nThrust          = %12g kN", T)
  printf("\nBurn time       = %12.6f s", tBurn)
  printf("\nMass after burn = %12.6E kg\n", m1)
  printf("\nEnd-of-burn-state vector:")
  printf("\n    r = [%10g, %10g, %10g] (km)", r1[0], r1[1], r1[2])
  printf("\n      Radius = %g", r1.norm())
  printf("\n    v = [%10g, %10g, %10g] (km/s)", v1[0], v1[1], v1[2])
  printf("\n      Speed = %g\n", v1.norm())
  printf("\nPost-burn trajectory:")
  printf("\n  Eccentricity   = %g", e)
  printf("\n  Semimajor axis = %g km", a)
  printf("\n  Apogee state vector:")
  printf("\n    r = [%17.10E, %17.10E, %17.10E] (km)", ra[0], ra[1], ra[2])
  printf("\n      Radius = %g", ra.norm())
  printf("\n    v = [%17.10E, %17.10E, %17.10E] (km/s)", va[0], va[1], va[2])
  printf("\n      Speed = %g", va.norm())
  printf("\n\n--------------------------------------------------------\n\n")
}

private fun printf(format: String, vararg args: Any?) {
  System.out.printf(format, *args)
}
