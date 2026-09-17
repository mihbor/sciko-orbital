package ltd.mbor.sciko.orbital

import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get

typealias Vector3 = MultiArray<Double, D1>
typealias Matrix3 = MultiArray<Double, D2>
typealias Matrix6 = MultiArray<Double, D2>

/**
 * Zonal gravitational model through degree three.
 *
 * The coefficients use the unnormalised zonal convention
 * `J2` = -C20 and `J3` = -C30. All distances are in the same units as
 * [referenceRadius], and [mu] has the corresponding distance^3/time^2 units.
 * The returned accelerations therefore have distance/time^2 units.
 *
 * No planetary constants are assumed. Construct a model with the constants
 * appropriate for the central body and set [j2] and [j3] to zero when only
 * the point-mass term is required.
 */
data class GravityModel(
    val mu: Double,
    val referenceRadius: Double,
    val j2: Double = 0.0,
    val j3: Double = 0.0,
) {
    init {
        require(mu > 0.0 && mu.isFinite()) { "mu must be finite and positive" }
        require(referenceRadius > 0.0 && referenceRadius.isFinite()) {
            "referenceRadius must be finite and positive"
        }
        require(j2.isFinite()) { "j2 must be finite" }
        require(j3.isFinite()) { "j3 must be finite" }
    }

    /** Total central, J2, and J3 acceleration, in the model's units. */
    fun acceleration(position: Vector3): Vector3 = addVectors(
        aMu(position),
        aJ2(position),
        aJ3(position),
    )

    /** Central point-mass acceleration. */
    fun aMu(position: Vector3): Vector3 {
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val scale = -mu / (r * r * r)
        return vector(scale * x, scale * y, scale * z)
    }

    /** J2 acceleration contribution using this model's J2 coefficient. */
    fun aJ2(position: Vector3): Vector3 = aJ2WithCoefficient(position, j2)

    /** J2 acceleration contribution for an explicitly supplied coefficient. */
    fun aJ2WithCoefficient(position: Vector3, coefficient: Double): Vector3 {
        require(coefficient.isFinite()) { "coefficient must be finite" }
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val r2 = r * r
        val s2 = z * z / r2
        val scale = 3.0 * mu * coefficient * referenceRadius * referenceRadius /
            (2.0 * r2 * r2 * r)
        return vector(
            scale * x * (5.0 * s2 - 1.0),
            scale * y * (5.0 * s2 - 1.0),
            scale * z * (5.0 * s2 - 3.0),
        )
    }

    /** Acceleration partial with respect to the J2 coefficient. */
    fun aJ2Coefficient(position: Vector3): Vector3 = aJ2WithCoefficient(position, 1.0)

    /** J3 acceleration contribution using this model's J3 coefficient. */
    fun aJ3(position: Vector3): Vector3 = aJ3WithCoefficient(position, j3)

    /** J3 acceleration contribution for an explicitly supplied coefficient. */
    fun aJ3WithCoefficient(position: Vector3, coefficient: Double): Vector3 {
        require(coefficient.isFinite()) { "coefficient must be finite" }
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val r2 = r * r
        val s2 = z * z / r2
        val s4 = s2 * s2
        val scale = mu * coefficient * referenceRadius * referenceRadius * referenceRadius /
            (2.0 * r2 * r2 * r)
        return vector(
            scale * (5.0 * x * z / r2) * (7.0 * s2 - 3.0),
            scale * (5.0 * y * z / r2) * (7.0 * s2 - 3.0),
            scale * (3.0 - 30.0 * s2 + 35.0 * s4),
        )
    }

    /** Acceleration partial with respect to the J3 coefficient. */
    fun aJ3Coefficient(position: Vector3): Vector3 = aJ3WithCoefficient(position, 1.0)

    /** Position Jacobian of the central acceleration. */
    fun daMuDr(position: Vector3): Matrix3 {
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val r2 = r * r
        val scale = mu / (r2 * r2 * r)
        return mk.ndarray(mk[
            mk[scale * (3.0 * x * x - r2), scale * 3.0 * x * y, scale * 3.0 * x * z],
            mk[scale * 3.0 * x * y, scale * (3.0 * y * y - r2), scale * 3.0 * y * z],
            mk[scale * 3.0 * x * z, scale * 3.0 * y * z, scale * (3.0 * z * z - r2)],
        ])
    }

    /** Position Jacobian of the J2 acceleration. */
    fun daJ2Dr(position: Vector3): Matrix3 {
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val r2 = r * r
        val q = z * z / r2
        val scale = 3.0 * mu * j2 * referenceRadius * referenceRadius /
            (2.0 * r2 * r2 * r)
        val xy = 5.0 - 35.0 * q
        val xz = 15.0 - 35.0 * q
        return mk.ndarray(mk[
            mk[
                scale * (5.0 * q - 1.0 + xy * x * x / r2),
                scale * xy * x * y / r2,
                scale * xz * x * z / r2,
            ],
            mk[
                scale * xy * x * y / r2,
                scale * (5.0 * q - 1.0 + xy * y * y / r2),
                scale * xz * y * z / r2,
            ],
            mk[
                scale * xz * x * z / r2,
                scale * xz * y * z / r2,
                scale * (-3.0 + 30.0 * q - 35.0 * q * q),
            ],
        ])
    }

    /** Position Jacobian of the J3 acceleration. */
    fun daJ3Dr(position: Vector3): Matrix3 {
        val (x, y, z) = position.components()
        val r = radius(x, y, z)
        val r2 = r * r
        val p = x / r
        val t = y / r
        val u = z / r
        val q = u * u
        val c = mu * j3 * referenceRadius * referenceRadius * referenceRadius /
            (2.0 * r2 * r2 * r)
        val l = 5.0 * u * (7.0 * q - 3.0)
        val lu = 15.0 * (7.0 * q - 1.0)
        val h = 3.0 - 30.0 * q + 35.0 * q * q
        val hu = 20.0 * u * (7.0 * q - 3.0)
        val scale = c / r
        return mk.ndarray(mk[
            mk[
                scale * (l * (1.0 - 6.0 * p * p) - p * p * u * lu),
                scale * (-6.0 * p * t * l - p * t * u * lu),
                scale * (-6.0 * p * u * l + p * (1.0 - q) * lu),
            ],
            mk[
                scale * (-6.0 * p * t * l - p * t * u * lu),
                scale * (l * (1.0 - 6.0 * t * t) - t * t * u * lu),
                scale * (-6.0 * t * u * l + t * (1.0 - q) * lu),
            ],
            mk[
                scale * (-p * (5.0 * h + u * hu)),
                scale * (-t * (5.0 * h + u * hu)),
                scale * (-5.0 * u * h + (1.0 - q) * hu),
            ],
        ])
    }

    /** Position Jacobian of the total configured acceleration. */
    fun daDr(position: Vector3): Matrix3 = addMatrices(
        daMuDr(position),
        daJ2Dr(position),
        daJ3Dr(position),
    )

    /**
     * Jacobian of [rDot; vDot] for a state ordered as [r_x, r_y, r_z, v_x, v_y, v_z].
     */
    fun stateJacobian(position: Vector3): Matrix6 {
        val ar = daDr(position)
        return mk.ndarray(mk[
            mk[0.0, 0.0, 0.0, 1.0, 0.0, 0.0],
            mk[0.0, 0.0, 0.0, 0.0, 1.0, 0.0],
            mk[0.0, 0.0, 0.0, 0.0, 0.0, 1.0],
            mk[ar[0, 0], ar[0, 1], ar[0, 2], 0.0, 0.0, 0.0],
            mk[ar[1, 0], ar[1, 1], ar[1, 2], 0.0, 0.0, 0.0],
            mk[ar[2, 0], ar[2, 1], ar[2, 2], 0.0, 0.0, 0.0],
        ])
    }

    private fun radius(x: Double, y: Double, z: Double): Double {
        val r2 = x * x + y * y + z * z
        require(r2 > 0.0) { "Position must not be the origin" }
        return kotlin.math.sqrt(r2)
    }

    private fun vector(x: Double, y: Double, z: Double): Vector3 = mk.ndarray(mk[x, y, z])

    private fun addVectors(vararg vectors: Vector3): Vector3 = vector(
        vectors.sumOf { it[0] },
        vectors.sumOf { it[1] },
        vectors.sumOf { it[2] },
    )

    private fun addMatrices(vararg matrices: Matrix3): Matrix3 = mk.ndarray(mk[
        mk[
            matrices.sumOf { it[0, 0] },
            matrices.sumOf { it[0, 1] },
            matrices.sumOf { it[0, 2] },
        ],
        mk[
            matrices.sumOf { it[1, 0] },
            matrices.sumOf { it[1, 1] },
            matrices.sumOf { it[1, 2] },
        ],
        mk[
            matrices.sumOf { it[2, 0] },
            matrices.sumOf { it[2, 1] },
            matrices.sumOf { it[2, 2] },
        ],
    ])
}

private fun Vector3.components(): Triple<Double, Double, Double> {
    require(size == 3) { "Expected a 3-element position vector, got shape $shape" }
    return Triple(this[0], this[1], this[2])
}
