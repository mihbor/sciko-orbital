@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

open external class Cylindrical(radius: Number = definedExternally, theta: Number = definedExternally, y: Number = definedExternally) {
    open var radius: Number
    open var theta: Number
    open var y: Number
    open fun clone(): Cylindrical /* this */
    open fun copy(other: Cylindrical): Cylindrical /* this */
    open fun set(radius: Number, theta: Number, y: Number): Cylindrical /* this */
    open fun setFromVector3(vec3: Vector3): Cylindrical /* this */
    open fun setFromCartesianCoords(x: Number, y: Number, z: Number): Cylindrical /* this */
}