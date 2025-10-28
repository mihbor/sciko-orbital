package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.operations.*
import kotlin.math.cos
import kotlin.math.sin

fun simpsonsLunarEphemeris(jd: Double): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val tfac = 36525L*3600*24
  val t = (jd - 2451545.0)/36525

  val a = mk.ndarray(mk[
    mk[383.0, 31.5, 10.6, 6.2, 3.2, 2.3, 0.8],
    mk[351.0, 28.9, 13.7, 9.7, 5.7, 2.9, 2.1],
    mk[153.2, 31.5, 12.5, 4.2, 2.5, 3.0, 1.8]
  ])*1e3

  val b = mk.ndarray(mk[
    mk[8399.685, 70.990, 16728.377, 1185.622, 7143.070, 15613.745, 8467.263],
    mk[8399.687, 70.997, 8433.466, 16728.380, 1185.667, 7143.058, 15613.755],
    mk[8399.672, 8433.464, 70.996, 16728.364, 1185.645, 104.881, 8399.116]
  ])

  val c = mk.ndarray(mk[
    mk[5.381, 6.169, 1.453, 0.481, 5.017, 0.857, 1.010],
    mk[3.811, 4.596, 4.766, 6.165, 5.164, 0.300, 5.565],
    mk[3.807, 1.629, 4.595, 6.162, 5.167, 2.555, 6.248]
  ])

  val pos = mk.zeros<Double>(3)
  val vel = mk.zeros<Double>(3)

  for (i in 0 until 3) {
    for (j in 0 until 7) {
      pos[i] += a[i, j]*sin(b[i, j]*t + c[i, j])
      vel[i] += a[i, j]*cos(b[i, j]*t + c[i, j])*b[i, j]
    }
    vel[i] /= tfac
  }

  return Pair(pos, vel)
}
