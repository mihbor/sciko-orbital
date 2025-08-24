package ltd.mbor.sciko.orbital

fun monthPlanetNames(monthId: Int, planetId: Int): Pair<String, String> {
  val months = listOf(
    "January",
    "February",
    "March",
    "April",
    "May",
    "June",
    "July",
    "August",
    "September",
    "October",
    "November",
    "December"
  )

  val planets = listOf(
    "Mercury",
    "Venus",
    "Earth",
    "Mars",
    "Jupiter",
    "Saturn",
    "Uranus",
    "Neptune",
    "Pluto"
  )

  val month = months.getOrNull(monthId - 1) ?: throw IllegalArgumentException("Invalid month ID: $monthId")
  val planet = planets.getOrNull(planetId - 1) ?: throw IllegalArgumentException("Invalid planet ID: $planetId")

  return month to planet
}
