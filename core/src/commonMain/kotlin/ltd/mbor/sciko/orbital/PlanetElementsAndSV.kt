package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import org.jetbrains.kotlinx.multik.ndarray.operations.times
import kotlin.math.atan
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

fun planetElementsAndSV(
  planetId: Int,
  year: Int,
  month: Int,
  day: Int,
  hour: Int,
  minute: Int,
  second: Int
): PlanetData {
  val mu = muSun// Gravitational parameter of the sun (km^3/s^2)

  // Julian day calculations
  val j0 = J0(year, month, day)
  val ut = (hour + minute/60.0 + second/3600.0)/24.0
  val jd = j0 + ut

  // Obtain J2000 orbital elements and rates
  val (J2000Coe, rates) = planetaryElements(planetId)

  // Calculate orbital elements at the given date
  val t0 = (jd - 2451545)/36525.0
  val elements = J2000Coe + rates*t0

  val a = elements[0]
  val e = elements[1]
  val incl = elements[2]
  val RA = zeroTo360(elements[3])
  val wHat = zeroTo360(elements[4])
  val L = zeroTo360(elements[5])
  val w = zeroTo360(wHat - RA)
  val M = zeroTo360(L - wHat)

  // Solve Kepler's equation for eccentric anomaly
  val E = keplerE(e, M.degrees)

  // Calculate true anomaly
  val TA = zeroTo360(2*atan(sqrt((1 + e)/(1 - e))*tan(E/2)).toDegrees())

  // Calculate angular momentum
  val h = sqrt(mu*a*(1 - e.pow(2)))

  // Orbital elements
  val coe = mk.ndarray(mk[h, e, RA.degrees, incl.degrees, w.degrees, TA.degrees, a, wHat.degrees, L.degrees, M.degrees, E])

  // State vectors
  val (r, v) = svFromCoe(coe, mu)

  return PlanetData(coe, r, v, jd)
}

data class PlanetData(
  val coe: MultiArray<Double, D1>,
  val r: MultiArray<Double, D1>,
  val v: MultiArray<Double, D1>,
  val jd: Double
)

fun planetaryElements(planetId: Int): Pair<MultiArray<Double, D1>, MultiArray<Double, D1>> {
  val J2000Elements = mk.ndarray(
    mk[
      mk[0.38709893, 0.20563069, 7.00487, 48.33167, 77.45645, 252.25084],
      mk[0.72333199, 0.00677323, 3.39471, 76.68069, 131.53298, 181.97973],
      mk[1.00000011, 0.01671022, 0.00005, -11.26064, 102.94719, 100.46435],
      mk[1.52366231, 0.09341233, 1.85061, 49.57854, 336.04084, 355.45332],
      mk[5.20336301, 0.04839266, 1.30530, 100.55615, 14.75385, 34.40438],
      mk[9.53707032, 0.05415060, 2.48446, 113.71504, 92.43194, 49.94432],
      mk[19.19126393, 0.04716771, 0.76986, 74.22988, 170.96424, 313.23218],
      mk[30.06896348, 0.00858587, 1.76917, 131.72169, 44.97135, 304.88003],
      mk[39.48168677, 0.24880766, 17.14175, 110.30347, 224.06676, 238.92881]
    ]
  )

  val centRates = mk.ndarray(
    mk[
      mk[0.00000066, 0.00002527, -23.51, -446.30, 573.57, 538101628.29],
      mk[0.00000092, -0.00004938, -2.86, -996.89, -108.80, 210664136.06],
      mk[-0.00000005, -0.00003804, -46.94, -18228.25, 1198.28, 129597740.63],
      mk[-0.00007221, 0.00011902, -25.47, -1020.19, 1560.78, 68905103.78],
      mk[0.00060737, -0.00012880, -4.15, 1217.17, 839.93, 10925078.35],
      mk[-0.00301530, -0.00036762, 6.11, -1591.05, -1948.89, 4401052.95],
      mk[0.00152025, -0.00019150, -2.09, -1681.4, 1312.56, 1542547.79],
      mk[-0.00125196, 0.00002514, -3.64, -151.25, -844.43, 786449.21],
      mk[-0.00076912, 0.00006465, 11.07, -37.33, -132.25, 522747.90]
    ]
  )

  val au = 149597871.0 // Astronomical unit in km

  val J2000Coe = J2000Elements.apply {
    this[planetId - 1, 0] *= au
  }[planetId - 1]

  val rates = centRates.apply {
    this[planetId - 1, 0] *= au
    for (i in 2..5) {
      this[planetId - 1, i] /= 3600.0 // Convert arcseconds to degrees
    }
  }[planetId - 1]

  return J2000Coe to rates
}
