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

external interface IUniform {
    var value: Any
}

external interface `T$9` {
    var diffuse: IUniform
    var opacity: IUniform
    var map: IUniform
    var uvTransform: IUniform
    var uv2Transform: IUniform
    var alphaMap: IUniform
}

external interface `T$10` {
    var specularMap: IUniform
}

external interface `T$11` {
    var envMap: IUniform
    var flipEnvMap: IUniform
    var reflectivity: IUniform
    var refractionRatio: IUniform
    var maxMipLevel: IUniform
}

external interface `T$12` {
    var aoMap: IUniform
    var aoMapIntensity: IUniform
}

external interface `T$13` {
    var lightMap: IUniform
    var lightMapIntensity: IUniform
}

external interface `T$14` {
    var emissiveMap: IUniform
}

external interface `T$15` {
    var bumpMap: IUniform
    var bumpScale: IUniform
}

external interface `T$16` {
    var normalMap: IUniform
    var normalScale: IUniform
}

external interface `T$17` {
    var displacementMap: IUniform
    var displacementScale: IUniform
    var displacementBias: IUniform
}

external interface `T$18` {
    var roughnessMap: IUniform
}

external interface `T$19` {
    var metalnessMap: IUniform
}

external interface `T$20` {
    var gradientMap: IUniform
}

external interface `T$21` {
    var fogDensity: IUniform
    var fogNear: IUniform
    var fogFar: IUniform
    var fogColor: IUniform
}

external interface `T$22` {
    var direction: Any
    var color: Any
}

external interface `T$23` {
    var value: Array<Any>
    var properties: `T$22`
}

external interface `T$24` {
    var shadowBias: Any
    var shadowNormalBias: Any
    var shadowRadius: Any
    var shadowMapSize: Any
}

external interface `T$25` {
    var value: Array<Any>
    var properties: `T$24`
}

external interface `T$26` {
    var color: Any
    var position: Any
    var direction: Any
    var distance: Any
    var coneCos: Any
    var penumbraCos: Any
    var decay: Any
}

external interface `T$27` {
    var value: Array<Any>
    var properties: `T$26`
}

external interface `T$28` {
    var value: Array<Any>
    var properties: `T$24`
}

external interface `T$29` {
    var color: Any
    var position: Any
    var decay: Any
    var distance: Any
}

external interface `T$30` {
    var value: Array<Any>
    var properties: `T$29`
}

external interface `T$31` {
    var value: Array<Any>
    var properties: `T$24`
}

external interface `T$32` {
    var direction: Any
    var skycolor: Any
    var groundColor: Any
}

external interface `T$33` {
    var value: Array<Any>
    var properties: `T$32`
}

external interface `T$34` {
    var color: Any
    var position: Any
    var width: Any
    var height: Any
}

external interface `T$35` {
    var value: Array<Any>
    var properties: `T$34`
}

external interface `T$36` {
    var ambientLightColor: IUniform
    var directionalLights: `T$23`
    var directionalLightShadows: `T$25`
    var directionalShadowMap: IUniform
    var directionalShadowMatrix: IUniform
    var spotLights: `T$27`
    var spotLightShadows: `T$28`
    var spotShadowMap: IUniform
    var spotShadowMatrix: IUniform
    var pointLights: `T$30`
    var pointLightShadows: `T$31`
    var pointShadowMap: IUniform
    var pointShadowMatrix: IUniform
    var hemisphereLights: `T$33`
    var rectAreaLights: `T$35`
}

external interface `T$37` {
    var diffuse: IUniform
    var opacity: IUniform
    var size: IUniform
    var scale: IUniform
    var map: IUniform
    var uvTransform: IUniform
}

external object UniformsLib {
    var common: `T$9`
    var specularmap: `T$10`
    var envmap: `T$11`
    var aomap: `T$12`
    var lightmap: `T$13`
    var emissivemap: `T$14`
    var bumpmap: `T$15`
    var normalmap: `T$16`
    var displacementmap: `T$17`
    var roughnessmap: `T$18`
    var metalnessmap: `T$19`
    var gradientmap: `T$20`
    var fog: `T$21`
    var lights: `T$36`
    var points: `T$37`
}