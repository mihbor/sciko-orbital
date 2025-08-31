@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

external interface `T$56` {
    var radius: Number
    var length: Number
    var capSegments: Number
    var radialSegments : Number
}

open external class CapsuleGeometry(radius: Number = definedExternally, length: Number = definedExternally, capSegments: Number = definedExternally, radialSegments : Number = definedExternally) : LatheGeometry {
    override var type: String
    override var parameters: `T$56`
}