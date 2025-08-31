@file:JsModule("three/examples/jsm/webxr/XRButton")
@file:JsNonModule
package three.js.examples.webxr

import org.w3c.dom.Node
import three.js.Renderer

external object XRButton {
    fun createButton(renderer: Renderer): Node
}
