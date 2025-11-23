package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.orbital.*
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Kotlin port of MATLAB Example_11_26.m
 *
 * Numerically integrates Euler's equations of motion for a spinning top.
 * The quaternion is used to obtain the time history of the top's orientation.
 */
fun main() {
  // Data from Example 11.15
  val g = 9.807                 // m/s^2
  val m = 0.5                   // kg
  val d = 0.05                  // m
  val A = 12e-4                 // kg·m^2
  val B = 12e-4                 // kg·m^2
  val C = 4.5e-4                // kg·m^2
  val ws0 = 1000.0 * 2 * PI / 60.0 // rad/s

  // Precession rate (rad/s): use 0 to obtain analog of Fig. 11.34
  // val wp0 = 51.93 * 2 * PI / 60.0 // to obtain Fig. 11.33
  val wp0 = 0.0

  val wn0 = 0.0                 // rad/s
  val theta = 60.0              // deg

  // Initial triad definition
  val z = doubleArrayOf(0.0, -sin(theta.degrees), cos(theta.degrees))
  val p = doubleArrayOf(1.0, 0.0, 0.0)

  fun cross(a: DoubleArray, b: DoubleArray): DoubleArray = doubleArrayOf(
    a[1] * b[2] - a[2] * b[1],
    a[2] * b[0] - a[0] * b[2],
    a[0] * b[1] - a[1] * b[0]
  )

  fun norm(v: DoubleArray): Double = sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2])

  fun unit(v: DoubleArray): DoubleArray { val n = norm(v); return doubleArrayOf(v[0]/n, v[1]/n, v[2]/n) }

  val yv = cross(z, p)
  val xv = cross(yv, z)
  val i = unit(xv)
  val j = unit(yv)
  val k = unit(z)

  // Initial DCM (body frame axes expressed in space frame)
  val QXx: MultiArray<Double, D2> = mk.ndarray(mk[
    mk[i[0], i[1], i[2]],
    mk[j[0], j[1], j[2]],
    mk[k[0], k[1], k[2]]
  ])

  // Initial Euler angles (deg)
  val (phi0, theta0, psi0) = dcmToEuler(QXx)

  // Initial quaternion (q4 is scalar part)
  val q0 = qFromDcm(QXx)

  // Initial body-frame angular velocities (rad/s)
  val wx0 = wp0 * sin(theta0.degrees) * sin(psi0.degrees) + wn0 * cos(psi0.degrees)
  val wy0 = wp0 * sin(theta0.degrees) * cos(psi0.degrees) - wn0 * sin(psi0.degrees)
  val wz0 = ws0 + wp0 * cos(theta0.degrees)

  val t0 = 0.0
  val tf = 1.153

  val f0 = mk.ndarray(mk[
    q0[0], q0[1], q0[2], q0[3],
    wx0, wy0, wz0
  ])

  // Integrate using RKF45
  val (t, f) = rkf45(t0..tf, f0) { tNow, state ->
    rates(tNow, state, g, m, d, A, B, C)
  }

  // Recover Euler angles and their rates across the timeline
  val prec = DoubleArray(t.size)
  val nut = DoubleArray(t.size)
  val spin = DoubleArray(t.size)
  val wp = DoubleArray(t.size)
  val wn = DoubleArray(t.size)
  val ws = DoubleArray(t.size)

  for (idx in t.indices) {
    // Normalize quaternion and compute DCM
    val q = normalizeQuat(f[idx])
    val Q = dcmFromQ(q)
    val (alpha, beta, gamma) = dcmToEuler(Q)
    prec[idx] = alpha
    nut[idx] = beta
    spin[idx] = gamma

    val wx = f[idx][4]
    val wy = f[idx][5]
    val wz = f[idx][6]

    // Euler angle rates (using degrees for angles)
    val sSpin = sin(spin[idx].degrees)
    val cSpin = cos(spin[idx].degrees)
    val sNut = sin(nut[idx].degrees)
    val cNut = cos(nut[idx].degrees)
    wp[idx] = (wx * sSpin + wy * cSpin) / sNut
    wn[idx] = wx * cSpin - wy * sSpin
    ws[idx] = -wp[idx] * cNut + wz
  }

  // Minimal text output (no plotting here)
  println("Example 11.26 — Spinning top (quaternion + RKF45)")
  println("Final Euler angles (deg):")
  println("  Precession = ${prec.last()}")
  println("  Nutation   = ${nut.last()}")
  println("  Spin       = ${spin.last()}")
  println("Final rates:")
  println("  wp (rad/s) = ${wp.last()}")
  println("  wn (rad/s) = ${wn.last()}")
  println("  ws (rad/s) = ${ws.last()}")

  // Plot charts similar to the MATLAB script using plotScalar helper
  val tSec = t
  val precDeg = prec.toList()
  val nutDeg = nut.toList()
  val spinDeg = spin.toList()
  val wpRpm = wp.map { it * 60.0 / (2.0 * PI) }
  val wnDegPerSec = wn.map { it.toDegrees() }
  val wsRpm = ws.map { it * 60.0 / (2.0 * PI) }

  plotScalar(
    Triple(tSec, precDeg, "Precession angle (deg)") ,
    xLabel = "time (s)", yLabel = "deg"
  ).save("Example11_26_prec.png")

  plotScalar(
    Triple(tSec, wpRpm, "Precession rate (rpm)") ,
    xLabel = "time (s)", yLabel = "rpm"
  ).save("Example11_26_wp.png")

  plotScalar(
    Triple(tSec, nutDeg, "Nutation angle (deg)") ,
    xLabel = "time (s)", yLabel = "deg"
  ).save("Example11_26_nut.png")

  plotScalar(
    Triple(tSec, wnDegPerSec, "Nutation rate (deg/s)") ,
    xLabel = "time (s)", yLabel = "deg/s"
  ).save("Example11_26_wn.png")

  plotScalar(
    Triple(tSec, spinDeg, "Spin angle (deg)") ,
    xLabel = "time (s)", yLabel = "deg"
  ).save("Example11_26_spin.png")

  plotScalar(
    Triple(tSec, wsRpm, "Spin rate (rpm)") ,
    xLabel = "time (s)", yLabel = "rpm"
  ).save("Example11_26_ws.png")
}

private fun normalizeQuat(qIn: MultiArray<Double, D1>): MultiArray<Double, D1> {
  val q1 = qIn[0]; val q2 = qIn[1]; val q3 = qIn[2]; val q4 = qIn[3]
  val n = sqrt(q1*q1 + q2*q2 + q3*q3 + q4*q4)
  return mk.ndarray(mk[q1/n, q2/n, q3/n, q4/n])
}

private fun rates(
  @Suppress("UNUSED_PARAMETER") t: Double,
  f: MultiArray<Double, D1>,
  g: Double, m: Double, d: Double,
  A: Double, B: Double, C: Double,
): MultiArray<Double, D1> {
  // State unpack
  val q = normalizeQuat(mk.ndarray(mk[f[0], f[1], f[2], f[3]]))
  val wx = f[4]; val wy = f[5]; val wz = f[6]

  // DCM from quaternion
  val Q = dcmFromQ(q)

  // Body components of the moment of the weight vector about pivot: M = Q * [-m*g*d*Q(3,2); m*g*d*Q(3,1); 0]
  val Q32 = Q[2, 1]
  val Q31 = Q[2, 0]
  val v1 = -m * g * d * Q32
  val v2 =  m * g * d * Q31
  val v3 = 0.0

  // M = Q * v (3x3 times 3x1)
  fun rowDot(r: Int): Double = Q[r,0]*v1 + Q[r,1]*v2 + Q[r,2]*v3
  val M1 = rowDot(0)
  val M2 = rowDot(1)
  val M3 = rowDot(2)

  // Quaternion kinematics: q_dot = 0.5 * Omega(q) * q
  val q1 = q[0]; val q2 = q[1]; val q3 = q[2]; val q4 = q[3]
  val qDot1 = 0.5 * ( 0.0 * q1 +  wz * q2 + (-wy) * q3 +  wx * q4)
  val qDot2 = 0.5 * ((-wz) * q1 + 0.0 * q2 +   wx * q3 +  wy * q4)
  val qDot3 = 0.5 * (  wy * q1 + (-wx) * q2 + 0.0 * q3 +  wz * q4)
  val qDot4 = 0.5 * ((-wx) * q1 + (-wy) * q2 + (-wz) * q3 + 0.0 * q4)

  // Euler's equations
  val wxDot = M1 / A - (C - B) * wy * wz / A
  val wyDot = M2 / B - (A - C) * wz * wx / B
  val wzDot = M3 / C - (B - A) * wx * wy / C

  return mk.ndarray(mk[qDot1, qDot2, qDot3, qDot4, wxDot, wyDot, wzDot])
}
