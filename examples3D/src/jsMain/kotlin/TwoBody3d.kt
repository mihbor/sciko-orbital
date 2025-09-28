import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.G
import ltd.mbor.sciko.orbital.rkf45
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import three.js.Color
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.SphereGeometry
import kotlin.math.pow

@OptIn(ExperimentalStdlibApi::class) // this is needed for eager initialization of multik engines
fun twoBody(): Pair<List<Double>, List<MultiArray<Double, D1>>> {
  mk.setEngine(KEEngineType)
  val t0 = 0.0
  val tf = 480.0

  val R1_0 = mk.ndarray(mk[0.0, 0.0, 0.0])
  val R2_0 = mk.ndarray(mk[3000.0, 0.0, 0.0])
  val V1_0 = mk.ndarray(mk[10.0, 20.0, 30.0])
  val V2_0 = mk.ndarray(mk[0.0, 40.0, 0.0])

  val y0 = R1_0 cat R2_0 cat V1_0 cat V2_0

  return rkf45(t0..tf, y0, odeFunction = ::rates)
}

fun twoBodyScene(): List<Mesh<SphereGeometry, MeshBasicMaterial>> {
  val metrial1 = MeshBasicMaterial().apply {
    color = Color(0xff0000)
  }
  val metrial2 = MeshBasicMaterial().apply {
    color = Color(0x00ff00)
  }
  return twoBody().second.flatMap {
    val R1 = it.slice<Double, D1, D1>(0..2)
    val R2 = it.slice<Double, D1, D1>(3..5)
    listOf(
      (Mesh(SphereGeometry(0.02), metrial1).apply {
        position.x = R1[0]*0.001
        position.y = R1[1]*0.001
        position.z = R1[2]*0.001
      }),
      (Mesh(SphereGeometry(0.02), metrial2).apply {
        position.x = R2[0]*0.001
        position.y = R2[1]*0.001
        position.z = R2[2]*0.001
      })
    )
  }
}

private fun rates(t: Double, y: MultiArray<Double, D1>): MultiArray<Double, D1> {

  val m1 = 1e26
  val m2 = 1e26

  val R1 = y.slice<Double, D1, D1>(0..2)
  val R2 = y.slice<Double, D1, D1>(3..5)
  val V1 = y.slice<Double, D1, D1>(6..8)
  val V2 = y.slice<Double, D1, D1>(9..11)

  val r = (R2 - R1).norm()

  val A1 = G*m2*(R2 - R1)/r.pow(3)
  val A2 = G*m1*(R1 - R2)/r.pow(3)

//  println("t[$t]: $y")

  return V1 cat V2 cat A1 cat A2
}
