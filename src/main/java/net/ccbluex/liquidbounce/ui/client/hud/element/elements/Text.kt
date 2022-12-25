/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.client.Interpolate
import net.ccbluex.liquidbounce.features.module.modules.render.LiquidBouncePlus.ColorMixer
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.ping
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.Palette
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.util.ChatAllowedCharacters
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import oh.yalan.NativeClass
import org.lwjgl.input.Keyboard
import java.awt.Color
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import kotlin.math.sqrt

/**
 * CustomHUD text element
 *
 * Allows to draw custom text
 */
@NativeClass
@ElementInfo(name = "Text", blur = true)
class Text(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F, side: Side = Side.default()) : Element(x, y, scale, side) {

    companion object {
        val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd")
        val HOUR_FORMAT = SimpleDateFormat("HH:mm")

        val DECIMAL_FORMAT = DecimalFormat("#.##")
    }

    val displayString = TextValue("DisplayText", "")
    private val redValue = IntegerValue("Red", 255, 0, 255)
    private val greenValue = IntegerValue("Green", 255, 0, 255)
    private val blueValue = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue = IntegerValue("Alpha", 255, 0, 255)
    private val redValue2 = IntegerValue("Red", 255, 0, 255)
    private val greenValue2 = IntegerValue("Green", 255, 0, 255)
    private val blueValue2 = IntegerValue("Blue", 255, 0, 255)
    private val alphaValue2 = IntegerValue("Alpha", 255, 0, 255)
    val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow", "Mixer", "RainbowG", "MixerG"), "Custom")
    val shadow = BoolValue("Shadow", true)
    private val mixerSecValue = IntegerValue("Mixer-Seconds", 2, 1, 10)
    private val mixerDistValue = IntegerValue("Mixer-Distance", 2, 0, 10)
    private val mixerIndexValue = IntegerValue("Mixer-Index", 50, 10, 1000)
    private val rectRedValue = IntegerValue("RectRed", 0, 0, 255)
    private val rectGreenValue = IntegerValue("RectGreen", 0, 0, 255)
    private val rectBlueValue = IntegerValue("RectBlue", 0, 0, 255)
    private val rectAlphaValue = IntegerValue("RectAlpha", 255, 0, 255)
    val rectColorModeValue = ListValue("RectColor", arrayOf("Custom", "Rainbow", "AnotherRainbow", "SkyRainbow", "Mixer"), "Custom")
    val rectValue = ListValue("Rect", arrayOf("Normal", "Mix", "Skeet", "Artemis", "Flux", "Novoline", "Border", "None"), "None")
    private val lineValue = BoolValue("SkeetLine", true)
    private val rectExpandValue = FloatValue("RectExpand", 0.3F, 0F, 1F)
    private val rainbowSpeed = IntegerValue("RainbowSpeed", 10, 1, 10)
    private val rainbowIndex = IntegerValue("RainbowIndex", 1, 1, 20)

    val fontValue = FontValue("Font", Fonts.font35)

    private var editMode = false
    private var editTicks = 0
    private var prevClick = 0L

    private var displayText = display

    private val display: String
        get() {
            val textContent = if (displayString.get().isEmpty() && !editMode) {
                "Text Element(Double click to edit)"
            } else {
                displayString.get()
            }

            return multiReplace(textContent)
        }

    private fun getReplacement(str: String): String? {
        if (mc.thePlayer != null) {
            when (str) {
                "x" -> return DECIMAL_FORMAT.format(mc.thePlayer.posX)
                "y" -> return DECIMAL_FORMAT.format(mc.thePlayer.posY)
                "z" -> return DECIMAL_FORMAT.format(mc.thePlayer.posZ)
                "xdp" -> return mc.thePlayer.posX.toString()
                "ydp" -> return mc.thePlayer.posY.toString()
                "zdp" -> return mc.thePlayer.posZ.toString()
                "velocity" -> return DECIMAL_FORMAT.format(sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ))
                "ping" -> return "${mc.thePlayer.ping}"
                "speed" -> return DECIMAL_FORMAT.format(MovementUtils.bps)
                "attackDist" -> return if (LiquidBounce.combatManager.target != null) mc.thePlayer.getDistanceToEntity(LiquidBounce.combatManager.target).toString() + " Blocks" else "Hasn't attacked"
                "health" -> return mc.thePlayer.health.toString()
            }
        }

        return when (str) {
            "username" -> mc.getSession().username
            "clientName" -> "AeroLite"
            "clientVersion" -> LiquidBounce.CLIENT_REAL_VERSION
            "clientCreator" -> LiquidBounce.CLIENT_CREATOR
            "fps" -> Minecraft.getDebugFPS().toString()
            "date" -> DATE_FORMAT.format(System.currentTimeMillis())
            "time" -> HOUR_FORMAT.format(System.currentTimeMillis())
            "serverIp" -> ServerUtils.getRemoteIp()
            "cps", "lcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.LEFT).toString()
            "mcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.MIDDLE).toString()
            "rcps" -> return CPSCounter.getCPS(CPSCounter.MouseButton.RIGHT).toString()
            else -> null // Null = don't replace
        }
    }

    private fun multiReplace(str: String): String {
        var lastPercent = -1
        val result = StringBuilder()
        for (i in str.indices) {
            if (str[i] == '%') {
                if (lastPercent != -1) {
                    if (lastPercent + 1 != i) {
                        val replacement = getReplacement(str.substring(lastPercent + 1, i))

                        if (replacement != null) {
                            result.append(replacement)
                            lastPercent = -1
                            continue
                        }
                    }
                    result.append(str, lastPercent, i)
                }
                lastPercent = i
            } else if (lastPercent == -1) {
                result.append(str[i])
            }
        }

        if (lastPercent != -1) {
            result.append(str, lastPercent, str.length)
        }

        return result.toString()
    }


    /**
     * Draw element
     */
    override fun drawElement(partialTicks: Float): Border {
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get())

        val fontRenderer = fontValue.get()
        val mixerColor = ColorMixer.getMixedColor(-mixerIndexValue.get() * mixerDistValue.get() * 10, mixerSecValue.get()).rgb
        val mixerColor2 = ColorMixer.getMixedColor(-mixerIndexValue.get() * mixerDistValue.get() * 10, mixerSecValue.get())

        val rectColor = when (rectColorModeValue.get().lowercase()) {
            "rainbow" -> ColorUtils.rainbow().rgb
            "skyrainbow" -> ColorUtils.skyRainbow(rainbowIndex.get(), 1F, 1F, rainbowSpeed.get().toDouble()).rgb
            "anotherrainbow" -> ColorUtils.fade(Color(rectRedValue.get(), rectGreenValue.get(), rectBlueValue.get(), rectAlphaValue.get()), 100, rainbowIndex.get()).rgb
            "mixer" -> mixerColor
            else -> Color(rectRedValue.get(), rectGreenValue.get(), rectBlueValue.get(), rectAlphaValue.get()).rgb
        }
        val expand = fontRenderer.FONT_HEIGHT * rectExpandValue.get()
        when (rectValue.get().lowercase()) {
            "normal" -> {
                RenderUtils.drawRect(-expand, -expand - 1, fontRenderer.getStringWidth(displayText) + expand, -expand, ColorUtils.rainbow())
                RenderUtils.drawRect(-expand, -expand, fontRenderer.getStringWidth(displayText) + expand, fontRenderer.FONT_HEIGHT + expand, rectColor)
            }
            "mix" -> {
                RenderUtils.drawGradientSidewaysV(-expand.toDouble(), -expand.toDouble(), fontRenderer.getStringWidth(displayText) + expand.toDouble(), fontRenderer.FONT_HEIGHT + expand.toDouble(), Color(redValue.get(),greenValue.get(),blueValue.get(),alphaValue.get()).rgb,Color(redValue2.get(),greenValue2.get(),blueValue2.get(),alphaValue2.get()).rgb)
            }
            "skeet" -> {
                RenderUtils.drawExhiRect(-4F, if (lineValue.get()) -5F else -4F, fontRenderer.getStringWidth(displayText) + 4F, fontRenderer.FONT_HEIGHT + 2F, 230f)
            }
            "artemis" -> {
                RenderUtils.drawGradientRoundedNoAlphaOutline(-4, if (lineValue.get()) -5 else -4, fontRenderer.getStringWidth(displayText) + 4, fontRenderer.FONT_HEIGHT + 2,5,ColorMixer.getMixedColor(1 * mixerDistValue.get(), mixerSecValue.get()).rgb,ColorMixer.getMixedColor((1 + 1) * mixerDistValue.get(), mixerSecValue.get()).rgb,1)
            }
            "flux" -> {
                RenderUtils.drawRect(-2F, -6F, fontRenderer.getStringWidth(displayText) + 2F, fontRenderer.FONT_HEIGHT + 0F,Color(50,50,50,255))
                RenderUtils.drawGradientRect(-1, -5, fontRenderer.getStringWidth(displayText) +1, -2,ColorMixer.getMixedColor(1 * mixerDistValue.get(), mixerSecValue.get()).rgb,ColorMixer.getMixedColor((-1) * mixerDistValue.get(), mixerSecValue.get()).rgb)
            }
            "novoline" -> { // really awful
                val c1 = ColorUtils.interpolateColorsBackAndForth(15,0, Color(0,255,50), Color(50,0,255), Interpolate.interpolateHue.get())
                val c2 = ColorUtils.interpolateColorsBackAndForth(15,180, Color(0,255,50), Color(50,0,255), Interpolate.interpolateHue.get())
                RenderUtils.drawGradientRoundedNoAlphaOutline(-4, if (lineValue.get()) -5 else -4, fontRenderer.getStringWidth(displayText) + 4, fontRenderer.FONT_HEIGHT + 2,5,c1.rgb,c2.rgb,1)
            }
            "border" -> {
                RenderUtils.drawBorder(-expand, -expand - 1, fontRenderer.getStringWidth(displayText) + expand, -expand, 1f, rectColor)
            }
        }
        val charArray = displayText.toCharArray()
        val counter = intArrayOf(0)
        var length = 0
        for (charIndex in charArray) {
            fontRenderer.drawString(
                charIndex.toString(), length.toFloat(), 0F, when (colorModeValue.get().lowercase()) {
                    "rainbow" -> ColorUtils.rainbow().rgb
                    "rainbowg" -> Palette.fade2(ColorUtils.rainbow(), counter[0] * 100, displayText.length * 200).rgb
                    "skyrainbow" -> ColorUtils.skyRainbow(rainbowIndex.get(), 1F, 1F, rainbowSpeed.get().toDouble()).rgb
                    "anotherrainbow" -> ColorUtils.fade(color, 100, rainbowIndex.get()).rgb
                    "mixer" -> mixerColor
                    "mixerg" -> Palette.fade2(mixerColor2, counter[0] * 100, displayText.length * 200).rgb
                    else -> color.rgb
                }, shadow.get()
            )
            if (editMode && mc.currentScreen is GuiHudDesigner && editTicks <= 40) {
                fontRenderer.drawString(
                    "_", fontRenderer.getStringWidth(displayText) + 2F,
                    0F, Color.WHITE.rgb, shadow.get()
                )
            }
            counter[0] += 1
            counter[0] = counter[0].coerceIn(0, displayText.length)
            length += fontRenderer.getCharWidth(charIndex)
        }

        if (editMode && mc.currentScreen !is GuiHudDesigner) {
            editMode = false
            updateElement()
        }

        return Border(
                -2F,
                -2F,
                fontRenderer.getStringWidth(displayText) + 2F,
                fontRenderer.FONT_HEIGHT.toFloat()
        )
    }

    override fun updateElement() {
        editTicks += 5
        if (editTicks > 80) editTicks = 0

        displayText = if (editMode) displayString.get() else display
    }

    override fun handleMouseClick(x: Double, y: Double, mouseButton: Int) {
        if (isInBorder(x, y) && mouseButton == 0) {
            if (System.currentTimeMillis() - prevClick <= 250L) {
                editMode = true
            }

            prevClick = System.currentTimeMillis()
        } else {
            editMode = false
        }
    }

    override fun handleKey(c: Char, keyCode: Int) {
        if (editMode && mc.currentScreen is GuiHudDesigner) {
            if (keyCode == Keyboard.KEY_BACK) {
                if (displayString.get().isNotEmpty()) {
                    displayString.set(displayString.get().substring(0, displayString.get().length - 1))
                }

                updateElement()
                return
            }

            if (ChatAllowedCharacters.isAllowedCharacter(c) || c == '§') {
                displayString.set(displayString.get() + c)
            }

            updateElement()
        }
    }

    fun setColor(c: Color): Text {
        redValue.set(c.red)
        greenValue.set(c.green)
        blueValue.set(c.blue)
        return this
    }
}
