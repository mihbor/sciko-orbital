package ltd.mbor.sciko.orbital.examples

import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.save
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.MultiArray
import org.jetbrains.kotlinx.multik.ndarray.data.get

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

fun plotNVector(
  timestamps: List<Double>,
  history: List<MultiArray<Double, D1>>,
  title: String,
  n: Int = history.first().size,
  labels: List<String>? = null
) {
  mapOf(
    "t" to (1..n).flatMap { timestamps },
    title to (0..<n).flatMap { i -> history.map { it[i] } },
    "labels" to (1..n).flatMap { i -> timestamps.map { labels?.get(i-1) ?: "${title}$i" } }
  ).plot {
    line {
      x("t")
      y(title)
      color("labels")
    }
  }.save("${title}Plot.png")
}
