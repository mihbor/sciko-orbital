package ltd.mbor.sciko.orbital

import kotlin.math.PI

const val G = 6.6742e-20
const val muEarth = 398600.0 // km^3/s^2
const val rEarth = 6378.0 //km

val Number.degrees get() = this.toDouble() * PI/180
fun Number.toDegrees() = this.toDouble()*180/PI

fun acos(a: Double) = kotlin.math.acos(a.coerceAtLeast(-1.0).coerceAtMost(1.0))
