package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
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
) {
  val θ: Double get() = f + ω
}

data class OrbitElementsAM(
  val a: Double,
  val e: Double,
  val Ω: Double,
  val i: Double,
  val ω: Double,
  val M: Double
)

data class OrbitElementsH(
  val h: Double,
  val e: Double,
  val Ω: Double,
  val i: Double,
  val ω: Double,
  val f: Double
) {
  val θ: Double get() = f + ω
}

fun OrbitElementsA.toOrbitElementsH(μ: Double = μEarth): OrbitElementsH {
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

fun orbitElementsA(r: MultiArray<Double, D1>, v: MultiArray<Double, D1>, μ: Double = μEarth): OrbitElementsA {
  val coe = coeFromSV(r, v, μ)
  val h = coe[0]
  val e = coe[1]
  val Ω = coe[2]
  val i = coe[3]
  val ω = coe[4]
  val f = coe[5]
  val a = coe[6]
  return OrbitElementsA(
    a = a,
    e = e,
    Ω = Ω,
    i = i,
    ω = ω,
    f = f
  )
}

fun orbitElementsH(r: MultiArray<Double, D1>, v: MultiArray<Double, D1>, μ: Double = μEarth): OrbitElementsH {
  val coe = coeFromSV(r, v, μ)
  val h = coe[0]
  val e = coe[1]
  val Ω = coe[2]
  val i = coe[3]
  val ω = coe[4]
  val f = coe[5]
  return OrbitElementsH(
    h = h,
    e = e,
    Ω = Ω,
    i = i,
    ω = ω,
    f = f
  )
}

fun OrbitElementsA.toOrbitElementsAM(): OrbitElementsAM {
  return OrbitElementsAM(
    a = a,
    e = e,
    Ω = Ω,
    i = i,
    ω = ω,
    M = M(E(f, e), e),
  )
}

fun E(f: Double, e: Double) = 2*atan(tan(f/2)*sqrt(1 - e)/sqrt(1 + e))
fun M(E: Double, e: Double) = E - e*sin(E)

fun OrbitElementsH.toStateVectors(μ: Double = μEarth): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
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

fun OrbitElementsA.toStateVectors(μ: Double) = toOrbitElementsH(μ).toStateVectors(μ)

fun svFromCoe(coe: MultiArray<Double, D1>, μ: Double) = OrbitElementsH(
  h = coe[0],
  e = coe[1],
  Ω = coe[2],
  i = coe[3],
  ω = coe[4],
  f = coe[5]
).toStateVectors(μ)
