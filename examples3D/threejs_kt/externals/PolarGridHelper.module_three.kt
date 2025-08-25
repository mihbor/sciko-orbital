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

external open class PolarGridHelper : LineSegments__0 {
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Color?, color2: Number?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: String?, color2: Number?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: Color?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: String?)
    constructor(radius: Number, radials: Number, circles: Number, divisions: Number, color1: Number?, color2: Number?)
    override var type: String
}