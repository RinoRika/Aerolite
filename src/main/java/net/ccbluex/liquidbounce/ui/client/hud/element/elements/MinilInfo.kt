package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import oh.yalan.NativeClass
import java.awt.Color

@NativeClass
@ElementInfo(name = "MinilInfo",blur = true)
class MinilInfo : Element() {
    var s=0
    var m=0
    var h=0
    var tag = "NM$.L"
    private val bgAlphaValue = IntegerValue("Alpha", 160,0,255)

    companion object;
    override fun drawElement(partialTicks: Float): Border {
        if (mc.theWorld!=null) {

            if (TimerUtils.delay(1000F)) {

                s += 1
                TimerUtils.reset()
            }
            if (s == 60) {
                m += 1
                s = 0
            }
            if (m == 60) {
                h += 1
                m = 0
            }
        }
        val timer= "${h}h ${m}m ${s}s"


        RenderUtils.drawRect(0f, 0f, 152f, 96f, Color(10, 10, 10, bgAlphaValue.get()).rgb)
        RenderUtils.drawRect(0f,0f,152f,17f,Color(60,60,60).rgb)


        Fonts.icon40.drawStringWithShadow("E", 3f,4f, Color(19,106,110).rgb)     //00
        Fonts.poppinsBold20.drawStringWithShadow(" Session Info", 15f, 3.0f, Color(160,160,160).rgb)   //字
        Fonts.icon40.drawStringWithShadow("F", 2.5f,20f + 1, Color(145,55,140).rgb)     //00000000000000000000000000000000
        Fonts.poppinsBold20.drawStringWithShadow(" Play Time", 14.5f, 21.5f, Color(160,160,160).rgb)    //字字字字字字字字字字字字字字字字字字字字字字字
        Fonts.poppinsBold20.drawStringWithShadow(timer, (147F - Fonts.font35.getStringWidth((timer))).toDouble().toFloat(), 21.5f, Color(160,160,160).rgb)
        Fonts.icon40.drawStringWithShadow("G", 2.5f,41f + 1, Color(175,55,55).rgb)     //00000000000000000000000000000000
        Fonts.poppinsBold20.drawStringWithShadow(" Kills", 14.5f, 43f, Color(160,160,160).rgb)    //字字字字字字字字字字字字字字字字字字字字字字字
        Fonts.poppinsBold20.drawStringWithShadow(LiquidBounce.combatManager.getKillCounts().toString(), (147F - Fonts.font35.getStringWidth((LiquidBounce.combatManager.getKillCounts()).toString())).toDouble().toFloat(), 43f, Color(160,160,160).rgb)
        Fonts.icon40.drawStringWithShadow("H", 2.5f,61f + 1, Color(243,180,48).rgb)     //00000000000000000000000000000000
        Fonts.poppinsBold20.drawStringWithShadow(" Games", 14.5f, 64.5f, Color(160,160,160).rgb)    //字字字字字字字字字字字字字字字字字字字字字字字
        Fonts.poppinsBold20.drawStringWithShadow(LiquidBounce.combatManager.getTotalPlayed().toString(), (147F - Fonts.font35.getStringWidth((LiquidBounce.combatManager.getTotalPlayed()).toString())).toDouble().toFloat(), 61f, Color(160,160,160).rgb)
        Fonts.icon40.drawStringWithShadow("r", 2.5f,83f, Color(98,183,68).rgb)     //00000000000000000000000000000000
        Fonts.poppinsBold20.drawStringWithShadow(" HurtTime",  14.5f, 84.5f, Color(160,160,160).rgb)     //字字字字字字字字字字字字字字字字字字字字字字字
        Fonts.poppinsBold20.drawStringWithShadow(mc.thePlayer.hurtTime.toString(), (147F - Fonts.font35.getStringWidth(mc.thePlayer.hurtTime.toString())).toDouble().toFloat(), 84.5f, Color(160,160,160).rgb)
        return Border(0f, -1f + 5, 152f, 96f)
    }
}