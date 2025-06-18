package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.PI
import kotlin.math.pow

/**
 * Computes the classical orbital elements (COE) from state vectors r and v using Algorithm 4.1.
 * @param r Position vector (km) as MultiArray<Double, D1>
 * @param v Velocity vector (km/s) as MultiArray<Double, D1>
 * @param mu Gravitational parameter (km^3/s^2)
 * @return Array of orbital elements: [h, e, RA, incl, w, TA, a]
 */
fun coeFromSV(
    R: MultiArray<Double, D1>,
    V: MultiArray<Double, D1>,
    mu: Double = muEarth
): List<Double> {
    val eps = 1e-10

    val r = R.norm()
    val v = V.norm()

    val vr = (R dot V) / r

    val H = R cross V
    val h = H.norm()

    val incl = acos(H[2] / h)

    val N = mk.ndarray(mk[0.0, 0.0, 1.0]) cross H
    val n = N.norm()

    val E = 1/mu*((v.pow(2) - mu/r)*R - r*vr*V)
    val e = E.norm()

    val RA = if (n != 0.0) {
      acos(N[0]/n).let {
        if (N[1] < 0) 2*PI - it
        else it
      }
    } else 0.0

    val w = if (n != 0.0 && e > eps) {
      acos((N dot E) / n / e).let {
        if (E[2] < 0) 2*PI - it
        else it
      }
    } else 0.0

    val TA = if (e > eps) {
      acos((E dot R) / e / r).let{
        if (vr < 0) 2*PI - it
        else it
      }
    } else {
      val cp = N cross R
      if (cp[2] >= 0) acos((N dot R)/n/r)
      else 2*PI - acos((N dot R)/n/r)
    }
    val a = h.pow(2) / mu / (1 - e.pow(2))

    return listOf(h, e, RA, incl, w, TA, a)
}

