@file:JsModule("three/examples/jsm/webxr/XRHandModelFactory")
@file:JsNonModule
package three.js.examples.webxr

import three.js.Group

external class XRHandModelFactory {
  fun createHandModel(controller: Group, profile: String? = definedExternally): Group
}