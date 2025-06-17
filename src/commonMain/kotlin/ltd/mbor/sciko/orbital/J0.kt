package ltd.mbor.sciko.orbital

/**
 * Computes the Julian day number at 0 UT for any year between 1900 and 2100 using Equation 5.48.
 *
 * @param year Year (range: 1901 - 2099)
 * @param month Month (range: 1 - 12)
 * @param day Day (range: 1 - 31)
 * @return Julian day at 0 hr UT (Universal Time)
 */
fun J0(year: Int, month: Int, day: Int): Double {
  return 367 * year - (7 * (year + ((month + 9) / 12)) / 4) +
    (275 * month / 9) + day + 1721013.5
}
