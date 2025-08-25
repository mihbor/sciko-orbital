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

external interface `T$8` {
    @nativeGetter
    operator fun get(uniform: String): IUniform?
    @nativeSetter
    operator fun set(uniform: String, value: IUniform)
}

external interface Shader {
    var uniforms: `T$8`
    var vertexShader: String
    var fragmentShader: String
}

external object ShaderLib {
    @nativeGetter
    operator fun get(name: String): Shader?
    @nativeSetter
    operator fun set(name: String, value: Shader)
    var basic: Shader
    var lambert: Shader
    var phong: Shader
    var standard: Shader
    var matcap: Shader
    var points: Shader
    var dashed: Shader
    var depth: Shader
    var normal: Shader
    var sprite: Shader
    var background: Shader
    var cube: Shader
    var equirect: Shader
    var distanceRGBA: Shader
    var shadow: Shader
    var physical: Shader
}