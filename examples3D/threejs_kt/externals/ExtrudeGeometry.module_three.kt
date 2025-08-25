@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")

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

external interface ExtrudeGeometryOptions {
    var curveSegments: Number?
        get() = definedExternally
        set(value) = definedExternally
    var steps: Number?
        get() = definedExternally
        set(value) = definedExternally
    var depth: Number?
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
    var extrudePath: Curve<Vector3>?
        get() = definedExternally
        set(value) = definedExternally
    var UVGenerator: UVGenerator?
        get() = definedExternally
        set(value) = definedExternally
}

external interface UVGenerator {
    fun generateTopUV(geometry: ExtrudeBufferGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number): Array<Vector2>
    fun generateSideWallUV(geometry: ExtrudeBufferGeometry, vertices: Array<Number>, indexA: Number, indexB: Number, indexC: Number, indexD: Number): Array<Vector2>
}

external open class ExtrudeBufferGeometry : BufferGeometry {
    constructor(shapes: Shape, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Shape)
    constructor(shapes: Array<Shape>, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Array<Shape>)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any = definedExternally)
    open fun addShape(shape: Shape, options: Any = definedExternally)

    companion object {
        var WorldUVGenerator: UVGenerator
    }
}

external open class ExtrudeGeometry : Geometry {
    constructor(shapes: Shape, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Shape)
    constructor(shapes: Array<Shape>, options: ExtrudeGeometryOptions = definedExternally)
    constructor(shapes: Array<Shape>)
    override var type: String
    open fun addShapeList(shapes: Array<Shape>, options: Any = definedExternally)
    open fun addShape(shape: Shape, options: Any = definedExternally)

    companion object {
        var WorldUVGenerator: UVGenerator
    }
}