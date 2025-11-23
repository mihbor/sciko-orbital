package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.acos
import kotlin.math.atan2

fun atan2d_0_360(y: Double, x: Double): Double {
    var angle = atan2(y, x).toDegrees()
    if (angle < 0) angle += 360.0
    return angle
}

fun acosd(x: Double): Double = acos(x).toDegrees()

fun dcmToEuler(Q: MultiArray<Double, D2>): Triple<Double, Double, Double> {
    val alpha = atan2d_0_360(Q[2, 0], -Q[2, 1])
    val beta = acosd(Q[2, 2])
    val gamma = atan2d_0_360(Q[0, 2], Q[1, 2])
    return Triple(alpha, beta, gamma)
}
