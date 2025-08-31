@file:JsModule("three/examples/jsm/geometries/TextGeometry")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js.examples.geometries

import three.js.ExtrudeGeometry
import three.js.Font

external interface TextGeometryParameters {
    var font: Font
    var size: Number?
        get() = definedExternally
        set(value) = definedExternally
    var height: Number?
        get() = definedExternally
        set(value) = definedExternally
    var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelEnabled: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var bevelThickness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSize: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelOffset: Number?
        get() = definedExternally
        set(value) = definedExternally
    var bevelSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
}

external interface `T$32` {
    var font: Font
    var size: Number
    var height: Number
    var curveSegments: Number
    var bevelEnabled: Boolean
    var bevelThickness: Number
    var bevelSize: Number
    var bevelOffset: Number
    var bevelSegments: Number
}

open external class TextGeometry(text: String, parameters: TextGeometryParameters) : ExtrudeGeometry {
    override var type: String
    open var parameters: `T$32`
}