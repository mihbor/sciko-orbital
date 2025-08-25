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
import tsstdlib.GLenum
import tsstdlib.WebGL2RenderingContext

external interface `T$85` {
    var buffer: WebGLBuffer
    var type: GLenum
    var bytesPerElement: Number
    var version: Number
}

external open class WebGLAttributes {
    constructor(gl: WebGLRenderingContext, capabilities: WebGLCapabilities)
    constructor(gl: WebGL2RenderingContext, capabilities: WebGLCapabilities)
    open fun get(attribute: BufferAttribute): `T$85`
    open fun get(attribute: InterleavedBufferAttribute): `T$85`
    open fun remove(attribute: BufferAttribute)
    open fun remove(attribute: InterleavedBufferAttribute)
    open fun update(attribute: BufferAttribute, bufferType: GLenum)
    open fun update(attribute: InterleavedBufferAttribute, bufferType: GLenum)
}