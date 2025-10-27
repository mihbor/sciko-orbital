package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.RVfromR0V0
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rEarth
import ltd.mbor.sciko.orbital.rkf45
import ltd.mbor.sciko.orbital.svFromCoe
import org.jetbrains.kotlinx.kandy.dsl.continuous
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.path
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
  // Gravitational parameter and Earth radius (km, km^3/s^2)
  val mu = muEarth
  val RE = rEarth

  // Target orbit at time t = 0
  val rp = RE + 300.0 // perigee radius (km)
  val e = 0.1         // eccentricity
  val i = 0.0         // inclination (rad)
  val RA = 0.0        // RAAN (rad)
  val omega = 0.0     // argument of perigee (rad)
  val theta = 0.0     // true anomaly (rad)

  // Additional computed parameters
  val ra = rp * (1 + e) / (1 - e)
  val h = sqrt(2 * mu * rp * ra / (ra + rp))
  val a = (rp + ra) / 2.0
  val T = 2 * PI / sqrt(mu) * a.pow(1.5)
  val n = 2 * PI / T

  // Prescribed initial relative state of chaser B in the co-moving frame
  val dr0 = mk.ndarray(mk[-1.0, 0.0, 0.0])
  val dv0 = mk.ndarray(mk[0.0, -2.0 * n * dr0[0], 0.0])

  val t0 = 0.0
  val tf = 5.0 * T

  // Target A initial state vector from orbital elements (Algorithm 4.5)
  val (R0, V0) = svFromCoe(mk.ndarray(mk[h, e, RA, i, omega, theta]), mu)

  // Initial state vector of B's relative motion (dx, dy, dz, dvx, dvy, dvz)
  val y0 = dr0 cat dv0

  // Integrate Equations 7.34 using RKF45
  val (t, y) = rkf45(t0..tf, y0) { t, f -> rates(t, f, R0, V0, mu) }

  // Prepare data for plotting: MATLAB used plot(y(:,2), y(:,1)) → plot dy vs dx
  val xPlot = y.map { it[1] } // dy
  val yPlot = y.map { it[0] } // dx

  // Produce a simple plot and save to a PNG (requires lets-plot/kandy runtime)
  val xmin = 0.0
  val xmax = 40.0
  val ymin = -5.0
  val ymax = 5.0

  val plt = plot {
    path {
      x(xPlot) { scale = continuous(xmin, xmax) }
      y(yPlot) { scale = continuous(ymin, ymax) }
    }
  }
  plt.save("Example7_03.png")

  // Also print first/last sample for quick inspection
  println("Start (y, x) = (${xPlot.first()}, ${yPlot.first()})")
  println("End   (y, x) = (${xPlot.last()}, ${yPlot.last()})")
}

private fun rates(
  t: Double,
  f: MultiArray<Double, D1>,
  R0: MultiArray<Double, D1>,
  V0: MultiArray<Double, D1>,
  mu: Double,
): MultiArray<Double, D1> {
  // Update target A state at time t (Algorithm 3.4)
  val (R, V) = RVfromR0V0(R0, V0, t, mu)

  val Rmag = R.norm()
  val RdotV = (R dot V)
  val hmag = (R cross V).norm()

  val dx = f[0]; val dy = f[1]; val dz = f[2]
  val dvx = f[3]; val dvy = f[4]; val dvz = f[5]

  val R2 = Rmag.pow(2)
  val R3 = Rmag.pow(3)
  val R4 = Rmag.pow(4)
  val h2 = hmag * hmag

  val dax = (2 * mu / R3 + h2 / R4) * dx - 2 * RdotV / R4 * hmag * dy + 2 * hmag / R2 * dvy
  val day = -(mu / R3 - h2 / R4) * dy + 2 * RdotV / R4 * hmag * dx - 2 * hmag / R2 * dvx
  val daz = -mu / R3 * dz

  return mk.ndarray(mk[dvx, dvy, dvz, dax, day, daz])
}
