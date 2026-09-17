package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RicFrameTest {
    private val state = mk.ndarray(mk[7000.0, 1000.0, 3000.0, -1.0, 7.0, 1.0])

    @Test
    fun `RIC rotation maps RIC basis into ECI`() {
        val actual = ricToEciRotation(state)
        val expected = arrayOf(
            doubleArrayOf(0.9113223768657671, -0.19015307541661133, -0.3651483716701107),
            doubleArrayOf(0.13018891098082386, 0.9745345115101332, -0.18257418583505536),
            doubleArrayOf(0.39056673294247163, 0.11884567213538208, 0.9128709291752769),
        )

        for (row in 0 until 3) {
            for (column in 0 until 3) {
                assertEquals(expected[row][column], actual[row, column], 1e-14)
            }
        }
    }

    @Test
    fun `RIC rotation is orthonormal`() {
        val rotation = ricToEciRotation(state)
        for (columnA in 0 until 3) {
            for (columnB in 0 until 3) {
                val dot = (0 until 3).sumOf { row -> rotation[row, columnA] * rotation[row, columnB] }
                assertEquals(if (columnA == columnB) 1.0 else 0.0, dot, 1e-14)
            }
        }
    }

    @Test
    fun `RIC spectral density transforms to ECI`() {
        val qRic = mk.ndarray(mk[
            mk[1e-14, 0.0, 0.0],
            mk[0.0, 1e-12, 0.0],
            mk[0.0, 0.0, 1e-13],
        ])
        val actual = qEciFromRic(state, qRic)
        val expected = arrayOf(
            doubleArrayOf(5.779661016949152e-14, -1.7745762711864405e-13, -5.2372881355932205e-14),
            doubleArrayOf(-1.7745762711864404e-13, 9.532203389830509e-13, 9.966101694915253e-14),
            doubleArrayOf(-5.23728813559322e-14, 9.966101694915255e-14, 9.898305084745764e-14),
        )

        for (row in 0 until 3) {
            for (column in 0 until 3) {
                assertEquals(expected[row][column], actual[row, column], 1e-25)
            }
        }
    }

    @Test
    fun `degenerate orbital frames are rejected`() {
        assertFailsWith<IllegalArgumentException> {
            ricToEciRotation(mk.ndarray(mk[0.0, 0.0, 0.0, 1.0, 0.0, 0.0]))
        }
        assertFailsWith<IllegalArgumentException> {
            ricToEciRotation(mk.ndarray(mk[1.0, 0.0, 0.0, 2.0, 0.0, 0.0]))
        }
        assertFailsWith<IllegalArgumentException> {
            qEciFromRic(state, mk.ndarray(mk[
                mk[1.0, 0.0],
                mk[0.0, 1.0],
            ]))
        }
    }
}
