import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.Window
import three.js.*
import three.js.examples.controls.OrbitControls
import three.js.examples.webxr.VRButton

val Window.aspectRatio get() = innerWidth.toDouble() / innerHeight

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
val controls = OrbitControls(camera, renderer.domElement)
val scene = createScene()
fun createScene() = Scene().apply {
  add(stars)
  add(AxesHelper(10.0))
  add(camera.apply {
    position.x = 5
    position.y = 20
    position.z = 25
  })
  controls.update()
  add(*orbitScene().toTypedArray())
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
