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

external open class Line<TGeometry, TMaterial>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, mode: Number = definedExternally) : Object3D {
    open var geometry: TGeometry
    open var material: TMaterial
    override var type: String /* "Line" | "LineLoop" | "LineSegments" | String */
    open var isLine: Boolean
    open var morphTargetInfluences: Array<Number>
    open var morphTargetDictionary: `T$49`
    open fun computeLineDistances(): Line<TGeometry, TMaterial> /* this */
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun updateMorphTargets()
}

external open class Line__0 : Line<dynamic /* Geometry | BufferGeometry */, dynamic /* Material | Array<Material> */>