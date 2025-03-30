package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

fun FandG(x: Double, t: Double, ro: Double, a: Double, mu: Double) : Pair<Double, Double> {

  val z = a*x.pow(2)

  val f = 1 - x.pow(2)/ro*stumpC(z)

  val g = t - 1/sqrt(mu)*x.pow(3)*stumpS(z)
  
  return f to g
}

fun FandGta(r0: MultiArray<Double, D1>, v0: MultiArray<Double, D1>, dta: Double, mu: Double) : Pair<Double, Double> {

  val h   = (r0 cross v0).norm()
  val vr0 = (v0 dot r0)/r0.norm()
  val r0  = r0.norm()
  val s   = sin(dta)
  val c   = cos(dta)

  val r   = h.pow(2)/mu/(1 + (h.pow(2)/mu/r0 - 1)*c - h*vr0*s/mu);

  val f   = 1 - mu*r*(1 - c)/h.pow(2);
  val g   = r*r0*s/h;

  return f to g
}
