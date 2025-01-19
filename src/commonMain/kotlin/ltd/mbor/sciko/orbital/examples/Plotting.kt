package ltd.mbor.sciko.orbital.examples

import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.layers.line

fun plotScalar(vararg histories: Triple<List<Double>, List<Double>, String>, xLabel: String = "t", yLabel: String = "values"): Plot {
  val map = mapOf(
    xLabel to histories.flatMap { it.first },
    yLabel to histories.flatMap { it.second },
    "labels" to histories.flatMap { it.first.map { _ -> it.third } },
  )
  return map.plot {
    line {
      x(xLabel)
      y(yLabel)
      color("labels") {
        legend.name = ""
      }
    }
  }
}