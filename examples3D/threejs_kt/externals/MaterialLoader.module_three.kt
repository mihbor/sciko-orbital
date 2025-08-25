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

external interface `T$78` {
    @nativeGetter
    operator fun get(key: String): Texture?
    @nativeSetter
    operator fun set(key: String, value: Texture)
}

external open class MaterialLoader(manager: LoadingManager = definedExternally) : Loader {
    open var textures: `T$78`
    open fun load(url: String, onLoad: (material: Material) -> Unit, onProgress: (event: ProgressEvent__0) -> Unit = definedExternally, onError: (event: Any /* Error | ErrorEvent */) -> Unit = definedExternally)
    open fun setTextures(textures: `T$78`): MaterialLoader /* this */
    open fun parse(json: Any): Material
}