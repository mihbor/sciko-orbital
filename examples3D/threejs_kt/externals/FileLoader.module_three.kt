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

external open class FileLoader(manager: LoadingManager = definedExternally) : Loader {
    open var mimeType: MimeType?
    open var responseType: String?
    open var withCredentials: String?
    open fun load(url: String, onLoad: (response: Any /* String | ArrayBuffer */) -> Unit = definedExternally, onProgress: (request: ProgressEvent__0) -> Unit = definedExternally, onError: (event: ErrorEvent) -> Unit = definedExternally): Any
    open fun setMimeType(mimeType: MimeType): FileLoader
    open fun setResponseType(responseType: String): FileLoader
    open fun setWithCredentials(value: Boolean): FileLoader
}