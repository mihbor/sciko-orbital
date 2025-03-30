package ltd.mbor.sciko.orbital

import kotlin.math.*

fun stumpS(z: Double) = if (z > 0)
  (sqrt(z) - sin(sqrt(z)))/(sqrt(z)).pow(3)
else if (z < 0)
  (sinh(sqrt(-z)) - sqrt(-z))/(sqrt(-z)).pow(3)
else
  1.0/6

fun stumpC(z: Double) = if (z > 0)
  (1.0 - cos(sqrt(z)))/z
else if (z < 0)
  (cosh(sqrt(-z)) - 1.0)/-z
else
  0.5
