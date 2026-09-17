package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.abs
import kotlin.math.max
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class GravityTest {
    private val gravity = GravityModel(
        mu = 398600.4415,
        referenceRadius = 6378.1363,
        j2 = 1.082626925638815e-3,
        j3 = -2.5326613168887e-6,
    )
    private val position = mk.ndarray(mk[7000.0, 1000.0, 2000.0])

    @Test
    fun `central acceleration follows inverse square law`() {
        val r = kotlin.math.sqrt(7000.0 * 7000.0 + 1000.0 * 1000.0 + 2000.0 * 2000.0)
        val scale = -gravity.mu / (r * r * r)

        assertVectorClose(
            vector(scale * 7000.0, scale * 1000.0, scale * 2000.0),
            gravity.aMu(position),
        )
    }

    @Test
    fun `configured coefficients scale their contributions`() {
        val j2 = gravity.aJ2(position)
        val j3 = gravity.aJ3(position)
        val modelWithoutZonals = gravity.copy(j2 = 0.0, j3 = 0.0)

        assertVectorClose(vector(0.0, 0.0, 0.0), modelWithoutZonals.aJ2(position))
        assertVectorClose(vector(0.0, 0.0, 0.0), modelWithoutZonals.aJ3(position))
        assertVectorClose(
            scaleVector(gravity.j2, gravity.aJ2Coefficient(position)),
            j2,
        )
        assertVectorClose(
            scaleVector(gravity.j3, gravity.aJ3Coefficient(position)),
            j3,
        )
    }

    @Test
    fun `analytical position Jacobians agree with finite differences`() {
        val cases = listOf(
            gravity::aMu to gravity::daMuDr,
            gravity::aJ2 to gravity::daJ2Dr,
            gravity::aJ3 to gravity::daJ3Dr,
        )

        cases.forEach { (acceleration, analyticalJacobian) ->
            assertMatrixClose(analyticalJacobian(position), centralFiniteDifference(acceleration, position))
        }
    }

    @Test
    fun `state Jacobian has kinematic identity and configured acceleration blocks`() {
        val state = gravity.stateJacobian(position)
        assertEquals(6, state.shape[0])
        assertEquals(6, state.shape[1])
        assertEquals(1.0, state[0, 3])
        assertEquals(1.0, state[1, 4])
        assertEquals(1.0, state[2, 5])

        val positionJacobian = gravity.daDr(position)
        for (row in 0 until 3) {
            for (column in 0 until 3) {
                assertEquals(0.0, state[row, column])
                assertEquals(0.0, state[row + 3, column + 3])
                assertEquals(positionJacobian[row, column], state[row + 3, column])
            }
        }
    }

    @Test
    fun `invalid positions and model constants are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            gravity.aMu(mk.ndarray(mk[0.0, 0.0, 0.0]))
        }
        assertFailsWith<IllegalArgumentException> {
            gravity.aMu(mk.ndarray(mk[1.0, 2.0]))
        }
        assertFailsWith<IllegalArgumentException> {
            GravityModel(mu = 0.0, referenceRadius = 6378.0)
        }
        assertFailsWith<IllegalArgumentException> {
            GravityModel(mu = 398600.0, referenceRadius = 0.0)
        }
    }

    private fun assertVectorClose(expected: Vector3, actual: Vector3) {
        for (i in 0 until 3) {
            assertEquals(expected[i], actual[i], absoluteTolerance = 1e-15)
        }
    }

    private fun assertMatrixClose(expected: Matrix3, actual: Matrix3) {
        for (row in 0 until 3) {
            for (column in 0 until 3) {
                val tolerance = 1e-8 * max(abs(expected[row, column]), 1e-10)
                assertTrue(
                    abs(expected[row, column] - actual[row, column]) <= tolerance,
                    "[$row,$column]: expected ${expected[row, column]}, actual ${actual[row, column]}, tolerance $tolerance",
                )
            }
        }
    }

    private fun centralFiniteDifference(
        acceleration: (Vector3) -> Vector3,
        at: Vector3,
        step: Double = 1e-2,
    ): Matrix3 {
        val rows = MutableList(3) { MutableList(3) { 0.0 } }
        for (column in 0 until 3) {
            val plus = List(3) { index -> at[index] + if (index == column) step else 0.0 }
            val minus = List(3) { index -> at[index] - if (index == column) step else 0.0 }
            val plusAcceleration = acceleration(vectorFrom(plus))
            val minusAcceleration = acceleration(vectorFrom(minus))
            for (row in 0 until 3) {
                rows[row][column] = (plusAcceleration[row] - minusAcceleration[row]) / (2.0 * step)
            }
        }
        return mk.ndarray(mk[
            mk[rows[0][0], rows[0][1], rows[0][2]],
            mk[rows[1][0], rows[1][1], rows[1][2]],
            mk[rows[2][0], rows[2][1], rows[2][2]],
        ])
    }

    private fun scaleVector(scale: Double, vector: Vector3): Vector3 = mk.ndarray(
        mk[scale * vector[0], scale * vector[1], scale * vector[2]],
    )

    private fun vector(x: Double, y: Double, z: Double): MultiArray<Double, D1> = mk.ndarray(mk[x, y, z])

    private fun vectorFrom(values: List<Double>): Vector3 = mk.ndarray(mk[values[0], values[1], values[2]])
}
