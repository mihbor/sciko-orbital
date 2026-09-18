package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class VariationalTest {
    @Test
    fun `propagates a scalar exponential and its transition`() {
        val result = propagateStateAndTransition(
            tspan = 0.0..2.0,
            initialState = mk.ndarray(mk[3.0]),
            tolerance = 1e-12,
            stateRate = { _, state -> mk.ndarray(mk[state[0]]) },
            stateJacobian = { _, _ -> mk.ndarray(mk[mk[1.0]]) },
        )

        assertEquals(3.0 * exp(2.0), result.state[0], 1e-10)
        assertEquals(exp(2.0), result.stateTransitionMatrix[0, 0], 1e-10)
    }

    @Test
    fun `initializes the transition as identity for zero duration`() {
        val result = propagateStateAndTransition(
            tspan = 4.0..4.0,
            initialState = mk.ndarray(mk[1.0, 2.0]),
            stateRate = { _, _ -> mk.ndarray(mk[0.0, 0.0]) },
            stateJacobian = { _, _ -> mk.ndarray(mk[
                mk[2.0, 0.0],
                mk[0.0, -1.0],
            ]) },
        )

        assertEquals(1.0, result.state[0])
        assertEquals(2.0, result.state[1])
        assertEquals(1.0, result.stateTransitionMatrix[0, 0])
        assertEquals(0.0, result.stateTransitionMatrix[0, 1])
        assertEquals(0.0, result.stateTransitionMatrix[1, 0])
        assertEquals(1.0, result.stateTransitionMatrix[1, 1])
    }

    @Test
    fun `rejects invalid variational inputs`() {
        val state = mk.ndarray(mk[1.0])
        assertFailsWith<IllegalArgumentException> {
            propagateStateAndTransition(
                tspan = 1.0..0.0,
                initialState = state,
                stateRate = { _, value -> value },
                stateJacobian = { _, _ -> mk.ndarray(mk[mk[1.0]]) },
            )
        }
        assertFailsWith<IllegalArgumentException> {
            propagateStateAndTransition(
                tspan = 0.0..1.0,
                initialState = state,
                tolerance = 0.0,
                stateRate = { _, value -> value },
                stateJacobian = { _, _ -> mk.ndarray(mk[mk[1.0]]) },
            )
        }
    }
}
