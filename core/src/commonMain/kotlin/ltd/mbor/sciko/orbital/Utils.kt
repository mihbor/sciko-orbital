package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.ndarray.complex.ComplexDouble
import kotlin.math.*

const val G = 6.6742e-20
const val muEarth = 398600.0 // km^3/s^2
const val rEarth = 6378.0 //km
const val mEarth = 5974e21

const val J2 = 1082.63e-6

const val muSun = 1.32712440018e11

const val muMoon = 4902.8 // km^3/s^2
const val rMoon = 1737.0
const val mMoon = 7348e19
const val saMoon = 384748.0 // = semimajor axis

val Number.degrees get() = this.toDouble() * PI/180
fun Number.toDegrees() = this.toDouble()*180/PI

fun acos(a: Double) = kotlin.math.acos(a.coerceAtLeast(-1.0).coerceAtMost(1.0))

fun ComplexDouble.pow(x: Double): ComplexDouble {
  val r = sqrt(this.re*this.re + this.im*this.im)
  val theta = atan2(this.im, this.re)
  val newR = r.pow(x)
  val newTheta = theta * x
  return ComplexDouble(
    re = newR * cos(newTheta),
    im = newR * sin(newTheta)
  )
}

fun sqrt(x: ComplexDouble) = x.pow(0.5)

fun sind(x: Double) = sin(x.degrees)
fun cosd(x: Double) = cos(x.degrees)
