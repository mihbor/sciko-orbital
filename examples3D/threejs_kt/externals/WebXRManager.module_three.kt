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

external open class WebXRManager(renderer: Any, gl: WebGLRenderingContext) {
    open var enabled: Boolean
    open var isPresenting: Boolean
    open fun getController(id: Number): Group
    open fun getControllerGrip(id: Number): Group
    open fun setFramebufferScaleFactor(value: Number)
    open fun setReferenceSpaceType(value: String)
    open fun getReferenceSpace(): Any
    open fun getSession(): Any
    open fun setSession(value: Any)
    open fun getCamera(camera: Camera): Camera
    open fun setAnimationLoop(callback: Function<*>)
    open fun dispose()
}