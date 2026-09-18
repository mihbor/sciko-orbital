package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.exp
import kotlin.test.Test
import kotlin.test.assertEquals

class RKF45Test {
    @Test
    fun `integrates a scalar exponential`() {
        val (_, states) = rkf45(
            tspan = 0.0..2.0,
            y0 = mk.ndarray(mk[3.0]),
            tolerance = 1e-12,
            odeFunction = { _, state -> state },
        )

        assertEquals(3.0 * exp(2.0), states.last()[0], 1e-10)
    }
}
