@file:JsQualifier("AnimationUtils")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package AnimationUtils

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
import AnimationClip

external fun arraySlice(array: Any, from: Number, to: Number): Any

external fun convertArray(array: Any, type: Any, forceClone: Boolean): Any

external fun isTypedArray(obj: Any): Boolean

external fun getKeyFrameOrder(times: Array<Number>): Array<Number>

external fun sortedArray(values: Array<Any>, stride: Number, order: Array<Number>): Array<Any>

external fun flattenJSON(jsonKeys: Array<String>, times: Array<Any>, values: Array<Any>, valuePropertyName: String)

external fun subclip(sourceClip: AnimationClip, name: String, startFrame: Number, endFrame: Number, fps: Number = definedExternally): AnimationClip

external fun makeClipAdditive(targetClip: AnimationClip, referenceFrame: Number = definedExternally, referenceClip: AnimationClip = definedExternally, fps: Number = definedExternally): AnimationClip