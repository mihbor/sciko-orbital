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

external interface `T$55` {
    var func: (u: Number, v: Number, dest: Vector3) -> Unit
    var slices: Number
    var stacks: Number
}

external open class ParametricBufferGeometry(func: (u: Number, v: Number, dest: Vector3) -> Unit, slices: Number, stacks: Number) : BufferGeometry {
    override var type: String
    open var parameters: `T$55`
}

external open class ParametricGeometry(func: (u: Number, v: Number, dest: Vector3) -> Unit, slices: Number, stacks: Number) : Geometry {
    override var type: String
    open var parameters: `T$55`
}