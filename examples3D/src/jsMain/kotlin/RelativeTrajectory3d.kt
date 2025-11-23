import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import three.js.*
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun relativeOrbitScene(): List<Object3D> {
  mk.setEngine(KEEngineType)
  val scale = 0.001
  val mu = muEarth

  // Input data: Spacecraft A
  val hA = 52059.0
  val eA = 0.025724
  val iA = 60.0.degrees
  val RAAN_A = 40.0.degrees
  val omega_A = 30.0.degrees
  val theta_A = 40.0.degrees

  // Spacecraft B
  val hB = 52362.0
  val eB = 0.0072696
  val iB = 50.0.degrees
  val RAAN_B = 40.0.degrees
  val omega_B = 120.0.degrees
  val theta_B = 40.0.degrees

  // Compute initial state vectors
  val (rA0, vA0) = svFromCoe(mk.ndarray(mk[hA, eA, RAAN_A, iA, omega_A, theta_A]), mu)
  val (rB0, vB0) = svFromCoe(mk.ndarray(mk[hB, eB, RAAN_B, iB, omega_B, theta_B]), mu)

  // Period of A (MATLAB formula reproduced)
  val TA = 2*PI / mu.pow(2.0) * (hA / sqrt(1.0 - eA.pow(2.0))).pow(3.0)

  val n = 100
  val dt = TA / n
  val nPeriods = 60

  val totalSteps = nPeriods * n

  var t = -dt

  val relPositions = ArrayList<MultiArray<Double, D1>>(totalSteps)

  (1..totalSteps).forEach {
    t += dt
    val (rA, vA) = RVfromR0V0(rA0, vA0, t, mu)
    val (rB, vB) = RVfromR0V0(rB0, vB0, t, mu)

    val (rRel, _, _) = rvaRelative(rA, vA, rB, vB, mu)
    relPositions += rRel
  }

  // Visualisation: produce a small sphere for each sample of the relative trajectory
  val material = MeshBasicMaterial().apply { color = Color(0x1f77b4) }
  val initialMaterial = MeshBasicMaterial().apply { color = Color(0xff0000) }
  val axisMaterial = MeshBasicMaterial().apply { color = Color(0x00ff00) }

  val meshes = mutableListOf<Mesh<SphereGeometry, MeshBasicMaterial>>()

  relPositions.forEachIndexed { idx, r ->
    val m = Mesh(SphereGeometry(0.02), material).apply {
      position.x = r[0] * scale
      position.y = r[1] * scale
      position.z = r[2] * scale
    }
    meshes += m
  }

  // initial B position marker
  val (rRel0, _, _) = rvaRelative(rA0, vA0, rB0, vB0, mu)
  meshes += Mesh(SphereGeometry(0.06), initialMaterial).apply {
    position.x = rRel0[0] * scale
    position.y = rRel0[1] * scale
    position.z = rRel0[2] * scale
  }

  // draw simple axes in the co-moving frame (as small spheres along axes)
//  meshes += Mesh(SphereGeometry(0.03), axisMaterial).apply { position.x = 4.0 * 1000.0 * scale }
//  meshes += Mesh(SphereGeometry(0.03), axisMaterial).apply { position.y = 7.0 * 1000.0 * scale }
//  meshes += Mesh(SphereGeometry(0.03), axisMaterial).apply { position.z = 4.0 * 1000.0 * scale }

  return listOf(Object3D().apply {
    rotateX(-PI / 2)
    add(*meshes.toTypedArray())
  })
}
