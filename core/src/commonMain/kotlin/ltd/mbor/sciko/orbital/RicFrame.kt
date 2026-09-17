package ltd.mbor.sciko.orbital

import ltd.mbor.sciko.linalg.cross
import ltd.mbor.sciko.linalg.norm
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.jetbrains.kotlinx.multik.ndarray.operations.div

/**
 * Returns the rotation whose columns are the RIC basis vectors expressed in
 * inertial coordinates: [R_hat, I_hat, C_hat].
 *
 * The input state is ordered as [r_x, r_y, r_z, v_x, v_y, v_z]. Therefore the
 * returned matrix maps RIC components into inertial components. Its transpose
 * maps inertial components into RIC components.
 */
fun ricToEciRotation(state: Vector3): Matrix3 {
    require(state.size == 6) { "state must be [r; v] with 6 elements" }

    val r = state.slice<Double, D1, D1>(0..2)
    val v = state.slice<Double, D1, D1>(3..5)
    val rNorm = r.norm()
    require(rNorm > 0.0) { "position must not be the origin" }

    val rHat = r / rNorm
    val h = r cross v
    val hNorm = h.norm()
    require(hNorm > 0.0) { "position and velocity must define a non-degenerate orbit plane" }

    val cHat = h / hNorm
    val iHat = cHat cross rHat

    // Rows are the RIC basis vectors in ECI coordinates, so transpose to
    // return the requested matrix whose columns are those basis vectors.
    return rHat.cat(iHat).cat(cHat).reshape(3, 3).transpose()
}

/** Transform a 3x3 acceleration spectral density from RIC coordinates to ECI. */
fun qEciFromRic(state: Vector3, qRic: Matrix3): Matrix3 {
    require(qRic.shape[0] == 3 && qRic.shape[1] == 3) { "qRic must be a 3x3 matrix" }
    val rotation = ricToEciRotation(state)
    return rotation dot qRic dot rotation.transpose()
}
