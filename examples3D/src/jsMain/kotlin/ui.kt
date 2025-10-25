import kotlinx.browser.window
import org.w3c.dom.events.Event
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.pointerevents.PointerEvent
import three.js.Color
import three.js.Object3D
import three.js.Vector2
import three.mesh.ui.*

val buttons = mutableListOf<Block>()
val uiProps = BlockProps().apply {
  justifyContent = "center"
  alignContent = "center"
  contentDirection = "column"
  padding = 0.02
  borderRadius = 0.05
  fontSize = 0.04
  fontFamily = "fonts/Roboto-msdf.json"
  fontTexture = "fonts/Roboto-msdf.png"
}

fun createControls() = Block(uiProps).apply {
  add(Block(BlockProps().apply {
    width = 0.4
    height = 0.1
    backgroundOpacity = 0.0
  }).apply {
    add(Text(TextProps("Click to show:")))
  })
  camera.add(this)
  position.set(1, 0.7, -2)
  add(createButton("2 body", ::twoBodyScene))
  add(createButton("orbit", ::orbitScene))
  add(createButton("lunar", ::lunarTrajectoryScene))
}

val buttonOptions = BlockProps().apply {
  width = 0.25
  height = 0.1
  justifyContent = "center"
  alignContent = "center"
  offset = 0.05
  margin = 0.02
  borderRadius = 0.04
}
val hoveredStateAttributes = BlockState(
  state = "hovered",
  attributes = BlockProps().apply {
    offset = 0.035
    backgroundColor = Color(0x999999)
    backgroundOpacity = 1.0
    fontColor = Color(0xffffff)
  }
)
val idleStateAttributes = BlockState(
  state = "idle",
  attributes = BlockProps().apply {
    offset = 0.035
    backgroundColor = Color(0x666666)
    backgroundOpacity = 0.3
    fontColor = Color(0xffffff)
  }
)

val selectedAttributes = BlockProps().apply {
  offset = 0.02
  backgroundColor = Color(0x777777)
  fontColor = Color(0x222222)
}

fun createButton(name: String, obj: () -> List<Object3D>) = Block(buttonOptions).apply {
  add(Text(TextProps(name)))
  setupState(BlockState(
    state = "selected",
    attributes = selectedAttributes,
    onSet = {
      scene.remove(*chosenObjects.toTypedArray())
      chosenObjects = obj.invoke()
      scene.add(*chosenObjects.toTypedArray())
    }
  ))
  setupState(hoveredStateAttributes)
  setupState(idleStateAttributes)
  buttons.add(this)
}

var mouse = Vector2()

fun clickHandler(event: Event) {
  if (event is MouseEvent) {
    event.preventDefault()
    val click = Vector2()
    val size = Vector2()
    renderer.getSize(size)
    click.x = 2.0 * event.clientX / size.x - 1
    click.y = 1 - 2.0 * event.clientY / size.y
    raycaster.setFromCamera(click, camera)
    val intersects = raycaster.intersectObjects(scene.children, true)
    val objects = intersects.map{it.`object`}
    buttonClicked(objects)
  }
}

fun pointerMoveHandler(event: Event) {
  if (event is PointerEvent) {
    mouse.x = 2.0 * event.clientX / window.innerWidth - 1
    mouse.y = 1 -2.0 * event.clientY / window.innerHeight
  }
}

fun updateButtons() {
//    console.log("pointermove ${JSON.stringify(mouse)}")
  if (mouse.x != null && mouse.y != null) {
    raycaster.setFromCamera(mouse, camera)
    val intersects = raycaster.intersectObjects(buttons.toTypedArray(), true)
    val intersected = intersects.getOrNull(0) ?.`object`
      ?.let { findAncestorInList(it, buttons) as Block? }
      ?.apply { setState("hovered") }

    buttons.filter { it != intersected }.forEach { it.setState("idle") }
  }
}

fun buttonClicked(intersects: List<Object3D>): Block? {
  console.log("Something clicked. Intersects: ${intersects.size}")
  return intersects.asSequence()
    .firstNotNullOfOrNull { findAncestorInList(it, buttons) as? Block }
    ?.apply { setState("selected") }
}

fun findAncestorInList(child: Object3D, list: List<Object3D>): Object3D? =
  if (list.contains(child)) child
  else child.parent?.let { findAncestorInList(it, list)}
