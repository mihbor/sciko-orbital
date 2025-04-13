package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.*

data class OrbitElementsA(
  val a: Double,
  val e: Double,
  val Ω: Double,
  val i: Double,
  val ω: Double,
  val f: Double
)

data class OrbitElementsH(
  val h: Double,
  val e: Double,
  val Ω: Double,
  val i: Double,
  val ω: Double,
  val f: Double
)

fun OrbitElementsA.toOrbitElementsH(μ: Double): OrbitElementsH {
  val p = a*(1-e.pow(2))
  val h = sqrt(μ*p)
  return OrbitElementsH(
    h = h,
    e = e,
    Ω = Ω,
    i = i,
    ω = ω,
    f = f,
  )
}

fun orbitElementsA(μ: Double, r: MultiArray<Double, D1>, v: MultiArray<Double, D1>): OrbitElementsA {
  val h = r cross v
  val a = 1/(2/r.norm() - (v dot v)/μ)
  val ir = r/r.norm()
  val e = (v cross h)/μ - ir
  val ie = e/e.norm()
  val ih = h/h.norm()
  val PN = (ie cat (ih cross ie) cat ih).reshape(3, 3)
  val Ω = atan2(PN[2, 0], -PN[2, 1])
  val i = acos(PN[2, 2])
  val ω = atan2(PN[0, 2], PN[1, 2])
  val f = atan2((ie cross ir) dot ih, ie dot ir)
  return OrbitElementsA(
    a = a,
    e = e.norm(),
    Ω = Ω,
    i = i,
    ω = ω,
    f = f
  )
}

fun orbitElementsH(μ: Double, r: MultiArray<Double, D1>, v: MultiArray<Double, D1>): OrbitElementsH {
  val h = r cross v
  val ir = r/r.norm()
  val e = (v cross h)/μ - ir
  val ie = e/e.norm()
  val ih = h/h.norm()
  val PN = (ie cat (ih cross ie) cat ih).reshape(3, 3)
  val Ω = atan2(PN[2, 0], -PN[2, 1])
  val i = acos(PN[2, 2])
  val ω = atan2(PN[0, 2], PN[1, 2])
  val f = atan2((ie cross ir) dot ih, ie dot ir)
  return OrbitElementsH(
    h = h.norm(),
    e = e.norm(),
    Ω = Ω,
    i = i,
    ω = ω,
    f = f
  )
}

fun OrbitElementsH.toStateVectors(μ: Double): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val rp = h.pow(2)/μ/(1.0 + e*cos(f)) * (cos(f)*mk.ndarray(mk[1.0, 0.0, 0.0]) + sin(f)*mk.ndarray(mk[0.0, 1.0, 0.0]))
  val vp = μ/h * (-sin(f)*mk.ndarray(mk[1.0, 0.0, 0.0]) + (e + cos(f))*mk.ndarray(mk[0.0, 1.0, 0.0]))
  val R3Ω = mk.ndarray(mk[
    mk[ cos(Ω), sin(Ω), 0.0],
    mk[-sin(Ω), cos(Ω), 0.0],
    mk[    0.0,    0.0, 1.0]
  ])
  val R1i = mk.ndarray(mk[
    mk[1.0,    0.0,    0.0],
    mk[0.0, cos(i), sin(i)],
    mk[0.0,-sin(i), cos(i)]
  ])
  val R3ω = mk.ndarray(mk[
    mk[ cos(ω), sin(ω), 0.0],
    mk[-sin(ω), cos(ω), 0.0],
    mk[    0.0,    0.0, 1.0]
  ])
  val QpX = (R3ω dot R1i dot R3Ω).transpose()
  val r = QpX dot rp
  val v = QpX dot vp
  return r.transpose() to v.transpose()
}