import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.J2
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rEarth
import ltd.mbor.sciko.orbital.rMoon
import ltd.mbor.sciko.orbital.rkf45
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import three.js.*
import kotlin.math.PI
import kotlin.math.pow

const val hours = 3600.0

fun aJ2(mu: Double, r: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val (x, y, z) = listOf(r[0], r[1], r[2])
  return -3.0/2*J2*mu*rEarth.pow(2)/r. norm().pow(4)*mk.ndarray(mk[
    (1.0 - 5*(z/r.norm()).pow(2))*x/r.norm(),
    (1.0 - 5*(z/r.norm()).pow(2))*y/r.norm(),
    (3.0 - 5*(z/r.norm()).pow(2))*z/r.norm()
  ])
}

private val t0 = 0.0
private val tf = 4 * hours
private val r0 = mk.ndarray(mk[8000.0, 0.0, 6000.0])
private val v0 = mk.ndarray(mk[0.0, 7.0, 0.0])

fun orbitScene(): List<Object3D> {
  val y0 = r0 cat v0
  val (t, y) = rkf45(t0..tf, y0) { t, y -> rates(t, y/*, aJ2(muEarth, y.slice(0..2))*/) }

  output(t, y)

  val earthMaterial = MeshBasicMaterial().apply {
    map = earthTex
  }
  val metrial = MeshBasicMaterial().apply {
    color = Color(0xffffff)
  }
  return listOf<Object3D>(
    Mesh(SphereGeometry(rMoon *0.001), earthMaterial),
  ) + Object3D().apply{
    rotateX(-PI/2)
    y.drop(1).forEach {
      val R1 = it.slice<Double, D1, D1>(0..2)
      add(Mesh(SphereGeometry(0.1), metrial).apply {
        position.x = R1[0]*0.001
        position.y = R1[1]*0.001
        position.z = R1[2]*0.001
      })
    }
  }
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

private fun printf(format: String, vararg args: Any?) {
  console.log(format, *args)
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

}
