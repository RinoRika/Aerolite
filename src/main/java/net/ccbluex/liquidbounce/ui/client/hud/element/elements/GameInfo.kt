package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.packet.NewElementsUtils
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import java.awt.Color

// Beautiful
@ElementInfo(name = "GameInfo",blur = true)
class GameInfo : Element() {
    var s=0
    var m=0
    var h=0
    var tag = "NM$.L"
    private val bgAlphaValue = IntegerValue("BackgroundAlpha", 160,0,255)
    private val ClockAlpha = IntegerValue("ClockAlpha", 160,0,255)
    private val TextAlpha = IntegerValue("TextAlpha", 160,0,255)
    private val textredValue = IntegerValue("TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("TextGreen", 255, 0, 255)
    private val textblueValue = IntegerValue("TextBlue", 255, 0, 255)



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

        /**
         *  Double形
         */
        val left = 0.0
        val right = 140.0
        val top = -1.0
        val bottom = 70.0


        //idk
        val Ztop = top + 10


        /**
         * Int形
         */
        val left2 = 0
        val right2 = 140
        val top2 = -1
        val bottom2 = 70

        /**
         * Float形
         */
        val left3 = 0f + 5
        val leftclock = 0f
        val right3 = 140f
        val top3 = -1f
        val bottom3 = 70f


        RenderUtils.drawGradientSidewaysV(left, top, right, bottom, Color(149, 40, 222 ,bgAlphaValue.get()).rgb , Color(24, 115, 210 , bgAlphaValue.get()).rgb)//渐变框
        NewElementsUtils.drawFilledCircleButINT(left2 + 115 , top2 + 50 ,20f , Color(175, 175, 175 ,ClockAlpha.get()).rgb)//int色实心圆-白
        NewElementsUtils.drawFilledCircleButINT(left2 + 115 + 2/5, top2 + 50,18.5f ,Color(24, 115, 210 ,bgAlphaValue.get()).rgb)//int色实心圆-蓝
        RenderUtils.drawGradientSidewaysV(left, top, right, top - 5, Color(0, 0, 0,150 ).rgb , Color(0, 0, 0,0).rgb)//上阴影
        RenderUtils.drawGradientSidewaysV(left, bottom, right, bottom + 5, Color(0, 0, 0,150 ).rgb , Color(0, 0, 0,0).rgb)//下阴影
//        RenderUtils.drawGradientSidewaysV(left, top, left - 3, bottom, Color(0, 0, 0,60 ).rgb , Color(0, 0, 0,0).rgb)//左阴影
//        RenderUtils.drawGradientSidewaysV(right, top, right + 3, bottom, Color(0, 0, 0,60 ).rgb , Color(0, 0, 0,0).rgb)//右阴影
        RenderUtils.drawGradientSidewaysV(left + 3, Ztop, right - 3, Ztop + 5 , Color(0, 0, 0,150 ).rgb , Color(0, 0, 0,0).rgb)//中部阴影


        /**
         * 第一行的 字体 [无属性]
         */
        Fonts.poppins16.drawStringWithShadow("PlayInfo",left3,top3 + 3,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:PlayInfo

        /**
         * 第二行的 字体 [属性：PlayKill]
         */
        Fonts.poppins16.drawStringWithShadow("Kills: ${LiquidBounce.combatManager.getKillCounts()}",left3,top3 + 20 - 2,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:Kills

        /**
         * 第三行的 字体 [属性:games-played]
         */
        Fonts.poppins16.drawStringWithShadow("Games: ${LiquidBounce.combatManager.getTotalPlayed()}",left3,top3 + 40 - 5,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:Played

        /**
         * 第三行的 字体 [属性:games-win]
         */
        Fonts.poppins16.drawStringWithShadow("Wins: ${LiquidBounce.combatManager.getWin()}",left3,top3 + 60 - 5,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:Played-win

        /**
         * 时钟
         */
        Fonts.poppins16.drawStringWithShadow("Time",leftclock + 95 ,top3 + 20 - 2,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:time
        Fonts.poppins16.drawStringWithShadow(Text.HOUR_FORMAT.format(System.currentTimeMillis()),leftclock + 95,top3 + 40 ,Color(textredValue.get(), textgreenValue.get(), textblueValue.get() ,TextAlpha.get()).rgb)//字:time




        return Border(0f, -1f + 5, 140f, 70f)
    }
}
//

/**
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
 * CODEs BY PACKET [Without skid but not fantastic code]
  */
