package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.Memory
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

@ElementInfo("MemoryUi")
class MemoryUi(x: Double = 0.0, y: Double = 0.0) : Element(x = x, y = y){
    private val decimalFormat = DecimalFormat("##0", DecimalFormatSymbols(Locale.ENGLISH))
    override fun drawElement(partialTicks: Float): Border {
        RenderUtils.drawShadow(0f,11f,100f,8f)
        //RenderUtils.drawRect(0f,11f,100f,19f, Color(110,110,110).rgb)
        Fonts.font35.drawString("Memory",2.5f,2.5f,Color(255,255,255).rgb,true)
        Fonts.font35.drawString("${Memory.usedMemorySize}/${Memory.maxMemorySize}MB",100f-Fonts.font35.getStringWidth("${Memory.usedMemorySize}/${Memory.maxMemorySize}MB"),21.5f,Color(255,255,255).rgb,true)
        Fonts.font35.drawString(decimalFormat.format(Memory.getMemory() * 100).toString() + "%",100f-Fonts.font35.getStringWidth(decimalFormat.format(Memory.getMemory() * 100).toString() + "%"),2.5f,Color(255,255,255).rgb,true)
        RenderUtils.drawRect(0f,11f,100f,19f,Color(0,0,0,95))
        RenderUtils.drawRect(0f,11f,100 * Memory.getMemory(),19f,Color(255,255,255,200))
        return Border(-1f,0f,101f,22.5f + Fonts.font35.FONT_HEIGHT)
    }
}