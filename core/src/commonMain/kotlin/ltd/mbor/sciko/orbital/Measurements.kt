package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.minus
import kotlin.math.PI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin

typealias Vector2 = MultiArray<Double, D1>

/** ECEF to ECI rotation about the z axis for a given Earth rotation angle. */
fun ecefToEciRotation(theta: Double): Matrix3 {
    require(theta.isFinite()) { "theta must be finite" }
    val c = cos(theta)
    val s = sin(theta)
    return mk.ndarray(mk[
        mk[c, -s, 0.0],
        mk[s, c, 0.0],
        mk[0.0, 0.0, 1.0],
    ])
}

/** Rotate a position from ECEF to ECI coordinates. */
fun ecefToEciPosition(positionEcef: Vector3, theta: Double): Vector3 {
    requireVector3(positionEcef, "positionEcef")
    require(theta.isFinite()) { "theta must be finite" }
    val c = cos(theta)
    val s = sin(theta)
    val x = positionEcef[0]
    val y = positionEcef[1]
    return mk.ndarray(mk[
        c * x - s * y,
        s * x + c * y,
        positionEcef[2],
    ])
}

/**
 * ECI velocity of a point fixed in the rotating Earth frame.
 *
 * [positionEci] is the point's ECI position and [rotationRate] is the
 * central body's positive z-axis rotation rate in rad/s.
 */
fun rotatingFrameVelocityEci(positionEci: Vector3, rotationRate: Double = omegaEarth): Vector3 {
    requireVector3(positionEci, "positionEci")
    require(rotationRate.isFinite()) { "rotationRate must be finite" }
    return mk.ndarray(mk[
        -rotationRate * positionEci[1],
        rotationRate * positionEci[0],
        0.0,
    ])
}

/** Station position in ECI coordinates from its ECEF position. */
fun stationPositionEci(stationPositionEcef: Vector3, theta: Double): Vector3 =
    ecefToEciPosition(stationPositionEcef, theta)

/** Station velocity in ECI coordinates from its ECI position. */
fun stationVelocityEci(stationPositionEci: Vector3, rotationRate: Double = omegaEarth): Vector3 =
    rotatingFrameVelocityEci(stationPositionEci, rotationRate)

/** Station velocity in ECI coordinates directly from ECEF position and angle. */
fun stationVelocityEciFromEcef(
    stationPositionEcef: Vector3,
    theta: Double,
    rotationRate: Double = omegaEarth,
): Vector3 = stationVelocityEci(
    stationPositionEci(stationPositionEcef, theta),
    rotationRate,
)

/**
 * Construct a spherical-Earth station position from geocentric latitude and
 * longitude in radians.
 */
fun stationEcefFromGeocentricLatLon(
    latitude: Double,
    longitude: Double,
    radius: Double = rEarth,
): Vector3 {
    require(latitude.isFinite()) { "latitude must be finite" }
    require(longitude.isFinite()) { "longitude must be finite" }
    require(radius > 0.0 && radius.isFinite()) { "radius must be finite and positive" }
    val cosLatitude = cos(latitude)
    return mk.ndarray(mk[
        radius * cosLatitude * cos(longitude),
        radius * cosLatitude * sin(longitude),
        radius * sin(latitude),
    ])
}

/** Convenience wrapper for [stationEcefFromGeocentricLatLon] with degree inputs. */
fun stationEcefFromGeocentricLatLonDegrees(
    latitudeDegrees: Double,
    longitudeDegrees: Double,
    radius: Double = rEarth,
): Vector3 = stationEcefFromGeocentricLatLon(
    latitudeDegrees * PI / 180.0,
    longitudeDegrees * PI / 180.0,
    radius,
)

/** Earth rotation angle after [timeSeconds] from [initialAngle]. */
fun earthRotationAngle(
    timeSeconds: Double,
    initialAngle: Double = 0.0,
    rotationRate: Double = omegaEarth,
): Double {
    require(timeSeconds.isFinite()) { "timeSeconds must be finite" }
    require(initialAngle.isFinite()) { "initialAngle must be finite" }
    require(rotationRate.isFinite()) { "rotationRate must be finite" }
    return initialAngle + rotationRate * timeSeconds
}

/** Geometric range between an ECI spacecraft position and station position. */
fun range(spacecraftPositionEci: Vector3, stationPositionEci: Vector3): Double {
    requireVector3(spacecraftPositionEci, "spacecraftPositionEci")
    requireVector3(stationPositionEci, "stationPositionEci")
    return (spacecraftPositionEci - stationPositionEci).norm()
}

/** Geometric range-rate between ECI spacecraft and station state vectors. */
fun rangeRate(
    spacecraftPositionEci: Vector3,
    spacecraftVelocityEci: Vector3,
    stationPositionEci: Vector3,
    stationVelocityEci: Vector3,
): Double {
    requireVector3(spacecraftPositionEci, "spacecraftPositionEci")
    requireVector3(spacecraftVelocityEci, "spacecraftVelocityEci")
    requireVector3(stationPositionEci, "stationPositionEci")
    requireVector3(stationVelocityEci, "stationVelocityEci")
    val relativePosition = spacecraftPositionEci - stationPositionEci
    val relativeVelocity = spacecraftVelocityEci - stationVelocityEci
    val rho = relativePosition.norm()
    require(rho > 0.0) { "range must be positive" }
    return (relativePosition dot relativeVelocity) / rho
}

/** Combined [range] and [rangeRate] measurement in [rho, rhoDot] order. */
fun rangeAndRangeRate(
    spacecraftPositionEci: Vector3,
    spacecraftVelocityEci: Vector3,
    stationPositionEci: Vector3,
    stationVelocityEci: Vector3,
): Vector2 = mk.ndarray(mk[
    range(spacecraftPositionEci, stationPositionEci),
    rangeRate(spacecraftPositionEci, spacecraftVelocityEci, stationPositionEci, stationVelocityEci),
])

/**
 * Combined range and range-rate measurement from a six-element ECI state and
 * a station ECEF position.
 */
fun rangeAndRangeRateFromState(
    stateEci: MultiArray<Double, D1>,
    stationPositionEcef: Vector3,
    theta: Double,
    rotationRate: Double = omegaEarth,
): Vector2 {
    require(stateEci.size == 6) { "stateEci must be [r; v] with 6 elements" }
    val spacecraftPositionEci = stateEci.slice<Double, D1, D1>(0..2)
    val spacecraftVelocityEci = stateEci.slice<Double, D1, D1>(3..5)
    val stationPosition = stationPositionEci(stationPositionEcef, theta)
    val stationVelocity = stationVelocityEci(stationPosition, rotationRate)
    return rangeAndRangeRate(
        spacecraftPositionEci,
        spacecraftVelocityEci,
        stationPosition,
        stationVelocity,
    )
}

/** Course-friendly overload for a six-element ECI state and station ECEF position. */
fun rangeAndRangeRate(
    stateEci: MultiArray<Double, D1>,
    stationPositionEcef: Vector3,
    theta: Double,
    rotationRate: Double = omegaEarth,
): Vector2 = rangeAndRangeRateFromState(stateEci, stationPositionEcef, theta, rotationRate)

/** Elevation angle above the station's local radial horizon, in radians. */
fun elevation(spacecraftPositionEci: Vector3, stationPositionEci: Vector3): Double {
    requireVector3(spacecraftPositionEci, "spacecraftPositionEci")
    requireVector3(stationPositionEci, "stationPositionEci")
    val lineOfSight = spacecraftPositionEci - stationPositionEci
    val rho = lineOfSight.norm()
    val stationNorm = stationPositionEci.norm()
    require(rho > 0.0) { "range must be positive" }
    require(stationNorm > 0.0) { "station position must not be the origin" }
    val sinElevation = (lineOfSight dot stationPositionEci) / (rho * stationNorm)
    return asin(sinElevation.coerceIn(-1.0, 1.0))
}

/** Whether a spacecraft is at or above [minimumElevation] from the station. */
fun isVisible(
    spacecraftPositionEci: Vector3,
    stationPositionEci: Vector3,
    minimumElevation: Double = 0.0,
): Boolean {
    require(minimumElevation.isFinite()) { "minimumElevation must be finite" }
    return elevation(spacecraftPositionEci, stationPositionEci) >= minimumElevation
}

private fun requireVector3(vector: Vector3, name: String) {
    require(vector.size == 3) { "$name must be a 3-vector, got ${vector.size} elements" }
}
