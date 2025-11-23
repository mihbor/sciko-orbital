package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.sqrt

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
            mk[k11 / 3.0, k12 / 3.0, k13 / 3.0, k14 / 3.0],
            mk[k12 / 3.0, k22 / 3.0, k23 / 3.0, k24 / 3.0],
            mk[k13 / 3.0, k23 / 3.0, k33 / 3.0, k34 / 3.0],
            mk[k14 / 3.0, k24 / 3.0, k34 / 3.0, k44 / 3.0]
        ]
    )

    // Power iteration to find the dominant eigenvector of the symmetric matrix K3
    val n = 4
    var v = DoubleArray(n) { 0.0 }
    v[0] = 1.0 // initial guess (deterministic)

    val maxIter = 1000
    val tol = 1e-12

    fun matVecMul(mat: MultiArray<Double, D2>, vec: DoubleArray): DoubleArray {
        val out = DoubleArray(n) { 0.0 }
        for (i in 0 until n) {
            var s = 0.0
            for (j in 0 until n) {
                s += mat[i, j] * vec[j]
            }
            out[i] = s
        }
        return out
    }

    fun norm(vec: DoubleArray): Double {
        var s = 0.0
        for (i in vec.indices) s += vec[i] * vec[i]
        return sqrt(s)
    }

    var iter = 0
    while (iter < maxIter) {
        val w = matVecMul(K3, v)
        val wnorm = norm(w)
        if (wnorm == 0.0) break
        val vNext = DoubleArray(n) { i -> w[i] / wnorm }
        // compute difference
        var diff = 0.0
        for (i in 0 until n) {
            val d = vNext[i] - v[i]
            diff += d * d
        }
        if (sqrt(diff) < tol) {
            v = vNext
            break
        }
        v = vNext
        iter++
    }

    // Ensure the returned quaternion has the same orientation as MATLAB (4th element is scalar)
    return mk.ndarray(mk[v[0], v[1], v[2], v[3]])
}
