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

external interface RenderTarget

external interface RenderItem {
    var id: Number
    var `object`: Object3D
    var geometry: BufferGeometry?
    var material: Material
    var program: WebGLProgram
    var groupOrder: Number
    var renderOrder: Number
    var z: Number
    var group: Group?
}

external open class WebGLRenderList(properties: WebGLProperties) {
    open var opaque: Array<RenderItem>
    open var transparent: Array<RenderItem>
    open fun init()
    open fun push(obj: Object3D, geometry: BufferGeometry?, material: Material, groupOrder: Number, z: Number, group: Group?)
    open fun unshift(obj: Object3D, geometry: BufferGeometry?, material: Material, groupOrder: Number, z: Number, group: Group?)
    open fun sort(opaqueSort: Function<*>, transparentSort: Function<*>)
    open fun finish()
}

external open class WebGLRenderLists(properties: WebGLProperties) {
    open fun dispose()
    open fun get(scene: Scene, camera: Camera): WebGLRenderList
}