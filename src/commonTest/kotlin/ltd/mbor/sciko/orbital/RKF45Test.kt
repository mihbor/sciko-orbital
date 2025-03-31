package ltd.mbor.sciko.orbital

import aJ2
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.map
import org.junit.Test
import kotlin.math.pow
import kotlin.test.assertEquals

fun round(d: Double, scale: Long = 2) = d.toBigDecimal().roundToDigitPositionAfterDecimalPoint(digitPosition = scale, roundingMode = RoundingMode.ROUND_HALF_CEILING).doubleValue(false)
fun roundSignificand(d: Double, decimalPrecision: Long = 6) = d.toBigDecimal().roundSignificand(DecimalMode(decimalPrecision = decimalPrecision, roundingMode = RoundingMode.ROUND_HALF_CEILING)).doubleValue(false)

const val G  = 6.6742e-20
//  Earth:
const val m1 = 5.974e24
const val R  = 6378.0
const val m2 = 1000.0
val mu    = G*(m1 + m2)
const val J2 = 1082.63e-6

private fun rates(t: Double, f: MultiArray<Double, D1>, ad: MultiArray<Double, D1> = mk.zeros(3)): MultiArray<Double, D1> {
  val x    = f[0]
  val y    = f[1]
  val z    = f[2]
  val vx   = f[3]
  val vy   = f[4]
  val vz   = f[5]
  val r    = (mk.ndarray(mk[x, y, z])).norm()
  val ax   = -mu*x/r.pow(3) + ad[0]
  val ay   = -mu*y/r.pow(3) + ad[1]
  val az   = -mu*z/r.pow(3) + ad[2]

  return mk.ndarray(mk[vx, vy, vz, ax, ay, az])
}

fun ratesWithJ2(t: Double, f: MultiArray<Double, D1>): MultiArray<Double, D1> {
  return rates(t, f, aJ2(mu, f.slice(0..2)))
}

class RKF45Test {
  @Test
  fun `integrate orbital motion`() {
    val t0 = 0.0
    val tf = 60*60.0
    val r0 = mk.ndarray(mk[2466.69,5941.54,3282.71])
    val v0 = mk.ndarray(mk[-6.80822,1.04998,3.61939])

    val y0 = r0 cat v0
    val (t, y) = rkf45(t0..tf, y0, odeFunction = ::rates)
    val rf = y.last().slice<Double, D1, D1>(0..2)
    val vf = y.last().slice<Double, D1, D1>(3..5)
    assertEquals(mk.ndarray(mk[-3419.32, -7146.16, -3758.41]), rf.map(::roundSignificand))
    assertEquals(mk.ndarray(mk[5.57968, -0.911545, -3.00336]), vf.map(::roundSignificand))
  }

  @Test
  fun `integrate orbit with J2 perturbation`() {
    val t0 = 0.0
    val tf = 4848.0
    val r0 = mk.ndarray(mk[-6685.20926,601.51244,3346.06634])
    val v0 = mk.ndarray(mk[-1.74294,-6.70242,-2.27739])

    val y0 = r0 cat v0
    val (t, y) = rkf45(t0..tf, y0, odeFunction = ::ratesWithJ2)
    val rf = y.last().slice<Double, D1, D1>(0..2)
    val vf = y.last().slice<Double, D1, D1>(3..5)
    assertEquals(mk.ndarray(mk[1729.62, 6889.32, 2384.98]), rf.map(::roundSignificand))
    assertEquals(mk.ndarray(mk[-6.52049, 0.525827, 3.22664]), vf.map(::roundSignificand))
  }

  @Test
  fun `integrate another orbit with J2 perturbation`() {
    val t0 = 0.0
    val tf = 4848.0
    val r0 = mk.ndarray(mk[-6685.21657,592.52839,3345.6716])
    val v0 = mk.ndarray(mk[-1.74283,-6.70475,-2.27334])

    val y0 = r0 cat v0
    val (t, y) = rkf45(t0..tf, y0, odeFunction = ::ratesWithJ2)
    val rf = y.last().slice<Double, D1, D1>(0..2)
    val vf = y.last().slice<Double, D1, D1>(3..5)
    assertEquals(mk.ndarray(mk[1737.45, 6881.70, 2372.62]), rf.map(::roundSignificand))
    assertEquals(mk.ndarray(mk[-6.52597, 0.536321, 3.23769]), vf.map(::roundSignificand))
  }
}