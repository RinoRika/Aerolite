package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import oh.yalan.NativeMethod
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.math.BigDecimal

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "Notifications")
class Notifications(x: Double = 0.0, y: Double = 0.0, scale: Float = 1F,
                    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)) : Element(x, y, scale, side) {
    /**
     * Example notification for CustomHUD designer
     */
    companion object {
        val notificationStyle = ListValue("Style", arrayOf("Novoline", "FDPOld"), "Novoline")
    }
    private val exampleNotification = Notification("Notification", "This is an example notification.", NotifyType.INFO)

    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border? {
        val notifications = mutableListOf<Notification>()
        //FUCK YOU java.util.ConcurrentModificationException
        for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
            GL11.glPushMatrix()

            if (notify.drawNotification(index)) {
                notifications.add(notify)
            }

            GL11.glPopMatrix()
        }
        for (notify in notifications) {
            LiquidBounce.hud.notifications.remove(notify)
        }

        if (mc.currentScreen is GuiHudDesigner) {
            if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                LiquidBounce.hud.addNotification(exampleNotification)

            exampleNotification.fadeState = FadeState.STAY
            exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

            return Border(-exampleNotification.width.toFloat() + 80, -exampleNotification.height.toFloat()-24.5f, 80F, -24.5F)
        }

        return null
    }

}

class Notification(val title: String, val content: String, val type: NotifyType, val time: Int = 2000, val animeTime: Int = 500) {
    val height = 30
    var fadeState = FadeState.IN
    var nowY = -height
    private val classicHeight = 30
    var string = ""
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()
    val width = Fonts.font32.getStringWidth(content) + 56

    /**
     * Draw notification
     */
    fun drawNotification(index: Int): Boolean {
        var color = Color(-1)
        val realY = -(index + 1) * (height + 10)
        var transY = nowY.toDouble() - 10.0
        val nowTime = System.currentTimeMillis()
        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeOutExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        if (type.toString() == "SUCCESS") {
            string = "a"
        }
        if (type.toString() == "ERROR") {
            string = "B"
        }
        if (type.toString() == "WARNING") {
            string = "D"
        }
        if (type.toString() == "INFO") {
            string = "C"
        }
        color = when (type.toString()) {
            "SUCCESS" -> Color.GREEN
            "ERROR" -> Color.RED
            "WARNING" -> Color.YELLOW
            "INFO" -> Color.GRAY
            else -> Color.WHITE
        }
        val transX = width - (width * pct) - width
        when (Notifications.Companion.notificationStyle.get()) {
            "Novoline" -> {
                GL11.glScaled(pct, pct, pct)
                val displayingTime =
                    BigDecimal(((time - time * ((nowTime - displayTime) / (animeTime * 2F + time))) / 1000).toDouble()).setScale(
                        1,
                        BigDecimal.ROUND_HALF_UP
                    )
                GL11.glTranslatef(-width.toFloat() / 2, -height.toFloat() / 2, 0F)
                RenderUtils.drawShadow(0F, 0F, width.toFloat(), height.toFloat())
                RenderUtils.drawRect(0F, 0F, width.toFloat(), height.toFloat(), Color(63, 63, 63, 100))
                RenderUtils.drawRect(
                    0.0,
                    height - 1.7,
                    (width * ((nowTime - displayTime) / (animeTime * 2F + time))).toDouble(),
                    height.toDouble(),
                    color.rgb
                )
                Fonts.font37.drawStringWithShadow(title, 27F, 6F, Color.WHITE.rgb)
                Fonts.font32.drawStringWithShadow(
                    content + " (" + displayingTime.toString() + "s)",
                    27F,
                    17.3F,
                    Color.WHITE.rgb
                )
                RenderUtils.drawFilledCircle(14, 16, 8.5F, Color(0, 0, 0, 70))
                Fonts.Nicon80.drawString(string, 4.5f, 8f, Color.WHITE.rgb)
            }
            "FDPOld" -> {
                GL11.glScaled(pct, pct, pct)
                GL11.glTranslatef(-width.toFloat() / 2, -height.toFloat() / 2, 0F)
                RenderUtils.drawRect(0F, 0F, width.toFloat(), classicHeight.toFloat(), Color(0, 0, 0, 100))
                RenderUtils.drawRect(0F, classicHeight - 2F, Math.max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), 0F), classicHeight.toFloat(), color)
                Fonts.font35.drawString(title, 4F, 4F, Color(255, 255, 255).rgb, false)
                Fonts.font35.drawString(content, 4F, 17F,Color(255, 255, 255).rgb, false)
            }
        }

        GlStateManager.resetColor()
        return false
    }
}

enum class NotifyType(var icon: String) {
    SUCCESS("check-circle"),
    ERROR("close-circle"),
    WARNING("warning"),
    INFO("information");
}


enum class FadeState { IN, STAY, OUT, END }

