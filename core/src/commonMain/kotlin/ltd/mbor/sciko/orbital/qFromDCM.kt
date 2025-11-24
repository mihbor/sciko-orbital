package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.eig
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.div

/**
 * Convert a direction cosine matrix (3x3) to a quaternion (4x1).
 * The returned quaternion follows the convention where the 4th element is the scalar part.
 */
fun qFromDcm(Q: MultiArray<Double, D2>): MultiArray<Double, D1> {
    // Build the 4x4 K3 matrix as in the original MATLAB implementation
    val k11 = Q[0, 0] - Q[1, 1] - Q[2, 2]
    val k12 = Q[1, 0] + Q[0, 1]
    val k13 = Q[2, 0] + Q[0, 2]
    val k14 = Q[1, 2] - Q[2, 1]

    val k22 = Q[1, 1] - Q[0, 0] - Q[2, 2]
    val k23 = Q[2, 1] + Q[1, 2]
    val k24 = Q[2, 0] - Q[0, 2]

    val k33 = Q[2, 2] - Q[0, 0] - Q[1, 1]
    val k34 = Q[0, 1] - Q[1, 0]

    val k44 = Q[0, 0] + Q[1, 1] + Q[2, 2]

    val K3 = mk.ndarray(
        mk[
            mk[k11, k12, k13, k14],
            mk[k12, k22, k23, k24],
            mk[k13, k23, k33, k34],
            mk[k14, k24, k34, k44]
        ]
    ) / 3.0
    val (eigval, eigvec) = mk.linalg.eig(K3)
    val diag = (0..3).map { eigval[it][it] }    // extract diagonal
    val (i, max) = diag.withIndex().maxBy { it.value } // value and index
    return eigvec.transpose()[i]
}
