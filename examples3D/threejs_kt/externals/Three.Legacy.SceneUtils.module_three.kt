@file:JsQualifier("SceneUtils")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package SceneUtils

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*
import Geometry
import Material
import Object3D
import Scene

external fun createMultiMaterialObject(geometry: Geometry, materials: Array<Material>): Object3D

external fun detach(child: Object3D, parent: Object3D, scene: Scene)

external fun attach(child: Object3D, scene: Scene, parent: Object3D)