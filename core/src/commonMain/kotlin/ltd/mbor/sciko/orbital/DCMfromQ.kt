package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get

/**
 * Converts a quaternion to a direction cosine matrix (DCM).
 *
 * @param q Quaternion represented as a 1‑dimensional array of length 4.
 *          The scalar part is expected to be the last element (q[3]).
 * @return 3x3 direction cosine matrix.
 */
fun dcmFromQ(q: MultiArray<Double, D1>): MultiArray<Double, D2> {
    val q1 = q[0]
    val q2 = q[1]
    val q3 = q[2]
    val q4 = q[3]

    val a11 = q1 * q1 - q2 * q2 - q3 * q3 + q4 * q4
    val a12 = 2.0 * (q1 * q2 + q3 * q4)
    val a13 = 2.0 * (q1 * q3 - q2 * q4)

    val a21 = 2.0 * (q1 * q2 - q3 * q4)
    val a22 = -q1 * q1 + q2 * q2 - q3 * q3 + q4 * q4
    val a23 = 2.0 * (q2 * q3 + q1 * q4)

    val a31 = 2.0 * (q1 * q3 + q2 * q4)
    val a32 = 2.0 * (q2 * q3 - q1 * q4)
    val a33 = -q1 * q1 - q2 * q2 + q3 * q3 + q4 * q4

    return mk.ndarray(
        mk[
            mk[a11, a12, a13],
            mk[a21, a22, a23],
            mk[a31, a32, a33]
        ]
    )
}
