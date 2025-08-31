@file:JsModule("three/examples/jsm/controls/OrbitControls")
@file:JsNonModule
package three.js.examples.controls

import org.w3c.dom.HTMLElement
import org.w3c.dom.Node
import org.w3c.dom.events.EventTarget
import three.js.Camera
import three.js.Vector3

external class OrbitControls(obj: Camera, domElement: HTMLElement) : Node {
    var target: Vector3
    var autoRotate: Boolean
    var enabled: Boolean

    fun update()
    fun reset()
    fun listenToKeyEvents(target: EventTarget)
}

