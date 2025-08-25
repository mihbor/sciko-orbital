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

external interface `T$60` {
    var radius: Number
    var tube: Number
    var tubularSegments: Number
    var radialSegments: Number
    var p: Number
    var q: Number
}

external open class TorusKnotBufferGeometry(radius: Number = definedExternally, tube: Number = definedExternally, tubularSegments: Number = definedExternally, radialSegments: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$60`
}

external open class TorusKnotGeometry(radius: Number = definedExternally, tube: Number = definedExternally, tubularSegments: Number = definedExternally, radialSegments: Number = definedExternally, p: Number = definedExternally, q: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$60`
}