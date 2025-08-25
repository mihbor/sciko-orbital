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

external interface `T$66` {
    var innerRadius: Number
    var outerRadius: Number
    var thetaSegments: Number
    var phiSegments: Number
    var thetaStart: Number
    var thetaLength: Number
}

external open class RingBufferGeometry(innerRadius: Number = definedExternally, outerRadius: Number = definedExternally, thetaSegments: Number = definedExternally, phiSegments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$66`
}

external open class RingGeometry(innerRadius: Number = definedExternally, outerRadius: Number = definedExternally, thetaSegments: Number = definedExternally, phiSegments: Number = definedExternally, thetaStart: Number = definedExternally, thetaLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$66`
}