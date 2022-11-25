package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue

@ModuleInfo(name = "Interpolate", ModuleCategory.CLIENT, canEnable = false)
object Interpolate : Module() {
    val interpolateHue = BoolValue("InterpolateHue", true)
    val color1r = IntegerValue("Color1-R", 255, 0, 255)
    val color1g = IntegerValue("Color1-G", 255, 0, 255)
    val color1b = IntegerValue("Color1-B", 255, 0, 255)
    val color2r = IntegerValue("Color2-R", 255, 0, 255)
    val color2g = IntegerValue("Color2-G", 255, 0, 255)
    val color2b = IntegerValue("Color2-B", 255, 0, 255)

}