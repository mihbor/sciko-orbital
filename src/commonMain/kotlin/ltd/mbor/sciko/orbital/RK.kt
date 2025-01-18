package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.min

/**
  This function uses a selected Runge-Kutta procedure to integrate
  a system of first-order differential equations dy/dt = f(t,y).

  y             - column vector of solutions
  f             - column vector of the derivatives dy/dt
  t             - time
  rk            - = 1 for RK1; = 2 for RK2; = 3 for RK3; = 4 for RK4
  n_stages      - the number of points within a time interval that
  the derivatives are to be computed
  a             - coefficients for locating the solution points within
  each time interval
  b             - coefficients for computing the derivatives at each
  interior point
  c             - coefficients for the computing solution at the end of
  the time step
  ode_function  - handle for user M-function in which the derivatives f
  are computed
  tspan         - the vector [t0 tf] giving the time interval for the
  solution
  t0            - initial time
  tf            - final time
  y0            - column vector of initial values of the vector y
  tout          - column vector of times at which y was evaluated
  yout          - a matrix, each row of which contains the components of y
  evaluated at the correponding time in tout
  h             - time step
  ti            - time at the beginning of a time step
  yi            - values of y at the beginning of a time step
  t_inner       - time within a given time step
  y_inner       - values of y within a given time step

  User M-function required: ode_function
  %}
 */

val rk1a = mk.ndarray(mk[0.0])
val rk1b = mk.ndarray(mk[mk[0.0]])
val rk1c = mk.ndarray(mk[1.0])

val rk2a = mk.ndarray(mk[0.0, 1.0])
val rk2b = mk.ndarray(mk[
  mk[0.0],
  mk[1.0]
])
val rk2c = mk.ndarray(mk[1.0/2, 1.0/2])

val rk3a = mk.ndarray(mk[0.0, 1.0/2, 1.0])
val rk3b = mk.ndarray(mk[
  mk[  0.0, 0.0],
  mk[1.0/2, 0.0],
  mk[ -1.0, 2.0],
])
val rk3c = mk.ndarray(mk[1.0/6, 2.0/3, 1.0/6])

val rk4a = mk.ndarray(mk[0.0, 1.0/2, 1.0/2, 1.0])
val rk4b = mk.ndarray(mk[
  mk[  0.0,   0.0, 0.0],
  mk[1.0/2,   0.0, 0.0],
  mk[  0.0, 1.0/2, 0.0],
  mk[  0.0,   0.0, 1.0],
])
val rk4c = mk.ndarray(mk[1.0/6, 1.0/3, 1.0/3, 1.0/6])

fun rk(
  odeFunction: (Double, MultiArray<Double, D1>) -> MultiArray<Double, D1>,
  tspan: Pair<Double, Double>,
  y0: MultiArray<Double, D1>,
  h0: Double,
  rk: Int
): Pair<List<Double>, List<MultiArray<Double, D1>>> {
  //Determine which of the four Runge-Kutta methods is to be used:
  val n_stages = rk
  val (a, b, c) = when(rk) {
    1 -> Triple(rk1a, rk1b, rk1c)
    2 -> Triple(rk2a, rk2b, rk2c)
    3 -> Triple(rk3a, rk3b, rk3c)
    4 -> Triple(rk4a, rk4b, rk4c)
    else -> error("The parameter rk  must have the value 1, 2, 3 or 4.")
  }
  val t0   = tspan.first
  val tf   = tspan.second
  var t    = t0
  var y    = y0
  var tOut = listOf(t)
  var yOut = listOf(y)
  var h    = h0
  val f    = mk.zeros<Double>(n_stages, y0.size)

  while (t < tf) {
    val ti = t
    val yi = y
    //Evaluate the time derivative(s) at the 'n_stages' points within the current interval:
    for (i in 0..<n_stages) {
      val tInner = ti + a[i] * h
      var yInner = yi
      for (j in 0..<i) {
        yInner = yInner + h * b[i, j] * f[j]
      }
      f[i] = odeFunction(tInner, yInner)
    }

    h    = min(h, tf-t)
    t    = t + h
    y    = yi + (h*f.transpose() dot c)
    tOut += t
    yOut += y
  }
  return tOut to yOut
}