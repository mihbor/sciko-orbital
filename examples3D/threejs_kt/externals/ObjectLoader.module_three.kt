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
import tsstdlib.ProgressEvent__0

external interface `T$77` {
    @nativeGetter
    operator fun get(key: String): HTMLImageElement?
    @nativeSetter
    operator fun set(key: String, value: HTMLImageElement)
}

external open class ObjectLoader(manager: LoadingManager = definedExternally) : Loader {
    open fun load(url: String, onLoad: (obj: ObjectType) -> Unit = definedExternally, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: Any /* Error | ErrorEvent */) -> Unit = definedExternally)
    open fun <T : Object3D> parse(json: Any, onLoad: (obj: Object3D) -> Unit = definedExternally): T
    open fun parseGeometries(json: Any): Array<Any>
    open fun parseMaterials(json: Any, textures: Array<Texture>): Array<Material>
    open fun parseAnimations(json: Any): Array<AnimationClip>
    open fun parseImages(json: Any, onLoad: () -> Unit): `T$77`
    open fun parseTextures(json: Any, images: Any): Array<Texture>
    open fun <T : Object3D> parseObject(data: Any, geometries: Array<Any>, materials: Array<Material>): T
}