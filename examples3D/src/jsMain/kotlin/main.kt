import kotlinx.browser.document
import kotlinx.browser.window
import org.jetbrains.kotlinx.multik.api.KEEngineType
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.kotlin.KEEngine
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.slice
import org.w3c.dom.Window
import three.js.AxesHelper
import three.js.BackSide
import three.js.Color
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.PerspectiveCamera
import three.js.Scene
import three.js.SphereGeometry
import three.js.TextureLoader
import three.js.Vector3
import three.js.WebGLRenderer
import three.js.WebGLRendererParameters
import three.webxr.VRButton

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight

val texLoader = TextureLoader()
val starsTex = texLoader.load("tycho_skymap.jpg")
val camera = PerspectiveCamera(60, window.aspectRatio, 0.5, 2e9)

val renderer = WebGLRenderer((js("{}") as WebGLRendererParameters).apply{
  antialias = false
  logarithmicDepthBuffer = false
}).apply {
  document.body?.appendChild( VRButton.createButton(this) )
  document.body?.appendChild(domElement)
  setSize(window.innerWidth, window.innerHeight-4)
  setPixelRatio(window.devicePixelRatio)
  xr.enabled = false
}

val stars = Mesh(SphereGeometry(1e9, 30, 30), MeshBasicMaterial().apply {
  map = starsTex
  side = BackSide
})
val scene = createScene()
val metrial1 = MeshBasicMaterial().apply {
  color = Color(0xff0000)
}
val metrial2 = MeshBasicMaterial().apply {
  color = Color(0x00ff00)
}
fun createScene() = Scene().apply {
  add(stars)
  add(AxesHelper(10.0))
  add(camera.apply {
    position.x = 5
    position.y = 8
    position.z = 25
  })
  twoBody().second.forEach {
    val R1 = it.slice<Double, D1, D1>(0..2)
    val R2 = it.slice<Double, D1, D1>(3..5)
    val V1 = it.slice<Double, D1, D1>(6..8)
    val V2 = it.slice<Double, D1, D1>(9..11)
    add(Mesh(SphereGeometry(0.02), metrial1).apply {
      position.x = R1[0]*0.001
      position.y = R1[1]*0.001
      position.z = R1[2]*0.001
    })
    add(Mesh(SphereGeometry(0.02), metrial2).apply {
      position.x = R2[0]*0.001
      position.y = R2[1]*0.001
      position.z = R2[2]*0.001
    })
  }
}

fun animate() {
  renderer.render(scene, camera)
  window.requestAnimationFrame { animate() }
}

fun main() {
  window.onresize = {
    camera.aspect = window.aspectRatio
    camera.updateProjectionMatrix()

    renderer.setSize(window.innerWidth, window.innerHeight - 4)
    renderer.render(scene, camera)
  }
  animate()
}