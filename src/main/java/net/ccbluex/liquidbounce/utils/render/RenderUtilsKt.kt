package net.ccbluex.liquidbounce.utils.render

import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11.*

object RenderUtilsKt: MinecraftInstance() {
    fun drawHead(skin: ResourceLocation, x: Float, y: Float, scale: Float, width: Int, height: Int, red: Float, green: Float, blue: Float, alpha: Float = 1F) {
        glPushMatrix()
        glTranslatef(x, y, 0F)
        glScalef(scale, scale, scale)
        glDisable(GL_DEPTH_TEST)
        glEnable(GL_BLEND)
        glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_ONE, GL_ZERO)
        glColor4f(red.coerceIn(0F, 1F), green.coerceIn(0F, 1F), blue.coerceIn(0F, 1F), alpha.coerceIn(0F, 1F))
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
            64F, 64F)
        glDepthMask(true)
        glDisable(GL_BLEND)
        glEnable(GL_DEPTH_TEST)
        glPopMatrix()
        glColor4f(1f, 1f, 1f, 1f)
    }
    fun interpolate(old: Double, current: Double): Double {
        return old + (current - old) * mc.timer.renderPartialTicks.toDouble()
    }

}