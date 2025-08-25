@file:JsQualifier("MathUtils")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package MathUtils

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
import Quaternion

external var DEG2RAD: Number

external var RAD2DEG: Number

external fun generateUUID(): String

external fun clamp(value: Number, min: Number, max: Number): Number

external fun euclideanModulo(n: Number, m: Number): Number

external fun mapLinear(x: Number, a1: Number, a2: Number, b1: Number, b2: Number): Number

external fun smoothstep(x: Number, min: Number, max: Number): Number

external fun smootherstep(x: Number, min: Number, max: Number): Number

external fun random16(): Number

external fun randInt(low: Number, high: Number): Number

external fun randFloat(low: Number, high: Number): Number

external fun randFloatSpread(range: Number): Number

external fun seededRandom(seed: Number = definedExternally): Number

external fun degToRad(degrees: Number): Number

external fun radToDeg(radians: Number): Number

external fun isPowerOfTwo(value: Number): Boolean

external fun lerp(x: Number, y: Number, t: Number): Number

external fun nearestPowerOfTwo(value: Number): Number

external fun nextPowerOfTwo(value: Number): Number

external fun floorPowerOfTwo(value: Number): Number

external fun ceilPowerOfTwo(value: Number): Number

external fun setQuaternionFromProperEuler(q: Quaternion, a: Number, b: Number, c: Number, order: String)