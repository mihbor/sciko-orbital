import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Window
import three.js.*
import three.js.examples.controls.OrbitControls
import three.js.examples.webxr.VRButton
import three.mesh.ui.ThreeMeshUI

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight

operator fun Number.minus(other: Number) = toDouble() - other.toDouble()
operator fun Number.plus(other: Number) = toDouble() + other.toDouble()
operator fun Number.times(other: Number) = toDouble() * other.toDouble()
operator fun Number.div(other: Number) = toDouble() / other.toDouble()
operator fun Number.compareTo(other: Number) = toDouble().compareTo(other.toDouble())

val texLoader = TextureLoader()
val starsTex = texLoader.load("tycho_skymap.jpg")
val camera = PerspectiveCamera(60, window.aspectRatio, 0.5, 2e9)

val renderer = WebGLRenderer((js("{}") as WebGLRendererParameters).apply{
  antialias = false
  logarithmicDepthBuffer = false
}).apply {
  document.body?.appendChild(VRButton.createButton(this))
  document.body?.appendChild(domElement)
  setSize(window.innerWidth, window.innerHeight-4)
  setPixelRatio(window.devicePixelRatio)
  xr.enabled = false
}

val stars = Mesh(SphereGeometry(1e9, 30, 30), MeshBasicMaterial().apply {
  map = starsTex
  side = BackSide
  color = Color(0x888888)
})
val earthTex = texLoader.load("1_earth_8k.jpg")
val moonTex = texLoader.load("8k_moon.jpg")
val controls = OrbitControls(camera, renderer.domElement)
val raycaster = Raycaster().apply {
  far = 2e8
}
val scene = createScene()
var chosenObjects = listOf<Object3D>()
fun createScene() = Scene().apply {
  add(stars)
  add(AxesHelper(10.0))
  add(camera.apply {
    position.x = 5
    position.y = 20
    position.z = 25
  })
  controls.update()
//  chosenObjects = orbitScene()
//  add(*chosenObjects.toTypedArray())
}

fun animate() {
  ThreeMeshUI.update()
  renderer.render(scene, camera)
  updateButtons()
  window.requestAnimationFrame { animate() }
}

external interface Options {
  var passive: Boolean
}

fun main() {
  window.onresize = {
    camera.aspect = window.aspectRatio
    camera.updateProjectionMatrix()

    renderer.setSize(window.innerWidth, window.innerHeight - 4)
    renderer.render(scene, camera)
  }
  createControls()
  document.addEventListener("click", ::clickHandler, false)
  val options = (js("{}") as Options).apply{
    passive = false
  }
  document.addEventListener("pointermove", ::pointerMoveHandler, options)
  animate()
}
