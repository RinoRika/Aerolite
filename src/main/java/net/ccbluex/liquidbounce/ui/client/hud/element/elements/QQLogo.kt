package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RenderUtils
import net.ccbluex.liquidbounce.utils.WbxMain
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.net.URL
import javax.imageio.ImageIO

@ElementInfo(name = "QQLogo")
class QQLogo : Element() {
    private var got = false
    override fun drawElement(partialTicks: Float): Border? {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        try {
            if (!got) {
                mc.textureManager.loadTexture(
                    ResourceLocation("sb"),
                    DynamicTexture(ImageIO.read(URL("https://q.qlogo.cn/headimg_dl?dst_uin=" + WbxMain.username + "&spec=640&img_type=png")))
                )
                got = true
            }
        } catch (throwable: Throwable) {
        }
        mc.textureManager.bindTexture(ResourceLocation("sb"))
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, 60, 60, 60f, 60f)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        val color = Color(45, 45, 45).rgb
        var i = 26f
        while (i <= 42) {
            RenderUtils.drawOutFullCircle(31.5f, 30f, i, color, 5f)
            i += 1f
        }
        RenderUtils.drawOutFullCircle(31.5f, 30f, 27f, Color(53, 141, 204).rgb, 2f)
        RenderUtils.drawGradientSideways(60.0, 30.0, 180.0, 45.0, Color(45, 45, 45, 255).rgb, Color(45, 45, 45, 0).rgb)
        RenderUtils.drawOutFullCircle(31.5f, 30f, 44f, Color(0, 230, 0).rgb, 3f, -7f, 320f)
        RenderUtils.drawGradientSideways(60.0, 1.0, 200.0, 26.5, Color(45, 45, 45, 255).rgb, Color(45, 45, 45, 0).rgb)
        Fonts.font35.drawString(
            mc.getSession().username + " | " + Math.round(mc.thePlayer.health) + "hp",
            80f,
            10f,
            Color(200, 200, 200).rgb
        )
        Fonts.font35.drawString(mc.thePlayer.foodStats.foodLevel.toString(), 90f, 34f, -1, false)
        Fonts.font35.drawString("Food", 105f, 34f, Color(236, 161, 4).rgb, false)
        return Border(0f, 0f, 120f, 30f)
    }
}