package ltd.mbor.sciko.orbital

import java.lang.Math.log
import kotlin.math.abs
import kotlin.math.ceil

/**
 *   This function evaluates a root of a function using
 *   the bisection method
 *
 *   tol  - error to within which the root is computed
 *   n    - number of iterations
 *   xl   - low end of the interval containing the root
 *   xu   - upper end of the interval containing the root
 *   i    - loop index
 *   xm   - mid-point of the interval from xl to xu
 *   fun  - name of the function whose root is being found
 *   fxl  - value of fun at xl
 *   fxm  - value of fun at xm
 *   root - the computed root
 */
fun bisect(f: (Double) -> Double, xl: Double, xu: Double): Double {
  val tol = 1e-6
  val n = ceil(log(abs(xu - xl) / tol) / log(2.0)).toInt()

  var xl = xl
  var xu = xu
  var xm = 0.0

  for (i in 1..n) {
    xm = (xl + xu) / 2
    val fxl = f(xl)
    val fxm = f(xm)
    if (fxl * fxm > 0) {
      xl = xm
    } else {
      xu = xm
    }
  }

  return xm
}
