package net.ccbluex.liquidbounce.features.module.modules.render

import me.stars.utils.BlurUtils
import me.stars.utils.BlurUtils2
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.RenderUtil
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Translate
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.client.gui.GuiGameOver
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import org.lwjgl.BufferUtils
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL12
import java.awt.Color
import java.nio.IntBuffer

@ModuleInfo(name = "JelloTabGui", category = ModuleCategory.RENDER, array = false)
class JelloTabGui : Module() {
    val notselectrect = BoolValue("SelectNotDrawRect", false)
    val selectgradientBackground = BoolValue("SelectGradientRect", false)
    val gradientBackground = BoolValue("GradientRect", true)
    val blur = BoolValue("Blur", false)

    val x = 5f
    val y = 50f
    val height = 78.5f
    var width1 = 75f

    var openModuleGui = false;
    var selectedCategory = 0
    var selecteModuleindex = 0

    var selecteModule = emptyList<Module>()
    var Modulecategory = mutableListOf<AnimaitonCategory>()

    val categoryAnimaiton = Translate(0f, 0f)
    val moduleAnimaiton = Translate(0f, 0f)
    var categoryPositonY = 0f
    var ModulePositonY = 0f

    var msTimer = MSTimer()

    init {
        state = true
        for(index in  0..ModuleCategory.values().lastIndex) {
            val animationcategory = AnimaitonCategory(ModuleCategory.values()[index].displayName , Translate(0f , 0f))
            Modulecategory.add(animationcategory)
        }
    }

    //��ʼ��ɫ
    var top = Color(255, 255, 255, 255)
    var bottom = Color(255, 255, 255, 255)

    //top gradient color
    var tRed = 0
    var tGreen= 0
    var tBlue = 0

    //last top gradient color
    var lasttRed = 0
    var lasttGreen = 0
    var lasttBlue= 0

    //bottom gradient color
    var bRed = 0
    var bGreen = 0
    var bBlue = 0

    //bottom top gradient color
    var lastbRed = 0
    var lastbGreen = 0
    var lastbBlue = 0

    //defaults
    var colorTop = 0
    var colorTopRight = 0
    var colorBottom = 0
    var colorBottomRight = 0

    @EventTarget
    fun onTick(event: TickEvent) {
        lasttRed = tRed
        lasttGreen = tGreen
        lasttBlue = tBlue
        lastbRed = bRed
        lastbGreen = bGreen
        lastbBlue = bBlue

        val top = ColorUtils.blend(ColorUtils.colorFromInt(colorTop), ColorUtils.colorFromInt(colorTopRight))
        val bottom = ColorUtils.blend(ColorUtils.colorFromInt(colorBottom), ColorUtils.colorFromInt(colorBottomRight))

        bRed += ((bottom.red - bRed) / 5 + 0.1).toInt()
        bGreen += ((bottom.green - bGreen) / 5 + 0.1).toInt()
        bBlue += ((bottom.blue - bBlue) / 5 + 0.1).toInt()

        tRed += ((top.red - tRed) / 5 + 0.1).toInt()
        tGreen += ((top.green - tGreen) / 5 + 0.1).toInt()
        tBlue += ((top.blue - tBlue) / 5 + 0.1).toInt()

        tRed = Math.min(tRed, 255)
        tGreen = Math.min(tGreen, 255)
        tBlue = Math.min(tBlue, 255)
        tRed = Math.max(tRed, 0)
        tGreen = Math.max(tGreen, 0)
        tBlue = Math.max(tBlue, 0)

        bRed = Math.min(bRed, 255)
        bGreen = Math.min(bGreen, 255)
        bBlue = Math.min(bBlue, 255)
        bRed = Math.max(bRed, 0)
        bGreen = Math.max(bGreen, 0)
        bBlue = Math.max(bBlue, 0)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        val sr = ScaledResolution(mc)
        if(msTimer.hasTimePassed(50)) {
            var p_148259_2_ = 0
            var p_148259_3_ = 0
            var pixelBuffer: IntBuffer? = null
            var pixelValues: IntArray? = null


            if (OpenGlHelper.isFramebufferEnabled()) {
                p_148259_2_ = 180
                p_148259_3_ = 280
            }

            var var6 = p_148259_2_ * p_148259_3_

            if (pixelBuffer == null || pixelBuffer.capacity() < var6) {
                pixelBuffer = BufferUtils.createIntBuffer(var6)
                pixelValues = IntArray(var6)
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            pixelBuffer!!.clear()

            GL11.glReadPixels(
                0,
                sr.scaledHeight - (p_148259_3_ - sr.scaledHeight) /*728*/,
                p_148259_2_,
                p_148259_3_,
                GL12.GL_BGRA,
                GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                pixelBuffer
            )

            pixelBuffer[pixelValues]

            if (mc.currentScreen !is GuiGameOver && mc.gameSettings.guiScale == 2) {
                if (pixelValues!!.size > 1000) {
                    colorTop = pixelValues[45 * sr.scaleFactor * p_148259_2_ + 10]
                    colorTopRight = pixelValues[45 * sr.scaleFactor * p_148259_2_ + 130]
                    colorBottom = pixelValues[(45 + 77) * sr.scaleFactor * p_148259_2_ + 10]
                    colorBottomRight = pixelValues[(45 + 77) * sr.scaleFactor * p_148259_2_ + 130]
                }
            }

            p_148259_2_ = 0
            p_148259_3_ = 0
            pixelBuffer = null
            pixelValues = null

            if (OpenGlHelper.isFramebufferEnabled()) {
                p_148259_2_ = 280
                p_148259_3_ = 150
            }

            var6 = p_148259_2_ * p_148259_3_

            if (pixelBuffer == null || pixelBuffer.capacity() < var6) {
                pixelBuffer = BufferUtils.createIntBuffer(var6)
                pixelValues = IntArray(var6)
            }

            GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1)
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)
            pixelBuffer!!.clear()

            GL11.glReadPixels(
                sr.scaledWidth - (p_148259_2_ - sr.scaledWidth),
                sr.scaledHeight - (p_148259_3_ - sr.scaledHeight) /*728*/,
                p_148259_2_,
                p_148259_3_,
                GL12.GL_BGRA,
                GL12.GL_UNSIGNED_INT_8_8_8_8_REV,
                pixelBuffer
            )

            pixelBuffer[pixelValues]
            msTimer.reset()
        }
    }

    var tR = 0
    var tG = 0
    var tB = 0

    var bR = 0
    var bG = 0
    var bB = 0

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        tR = smoothAnimation(tRed.toDouble(), lasttRed.toDouble())
        tG = smoothAnimation(tGreen.toDouble(), lasttGreen.toDouble())
        tB = smoothAnimation(tBlue.toDouble(), lasttBlue.toDouble())

        bR = smoothAnimation(bRed.toDouble(), lastbRed.toDouble())
        bG = smoothAnimation(bGreen.toDouble(), lastbGreen.toDouble())
        bB = smoothAnimation(bBlue.toDouble(), lastbBlue.toDouble())

        if(gradientBackground.get()) {
            RenderUtil.R2DUtils.drawGradientRect(5f , 50f , 80f ,  127.5f,
                Color(tR, tG, tB, 255).rgb,
                Color(bR, bG, bB, 255).rgb)
        }

        updateBackGound()
        enabler()
        enablerScissorBox()

        categoryAnimaiton.translate(0f , selectedCategory * 15f , 2.0)
        val categorysupery = if(categoryAnimaiton.y - 60f > 0) categoryAnimaiton.y - 60f else 0f

        if(!notselectrect.get()) {
            RenderUtils.drawRect(
                x,
                y + categoryAnimaiton.y - categorysupery,
                width1 + 5,
                y + categoryAnimaiton.y + 17f - categorysupery,
                Color(55, 55, 55, 50)
            )
        }

        if(selectgradientBackground.get()) {
            RenderUtil.R2DUtils.drawGradientRect(x, y + categoryAnimaiton.y - categorysupery, width1 + 5, y + categoryAnimaiton.y + 17f - categorysupery,  /*415277420*/
                Color(tR, tG, tB, 255).rgb,
                Color(bR, bG, bB, 255).rgb)
        }

        categoryPositonY = 0f
        Modulecategory.forEachIndexed { index, category ->

            category.animation.translate(if(selectedCategory == index) 15f else 5f , 0f ,  2.0 )

            val font = Fonts.fontJello
            font.drawString(category.displayname, x + category.animation.x , y + 5f + categoryPositonY - categorysupery, -1)
            categoryPositonY += 15f
        }

        disablerScissorBox()
        disabler()

        enabler()
        moduleAnimaiton.translate(0f , selecteModuleindex * 15f , 2.0)

        val modulesupery = if(moduleAnimaiton.y - 150f > 0) moduleAnimaiton.y - 150f else 0f
        val positiony = (if(ModulePositonY >= 165) 165f else ModulePositonY)

        if(openModuleGui) {
            if(gradientBackground.get()) {
                RenderUtil.R2DUtils.drawGradientRect(90f, 50f, 170f, 216.67f,
                    Color(tR, tG, tB, 255).rgb,
                    Color(bR, bG, bB, 255).rgb)
            }

            RenderUtils.drawShadow(x + width1 + 10, y,  width1 + 5,  positiony + 2f)

            if (blur.get()) BlurUtils.blurArea(x + width1 + 90f, y, width1 + 15, positiony + 52.5f, 10F)

            if(!notselectrect.get()) {
                RenderUtils.drawRect(
                    x + width1 + 10,
                    y + moduleAnimaiton.y - modulesupery,
                    x + width1 + width1 + 15,
                    y + moduleAnimaiton.y - modulesupery + 17f,
                    Color(55, 55, 55, 50)
                )
            }

            if(selectgradientBackground.get()) {
                RenderUtil.R2DUtils.drawGradientRect(x + width1 + 10, y + moduleAnimaiton.y - modulesupery, x + width1 + width1 + 15, y + moduleAnimaiton.y - modulesupery + 17f,
                    Color(tR, tG, tB, 255).rgb,
                    Color(bR, bG, bB, 255).rgb)
            }
        }

        RenderUtils.makeScissorBox(x + width1 + 10, y,  x + width1 + width1 + 15, y + positiony)
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        ModulePositonY = 0f
        selecteModule.forEachIndexed {index , module ->
            module.tab.translate(if(selecteModuleindex == index) 15f else 5f , 0f , 2.0)
            val font = Fonts.fontJello
            font.drawString(module.name, x + width1 + 10 + module.tab.x, y + 5f + ModulePositonY - modulesupery , -1)
            ModulePositonY += 15f
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        disabler()
    }

    private fun updateBackGound() {
        RenderUtils.drawShadow(x , y , width1 , height - 1)
        if (blur.get()) BlurUtils.blurArea(x, y, width1 + 4.9f, height + 49.5f, 10F)
    }

    private fun enablerScissorBox() {
        RenderUtils.makeScissorBox(x, y,x + width1, y + height )
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
    }

    private fun disablerScissorBox() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }

    private fun enabler() {
        GlStateManager.pushMatrix()
    }

    private fun disabler() {
        GlStateManager.popMatrix()
    }


    @EventTarget
    fun onKey(event : KeyEvent) {
        val key = event.key
        handleKey(key)
    }

    fun drawGradientRect(x: Float, y: Float, x1: Float, y1: Float, topColor: Int, bottomColor: Int) {
        RenderUtil.R2DUtils.enableGL2D()
        GL11.glShadeModel(7425)
        GL11.glBegin(7)
        RenderUtil.R2DUtils.glColor(topColor)
        GL11.glVertex2f(x, y1)
        GL11.glVertex2f(x1, y1)
        RenderUtil.R2DUtils.glColor(bottomColor)
        GL11.glVertex2f(x1, y)
        GL11.glVertex2f(x, y)
        GL11.glEnd()
        GL11.glShadeModel(7424)
        RenderUtil.R2DUtils.disableGL2D()
    }

    private fun handleKey(keyCode: Int) {
        when (keyCode) {
            Keyboard.KEY_UP  -> { parseAction(Action.UP) }
            Keyboard.KEY_DOWN -> { parseAction(Action.DOWN) }
            Keyboard.KEY_LEFT -> { parseAction(Action.LEFT) }
            Keyboard.KEY_RIGHT -> { parseAction(Action.RIGHT) }
            Keyboard.KEY_RETURN-> { parseAction(Action.TOGGLE) }
        }
    }

    fun smoothAnimation(current: Double, last: Double): Int {
        return (current * mc.timer.renderPartialTicks + last * (1.0f - mc.timer.renderPartialTicks)).toInt()
    }

    private fun parseAction(action: Action) {
        when (action) {
            Action.UP -> {
                if (selectedCategory > 0 && !openModuleGui) selectedCategory--
                if (selecteModuleindex > 0) selecteModuleindex--
            }
            Action.DOWN -> {
                if (selectedCategory < Modulecategory.lastIndex && !openModuleGui) selectedCategory++
                if (selecteModuleindex < selecteModule.lastIndex) selecteModuleindex++
            }
            Action.LEFT -> {
                if(openModuleGui) {
                    openModuleGui = false
                    selecteModuleindex = 0
                    selecteModule = listOf()
                }
            }
            Action.RIGHT -> {
                if(!openModuleGui) {
                    openModuleGui = true
                    selecteModule = LiquidBounce.moduleManager.modules.filter { it.category == ModuleCategory.values()[selectedCategory]}.sortedBy { 0 }
                }
            }
            Action.TOGGLE -> {
                if(openModuleGui) {
                    val selecetd = selecteModule[selecteModuleindex]
                    selecetd.toggle()
                }
            }
        }
    }

    /**
     * TabGUI Action
     */
    enum class Action { UP, DOWN, LEFT, RIGHT, TOGGLE }

    class AnimaitonCategory(var displayname : String , var animation : Translate) {}
}
