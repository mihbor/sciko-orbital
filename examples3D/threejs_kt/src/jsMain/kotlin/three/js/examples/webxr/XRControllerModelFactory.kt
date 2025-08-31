@file:JsModule("three/examples/jsm/webxr/XRControllerModelFactory")
@file:JsNonModule
package three.js.examples.webxr

import three.js.Object3D
import three.js.examples.loaders.GLTFLoader

external class XRControllerModel: Object3D

external class XRControllerModelFactory(gltfLoader: GLTFLoader = definedExternally) {
  fun createControllerModel(controller: Object3D): XRControllerModel
}