package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.J2
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rEarth
import ltd.mbor.sciko.orbital.rkf45
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.pow

val hours = 3600

fun aJ2(mu: Double, r: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val (x, y, z) = listOf(r[0], r[1], r[2])
  return -3.0/2*J2*mu*rEarth.pow(2)/r. norm().pow(4)*mk.ndarray(mk[
    (1.0 - 5*(z/r.norm()).pow(2))*x/r.norm(),
    (1.0 - 5*(z/r.norm()).pow(2))*y/r.norm(),
    (3.0 - 5*(z/r.norm()).pow(2))*z/r.norm()
  ])
}

private val t0 = 0.0
private val tf = 4848.0
private val r0 = mk.ndarray(mk[-6685.20926,601.51244,3346.06634])
private val v0 = mk.ndarray(mk[-1.74294,-6.70242,-2.27739])

fun main() {
  val y0 = r0 cat v0
  val (t, y) = rkf45(t0..tf, y0) { t, y -> rates(t, y, aJ2(muEarth, y.slice(0..2))) }

  output(t, y)
}

private fun rates(t: Double, f: MultiArray<Double, D1>, ad: MultiArray<Double, D1> = mk.zeros(3)): MultiArray<Double, D1> {
  val x    = f[0]
  val y    = f[1]
  val z    = f[2]
  val vx   = f[3]
  val vy   = f[4]
  val vz   = f[5]
  val r    = (mk.ndarray(mk[x, y, z])).norm()
  val ax   = -muEarth*x/r.pow(3) + ad[0]
  val ay   = -muEarth*y/r.pow(3) + ad[1]
  val az   = -muEarth*z/r.pow(3) + ad[2]

  return mk.ndarray(mk[vx, vy, vz, ax, ay, az])
}

private fun output(t: List<Double>, y: List<MultiArray<Double, D1>>) {
  val r = y.map{ mk.ndarray(mk[it[0], it[1], it[2]]).norm() }
  val rf = y.last().slice<Double, D1, D1>(0..2)
  val vf = y.last().slice<Double, D1, D1>(3..5)

  val (imax, rmax) = r.withIndex().maxBy { it.value }
  val (imin, rmin) = r.withIndex().minBy { it.value }

  val v_at_rmax   = mk.ndarray(mk[y[imax][3], y[imax][4], y[imax][5]]).norm()
  val v_at_rmin   = mk.ndarray(mk[y[imin][3], y[imin][4], y[imin][5]]).norm()

  printf("\n\n--------------------------------------------------------\n")
  printf("\n Earth Orbit\n")
  printf("\n The initial position is [%g, %g, %g] (km).",r0[0], r0[1], r0[2])
  printf("\n   Magnitude = %g km\n", r0.norm())
  printf("\n The initial velocity is [%g, %g, %g] (km/s).",v0[0], v0[1], v0[2])
  printf("\n   Magnitude = %g km/s\n", v0.norm())
  printf("\n Initial time = %g h.\n Final time   = %g h.\n",0.0,t.last()/hours)
  printf("\n The final position is [%g, %g, %g] (km).",rf[0], rf[1], rf[2])
  printf("\n   Magnitude = %g km\n", rf.norm())
  printf("\n The final velocity is [%g, %g, %g] (km/s).",vf[0], vf[1], vf[2])
  printf("\n   Magnitude = %g km/s\n", vf.norm())
  printf("\n The minimum altitude is %g km at time = %g h.",rmin-rEarth, t[imin]/hours)
  printf("\n The speed at that point is %g km/s.\n", v_at_rmin)
  printf("\n The maximum altitude is %g km at time = %g h.",rmax-rEarth, t[imax]/hours)
  printf("\n The speed at that point is %g km/s\n", v_at_rmax)
  printf("\n--------------------------------------------------------\n\n")

  // TODO: 3d plotting
}

private fun printf(format: String, vararg args: Any?) {
  System.out.printf(format, *args)
}

//The final position is [-3419.32, -7146.16, -3758.41] (km).
//Magnitude = 8768.41 km
//
//The final velocity is [5.57968, -0.911545, -3.00336] (km/s).
//Magnitude = 6.40186 km/s

// J2 1:
//The final position is [1729.62, 6889.32, 2384.98] (km).
//Magnitude = 7492.83 km
//
//The final velocity is [-6.52049, 0.525827, 3.22664] (km/s).
//Magnitude = 7.29414 km/s

//J2 2:
//The final position is [1737.45, 6881.70, 2372.62] (km).
//Magnitude = 7483.71 km
//
//The final velocity is [-6.52597, 0.536321, 3.23769] (km/s).
//Magnitude = 7.30469 km/s