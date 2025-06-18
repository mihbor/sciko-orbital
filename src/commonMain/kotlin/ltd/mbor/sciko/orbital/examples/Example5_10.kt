package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.coeFromSV
import ltd.mbor.sciko.orbital.degrees
import ltd.mbor.sciko.orbital.rvFromObserve
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt

fun main() {
    // Constants
    val deg = PI / 180.0
    val f = 1.0 / 298.256421867
    val Re = 6378.13655
    val wE = 7.292115e-5
    val mu = 398600.4418

    // Data declaration for Example 5.10
    val rho = 2551.0
    val rhodot = 0.0
    val A = 90.degrees
    val ADot = 0.1130.degrees
    val altitude = 30.degrees
    val aDot = 0.05651.degrees
    val theta = 300.degrees
    val phi = 60.degrees
    val H = 0.0

    // Earth's rotation vector (rad/s)
    val omega = mk.ndarray(mk[0.0, 0.0, wE])

    // Algorithm 5.4: Position and velocity from observation
    val (r, v) = rvFromObserve(
        omega = omega,
        phi = phi,
        A = A,
        a = altitude,
        H = H,
        aDot = aDot,
        ADot = ADot,
        theta = theta,
        wE = wE,
        rho = rho,
        rhoDot = rhodot,
        Re = Re,
        f = f
    )

    // Algorithm 4.2: Classical orbital elements
    val coe = coeFromSV(r, v, mu)
    val h = coe[0]
    val e = coe[1]
    val RA = coe[2]
    val incl = coe[3]
    val w = coe[4]
    val TA = coe[5]
    val a = coe[6]

    // Equation 2.40: Perigee radius
    val rp = h * h / mu / (1 + e)

    // Output
    println("-----------------------------------------------------")
    println("Example 5.10: Orbit determination from observation")
    println("Input data:")
    println("  Slant range (km)              = $rho km")
    println("  Slant range rate (km/s)       = $rhodot km/s")
    println("  Azimuth (deg)                 = ${A.toDegrees()} deg")
    println("  Azimuth rate (deg/s)          = ${ADot.toDegrees()} deg/s")
    println("  Elevation (deg)               = %3f deg".format(altitude.toDegrees()))
    println("  Elevation rate (deg/s)        = ${aDot.toDegrees()} deg/s")
    println("  Local sidereal time (deg)     = %3f deg".format(theta.toDegrees()))
    println("  Latitude (deg)                = %3f deg".format(phi.toDegrees()))
    println("  Altitude above sea level (km) = $H km")
    println("\n Solution:")

    println("\n State vector:");
    println("  r (km)                        = [%.2f, %.2f, %.2f]".format(r[0], r[1], r[2]))
    println("  v (km/s)                      = [%f, %f, %f]".format(v[0], v[1], v[2]))

    println("\n\n Orbital elements:\n")
    println("  Angular momentum (km^2/s)      = %.1f km^2/s".format(h))
    println("  Eccentricity                   = %.6f".format(e))
    println("  Inclination (deg)              = %.3f deg".format(incl.toDegrees()))
    println("  RA of ascending node (deg)     = %.2f deg".format(RA.toDegrees()))
    println("  Argument of perigee (deg)      = %.2f deg".format(w.toDegrees()))
    println("  True anomaly (deg)             = %.3f deg".format(TA.toDegrees()))
    println("  Semimajor axis (km)            = %.2f km".format(a))
    println("  Perigee radius (km)            = %.2f km".format(rp))
  //...If the orbit is an ellipse, output its period:
  if (e < 1) {
    val T = 2*PI/sqrt(mu)*a.pow(1.5)
    println("  Period:")
    println("    Seconds                      = %.2f".format(T))
    println("    Minutes                      = %.4f".format(T/60))
    println("    Hours                        = %.5f".format(T/3600))
    println("    Days                         = %.7f".format(T/24/3600))
  }
    println("-----------------------------------------------------")
}

