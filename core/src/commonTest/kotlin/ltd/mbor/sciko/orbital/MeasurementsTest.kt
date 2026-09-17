package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class MeasurementsTest {
    private val stationEcef = mk.ndarray(mk[6378.0, 0.0, 0.0])
    private val spacecraftPosition = mk.ndarray(mk[7000.0, 0.0, 0.0])
    private val spacecraftVelocity = mk.ndarray(mk[0.0, 7.5, 0.0])

    @Test
    fun `ECEF to ECI rotation and station velocity follow Earth rotation`() {
        assertVectorClose(stationEcef, stationPositionEci(stationEcef, 0.0))
        assertVectorClose(
            mk.ndarray(mk[0.0, 6378.0, 0.0]),
            stationPositionEci(stationEcef, PI / 2.0),
        )
        assertVectorClose(
            mk.ndarray(mk[-2.0, 1.0, 0.0]),
            stationVelocityEci(mk.ndarray(mk[1.0, 2.0, 3.0]), rotationRate = 1.0),
        )
    }

    @Test
    fun `range and range rate use relative ECI state`() {
        val stationVelocity = stationVelocityEci(stationEcef)
        val measurement = rangeAndRangeRate(
            spacecraftPosition,
            spacecraftVelocity,
            stationEcef,
            stationVelocity,
        )

        assertEquals(622.0, measurement[0], 1e-9)
        assertEquals(0.0, measurement[1], 1e-9)

        val state = mk.ndarray(mk[7000.0, 0.0, 0.0, 0.0, 7.5, 0.0])
        assertVectorClose(
            measurement,
            rangeAndRangeRateFromState(state, stationEcef, 0.0),
        )
    }

    @Test
    fun `range and range rate account for Earth rotation angle`() {
        val state = mk.ndarray(mk[7000.0, 0.0, 0.0, 0.0, 7.5, 0.0])
        val theta = 122.0 * PI / 180.0
        val measurement = rangeAndRangeRate(state, stationEcef, theta)

        assertEquals(11704.547617994953, measurement[0], 1e-9)
        assertEquals(-3.230623072777744, measurement[1], 1e-12)
    }

    @Test
    fun `station latitude longitude and Earth angle helpers are parameterized`() {
        assertVectorClose(
            mk.ndarray(mk[0.0, 6378.0, 0.0]),
            stationEcefFromGeocentricLatLon(0.0, PI / 2.0),
        )
        assertVectorClose(
            mk.ndarray(mk[0.0, 0.0, 2.0]),
            stationEcefFromGeocentricLatLonDegrees(90.0, 0.0, radius = 2.0),
        )
        assertEquals(3.5, earthRotationAngle(2.0, initialAngle = 1.5, rotationRate = 1.0))
    }

    @Test
    fun `elevation and visibility use a configurable elevation mask`() {
        assertEquals(PI / 2.0, elevation(spacecraftPosition, stationEcef), 1e-14)
        assertTrue(isVisible(spacecraftPosition, stationEcef, minimumElevation = 10.0 * PI / 180.0))

        val belowHorizon = mk.ndarray(mk[0.0, 7000.0, 0.0])
        assertTrue(elevation(belowHorizon, stationEcef) < 0.0)
        assertTrue(!isVisible(belowHorizon, stationEcef))
    }

    @Test
    fun `invalid geometry is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            stationPositionEci(mk.ndarray(mk[1.0, 2.0]), 0.0)
        }
        assertEquals(0.0, range(spacecraftPosition, spacecraftPosition), 1e-12)
        assertFailsWith<IllegalArgumentException> {
            rangeRate(
                spacecraftPosition,
                mk.ndarray(mk[0.0, 0.0, 0.0]),
                spacecraftPosition,
                mk.ndarray(mk[0.0, 0.0, 0.0]),
            )
        }
        assertFailsWith<IllegalArgumentException> {
            elevation(spacecraftPosition, mk.ndarray(mk[0.0, 0.0, 0.0]))
        }
        assertFailsWith<IllegalArgumentException> {
            rangeAndRangeRateFromState(mk.ndarray(mk[1.0, 2.0, 3.0]), stationEcef, 0.0)
        }
    }

    private fun assertVectorClose(expected: Vector3, actual: Vector3) {
        for (i in 0 until expected.size) {
            assertEquals(expected[i], actual[i], 1e-12)
        }
    }
}
