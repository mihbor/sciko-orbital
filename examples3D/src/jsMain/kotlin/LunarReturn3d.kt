import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.J0
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rEarth
import ltd.mbor.sciko.orbital.rMoon
import ltd.mbor.sciko.orbital.rkf45
import ltd.mbor.sciko.orbital.simpsonsLunarEphemeris
import ltd.mbor.sciko.orbital.degrees
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
import three.js.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// Kotlin/JS port of MATLAB Example_9_03.m with 3D visualization similar to LunarTrajectory3d.kt

private const val days = 24 * 3600.0
private const val muMoon = 4902.8 // km^3/s^2 (close to MATLAB value)

private fun juliandate(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Double {
  // JD at 0h UT + fraction of day
  val jd0 = J0(year, month, day)
  val frac = (hour + minute / 60.0 + second / 3600.0) / 24.0
  return jd0 + frac
}

private fun unit(v: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val n = v.norm()
  return v/n
}

fun lunarReturnScene(): List<Object3D> {
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

  // Post-process for visualization: perilune detection and moon path sampling
  var distMin = Double.POSITIVE_INFINITY

  // We'll also collect Moon positions for rendering (sparse sampling for perf)
  var moonSample: MultiArray<Double, D1>? = null

  yOut.forEachIndexed { i, state ->
    val r = mk.ndarray(mk[state[0], state[1], state[2]])
    val jd = jd0 - (ttt - tOut[i]) / days
    val (rm, _) = simpsonsLunarEphemeris(jd)
    val d = (r-rm).norm()
    if (d < distMin) { distMin = d; moonSample = rm }
  }

  // Three.js visualization similar to LunarTrajectory3d.kt
  val earthMaterial = MeshBasicMaterial().apply { map = earthTex }
  val moonMaterial = MeshBasicMaterial().apply { map = moonTex }
  val trajMaterial = MeshBasicMaterial().apply { color = Color(0xffffff) }
  val startMaterial = MeshBasicMaterial().apply { color = Color(0x00ff00) }
  val periluneMaterial = MeshBasicMaterial().apply { color = Color(0xff0000) }
  val endMaterial = MeshBasicMaterial().apply { color = Color(0x0000ff) }

  val scale = 0.0001

  val objects = mutableListOf<Object3D>()

  // Earth at origin
  objects += Mesh(SphereGeometry(rEarth * scale), earthMaterial)

  // Moon samples as small spheres
  val moonGroup = Object3D().apply {
    rotateX(-PI/2)
    moonSample?.let { m ->
      add(Mesh(SphereGeometry(rMoon * scale), moonMaterial).apply {
        position.x = m[0] * scale
        position.y = m[1] * scale
        position.z = m[2] * scale
      })
    }
  }
  objects += moonGroup

  // Spacecraft trajectory points
  val trajGroup = Object3D().apply {
    rotateX(-PI/2)
    yOut.forEach { state ->
      val x = state[0] * scale
      val y = state[1] * scale
      val z = state[2] * scale
      add(Mesh(SphereGeometry(0.05), trajMaterial).apply {
        position.x = x
        position.y = y
        position.z = z
      })
    }
  }
  objects += trajGroup

  // Markers: start, perilune, end
//  fun addMarker(state: MultiArray<Double, D1>, material: MeshBasicMaterial, size: Double = 0.2) = Mesh(SphereGeometry(size), material).apply {
//    position.x = state[0] * scale
//    position.y = state[1] * scale
//    position.z = state[2] * scale
//  }

//  objects += Object3D().apply { rotateX(-PI/2); add(addMarker(yOut.first(), startMaterial)) }
//  objects += Object3D().apply { rotateX(-PI/2); add(addMarker(yOut[imin], periluneMaterial, size = 0.15)) }
//  objects += Object3D().apply { rotateX(-PI/2); add(addMarker(yOut.last(), endMaterial)) }

  // Console output (brief)
  println("Example 9.03 (3D Earth-Moon restricted) — Kotlin/JS")
  println("Flight time days = "+ (tf / days))
  println("Perilune distance (km) ≈ "+ distMin)

  return objects
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

  val aEarth = -muEarth * r / (rmag*rmag*rmag)

  val aMoon = muMoon * (rms / (rmsMag*rmsMag*rmsMag) - rm / (rmMag*rmMag*rmMag))

  val aX = aEarth[0] + aMoon[0]
  val aY = aEarth[1] + aMoon[1]
  val aZ = aEarth[2] + aMoon[2]

  return mk.ndarray(mk[vX, vY, vZ, aX, aY, aZ])
}