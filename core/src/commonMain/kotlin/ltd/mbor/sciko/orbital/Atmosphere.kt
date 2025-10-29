package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.min

/**
 * Calculates atmospheric density for geometric altitudes from sea level through 1000 km
 * using exponential interpolation based on the USSA76 model tables.
 *
 * Ported from MATLAB function `atmosphere.m`.
 *
 * @param z Geometric altitude in km. Values are clamped to [0, 1000].
 * @return Density in kg/m^3.
 */
fun atmosphere(z: Double): Double {
  // Geometric altitudes (km), 28 breakpoints define 27 intervals
  val h = mk.ndarray(mk[
    0.0, 25.0, 30.0, 40.0, 50.0, 60.0, 70.0,
    80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0,
    150.0, 180.0, 200.0, 250.0, 300.0, 350.0, 400.0,
    450.0, 500.0, 600.0, 700.0, 800.0, 900.0, 1000.0
  ])

  // Corresponding densities (kg/m^3) from USSA76 for each interval start (length 27)
  val r = mk.ndarray(mk[
    1.225,      4.008e-2,  1.841e-2,  3.996e-3,  1.027e-3,  3.097e-4,  8.283e-5,
    1.846e-5,   3.416e-6,  5.606e-7,  9.708e-8,  2.222e-8,  8.152e-9,  3.831e-9,
    2.076e-9,   5.194e-10, 2.541e-10, 6.073e-11, 1.916e-11, 7.014e-12, 2.803e-12,
    1.184e-12,  5.215e-13, 1.137e-13, 3.070e-14, 1.136e-14, 5.759e-15, 3.561e-15
  ])

  // Scale heights (km) for each interval (length 27)
  val H = mk.ndarray(mk[
    7.310,  6.427,  6.546,   7.360,   8.342,   7.583,   6.661,
    5.927,  5.533,  5.703,   6.782,   9.973,  13.243,  16.322,
    21.652, 27.974, 34.934,  43.342,  49.755,  54.513,  58.019,
    60.980, 65.654, 76.377, 100.587, 147.203, 208.020
  ])

  // Clamp altitude to valid range
  val zClamped = min(1000.0, max(0.0, z))

  // Determine the interpolation interval index i (0-based for Kotlin)
  var i = 0
  // There are 27 intervals: [h[0],h[1]), [h[1],h[2]), ..., [h[26],h[27]]
  for (j in 0 until 27) {
    if (zClamped >= h[j] && zClamped < h[j + 1]) {
      i = j
    }
  }
  if (zClamped == 1000.0) {
    i = 26
  }

  // Exponential interpolation: density = r[i] * exp(-(z - h[i]) / H[i])
  return r[i] * exp(-(zClamped - h[i]) / H[i])
}
