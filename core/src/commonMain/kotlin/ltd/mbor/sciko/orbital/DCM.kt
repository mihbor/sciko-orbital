package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.asin
import kotlin.math.atan2

typealias DCM = MultiArray<Double, D2>

fun dcm2euler(dcm: DCM): MultiArray<Double, D1> {
  val phi = atan2(dcm[2, 0], dcm[2, 1])
  val theta = acos(-dcm[2, 2])
  val psi = atan2(dcm[0, 2], dcm[1, 2])
  return mk.ndarray(mk[phi, theta, psi])
}

fun dcm2ypr(dcm: DCM): MultiArray<Double, D1> {
  val yaw = atan2(dcm[0, 1], dcm[0, 0])
  val pitch = asin(-dcm[0, 2])
  val roll = atan2(dcm[1, 2], dcm[2, 2])
  return mk.ndarray(mk[yaw, pitch, roll])
}
