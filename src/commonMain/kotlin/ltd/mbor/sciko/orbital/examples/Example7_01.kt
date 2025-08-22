package ltd.mbor.sciko.orbital.examples

import ltd.mbor.sciko.linalg.norm
import ltd.mbor.sciko.orbital.degrees
import ltd.mbor.sciko.orbital.muEarth
import ltd.mbor.sciko.orbital.rvaRelative
import ltd.mbor.sciko.orbital.svFromCoe
import ltd.mbor.sciko.orbital.toDegrees
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.ndarray.operations.toList

fun main() {
  val mu = muEarth

  // Input data for Spacecraft A
  val hA = 52059.0
  val eA = 0.025724
  val iA = 60.degrees
  val RAANA = 40.degrees
  val omegaA = 30.degrees
  val thetaA = 40.degrees

  // Input data for Spacecraft B
  val hB = 52362.0
  val eB = 0.0072696
  val iB = 50.degrees
  val RAANB = 40.degrees
  val omegaB = 120.degrees
  val thetaB = 40.degrees

  // Compute the initial state vectors of A and B
  val (rA, vA) = svFromCoe(mk.ndarray(mk[hA, eA, RAANA, iA, omegaA, thetaA]), mu)
  val (rB, vB) = svFromCoe(mk.ndarray(mk[hB, eB, RAANB, iB, omegaB, thetaB]), mu)

  // Compute relative position, velocity, and acceleration
  val (rRel, vRel, aRel) = rvaRelative(rA, vA, rB, vB, mu)

  // Output results
  println("\nOrbital parameters of spacecraft A:")
  println("   angular momentum    = $hA (km^2/s)")
  println("   eccentricity        = $eA")
  println("   inclination         = ${iA.toDegrees()} (deg)")
  println("   RAAN                = ${RAANA.toDegrees()} (deg)")
  println("   argument of perigee = ${omegaA.toDegrees()} (deg)")
  println("   true anomaly        = ${thetaA.toDegrees()} (deg)")

  println("\nState vector of spacecraft A:")
  println("   r = ${rA.toList()}")
  println("       (magnitude = ${rA.norm()})")
  println("   v = ${vA.toList()}")
  println("       (magnitude = ${vA.norm()})")

  println("\nOrbital parameters of spacecraft B:")
  println("   angular momentum    = $hB (km^2/s)")
  println("   eccentricity        = $eB")
  println("   inclination         = ${iB.toDegrees()} (deg)")
  println("   RAAN                = ${RAANB.toDegrees()} (deg)")
  println("   argument of perigee = ${omegaB.toDegrees()} (deg)")
  println("   true anomaly        = ${thetaB.toDegrees()} (deg)")

  println("\nState vector of spacecraft B:")
  println("   r = ${rB.toList()}")
  println("       (magnitude = ${rB.norm()})")
  println("   v = ${vB.toList()}")
  println("       (magnitude = ${vB.norm()})")

  println("\nIn the co-moving frame attached to A:")
  println("   Position of B relative to A = ${rRel.toList()}")
  println("      (magnitude = ${rRel.norm()})")
  println("   Velocity of B relative to A = ${vRel.toList()}")
  println("      (magnitude = ${vRel.norm()})")
  println("   Acceleration of B relative to A = ${aRel.toList()}")
  println("      (magnitude = ${aRel.norm()})")
}
