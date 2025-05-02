package ltd.mbor.sciko.orbital

import kotlin.math.PI

const val muEarth = 398600.0 // km^3/s^2

val Number.degrees get() = this.toDouble() * PI/180
fun Number.toDegrees() = this.toDouble()*180/PI
