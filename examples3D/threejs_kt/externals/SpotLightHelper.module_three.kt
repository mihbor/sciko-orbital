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

external open class SpotLightHelper : Object3D {
    constructor(light: Light, color: Color = definedExternally)
    constructor(light: Light)
    constructor(light: Light, color: String = definedExternally)
    constructor(light: Light, color: Number = definedExternally)
    open var light: Light
    override var matrix: Matrix4
    override var matrixAutoUpdate: Boolean
    open var color: dynamic /* Color? | String? | Number? */
    open var cone: LineSegments__0
    open fun dispose()
    open fun update()
}