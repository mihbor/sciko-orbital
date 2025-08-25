@file:JsQualifier("ImageUtils")
@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package ImageUtils

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
import Mapping
import Texture

external fun getDataURL(image: Any): String

external var crossOrigin: String

external fun loadTexture(url: String, mapping: Mapping = definedExternally, onLoad: (texture: Texture) -> Unit = definedExternally, onError: (message: String) -> Unit = definedExternally): Texture

external fun loadTextureCube(array: Array<String>, mapping: Mapping = definedExternally, onLoad: (texture: Texture) -> Unit = definedExternally, onError: (message: String) -> Unit = definedExternally): Texture