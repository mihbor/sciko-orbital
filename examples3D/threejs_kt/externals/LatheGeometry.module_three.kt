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

external interface `T$68` {
    var points: Array<Vector2>
    var segments: Number
    var phiStart: Number
    var phiLength: Number
}

external open class LatheBufferGeometry(points: Array<Vector2>, segments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally) : BufferGeometry {
    override var type: String
    open var parameters: `T$68`
}

external interface `T$69` {
    var points: Array<Vector2>
    var segments: Number
    var phiStart: Number
    var phiLength: Number
}

external open class LatheGeometry(points: Array<Vector2>, segments: Number = definedExternally, phiStart: Number = definedExternally, phiLength: Number = definedExternally) : Geometry {
    override var type: String
    open var parameters: `T$69`
}