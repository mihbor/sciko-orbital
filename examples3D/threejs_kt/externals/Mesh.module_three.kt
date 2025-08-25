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

external interface `T$49` {
    @nativeGetter
    operator fun get(key: String): Number?
    @nativeSetter
    operator fun set(key: String, value: Number)
}

external open class Mesh<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D {
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$49`
    open var isMesh: Boolean
    override var type: String
    open fun updateMorphTargets()
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
}

external open class Mesh__0 : Mesh<dynamic /* Geometry | BufferGeometry */, dynamic /* Material | Array<Material> */>