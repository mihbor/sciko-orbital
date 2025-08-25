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

external open class CurvePath<T : Vector> : Curve<T> {
    override var type: String
    open var curves: Array<Curve<T>>
    open var autoClose: Boolean
    open fun add(curve: Curve<T>)
    open fun closePath()
    open fun getPoint(t: Number): T
    open fun getCurveLengths(): Array<Number>
    open fun createPointsGeometry(divisions: Number): Geometry
    open fun createSpacedPointsGeometry(divisions: Number): Geometry
    open fun createGeometry(points: Array<T>): Geometry
}