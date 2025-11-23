import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import three.js.*
import kotlin.math.*


private const val days = 24 * 3600
private const val m1 = mEarth
private const val m2 = mMoon
private const val r12 = 384400.0

private val M = m1 + m2
private val pi_1 = m1 / M
private val pi_2 = m2 / M

private const val mu1 = 398600.0
private const val mu2 = 4903.02
private val mu = mu1 + mu2

private val W = sqrt(mu / r12.pow(3))
private val x1 = -pi_2 * r12
private val x2 = pi_1 * r12

private val d0 = 200.0
private val phi = -90.0.degrees
private val v0 = 10.9148
private val gamma = 20.0.degrees
private val t0 = 0.0
private val tf = 3.16689 * days
private val r0 = rEarth + d0
private val x = r0 * cos(phi) + x1
private val y = r0 * sin(phi)

fun lunarTrajectoryScene(): List<Object3D> {
  mk.setEngine(KEEngineType)
  val scale = 0.0001

  val vx = v0 * (sin(gamma) * cos(phi) - cos(gamma) * sin(phi))
  val vy = v0 * (sin(gamma) * sin(phi) + cos(gamma) * cos(phi))
  val f0 = mk.ndarray(mk[x, y, vx, vy])

  val (t, f) = rkf45(t0..tf, f0, odeFunction = ::rates)
  val xf = f.last()[0]
  val yf = f.last()[1]
  val vxf = f.last()[2]
  val vyf = f.last()[3]

  val df = sqrt((xf - x2).pow(2) + yf.pow(2)) - rMoon
  val vf = sqrt(vxf.pow(2) + vyf.pow(2))

  output(d0, phi, gamma, tf, df, vf, f)

  val earthMaterial = MeshBasicMaterial().apply {
    map = earthTex
  }
  val moonMaterial = MeshBasicMaterial().apply {
    map = moonTex
  }
  val metrial = MeshBasicMaterial().apply {
    color = Color(0xffffff)
  }
  return listOf<Object3D>(
    Mesh(SphereGeometry(rEarth*scale), earthMaterial).apply {
      position.x = x1 * scale
    },
  ) + Object3D().apply{
    rotateX(-PI/2)
    add(Mesh(SphereGeometry(rMoon*scale), moonMaterial).apply {
      position.x = x2 * scale
    })
    f.drop(1).forEach {
      val x = it[0]
      val y = it[1]
      add(Mesh(SphereGeometry(0.05), metrial).apply {
        position.x = x*scale
        position.y = y*scale
      })
    }
  }
}

private fun rates(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val x = f[0]
  val y = f[1]
  val vx = f[2]
  val vy = f[3]

  val r1 = sqrt((x + pi_2 * r12).pow(2) + y.pow(2))
  val r2 = sqrt((x - pi_1 * r12).pow(2) + y.pow(2))

  val ax = 2 * W * vy + W.pow(2) * x - mu1 * (x - x1) / r1.pow(3) - mu2 * (x - x2) / r2.pow(3)
  val ay = -2 * W * vx + W.pow(2) * y - (mu1 / r1.pow(3) + mu2 / r2.pow(3)) * y

  return mk.ndarray(mk[vx, vy, ax, ay])
}

private fun printf(format: String, vararg args: Any?) {
  console.log(format, *args)
}

private fun output(d0: Double, phi: Double, gamma: Double, tf: Double, df: Double, vf: Double, f: List<MultiArray<Double, D1>>) {
  println("------------------------------------------------------------")
  println("\n Example 2.18: Lunar trajectory using the restricted")
  println("\n three body equations.\n")
  println("\n Initial Earth altitude (km)         = $d0")
  println("\n Initial angle between radial")
  println("\n   and earth-moon line (degrees)     = ${phi.toDegrees()}")
  println("\n Initial flight path angle (degrees) = ${gamma.toDegrees()}")
  println("\n Flight time (days)                  = ${tf / days}")
  println("\n Final distance from the moon (km)   = $df")
  println("\n Final relative speed (km/s)         = $vf")
  println("------------------------------------------------------------")
  val x = f.map{it[0]}
  val y = f.map{it[1]}
//  plot(x, y)
}
