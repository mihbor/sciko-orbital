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

external interface `T$56` {
    var vertices: Array<Number>
    var indices: Array<Number>
    var radius: Number
    var detail: Number
}

external open class PolyhedronBufferGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$56`
}

external interface `T$57` {
    var vertices: Array<Number>
    var indices: Array<Number>
    var radius: Number
    var detail: Number
}

external open class PolyhedronGeometry(vertices: Array<Number>, indices: Array<Number>, radius: Number = definedExternally, detail: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$57`
}