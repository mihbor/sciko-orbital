package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray

fun interplanetary(
  depart: List<Int>,
  arrive: List<Int>
): TrajectoryData {

  // Extract departure details
  val planetId1 = depart[0]
  val year1 = depart[1]
  val month1 = depart[2]
  val day1 = depart[3]
  val hour1 = depart[4]
  val minute1 = depart[5]
  val second1 = depart[6]

  // Get state vector for planet 1
  val planet1Data = planetElementsAndSV(planetId1, year1, month1, day1, hour1, minute1, second1)

  // Extract arrival details
  val planetId2 = arrive[0]
  val year2 = arrive[1]
  val month2 = arrive[2]
  val day2 = arrive[3]
  val hour2 = arrive[4]
  val minute2 = arrive[5]
  val second2 = arrive[6]

  // Get state vector for planet 2
  val planet2Data = planetElementsAndSV(planetId2, year2, month2, day2, hour2, minute2, second2)

  // Time of flight in seconds
  val tof = (planet2Data.jd - planet1Data.jd)*24*3600

  // Patched conic assumption
  val R1 = planet1Data.r
  val R2 = planet2Data.r

  // Use Lambert solver to find spacecraft velocity at departure and arrival
  val (V1, V2) = lambert(R1, R2, tof, Trajectory.PROGRADE, muSun)

  return TrajectoryData(planet1Data, planet2Data, V1, V2)
}

data class TrajectoryData(
  val planet1: PlanetData,
  val planet2: PlanetData,
  val V1: MultiArray<Double, D1>,
  val V2: MultiArray<Double, D1>
)
