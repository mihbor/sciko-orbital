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
import tsstdlib.AudioContext
import tsstdlib.GainNode

external open class AudioListener : Object3D {
    override var type: String /* "AudioListener" */
    open var context: AudioContext
    open var gain: GainNode
    open var filter: Any?
    open var timeDelta: Number
    open fun getInput(): GainNode
    open fun removeFilter(): AudioListener /* this */
    open fun setFilter(value: Any): AudioListener /* this */
    open fun getFilter(): Any
    open fun setMasterVolume(value: Number): AudioListener /* this */
    open fun getMasterVolume(): Number
    override fun updateMatrixWorld(force: Boolean)
}