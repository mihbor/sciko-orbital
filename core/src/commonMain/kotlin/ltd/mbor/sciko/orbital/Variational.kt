package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get

typealias StateVector = MultiArray<Double, D1>
typealias StateMatrix = MultiArray<Double, D2>

/** Final state and state-transition matrix from a variational propagation. */
data class StateTransitionResult(
    val state: StateVector,
    val stateTransitionMatrix: StateMatrix,
)

/**
 * Propagate a state and its state-transition matrix.
 *
 * For a system `xDot = f(t, x)`, the transition matrix is integrated from
 * `Phi(t0) = I` using `PhiDot = A(t, x) Phi`, where `A` is supplied by
 * [stateJacobian]. The state and matrix are flattened only internally; the
 * callbacks receive ordinary Multik arrays.
 */
fun propagateStateAndTransition(
    tspan: ClosedRange<Double>,
    initialState: StateVector,
    tolerance: Double = 1e-12,
    stateRate: (Double, StateVector) -> StateVector,
    stateJacobian: (Double, StateVector) -> StateMatrix,
): StateTransitionResult {
    val t0 = tspan.start
    val tf = tspan.endInclusive
    require(t0.isFinite() && tf.isFinite()) { "tspan must have finite endpoints" }
    require(tf >= t0) { "tspan must be non-decreasing" }
    require(initialState.size > 0) { "initialState must not be empty" }
    require(tolerance > 0.0 && tolerance.isFinite()) {
        "tolerance must be finite and positive"
    }

    val stateSize = initialState.size
    val augmented = DoubleArray(stateSize + stateSize * stateSize)
    for (index in 0 until stateSize) augmented[index] = initialState[index]
    for (index in 0 until stateSize) {
        augmented[stateSize + index * stateSize + index] = 1.0
    }

    val (_, states) = rkf45(
        tspan = t0..tf,
        y0 = mk.ndarray(augmented.toList()),
        tolerance = tolerance,
        odeFunction = { time, current ->
            variationalRates(time, current, stateSize, stateRate, stateJacobian)
        },
    )
    val final = states.last()
    val finalState = mk.ndarray(List(stateSize) { final[it] })
    val transition = mk.ndarray(
        List(stateSize) { row ->
            List(stateSize) { column ->
                final[stateSize + row * stateSize + column]
            }
        },
    )
    return StateTransitionResult(finalState, transition)
}

private fun variationalRates(
    time: Double,
    augmented: StateVector,
    stateSize: Int,
    stateRate: (Double, StateVector) -> StateVector,
    stateJacobian: (Double, StateVector) -> StateMatrix,
): StateVector {
    require(augmented.size == stateSize + stateSize * stateSize) {
        "augmented state has an unexpected size"
    }

    val state = mk.ndarray(List(stateSize) { augmented[it] })
    val rate = stateRate(time, state)
    require(rate.size == stateSize) {
        "stateRate returned ${rate.size} elements; expected $stateSize"
    }
    val jacobian = stateJacobian(time, state)
    require(jacobian.shape[0] == stateSize && jacobian.shape[1] == stateSize) {
        "stateJacobian must be ${stateSize}x$stateSize"
    }

    val result = MutableList(stateSize + stateSize * stateSize) { 0.0 }
    for (index in 0 until stateSize) result[index] = rate[index]
    for (row in 0 until stateSize) {
        for (column in 0 until stateSize) {
            result[stateSize + row * stateSize + column] =
                (0 until stateSize).sumOf { index ->
                    jacobian[row, index] * augmented[stateSize + index * stateSize + column]
                }
        }
    }
    return mk.ndarray(result)
}
