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

external interface `T$67` {
    var width: Number
    var height: Number
    var widthSegments: Number
    var heightSegments: Number
}

external open class PlaneBufferGeometry(width: Number = definedExternally, height: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$67`
}

external open class PlaneGeometry(width: Number = definedExternally, height: Number = definedExternally, widthSegments: Number = definedExternally, heightSegments: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$67`
}