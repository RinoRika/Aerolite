/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.render.util.ColorMixer
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.RenderUtils.makeScissorBox
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.utils.Particle
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.utils.ShapeType
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.AnimationUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.utils.render.ColorUtils.interpolateColorC
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.AbstractClientPlayer
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiChat
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.gui.inventory.GuiInventory
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.util.MathHelper
import net.minecraft.util.ResourceLocation
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.math.pow
import kotlin.math.roundToInt

@ElementInfo(name = "Targets")
open class Targets : Element(-46.0, -40.0, 1F, Side(Side.Horizontal.MIDDLE, Side.Vertical.MIDDLE)) {

    val modeValue = ListValue("Mode", arrayOf("Aerolite", "Aerolite2", "AeroliteOld", "Stitch", "FDP", "Vape", "Bar", "OverFlow", "Chill", "Rice", "Slowly", "Remix", "Romantic", "Novoline", "Novoline2", "Novoline3", "Astolfo", "Liquid", "Flux", "Rise", "Exhibition", "ExhibitionOld", "Zamorozka", "Arris", "Tenacity", "TenacityNew", "WaterMelon", "SparklingWater", "Hanabi"), "FDP")
    private val modeRise = ListValue("RiseMode", arrayOf("Original", "New1", "New2", "Rise6"), "Rise6")

    private val chillFontSpeed = FloatValue("Chill-FontSpeed", 0.5F, 0.01F, 1F).displayable { modeValue.get().equals("chill", true) }
    private val chillRoundValue = BoolValue("Chill-RoundedBar", true).displayable { modeValue.get().equals("chill", true) }

    private var Health: Float = 0F

    private val fontValue = FontValue("Font", Fonts.font40)

    val shadowValue = BoolValue("Shadow", false)
    val shadowStrength = FloatValue("Shadow-Strength", 1F, 0.01F, 40F).displayable { shadowValue.get() }
    val shadowColorMode = ListValue("Shadow-Color", arrayOf("Background", "Custom", "Bar"), "Background").displayable { shadowValue.get() }
    private val shadowX = FloatValue("ShadowX", 0f, 0f, 300f)
    private val shadowY = FloatValue("ShadowY", 0f, 0f, 60f)

    val shadowColorRedValue = IntegerValue("Shadow-Red", 0, 0, 255).displayable { shadowValue.get() && shadowColorMode.get().equals("custom", true) }
    val shadowColorGreenValue = IntegerValue("Shadow-Green", 111, 0, 255).displayable { shadowValue.get() && shadowColorMode.get().equals("custom", true) }
    val shadowColorBlueValue = IntegerValue("Shadow-Blue", 255, 0, 255).displayable { shadowValue.get() && shadowColorMode.get().equals("custom", true) }


    private val animSpeedValue = IntegerValue("AnimSpeed", 10, 5, 20)
    private val hpAnimTypeValue = EaseUtils.getEnumEasingList("HpAnimType")
    private val hpAnimOrderValue = EaseUtils.getEnumEasingOrderList("HpAnimOrder")

    private val switchModeValue = ListValue("SwitchMode", arrayOf("Slide", "Zoom", "None"), "Slide")
    private val switchAnimTypeValue = EaseUtils.getEnumEasingList("SwitchAnimType")
    private val switchAnimOrderValue = EaseUtils.getEnumEasingOrderList("SwitchAnimOrder")
    private val switchAnimSpeedValue = IntegerValue("SwitchAnimSpeed", 20, 5, 40)

    val noAnimValue = BoolValue("No-Animation", false)
    val globalAnimSpeed = FloatValue("Global-AnimSpeed", 3F, 1F, 9F).displayable { noAnimValue.equals("No-Animation") }

    private val arrisRoundedValue = BoolValue("ArrisRounded", true).displayable { modeValue.equals("Arris") }

    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Sky", "Slowly", "Fade", "Health"), "Health")
    private val redValue = IntegerValue("Red", 252, 0, 255)
    private val greenValue = IntegerValue("Green", 96, 0, 255)
    private val blueValue = IntegerValue("Blue", 66, 0, 255)
    private val gredValue = IntegerValue("GradientRed", 255, 0, 255)
    private val ggreenValue = IntegerValue("GradientGreen", 255, 0, 255)
    private val gblueValue = IntegerValue("GradientBlue", 255, 0, 255)
    private val saturationValue = FloatValue("Saturation", 1F, 0F, 1F)
    private val brightnessValue = FloatValue("Brightness", 1F, 0F, 1F)

    private val bgRedValue = IntegerValue("Background-Red", 0, 0, 255)
    private val bgGreenValue = IntegerValue("Background-Green", 0, 0, 255)
    private val bgBlueValue = IntegerValue("Background-Blue", 0, 0, 255)
    private val bgAlphaValue = IntegerValue("Background-Alpha", 160, 0, 255)

    private val tenacityNewStatic = BoolValue("TenacityNewStaticRainbow", false)
    private val rainbowSpeed = IntegerValue("RainbowSpeed", 1, 1, 10)
    private val fadeValue = BoolValue("FadeAnim", false)
    private val fadeSpeed = FloatValue("Fade-Speed", 1F, 0F, 5F)
    private val waveSecondValue = IntegerValue("Seconds", 2, 1, 10)


    private val riseHurtTime = BoolValue("RiseHurt", true).displayable { modeValue.equals("Rise") }
    private val riseAlpha = IntegerValue("RiseAlpha", 130, 0, 255).displayable { modeValue.equals("Rise") }
    private val riseCountValue = IntegerValue("Rise-Count", 5, 1, 20).displayable { modeValue.equals("Rise") }
    private val riseSizeValue = FloatValue("Rise-Size", 1f, 0.5f, 3f).displayable { modeValue.equals("Rise") }
    private val riseAlphaValue = FloatValue("Rise-Alpha", 0.7f, 0.1f, 1f).displayable { modeValue.equals("Rise") }
    private val riseDistanceValue = FloatValue("Rise-Distance", 1f, 0.5f, 2f).displayable { modeValue.equals("Rise") }
    private val riseMoveTimeValue = IntegerValue("Rise-MoveTime", 20, 5, 40).displayable { modeValue.equals("Rise") }
    private val riseFadeTimeValue = IntegerValue("Rise-FadeTime", 20, 5, 40).displayable { modeValue.equals("Rise") }

    private val gradientLoopValue = IntegerValue("GradientLoop", 4, 1, 40).displayable { modeValue.get().equals("Rice", true) }
    private val gradientDistanceValue = IntegerValue("GradientDistance", 50, 1, 200).displayable { modeValue.get().equals("Rice", true) }
    private val gradientRoundedBarValue = BoolValue("GradientRoundedBar", true).displayable { modeValue.get().equals("Rice", true) }

    private val riceParticle = BoolValue("Rice-Particle", true).displayable { modeValue.get().equals("Rice", true) }
    private val riceParticleSpin = BoolValue("Rice-ParticleSpin", true).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val generateAmountValue = IntegerValue("GenerateAmount", 10, 1, 40).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val riceParticleCircle = ListValue("Circle-Particles", arrayOf("Outline", "Solid", "None"), "Solid").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val riceParticleRect = ListValue("Rect-Particles", arrayOf("Outline", "Solid", "None"), "Outline").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val riceParticleTriangle = ListValue("Triangle-Particles", arrayOf("Outline", "Solid", "None"), "Outline").displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }

    private val riceParticleSpeed = FloatValue("Rice-ParticleSpeed", 0.05F, 0.01F, 0.2F).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val riceParticleFade = BoolValue("Rice-ParticleFade", true).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val riceParticleFadingSpeed = FloatValue("ParticleFadingSpeed", 0.05F, 0.01F, 0.2F).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }

    private val particleRange = FloatValue("Rice-ParticleRange", 50f, 0f, 50f).displayable { modeValue.get().equals("Rice", true) && riceParticle.get() }
    private val minParticleSize: FloatValue = object : FloatValue("MinParticleSize", 0.5f, 0f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxParticleSize.get()
            if (v < newValue) set(v)
        }
    }
    val maxParticleSize: FloatValue = object : FloatValue("MaxParticleSize", 2.5f, 0f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minParticleSize.get()
            if (v > newValue) set(v)
        }
    }


    var animProgress = 0F

    var easingHealth = 0F
    var barColor = Color(-1)
    var bgColor = Color(-1)

    private var prevTarget: EntityLivingBase? = null
    private var displayPercent = 0f
    private var lastUpdate = System.currentTimeMillis()

    private val decimalFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))
    private val decimalFormat2 = DecimalFormat("##0.0", DecimalFormatSymbols(Locale.ENGLISH))
    private val ndecimalFormat = DecimalFormat("#", DecimalFormatSymbols(Locale.ENGLISH))

    val shadowOpaque: Color
        get() = ColorUtils.reAlpha(when (shadowColorMode.get().lowercase(Locale.getDefault())) {
            "background" -> bgColor
            "custom" -> Color(shadowColorRedValue.get(), shadowColorGreenValue.get(), shadowColorBlueValue.get())
            else -> barColor
        }, 1F - animProgress)

    val particleList = mutableListOf<Particle>()
    private var gotDamaged = false

    private var progress: Float = 0F
    private var progressChill = 0F

    private var hpEaseAnimation: Animation? = null
    private var pastHP = 0f
    private var easingHP = 0f
        get() {
            if (hpEaseAnimation != null) {
                field = hpEaseAnimation!!.value.toFloat()
                if (hpEaseAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                    hpEaseAnimation = null
                }
            }
            return field
        }
        set(value) {
            if (hpEaseAnimation == null || (hpEaseAnimation != null && hpEaseAnimation!!.to != value.toDouble())) {
                hpEaseAnimation = Animation(EaseUtils.EnumEasingType.valueOf(hpAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(hpAnimOrderValue.get()), field.toDouble(), value.toDouble(), animSpeedValue.get() * 100L).start()
            }
        }

    private val numberRenderer = CharRenderer(false)

    private var calcScaleX = 0F
    private var calcScaleY = 0F
    private var calcTranslateX = 0F
    private var calcTranslateY = 0F

    private fun getHealth(entity: EntityLivingBase?): Float {
        return entity?.health ?: 0f
    }

    override fun drawElement(partialTicks: Float): Border? {

        var target = LiquidBounce.combatManager.target
        val time = System.currentTimeMillis()
        val pct = (time - lastUpdate) / (switchAnimSpeedValue.get() * 50f)
        lastUpdate = System.currentTimeMillis()

        if (mc.currentScreen is GuiHudDesigner || mc.currentScreen is GuiChat) {
            target = mc.thePlayer
        }
        if (target != null) {
            prevTarget = target
        }
        prevTarget ?: return getTBorder()

        if (target != null) {

            if (displayPercent < 1) {
                displayPercent += pct
            }
            if (displayPercent > 1) {
                displayPercent = 1f
            }
        } else {
            if (displayPercent > 0) {
                displayPercent -= pct
            }
            if (displayPercent < 0) {
                displayPercent = 0f
                prevTarget = null
                return getTBorder()
            }
        }



        if (hpEaseAnimation != null) {
            easingHP = hpEaseAnimation!!.value.toFloat()
            if (hpEaseAnimation!!.state == Animation.EnumAnimationState.STOPPED) {
                hpEaseAnimation = null
            }
        }

        if (hpEaseAnimation == null || (hpEaseAnimation != null && hpEaseAnimation!!.to != getHealth(target).toDouble())) {
            hpEaseAnimation = Animation(EaseUtils.EnumEasingType.valueOf(hpAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(hpAnimOrderValue.get()), pastHP.toDouble(), getHealth(target).toDouble(), animSpeedValue.get() * 100L).start()
        }

        pastHP = getHealth(target)

        val easedPersent = EaseUtils.apply(EaseUtils.EnumEasingType.valueOf(switchAnimTypeValue.get()), EaseUtils.EnumEasingOrder.valueOf(switchAnimOrderValue.get()), displayPercent.toDouble()).toFloat()
        when (switchModeValue.get().lowercase()) {
            "zoom" -> {
                val border = getTBorder() ?: return null
                GL11.glScalef(easedPersent, easedPersent, easedPersent)
                GL11.glTranslatef(((border.x2 * 0.5f * (1 - easedPersent)) / easedPersent), ((border.y2 * 0.5f * (1 - easedPersent)) / easedPersent), 0f)
            }
            "slide" -> {
                val percent = EaseUtils.easeInQuint(1.0 - easedPersent)
                val xAxis = ScaledResolution(mc).scaledWidth - renderX
                GL11.glTranslated(xAxis * percent, 0.0, 0.0)
            }
        }

        val preBarColor = when (colorModeValue.get()) {
            "Rainbow" -> Color(ColorUtils.hslRainbow(100 * rainbowSpeed.get()).rgb)
            "Custom" -> Color(redValue.get(), greenValue.get(), blueValue.get())
            "Sky" -> ColorUtils.skyRainbow(0, saturationValue.get(), brightnessValue.get(), rainbowSpeed.get().toDouble())
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), 0, 100)
            "Health" -> if (target != null) BlendUtils.getHealthColor(target.health, target.maxHealth) else Color.red
            else -> ColorUtils.slowlyRainbow(System.nanoTime(), 0, saturationValue.get(), brightnessValue.get())
        }

        progress += 0.0025F * RenderUtils.deltaTime * if (target != null) -1F else 1F
        progressChill += 0.0075F * RenderUtils.deltaTime * if (target != null) -1F else 1F

        val preBgColor = Color(bgRedValue.get(), bgGreenValue.get(), bgBlueValue.get(), bgAlphaValue.get())

        if (fadeValue.get())
            animProgress += (0.0075F * fadeSpeed.get() * RenderUtils.deltaTime)
        else animProgress = 0F

        animProgress = animProgress.coerceIn(0F, 1F)

        barColor = ColorUtils.reAlpha(preBarColor, preBarColor.alpha / 255F * (1F - animProgress))
        bgColor = ColorUtils.reAlpha(preBgColor, preBgColor.alpha / 255F * (1F - animProgress))


        val calcScaleX = animProgress * (4F / 2F)
        val calcScaleY = animProgress * (4F / 2F)
        val calcTranslateX =  2F * calcScaleX
        val calcTranslateY = 2F * calcScaleY

        if (fadeValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
            GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
        }

        if (fadeValue.get())
            GL11.glPopMatrix()

        GlStateManager.resetColor()

        // FDP You shit
        // Skid lb+ without methods
        updateAnim(prevTarget!!.health)

        when (modeValue.get().lowercase()) {
            "fdp" -> drawFDP(prevTarget!!)
            "novoline" -> drawNovo(prevTarget!!)
            "novoline2" -> drawNovo2(prevTarget!!)
            "astolfo" -> drawAstolfo(prevTarget!!)
            "liquid" -> drawLiquid(prevTarget!!)
            "flux" -> drawFlux(prevTarget!!)
            "rise" -> {
                when (modeRise.get().lowercase()) {
                    "original" -> drawRise(prevTarget!!)
                    "new1" -> drawRiseNew(prevTarget!!)
                    "new2" -> drawRiseNewNew(prevTarget!!)
                    "rise6" -> drawRiseLatest(prevTarget!!)
                }
            }
            "vape" -> drawVape(prevTarget!!)
            "stitch" -> drawStitch(prevTarget!!)
            "zamorozka" -> drawZamorozka(prevTarget!!)
            "arris" -> drawArris(prevTarget!!)
            "tenacity" -> drawTenacity(prevTarget!!)
            "tenacitynew" -> drawTenacityNew(prevTarget!!)
            "chill" -> {
                drawChill(prevTarget!! as EntityPlayer)
                updateData(renderX.toFloat() + calcTranslateX, renderY.toFloat() + calcTranslateY, calcScaleX, calcScaleY)
            }
            "novoline3" -> drawNovo3(prevTarget!!)
            "remix" -> drawRemix(prevTarget!! as EntityPlayer)
            "rice" -> drawRice(prevTarget!!)
            "slowly" -> drawSlowly(prevTarget!!)
            "watermelon" -> drawWaterMelon(prevTarget!!)
            "sparklingwater" -> drawSparklingWater(prevTarget!!)
            "exhibition" -> drawExhibition(prevTarget!! as EntityPlayer)
            "exhibitionold" -> drawExhibitionOld(prevTarget!! as EntityPlayer)
            "bar" -> drawBar(prevTarget!!)
            "aerolite" -> drawAerolite(prevTarget!!)
            "aerolite2" -> drawAerolite2(prevTarget!!)
            "aeroliteold" -> drawAeroliteOld(prevTarget!!)
            "overflow" -> drawOverFlow(prevTarget!!)
            "romantic" -> drawRomantic(prevTarget!!)
            "hanabi" -> drawHanabi(prevTarget!!)
        }

        return getTBorder()

    }

    fun updateData(_a: Float, _b: Float, _c: Float, _d: Float) {
        calcTranslateX = _a
        calcTranslateY = _b
        calcScaleX = _c
        calcScaleY = _d
    }

    private fun drawAerolite(target: EntityLivingBase) {
        val font = Fonts.gs40
        val hpPos = 33F + ((getHealth(target) / target.maxHealth * 10000).roundToInt() / 100)
        val hurtPercent = target.hurtPercent
        RenderUtils.drawGradientSidewaysH(0.0,0.0,140.0,40.0, ColorUtils.rainbow().rgb, ColorMixer.getMixedColor(1000, 3).rgb)
        RoundedUtil.drawRound(-1.0f, -1.0f, 141.0f, 41.0f, 1.5f, ColorUtils.rainbow())

        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        RenderUtils.quickDrawHead(target.skin,1,3,30,30)
        GL11.glPopMatrix()
        RenderUtils.drawShadow(0f,0f,140f,40f)

        font.drawString(target.name,35,4,Color.white.rgb)
        font.drawString("Health: "+target.health.roundToInt(),35,15,Color.WHITE.rgb)
        font.drawString("Armor: "+target.totalArmorValue,35,26,Color.WHITE.rgb)
        RenderUtils.drawRoundedCornerRect(0f,38f,hpPos + 3f,40f,2f, ColorUtils.rainbow().rgb)
        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(-1F, -1F, 141F, 41F, Color(0, 0, 0).rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(-1F, -1F, 141F, 41F, Color(0, 0, 0).rgb)
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }

    private fun drawAstolfo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.skyRainbow(1, 1F, 0.9F, 5.0)
        val hpPct = easingHP / target.maxHealth

        RenderUtils.drawRect(0F, 0F, 140F, 60F, Color(0, 0, 0, 110).rgb)

        // health rect
        RenderUtils.drawRect(3F, 55F, 137F, 58F, ColorUtils.reAlpha(color, 100).rgb)
        RenderUtils.drawRect(3F, 55F, 3 + (hpPct * 134F), 58F, color.rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(18, 46, 20, target)

        font.drawStringWithShadow(target.name, 37F, 6F, -1)
        GL11.glPushMatrix()
        GL11.glScalef(2F, 2F, 2F)
        font.drawString("${getHealth(target).roundToInt()} ❤", 19, 9, color.rgb)
        GL11.glPopMatrix()
        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(0F, 0F, 140F, 60F, Color(0, 0, 0).rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(0F, 0F, 140F, 60F, Color(0, 0, 0).rgb)
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }


    private fun drawNovo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.healthColor(getHealth(target), target.maxHealth)
        val darkColor = ColorUtils.darker(color, 0.6F)
        val hpPos = 33F + ((easingHP / target.maxHealth * 10000).roundToInt() / 100)

        RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
        font.drawString(target.name, 33, 5, Color.WHITE.rgb)
        RenderUtils.drawEntityOnScreen(20, 35, 15, target)
        RenderUtils.drawRect(hpPos, 18F, 33F + ((easingHP / target.maxHealth * 10000).roundToInt() / 100), 25F, darkColor)
        RenderUtils.drawRect(33F, 18F, hpPos, 25F, color)
        font.drawString("❤", 33, 30, Color.RED.rgb)
        font.drawString(decimalFormat.format(getHealth(target)), 43, 30, Color.WHITE.rgb)
        GL11.glPushMatrix()
        ShadowUtils.shadow(shadowStrength.get(), {
            GL11.glPushMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            if (fadeValue.get()) {
                GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
            }
            RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
            GL11.glPopMatrix()
        }, {
            GL11.glPushMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            if (fadeValue.get()) {
                GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
            }
            RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
            GL11.glPopMatrix()
        })
        GL11.glPopMatrix()
        GL11.glTranslated(renderX, renderY, 0.0)
    }

    private fun drawNovo2(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.healthColor(getHealth(target), target.maxHealth)
        val darkColor = ColorUtils.darker(color, 0.6F)

        RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
        font.drawString(target.name, 35, 5, Color.WHITE.rgb)
        RenderUtils.drawHead(target.skin, 2, 2, 30, 30)
        RenderUtils.drawRect(35F, 17F, ((getHealth(target) / target.maxHealth) * 100) + 35F,
            35F, Color(252, 96, 66).rgb)

        font.drawString((decimalFormat.format((easingHP / target.maxHealth) * 100)) + "%", 40, 20, Color.WHITE.rgb)
    }

    private fun drawLiquid(target: EntityLivingBase) {
        val width = (38 + target.name.let(Fonts.font40::getStringWidth))
            .coerceAtLeast(118)
            .toFloat()
        // Draw rect box
        RenderUtils.drawBorderedRect(0F, 0F, width, 36F, 3F, Color.BLACK.rgb, Color.BLACK.rgb)

        // Damage animation
        if (easingHP > getHealth(target)) {
            RenderUtils.drawRect(0F, 34F, (easingHP / target.maxHealth) * width,
                36F, Color(252, 185, 65).rgb)
        }

        // Health bar
        RenderUtils.drawRect(0F, 34F, (getHealth(target) / target.maxHealth) * width,
            36F, Color(252, 96, 66).rgb)

        // Heal animation
        if (easingHP < getHealth(target)) {
            RenderUtils.drawRect((easingHP / target.maxHealth) * width, 34F,
                (getHealth(target) / target.maxHealth) * width, 36F, Color(44, 201, 144).rgb)
        }

        target.name.let { Fonts.font40.drawString(it, 36, 3, 0xffffff) }
        Fonts.font35.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 36, 15, 0xffffff)

        // Draw info
        RenderUtils.drawHead(target.skin, 2, 2, 30, 30)
        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        if (playerInfo != null) {
            Fonts.font35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
                36, 24, 0xffffff)
        }

        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                val width = (38 + Fonts.font40.getStringWidth(target.name)).coerceAtLeast(118).toFloat()
                RenderUtils.newDrawRect(0F, 0F, width, 36F, shadowOpaque.rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                val width = (38 + Fonts.font40.getStringWidth(target.name)).coerceAtLeast(118).toFloat()
                RenderUtils.newDrawRect(0F, 0F, width, 36F, shadowOpaque.rgb)
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }

    private fun drawZamorozka(target: EntityLivingBase) {
        val font = fontValue.get()

        // Frame
        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 55f, 5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRect(7f, 7f, 35f, 40f, Color(0, 0, 0, 70).rgb)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        RenderUtils.drawEntityOnScreen(21, 38, 15, target)

        // Healthbar
        val barLength = 143 - 7f
        RenderUtils.drawRoundedCornerRect(7f, 45f, 143f, 50f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(7f, 45f, 7 + ((easingHP / target.maxHealth) * barLength), 50f, 2.5f, ColorUtils.rainbowWithAlpha(90).rgb)
        RenderUtils.drawRoundedCornerRect(7f, 45f, 7 + ((target.health / target.maxHealth) * barLength), 50f, 2.5f, ColorUtils.rainbow().rgb)

        // Info
        RenderUtils.drawRoundedCornerRect(43f, 15f - font.FONT_HEIGHT, 143f, 17f, (font.FONT_HEIGHT + 1) * 0.45f, Color(0, 0, 0, 70).rgb)
        font.drawCenteredString("${target.name} ${if (target.ping != -1) { "§f${target.ping}ms" } else { "" }}", 93f, 16f - font.FONT_HEIGHT, ColorUtils.rainbow().rgb, false)
        font.drawString("Health: ${decimalFormat.format(easingHP)} §7/ ${decimalFormat.format(target.maxHealth)}", 43, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)
        font.drawString("Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 43, 11 + font.FONT_HEIGHT * 2, Color.WHITE.rgb)
    }

    private val riseParticleList = mutableListOf<RiseParticle>()

    private fun drawRise(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, riseAlpha.get()).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 30

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString("Name ${target.name}", 40, 11, Color.WHITE.rgb)
        if (riseHurtTime.get()) {
            font.drawString("Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}", 40, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)
        } else {
            font.drawString("Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))}", 40, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)
        }

        // 渐变血量条
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = (5 + ((135 - font.getStringWidth(decimalFormat.format(target.maxHealth))) * (easingHP / target.maxHealth))).toInt()
        for (i in 5..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), 39.0, x1, 45.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 43 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)

        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawRiseNew(target: EntityLivingBase) {
        val font = fontValue.get()

        RenderUtils.drawRoundedCornerRect(0f, 0f, 150f, 50f, 5f, Color(0, 0, 0, riseAlpha.get()).rgb)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 38

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 7f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        font.drawString(target.name, 48, 8, Color.WHITE.rgb)

        // 渐变血量条
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = 48 + ( (easingHP/ target.maxHealth) * 97f).toInt()
        for (i in 48..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), (13 + font.FONT_HEIGHT).toDouble(), x1, 45.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawOverFlow(target: EntityLivingBase) {
        fun drawHead(skin: ResourceLocation, width: Int, height: Int) {
            GL11.glColor4f(1F, 1F, 1F, 1F)
            mc.textureManager.bindTexture(skin)
            Gui.drawScaledCustomSizeModalRect(1, 1, 8F, 8F, 8, 8, width, height,
                64F, 64F)
        }
        val width = 34 + Fonts.font40.getStringWidth(target.name).coerceAtLeast(70)

        //BackGround
        RenderUtils.drawRect(0f,0f,width.toFloat(),32f,Color(61,61,61).rgb)

        //Target Name
        Fonts.font40.drawString(target.name,34f,2f,Color(255,255,255).rgb,true)

        //Health Bar BG
        RenderUtils.drawRect(34f,12f,width.toFloat() - 2f,20f,Color(31,31,31))

        if (easingHealth > target.health)
            RenderUtils.drawRect(34F, 12F,32f+(easingHealth / target.maxHealth) * (width - 34f),20F,
                Color(0, 120, 200).rgb
            )

        //Health Bar
        if (target.health > 0)
            RenderUtils.drawRect(34f,12f,32f+(target.health / target.maxHealth) * (width - 34f),20f,Color(0,120,200))

        easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        Fonts.font35.drawString("${decimalFormat.format((target.health / target.maxHealth) * 100)}%",width / 2f + 8f,13.5f,Color(250,250,250).rgb,true)
        //info
        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        if (playerInfo != null) {
            // Draw head
            val locationSkin = playerInfo.locationSkin
            //Head BG
            RenderUtils.drawRect(0f,0f,32f,32f,if (mc.thePlayer.isBlocking || LiquidBounce.moduleManager[KillAura::class.java]!!.blockingStatus) Color(78,186,213) else Color(255,255,255))
            drawHead(locationSkin, 30, 30)
        }
    }

    private fun drawRomantic(target: EntityLivingBase) {
        fun drawRomanticHead(skin: ResourceLocation, width: Int, height: Int, hurtPercent: Float) {
            GL11.glColor4f(1F, 1F - hurtPercent, 1F - hurtPercent, 1F)
            mc.textureManager.bindTexture(skin)
            Gui.drawScaledCustomSizeModalRect(2, 2, 8F, 8F, 8, 8, width, height,
                64F, 64F)
        }
        val width = 26f + Fonts.font30.getStringWidth(target.name).coerceAtLeast(90)
        val size = 24f
        val hurtPercent = target.hurtTime / 10f
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        RenderUtils.drawRect(0f,0f,width,38f,Color(0,0,0,30))
        RenderUtils.drawShadow(0f,0f,width,38f)
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        GL11.glEnable(GL11.GL_POINT_SMOOTH)

        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)
        if (playerInfo != null) {
            val locationSkin = playerInfo.locationSkin
            drawRomanticHead(locationSkin, 24, 24,hurtPercent)
        }
        GL11.glPopMatrix()
        //Damage Anim
        if (easingHealth > target.health)
            RenderUtils.drawRect(34F, 20F,34f+(width-42f) * (easingHealth / target.maxHealth),28F,
                Color(255,10,10)
            )
        GlStateManager.resetColor()
        Fonts.font30.drawString(target.name,34f,20f-Fonts.font30.height,Color(255,255,255).rgb)
        RenderUtils.drawRect(34f,20f,width - 8f,28f,Color(61,61,61,50))
        RenderUtils.drawRect(34f,20f,34f+(target.health/target.maxHealth)*(width - 42f),28f,Color(255,255,255))
        // Heal animation
        if (easingHealth < target.health)
            RenderUtils.drawRect((easingHealth / target.maxHealth) * (width-42f) + 34f, 20F,
                (target.health / target.maxHealth) * (width-42f) + 34f, 28F, Color(255,255,255).rgb)
        easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
    }

    private fun drawRiseNewNew(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(60)*1.65f + font.getStringWidth("00")
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 45f, 7f, Color(0, 0, 0, riseAlpha.get()).rgb)

        // circle player avatar
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 30

        //draw head
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)
        GL11.glPopMatrix()

        // draw name
        GL11.glPushMatrix()
        GL11.glScalef(1.5f, 1.5f, 1.5f)
        font.drawString("${target.name}", 32, 8, Color.WHITE.rgb)
        GL11.glPopMatrix()

        // draw health
        GL11.glEnable(3042)
        GL11.glDisable(3553)
        GL11.glBlendFunc(770, 771)
        GL11.glEnable(2848)
        GL11.glShadeModel(7425)
        val stopPos = (48 + ((additionalWidth - 5 - font.getStringWidth(decimalFormat.format(target.maxHealth))) * (easingHP / target.maxHealth))).toInt()
        for (i in 48..stopPos step 5) {
            val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
            RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), 30.0, x1, 38.0,
                ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
        }
        GL11.glEnable(3553)
        GL11.glDisable(3042)
        GL11.glDisable(2848)
        GL11.glShadeModel(7424)
        GL11.glColor4f(1f, 1f, 1f, 1f)
        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 36 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)


        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawRiseLatest(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = ((font.getStringWidth(target.name) * 1.1).toInt().coerceAtLeast(70) + font.getStringWidth("Name: ") * 1.1 + 7.0).roundToInt()
        val healthBarWidth = additionalWidth - (font.getStringWidth("20") * 1.15).roundToInt() - 16
        RenderUtils.drawRoundedCornerRect(0f, 0f, 50f + additionalWidth, 50f, 6f, Color(0, 0, 0, 180).rgb)
        RenderUtils.drawShadow(2f, 2f, 48f + additionalWidth, 48f)

        // circle player avatar
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.2f * hurtPercent * 2)
        } else {
            0.8f + (0.2f * (hurtPercent - 0.5f) * 2)
        }
        val size = 45

        //draw head
        GL11.glPushMatrix()
        GL11.glTranslatef(7f, 7f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 7F)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_TEXTURE_2D)
        Stencil.erase(true)
        drawHead(target.skin, 4, 4, 30, 30, 1F - getFadeProgress()) //playerInfo.locationSkin
        Stencil.dispose()
        GL11.glPopMatrix()

        // draw name
        GL11.glPushMatrix()
        GL11.glScalef(1.1f, 1.1f, 1.1f)
        font.drawString("Name: ${target.name}", 45, 14, Color(115, 208, 255, 255).rgb)
        font.drawString("Name:", 45, 14, Color.WHITE.rgb)
        GL11.glPopMatrix()

        // draw health
        RenderUtils.drawRoundedCornerRect(50f, 31f, 50f + healthBarWidth , 39f, 3f, Color(20, 20, 20, 255).rgb)
        RenderUtils.drawRoundedCornerRect(50f, 31f, 50f + (healthBarWidth * (easingHP / target.maxHealth)) , 39f, 4f, Color(122, 214, 255, 255).rgb)
        RenderUtils.drawRoundedCornerRect(52f, 31f, 48f + (healthBarWidth * (easingHP / target.maxHealth)) , 34f, 2f, Color(255, 255, 255, 30).rgb)
        RenderUtils.drawRoundedCornerRect(52f, 36f, 48f + (healthBarWidth * (easingHP / target.maxHealth)) , 39f, 2f, Color(0, 0, 0, 30).rgb)
        GL11.glPushMatrix()
        GL11.glScalef(1.15f, 1.15f, 1.15f)
        font.drawString(getHealth(target).roundToInt().toString(), ((38 + additionalWidth.toInt() - font.getStringWidth((getHealth(target) * 1.15).roundToInt().toString()).toInt()) / 1.15).roundToInt()   , 31 - (font.FONT_HEIGHT/2).toInt(), Color(115, 208, 255, 255).rgb)
        GL11.glPopMatrix()


        if(target.hurtTime >= 9) {
            for(i in 0 until riseCountValue.get()) {
                riseParticleList.add(RiseParticle())
            }
        }

        val curTime = System.currentTimeMillis()
        riseParticleList.map { it }.forEach { rp ->
            if ((curTime - rp.time) > ((riseMoveTimeValue.get() + riseFadeTimeValue.get()) * 50)) {
                riseParticleList.remove(rp)
            }
            val movePercent = if((curTime - rp.time) < riseMoveTimeValue.get() * 50) {
                (curTime - rp.time) / (riseMoveTimeValue.get() * 50f)
            } else {
                1f
            }
            val x = (movePercent * rp.x * 0.5f * riseDistanceValue.get()) + 20
            val y = (movePercent * rp.y * 0.5f * riseDistanceValue.get()) + 20
            val alpha = if((curTime - rp.time) > riseMoveTimeValue.get() * 50) {
                1f - ((curTime - rp.time - riseMoveTimeValue.get() * 50) / (riseFadeTimeValue.get() * 50f)).coerceAtMost(1f)
            } else {
                1f
            } * riseAlphaValue.get()
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.color.red, rp.color.green, rp.color.blue, (alpha * 255).toInt()).rgb)
        }
    }

    private fun drawVape(target: EntityLivingBase) {
        RoundedUtil.drawRound(0F, 0F, 110f, 40f, 1f, Color(30, 30, 30, 240))

        GL11.glPushMatrix()
        GL11.glTranslated(19.0, 33.0, 0.0)
        GlStateManager.disableBlend()
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        makeScissorBox(3F, 4F, 31F, 31F)
        val pitch: Float = target.rotationPitch
        target.rotationPitch = 0F
        GuiInventory.drawEntityOnScreen(0, 0, 14, -100.0f, 0f, target)
        target.rotationPitch = pitch
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
        GlStateManager.enableBlend()
        GL11.glPopMatrix()

        Fonts.tc35.drawString(target.name, 36.5f, 12.6f / 2f - Fonts.tc35.height / 2f, -1)

        val targetHealth = target.health
        val targetMaxHealth = target.maxHealth
        val targetAbsorptionAmount = target.absorptionAmount
        val targetHealthDWithAbs = targetHealth / (targetMaxHealth + targetAbsorptionAmount).coerceAtLeast(1.0f)
        val targetHealthD = targetHealth / targetMaxHealth.coerceAtLeast(1.0f)
        val color: Color? = interpolateColorC(Color.RED, Color(5, 134, 105), targetHealthD)

        RoundedUtil.drawRound(37f, 12.6f, 68f, 2.9f, 1f, Color(43, 42, 43))
        RoundedUtil.drawRound(37f, 12.6f, 68f * targetHealthDWithAbs, 2.9f, 1f, color)
        if (targetAbsorptionAmount > 0) {
            val absLength = 49f * (targetAbsorptionAmount / (targetMaxHealth + targetAbsorptionAmount))
            RoundedUtil.drawRound(37f + 68f * targetHealthDWithAbs,
                12.6f,
                absLength,
                2.9f,
                1f,
                Color(0xFFAA00))
        }

        val hp = (targetHealth + targetAbsorptionAmount).toString() + " hp"
        Fonts.tc35.drawString(hp,
            105f - Fonts.tc35.getStringWidth(hp),
            (12.6f - Fonts.tc35.height) / 2f,
            -1)

        if (target is EntityPlayer) {
            val arrayList: MutableList<ItemStack> = target.inventory.armorInventory.toMutableList()
            if (target.inventory.getCurrentItem() != null) arrayList.add(target.inventory.getCurrentItem())
            if (arrayList.isEmpty()) return
            var n = 0f
            arrayList.reverse()
            GL11.glPushMatrix()
            GL11.glTranslatef((x + 36.5f).toFloat(), (y + 18.5f).toFloat(), 0f)
            GL11.glScaled(0.8, 0.8, 0.8)
            for (item in arrayList) {
                RoundedUtil.drawRound(n, 0f, 16f, 16f, 0.5f, Color(26, 25, 26))
                RenderHelper.enableGUIStandardItemLighting()
                mc.renderItem.renderItemAndEffectIntoGUI(item, n.toInt(), 0)
                RenderHelper.disableStandardItemLighting()
                n += 17
            }
            GL11.glScalef(1f, 1f, 1f)
            GL11.glPopMatrix()
        }
    }

    class RiseParticle {
        val color = ColorUtils.rainbow(RandomUtils.nextInt(0, 30))
        val alpha = RandomUtils.nextInt(150, 255)
        val time = System.currentTimeMillis()
        val x = RandomUtils.nextInt(-50, 50)
        val y = RandomUtils.nextInt(-50, 50)
    }

    private fun drawBar(target: EntityLivingBase) {
        Health = easingHP

        val width = (38 + Fonts.font40.getStringWidth(target.name))
            .coerceAtLeast(119)
            .toFloat()

        RenderUtils.drawBorderedRect(3F, 37F, 115F, 42F, 4.2F, Color(16, 16, 16, 255).rgb, Color(10, 10, 10, 100).rgb)
        RenderUtils.drawBorderedRect(3F, 37F, 115F, 42F, 1.2F, Color(255, 255, 255, 180).rgb, Color(255, 180, 255, 0).rgb)
        if (Health > getHealth(target))
            RenderUtils.drawRect(3F, 37F, (Health / target.maxHealth) * width - 4F,
                42F, Color(250, 0, 0, 120).rgb)

        RenderUtils.drawRect(3.2F, 37F, (getHealth(target) / target.maxHealth) * width - 4F,
            42F, Color(220, 0, 0, 220).rgb)
        if (Health < target.health)
            RenderUtils.drawRect((Health / target.maxHealth) * width, 37F,
                (getHealth(target) / target.maxHealth) * width, 42F, Color(44, 201, 144).rgb)
        RenderUtils.drawBorderedRect(3F, 37F, 115F, 42F, 1.2F, Color(255, 255, 255, 180).rgb, Color(255, 180, 255, 0).rgb)



        mc.fontRendererObj.drawStringWithShadow(target.name.toString(), 36F, 22F, 0xFFFFFF)

        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mc.fontRendererObj.drawStringWithShadow(target.name.toString(), 36F, 22F, 0xFFFFFF)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                mc.fontRendererObj.drawStringWithShadow(target.name.toString(), 36F, 22F, 0xFFFFFF)
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }

    private fun drawFDP(target: EntityLivingBase) {
        val font = fontValue.get()
        val addedLen = (60 + font.getStringWidth(target.name) * 1.60f).toFloat()

        RenderUtils.drawRect(0f, 0f, addedLen, 47f, Color(0, 0, 0, 120).rgb)
        RenderUtils.drawRoundedCornerRect(0f, 0f, (easingHP / target.maxHealth) * addedLen, 47f, 3f, Color(0, 0, 0, 90).rgb)

        RenderUtils.drawShadow(0f, 0f, addedLen, 47f)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35

        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        GL11.glPushMatrix()
        GL11.glScalef(1.5f, 1.5f, 1.5f)
        font.drawString(target.name, 39, 8, Color.WHITE.rgb)
        GL11.glPopMatrix()
        font.drawString("Health ${getHealth(target).roundToInt()}", 56, 12 + (font.FONT_HEIGHT * 1.5).toInt(), Color.WHITE.rgb)

    }

    private fun drawExhibition(entity: EntityPlayer) {
        val font = Fonts.font35
        val minWidth = 126F.coerceAtLeast(47F + font.getStringWidth(entity.name))

        RenderUtils.drawExhiRect(0F, 0F, minWidth, 45F, 1F - getFadeProgress())

        RenderUtils.drawRect(2.5F, 2.5F, 42.5F, 42.5F, getColor(Color(59, 59, 59)).rgb)
        RenderUtils.drawRect(3F, 3F, 42F, 42F, getColor(Color(19, 19, 19)).rgb)

        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        RenderUtils.drawEntityOnScreen(22, 40, 16, entity)

        font.drawString(entity.name, 46, 5, getColor(-1).rgb)

        val barLength = 70F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(45F, 14F, 45F + 70F, 18F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker()).rgb)
        RenderUtils.drawRect(45F, 14F, 45F + barLength, 18F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth)).rgb)

        for (i in 0..9)
            RenderUtils.drawRectBasedBorder(45F + i * 7F, 14F, 45F + (i + 1) * 7F, 18F, 0.5F, getColor(Color.black).rgb)

        Fonts.font35.drawString("HP:${entity.health.toInt()} | Dist:${mc.thePlayer.getDistanceToEntityBox(entity).toInt()}", 45F, 21F, getColor(-1).rgb)

        GlStateManager.resetColor()
        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()

        val renderItem = mc.renderItem

        var x = 45
        var y = 28

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.item == null)
                continue

            renderItem.renderItemIntoGUI(stack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
            RenderUtils.drawExhiEnchants(stack, x.toFloat(), y.toFloat())

            x += 16
        }

        val mainStack = entity.heldItem
        if (mainStack != null && mainStack.item != null) {
            renderItem.renderItemIntoGUI(mainStack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
            RenderUtils.drawExhiEnchants(mainStack, x.toFloat(), y.toFloat())
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
    }

    private fun drawExhibitionOld(entity: EntityPlayer) {
        val font = Fonts.minecraftFont
        val minWidth = 126F.coerceAtLeast(47F + font.getStringWidth(entity.name))

        RenderUtils.drawRect(5F, 2F, minWidth - 5f, 41F, Color(0, 0, 0, 170).rgb)
        // RenderUtils.drawRect(3F, 3F, 42F, 42F, getColor(Color(19, 19, 19)).rgb)

        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        RenderUtils.drawEntityOnScreen(22, 40, 16, entity)

        Fonts.minecraftFont.drawStringWithShadow(entity.name, 46f, 5f, getColor(-1).rgb)

        val barLength = 70F * (entity.health / entity.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(45F, 14F, 45F + 70F, 18F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth).darker()).rgb)
        RenderUtils.drawRect(45F, 14F, 45F + barLength, 18F, getColor(BlendUtils.getHealthColor(entity.health, entity.maxHealth)).rgb)

        for (i in 0..9)
            RenderUtils.drawRectBasedBorder(45F + i * 7F, 14F, 45F + (i + 1) * 7F, 18F, 0.5F, Color(20, 20, 20, 200).rgb)

        Fonts.font35.drawString("HP:${entity.health.toInt()} | Dist:${mc.thePlayer.getDistanceToEntityBox(entity).toInt()}", 45F, 21F, getColor(-1).rgb)

        GlStateManager.resetColor()
        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()

        val renderItem = mc.renderItem

        var x = 40
        var y = 25

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.item == null)
                continue

            renderItem.renderItemIntoGUI(stack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)
            RenderUtils.drawExhiEnchants(stack, x.toFloat(), y.toFloat())

            x += 16
        }

        val mainStack = entity.heldItem
        if (mainStack != null && mainStack.item != null) {
            renderItem.renderItemIntoGUI(mainStack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
            RenderUtils.drawExhiEnchants(mainStack, x.toFloat(), y.toFloat())
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()

    }

    private fun drawStitch(target: EntityLivingBase) {
        val tWidth = (110F + Fonts.tc40.getStringWidth(target.name)).coerceAtLeast(120F)
        // background
        RenderUtils.drawRoundedCornerRect(0F, 0F, tWidth, 65F, 7F, Color(255, 255, 255, 40).rgb)
        // circle player avatar
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPushMatrix()
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle((tWidth.toInt()/2) - 15, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle((tWidth.toInt()/2) - 15, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)
        GL11.glPopMatrix()
        // name
        Fonts.tc40.drawCenteredString(target.name, tWidth/2F, 39F, getColor(-1).rgb, false)

        "${ndecimalFormat.format((easingHP / target.maxHealth) * 100)}%".also {
            Fonts.font32.drawString(it, ((easingHP / target.maxHealth) * (tWidth - 5) - Fonts.font32.getStringWidth(it)).coerceAtLeast(40f), 60f - Fonts.font32.FONT_HEIGHT, Color.WHITE.rgb, false)
        }

        // hp bar
        RenderUtils.drawRoundedCornerRect(5f, 58f, (tWidth - 5), 62f, 2.5f, Color(0, 0, 0, 150).rgb)
        RenderUtils.drawRoundedCornerRect(5f, 58f, (easingHP / target.maxHealth) * (tWidth - 5), 62f, 2.5f, ColorUtils.rainbow().rgb)

    }


    private fun drawFlux(target: EntityLivingBase) {
        val width = (38 + target.name.let(Fonts.font40::getStringWidth))
            .coerceAtLeast(70)
            .toFloat()

        // draw background
        RenderUtils.drawRect(0F, 0F, width, 34F, Color(40, 40, 40).rgb)
        RenderUtils.drawRect(2F, 22F, width - 2F, 24F, Color.BLACK.rgb)
        RenderUtils.drawRect(2F, 28F, width - 2F, 30F, Color.BLACK.rgb)

        // draw bars
        RenderUtils.drawRect(2F, 22F, 2 + (easingHP / target.maxHealth) * (width - 4), 24F, Color(231, 182, 0).rgb)
        RenderUtils.drawRect(2F, 22F, 2 + (getHealth(target) / target.maxHealth) * (width - 4), 24F, Color(0, 224, 84).rgb)
        RenderUtils.drawRect(2F, 28F, 2 + (target.totalArmorValue / 20F) * (width - 4), 30F, Color(77, 128, 255).rgb)

        // draw text
        Fonts.font40.drawString(target.name, 22, 3, Color.WHITE.rgb)
        GL11.glPushMatrix()
        GL11.glScaled(0.7, 0.7, 0.7)
        Fonts.font35.drawString("Health: ${decimalFormat.format(getHealth(target))}", 22 / 0.7F, (4 + Fonts.font40.height) / 0.7F, Color.WHITE.rgb)
        GL11.glPopMatrix()

        // Draw head
        RenderUtils.drawHead(target.skin, 2, 2, 16, 16)
    }

    private fun drawNovo3(target: EntityLivingBase) {
        val width = (38 + Fonts.font40.getStringWidth(target.name)).coerceAtLeast(118).toFloat()
        RenderUtils.drawRect(0f, 0f, width + 14f, 44f, Color(0, 0, 0,
            bgAlphaValue.get()).rgb)
        drawPlayerHead(target.skin, 3, 3, 30, 30)
        Fonts.font35.drawString(target.name, 34.5f, 4f, Color.WHITE.rgb)
        Fonts.font35.drawString("Health: ${decimalFormat.format(target.health)}", 34.5f, 14f, Color.WHITE.rgb)
        Fonts.font35.drawString(
            "Distance: ${decimalFormat.format(mc.thePlayer.getDistanceToEntity(target))}m",
            34.5f,
            24f,
            Color.WHITE.rgb
        )
        RenderUtils.drawRect(2.5f, 35.5f, width + 11.5f, 37.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawGradientSidewaysH(3.0, 36.0, 3.0 + (easingHealth / target.maxHealth) * (width + 8f), 37.0,  ColorUtils.darkerFixed(barColor, 1.5f).rgb, barColor.rgb)
        RenderUtils.drawRect(2.5f, 39.5f, width + 11.5f, 41.5f, Color(0, 0, 0, 200).rgb)
        RenderUtils.drawGradientSidewaysH(
            3.0,
            40.0,
            3.0 + (target.totalArmorValue / 20F) * (width + 8f),
            41.0,
            Color(97, 148, 200).rgb,
            Color(77, 128, 255).rgb
        )
        easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
    }

    private fun drawArris(target: EntityLivingBase) {
        val font = fontValue.get()

        val hp = decimalFormat.format(easingHP)
        val additionalWidth = font.getStringWidth("${target.name}  $hp hp").coerceAtLeast(75)
        if(arrisRoundedValue.get()){
            RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
        } else {
            RenderUtils.drawRect(0f, 0f, 45f + additionalWidth, 1f, ColorUtils.rainbow())
            RenderUtils.drawRect(0f, 1f, 45f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)
        }

        RenderUtils.drawHead(target.skin, 5, 5, 30, 30)

        // info text
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        "$hp hp".also {
            font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.LIGHT_GRAY.rgb)
        }

        // hp bar
        val yPos = 5 + font.FONT_HEIGHT + 3f
        RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.GREEN.rgb)
        RenderUtils.drawRect(40f, yPos + 9, 40 + (target.totalArmorValue / 20F) * additionalWidth, yPos + 13, Color(77, 128, 255).rgb)
        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()

            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                // the part to blur for the epic glow fr
                RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                // the part to cut
                RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
                GL11.glPopMatrix()
            })

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }

    private fun drawTenacity(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)

        // circle player avatar
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        // info text
        font.drawCenteredString(target.name, 40 + (additionalWidth / 2f), 5f, Color.WHITE.rgb, false)
        "${decimalFormat.format((easingHP / target.maxHealth) * 100)}%".also {
            font.drawString(it, (40f + (easingHP / target.maxHealth) * additionalWidth - font.getStringWidth(it)).coerceAtLeast(40f), 28f - font.FONT_HEIGHT, Color.WHITE.rgb, false)
        }

        // hp bar
        RenderUtils.drawRoundedCornerRect(40f, 28f, 40f + additionalWidth, 33f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(40f, 28f, 40f + (easingHP / target.maxHealth) * additionalWidth, 33f, 2.5f, ColorUtils.rainbow().rgb)
    }

    private fun drawTenacityNew(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        val hurtPercent = target.hurtPercent

        //background is halal

        if (tenacityNewStatic.get()) {
            RenderUtils.drawRoundedCornerRect(0f, 5f, 59f + additionalWidth.toFloat(), 45f, 6f, ColorUtils.rainbow().rgb)
        } else {

            //curved sides
            RenderUtils.drawRoundedCornerRect(0f, 5f, 12f, 45f, 6f, ColorUtils.hslRainbow(6, indexOffset = 10).rgb)
            RenderUtils.drawRoundedCornerRect(120f, 5f, 59f + additionalWidth.toFloat(), 45f, 6f, ColorUtils.hslRainbow(129, indexOffset = 10).rgb)

            //rain bowwww

            //random OpenGl stuff idk
            GL11.glEnable(3042)
            GL11.glDisable(3553)
            GL11.glBlendFunc(770, 771)
            GL11.glEnable(2848)
            GL11.glShadeModel(7425)

            //stop pos mometno
            val stopPos = 50 + additionalWidth.toInt()

            //draw
            for (i in 6..stopPos step 5) {
                val x1 = (i + 5).coerceAtMost(stopPos).toDouble()
                RenderUtils.quickDrawGradientSidewaysH(i.toDouble(), 5.0, x1, 45.0,
                    ColorUtils.hslRainbow(i, indexOffset = 10).rgb, ColorUtils.hslRainbow(x1.toInt(), indexOffset = 10).rgb)
            }


            //random OpenGl stuff idfk
            GL11.glEnable(3553)
            GL11.glDisable(3042)
            GL11.glDisable(2848)
            GL11.glShadeModel(7424)
        }




        //draw head stuff
        val scale = if (hurtPercent == 0f) { 1f } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35


        // circle player avatar + rise anims
        GL11.glColor4f(1f, 1f, 1f, 1f)
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        //scale
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        //color
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        //draw
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        GL11.glPopMatrix()



        // info text
        font.drawCenteredString(target.name, 45 + (additionalWidth / 2f), 1f + font.FONT_HEIGHT, Color.WHITE.rgb, false)
        val infoStr = ((((easingHP / target.maxHealth) * 100).roundToInt()).toString() + " - " + ((mc.thePlayer.getDistanceToEntityBox(target)).roundToInt()).toString() + "M")

        font.drawString(infoStr, 45f + ((additionalWidth - font.getStringWidth(infoStr)) / 2f), 2f + (font.FONT_HEIGHT + font.FONT_HEIGHT).toFloat(), Color.WHITE.rgb, false)



        //hp bar
        RenderUtils.drawRoundedCornerRect(44f, 32f, 44f + additionalWidth, 38f, 2.5f, Color(60, 60, 60, 130).rgb)
        RenderUtils.drawRoundedCornerRect(44f, 32f, 44f + (easingHP / target.maxHealth) * additionalWidth, 38f, 2.5f, Color(240, 240, 240, 250).rgb)
    }


    private fun drawChill(entity: EntityPlayer) {
        updateAnim(entity.health)

        val name = entity.name
        val health = entity.health
        val tWidth = (45F + Fonts.font40.getStringWidth(name).coerceAtLeast(Fonts.font40.getStringWidth(decimalFormat.format(health)))).coerceAtLeast(120F)
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)

        // background
        RenderUtils.drawRoundedCornerRect(0F, 0F, tWidth, 48F, 7F, bgColor.rgb)
        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // head
        if (playerInfo != null) {
            Stencil.write(false)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            RenderUtils.fastRoundedRect(4F, 4F, 34F, 34F, 7F)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            Stencil.erase(true)
            drawHead(playerInfo.locationSkin, 4, 4, 30, 30, 1F - getFadeProgress())
            Stencil.dispose()
        }

        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // name + health
        Fonts.font40.drawString(name, 38F, 6F, getColor(-1).rgb)
        numberRenderer.renderChar(health, calcTranslateX, calcTranslateY, 38F, 17F, calcScaleX, calcScaleY, false, chillFontSpeed.get(), getColor(-1).rgb)

        // health bar
        RenderUtils.drawRoundedCornerRect(4F, 38F, tWidth - 4F, 44F, 3F, barColor.darker().rgb)

        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.fastRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F)
        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        if (chillRoundValue.get())
            RenderUtils.customRounded(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, 0F, 3F, 3F, 0F, barColor.rgb)
        else
            RenderUtils.drawRect(4F, 38F, 4F + (easingHealth / entity.maxHealth) * (tWidth - 8F), 44F, barColor.rgb)
        Stencil.dispose()
    }


    private fun drawRemix(entity: EntityPlayer) {
        updateAnim(entity.health)

        // background
        RenderUtils.newDrawRect(0F, 0F, 146F, 49F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(1F, 1F, 145F, 48F, getColor(Color(35, 35, 35)).rgb)

        // health bar
        RenderUtils.newDrawRect(4F, 40F, 142F, 45F, getColor(Color.red.darker().darker()).rgb)
        RenderUtils.newDrawRect(4F, 40F, 4F + (easingHealth / entity.maxHealth).coerceIn(0F, 1F) * 138F, 45F, barColor.rgb)

        // head
        RenderUtils.newDrawRect(4F, 4F, 38F, 38F, getColor(Color(150, 150, 150)).rgb)
        RenderUtils.newDrawRect(5F, 5F, 37F, 37F, getColor(Color(0, 0, 0)).rgb)

        // armor bar
        RenderUtils.newDrawRect(40F, 36F, 141.5F, 38F, getColor(Color.blue.darker()).rgb)
        RenderUtils.newDrawRect(40F, 36F, 40F + (entity.getTotalArmorValue().toFloat() / 20F).coerceIn(0F, 1F) * 101.5F, 38F, getColor(Color.blue).rgb)

        // armor item background
        RenderUtils.newDrawRect(40F, 16F, 58F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(41F, 17F, 57F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(60F, 16F, 78F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(61F, 17F, 77F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(80F, 16F, 98F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(81F, 17F, 97F, 33F, getColor(Color(95, 95, 95)).rgb)

        RenderUtils.newDrawRect(100F, 16F, 118F, 34F, getColor(Color(25, 25, 25)).rgb)
        RenderUtils.newDrawRect(101F, 17F, 117F, 33F, getColor(Color(95, 95, 95)).rgb)

        // name
        Fonts.minecraftFont.drawStringWithShadow(entity.name, 41F, 5F, getColor(-1).rgb)

        // ping
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null) {
            // actual head
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, 5, 5, 32, 32, 1F - getFadeProgress())

            val responseTime = mc.netHandler.getPlayerInfo(entity.uniqueID).responseTime.toInt()
            val stringTime = "${responseTime.coerceAtLeast(0)}ms"

            var j = 0

            if (responseTime < 0)
                j = 5
            else if (responseTime < 150)
                j = 0
            else if (responseTime < 300)
                j = 1
            else if (responseTime < 600)
                j = 2
            else if (responseTime < 1000)
                j = 3
            else
                j = 4

            mc.textureManager.bindTexture(Gui.icons)
            RenderUtils.drawTexturedModalRect(132, 18, 0, 176 + j * 8, 10, 8, 100.0F)

            GL11.glPushMatrix()
            GL11.glTranslatef(142F - Fonts.minecraftFont.getStringWidth(stringTime) / 2F, 28F, 0F)
            GL11.glScalef(0.5F, 0.5F, 0.5F)
            Fonts.minecraftFont.drawStringWithShadow(stringTime, 0F, 0F, getColor(-1).rgb)
            GL11.glPopMatrix()
        }

        // armor items
        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        RenderHelper.enableGUIStandardItemLighting()

        val renderItem = mc.renderItem

        var x = 41
        var y = 17

        for (index in 3 downTo 0) {
            val stack = entity.inventory.armorInventory[index] ?: continue

            if (stack.getItem() == null)
                continue

            renderItem.renderItemAndEffectIntoGUI(stack, x, y)
            x += 20
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
    }

    private fun drawWaterMelon(target: EntityLivingBase) {
        // background rect
        RenderUtils.drawRoundedCornerRect(
            -1.5f, 2.5f, 152.5f, 52.5f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -1f, 2f, 152f, 52f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0.5f, 1.5f, 151.5f, 51.5f,
            5.0f, Color(0, 0, 0, 40).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0f, 1f, 151.0f, 51.0f,
            5.0f, Color(0, 0, 0, 60).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            0.5f, 0.5f, 150.5f, 50.5f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            1f, 0f, 150.0f, 50.0f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        // head size based on hurt
        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) {
            1f
        } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 35
        // draw head
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)
        // 受伤的缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)
        // 受伤的红色效果
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        // 绘制头部图片
        GL11.glColor4f(1f, 1f, 1f, 1f)
        mc.textureManager.bindTexture(target.skin)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 8f, 8f, 8, 8, 30, 30, 64f, 64f)
        RenderUtils.drawScaledCustomSizeModalCircle(5, 5, 40f, 8f, 8, 8, 30, 30, 64f, 64f)

        GL11.glPopMatrix()
        // draw name of target
        Fonts.font40.drawString("${target.name}", 45f, 12f, Color.WHITE.rgb)
        val df = DecimalFormat("0.00");
        // draw armour percent
        Fonts.font35.drawString(
            "Armor ${(df.format(PlayerUtils.getAr(target) * 100))}%",
            45f,
            24f,
            Color(200, 200, 200).rgb
        )
        // draw bar
        RenderUtils.drawRoundedCornerRect(45f, 32f, 145f, 42f, 5f, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawRoundedCornerRect(
            45f,
            32f,
            45f + (easingHP / target.maxHealth) * 100f,
            42f,
            5f,
            ColorUtils.rainbow().rgb
        )
        // draw hp as text
        Fonts.font35.drawString(
            "${((df.format((easingHP / target.maxHealth) * 100)))}%",
            80f,
            34f,
            Color(255, 255, 255).rgb,
            true
        )
    }

    private fun drawSparklingWater(target: EntityLivingBase) {
        // background
        RenderUtils.drawRoundedCornerRect(
            -1.5f, 2.5f, 152.5f, 52.5f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -1f, 2f, 152f, 52f,
            5.0f, Color(0, 0, 0, 26).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0.5f, 1.5f, 151.5f, 51.5f,
            5.0f, Color(0, 0, 0, 40).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            -0f, 1f, 151.0f, 51.0f,
            5.0f, Color(0, 0, 0, 60).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            0.5f, 0.5f, 150.5f, 50.5f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        RenderUtils.drawRoundedCornerRect(
            1f, 0f, 150.0f, 50.0f,
            5.0f, Color(0, 0, 0, 50).rgb
        )
        // draw entity
        if(target.hurtTime > 1) {
            GL11.glColor4f(1f, 0f, 0f, 0.5f)
            RenderUtils.drawEntityOnScreen(25, 48, 32, target)
        } else {
            GL11.glColor4f(1f, 1f, 1f, 1f)
            RenderUtils.drawEntityOnScreen(25, 45, 30, target)
        }

        // target text
        Fonts.font40.drawString("${target.name}", 45f, 6f, Color.WHITE.rgb)
        val df = DecimalFormat("0.00");
        // armour text
        Fonts.font35.drawString(
            "Armor ${(df.format(PlayerUtils.getAr(target) * 100))}%",
            45f,
            40f,
            Color(200, 200, 200).rgb
        )//bar
        RenderUtils.drawRoundedCornerRect(45f, 23f, 145f, 33f, 5f, Color(0, 0, 0, 100).rgb)
        RenderUtils.drawRoundedCornerRect(
            45f,
            23f,
            45f + (easingHP / target.maxHealth) * 100f,
            33f,
            5f,
            ColorUtils.rainbow().rgb
        )
        Fonts.font35.drawString(
            "${((df.format((easingHP / target.maxHealth) * 100)))}%",
            80f,
            25f,
            Color(255, 255, 255).rgb,
            true
        )
    }

    private fun drawRice(entity: EntityLivingBase) {
        updateAnim(entity.health)

        val font = Fonts.font40
        val name = "Name: ${entity.name}"
        val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
        val healthName = decimalFormat2.format(easingHealth)

        val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)
        val maxHealthLength = font.getStringWidth(decimalFormat2.format(entity.maxHealth)).toFloat()

        // background
        RenderUtils.drawRoundedCornerRect(0F, 0F, 10F + length, 55F, 8F, bgColor.rgb)

        if(entity.hurtTime >= 9) {
            gotDamaged = true
        }
        // particle engine
        if (riceParticle.get()) {
            // adding system
            if (gotDamaged) {
                for (j in 0..(generateAmountValue.get())) {
                    val parSize = RandomUtils.nextFloat(minParticleSize.get(), maxParticleSize.get())
                    val parDistX = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    val parDistY = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    val firstChar = RandomUtils.random(1, "${if (riceParticleCircle.get().equals("none", true)) "" else "c"}${if (riceParticleRect.get().equals("none", true)) "" else "r"}${if (riceParticleTriangle.get().equals("none", true)) "" else "t"}")
                    val drawType = ShapeType.getTypeFromName(when (firstChar) {
                        "c" -> "c_${riceParticleCircle.get().lowercase(Locale.getDefault())}"
                        "r" -> "r_${riceParticleRect.get().lowercase(Locale.getDefault())}"
                        else -> "t_${riceParticleTriangle.get().lowercase(Locale.getDefault())}"
                    }) ?: break

                    particleList.add(
                        Particle(
                            BlendUtils.blendColors(
                                floatArrayOf(0F, 1F),
                                arrayOf<Color>(Color.white, barColor),
                                if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.5F, 1.0F) else 0F),
                            parDistX, parDistY, parSize, drawType)
                    )
                }
                gotDamaged = false
            }

            // render and removing system
            val deleteQueue = mutableListOf<Particle>()

            particleList.forEach { particle ->
                if (particle.alpha > 0F)
                    particle.render(20F, 20F, riceParticleFade.get(), riceParticleSpeed.get(), riceParticleFadingSpeed.get(), riceParticleSpin.get())
                else
                    deleteQueue.add(particle)
            }

            particleList.removeAll(deleteQueue)
        }

        // custom head
        val scaleHT = (entity.hurtTime.toFloat() / entity.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin,
                5F + 15F * (scaleHT * 0.2F),
                5F + 15F * (scaleHT * 0.2F),
                1F - scaleHT * 0.2F,
                30, 30,
                1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F,
                1F - getFadeProgress())

        // player's info
        GlStateManager.resetColor()
        font.drawString(name, 39F, 11F, getColor(-1).rgb)
        font.drawString(info, 39F, 23F, getColor(-1).rgb)

        // gradient health bar
        val barWidth = (length - 5F - maxHealthLength) * (easingHealth / entity.maxHealth).coerceIn(0F, 1F)
        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

        if (gradientRoundedBarValue.get()) {
            if (barWidth > 0F)
                RenderUtils.fastRoundedRect(5F, 42F, 5F + barWidth, 48F, 3F)
        } else
            RenderUtils.quickDrawRect(5F, 42F, 5F + barWidth, 48F)

        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        when (colorModeValue.get().lowercase(Locale.getDefault())) {
            "custom", "health" -> RenderUtils.drawRect(5F, 42F, length - maxHealthLength, 48F, barColor.rgb)
            else -> for (i in 0 until gradientLoopValue.get()) {
                val barStart = i.toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                val barEnd = (i + 1).toDouble() / gradientLoopValue.get().toDouble() * (length - 5F - maxHealthLength).toDouble()
                RenderUtils.drawGradientSidewaysNormal(5.0 + barStart, 42.0, 5.0 + barEnd, 48.0, getColorAtIndex(i), getColorAtIndex(i + 1))
            }
        }
        Stencil.dispose()

        GlStateManager.resetColor()
        font.drawString(healthName, 10F + barWidth, 41F, getColor(-1).rgb)
        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                // the part to blur for the epic glow
                val font = Fonts.font40
                val name = "Name: ${entity.name}"
                val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
                val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)
                RenderUtils.originalRoundedRect(0F, 0F, 10F + length, 55F, 8F, shadowOpaque.rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                // the part to cut
                val font = Fonts.font40
                val name = "Name: ${entity.name}"
                val info = "Distance: ${decimalFormat2.format(mc.thePlayer.getDistanceToEntityBox(entity))}"
                val length = (font.getStringWidth(name).coerceAtLeast(font.getStringWidth(info)).toFloat() + 40F).coerceAtLeast(125F)

                RenderUtils.originalRoundedRect(0F, 0F, 10F + length, 55F, 8F, shadowOpaque.rgb)

                GL11.glPopMatrix()
            })

            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
    }

    private fun drawSlowly(entity: EntityLivingBase) {
        val font = Fonts.minecraftFont
        val healthString = "${decimalFormat2.format(entity.health)} ❤"
        val length = 60.coerceAtLeast(font.getStringWidth(entity.name)).coerceAtLeast(font.getStringWidth(healthString)).toFloat() + 10F

        updateAnim(entity.health)

        RenderUtils.drawRect(0F, 0F, 32F + length, 36F, bgColor.rgb)

        if (mc.netHandler.getPlayerInfo(entity.uniqueID) != null)
            drawHead(mc.netHandler.getPlayerInfo(entity.uniqueID).locationSkin, 1, 1, 30, 30, 1F - getFadeProgress())

        font.drawStringWithShadow(entity.name, 33F, 2F, getColor(-1).rgb)
        font.drawStringWithShadow(healthString, length + 31F - font.getStringWidth(healthString).toFloat(), 22F, barColor.rgb)

        RenderUtils.drawRect(0F, 32F, (easingHealth / entity.maxHealth.toFloat()).coerceIn(0F, entity.maxHealth.toFloat()) * (length + 32F), 36F, barColor.rgb)

    }

    private class CharRenderer(val small: Boolean) {
        var moveY = FloatArray(20)
        var moveX = FloatArray(20)

        private val numberList = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ".")

        private val deFormat = DecimalFormat("##0.00", DecimalFormatSymbols(Locale.ENGLISH))

        init {
            for (i in 0..19) {
                moveX[i] = 0F
                moveY[i] = 0F
            }
        }

        fun renderChar(number: Float, orgX: Float, orgY: Float, initX: Float, initY: Float, scaleX: Float, scaleY: Float, shadow: Boolean, fontSpeed: Float, color: Int): Float {
            val reFormat = deFormat.format(number.toDouble()) // string
            val fontRend = if (small) Fonts.font40 else Fonts.font72
            val delta = RenderUtils.deltaTime
            val scaledRes = ScaledResolution(mc)

            var indexX = 0
            var indexY = 0
            var animX = 0F

            val cutY = initY + fontRend.FONT_HEIGHT.toFloat() * (3F / 4F)

            GL11.glEnable(3089)
            RenderUtils.makeScissorBox(0F, orgY + initY - 4F * scaleY, scaledRes.scaledWidth.toFloat(), orgY + cutY - 4F * scaleY)
            for (char in reFormat.toCharArray()) {
                moveX[indexX] = AnimationUtils.animate(animX, moveX[indexX], fontSpeed * 0.025F * delta)
                animX = moveX[indexX]

                val pos = numberList.indexOf("$char")
                val expectAnim = (fontRend.FONT_HEIGHT.toFloat() + 2F) * pos
                val expectAnimMin = (fontRend.FONT_HEIGHT.toFloat() + 2F) * (pos - 2)
                val expectAnimMax = (fontRend.FONT_HEIGHT.toFloat() + 2F) * (pos + 2)

                if (pos >= 0) {
                    moveY[indexY] = AnimationUtils.animate(expectAnim, moveY[indexY], fontSpeed * 0.02F * delta)

                    GL11.glTranslatef(0F, initY - moveY[indexY], 0F)
                    numberList.forEachIndexed { index, num ->
                        if ((fontRend.FONT_HEIGHT.toFloat() + 2F) * index >= expectAnimMin && (fontRend.FONT_HEIGHT.toFloat() + 2F) * index <= expectAnimMax) {
                            fontRend.drawString(num, initX + moveX[indexX], (fontRend.FONT_HEIGHT.toFloat() + 2F) * index, color, shadow)
                        }
                    }
                    GL11.glTranslatef(0F, -initY + moveY[indexY], 0F)
                } else {
                    moveY[indexY] = 0F
                    fontRend.drawString("$char", initX + moveX[indexX], initY, color, shadow)
                }

                animX += fontRend.getStringWidth("$char")
                indexX++
                indexY++
            }
            GL11.glDisable(3089)

            return animX
        }
    }

    fun drawHead(skin: ResourceLocation, x: Int = 2, y: Int = 2, width: Int, height: Int, alpha: Float = 1F) {
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(x, y, 8F, 8F, 8, 8, width, height,
            64F, 64F)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
    }

    fun drawHead(skin: ResourceLocation, x: Float, y: Float, scale: Float, width: Int, height: Int, red: Float, green: Float, blue: Float, alpha: Float = 1F) {
        GL11.glPushMatrix()
        GL11.glTranslatef(x, y, 0F)
        GL11.glScalef(scale, scale, scale)
        GL11.glDisable(GL11.GL_DEPTH_TEST)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glDepthMask(false)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO)
        GL11.glColor4f(red.coerceIn(0F, 1F), green.coerceIn(0F, 1F), blue.coerceIn(0F, 1F), alpha.coerceIn(0F, 1F))
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(0, 0, 8F, 8F, 8, 8, width, height,
            64F, 64F)
        GL11.glDepthMask(true)
        GL11.glDisable(GL11.GL_BLEND)
        GL11.glEnable(GL11.GL_DEPTH_TEST)
        GL11.glPopMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f)
    }

    private fun drawAeroliteOld(target: EntityLivingBase) {
        val font = fontValue.get()
        val hurtPercent = target.hurtPercent
        val hp = decimalFormat.format(easingHP)
        val yPos = 5 + font.FONT_HEIGHT + 3f
        val additionalWidth = font.getStringWidth("${target.name}  ${hp} HP").coerceAtLeast(75)
        if ((getHealth(target).roundToInt() / target.maxHealth) <= 1) {
            if ((getHealth(target).roundToInt() / target.maxHealth) >= 0.7) {
                RenderUtils.drawBorder(-1f, -1f, 46f + additionalWidth, 41f, 2f, Color.GREEN.rgb)
                RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.GREEN.rgb)
            }
            if ((getHealth(target).roundToInt() / target.maxHealth) < 0.7 && (getHealth(target).roundToInt() / target.maxHealth) >= 0.4) {
                RenderUtils.drawBorder(-1f, -1f, 46f + additionalWidth, 41f, 2f, Color.YELLOW.rgb)
                RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.YELLOW.rgb)
            }
            if ((getHealth(target).roundToInt() / target.maxHealth) < 0.4) {
                RenderUtils.drawBorder(-1f, -1f, 46f + additionalWidth, 41f, 2f, Color.RED.rgb)
                RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.RED.rgb)
            }
        } else {
            RenderUtils.drawBorder(-1f, -1f, 46f + additionalWidth, 41f, 2f, Color.BLUE.rgb)
            RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, ColorUtils.rainbow().rgb)
        }
        RenderUtils.drawRect(0f, 0f, 45f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)

        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        RenderUtils.quickDrawHead(target.skin, 5, 5, 32, 32)

        // info text
        if (target.isDead) {
            font.drawString(target.name, 40, 5, Color.WHITE.rgb)
            "DIED".also {
                font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.RED.rgb)
            }
        } else {
            font.drawString(target.name, 40, 5, Color.WHITE.rgb)
            "$hp HP".also {
                font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.LIGHT_GRAY.rgb)
            }
        }
        RenderUtils.drawRect(40f, yPos + 9, 40 + (target.totalArmorValue / 20F) * additionalWidth, yPos + 13, Color(77, 128, 255).rgb)
    }

    fun drawPlayerHead(skin: ResourceLocation, x: Int, y: Int, width: Int, height: Int) {
        GL11.glColor4f(1F, 1F, 1F, 1F)
        mc.textureManager.bindTexture(skin)
        Gui.drawScaledCustomSizeModalRect(
            x, y, 8F, 8F, 8, 8, width, height,
            64F, 64F
        )
    }

    private fun drawAerolite2(target: EntityLivingBase) {
        val font = Fonts.font40
        val hurtPercent = target.hurtPercent
        val yPos = 5 + font.FONT_HEIGHT + 3f
        val additionalWidth = font.getStringWidth("${target.name} ").coerceAtLeast(100)
        val additionalWidth2 = 60
        RenderUtils.drawBorder(-1f, -1f, 46f + additionalWidth, 41f, 1f, barColor.rgb)
        //   RenderUtils.drawGradientSidewaysNormal(40.0, yPos.toDouble(), 40.0 + (easingHP / target.maxHealth).toInt() * additionalWidth2, yPos.toInt() + 4.0, dgc1.rgb, dgc2.rgb)
        RenderUtils.drawRect(40f, yPos, 40 + (getHealth(target).roundToInt() / target.maxHealth) * additionalWidth2, yPos + 4, barColor.rgb)
        RenderUtils.drawRect(0f, 0f, 46f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)
        RenderUtils.drawRect(40f, yPos + 9, 40 + (target.totalArmorValue / 20F) * additionalWidth, yPos + 13, Color(77, 128, 255).rgb)
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        Fonts.font52.drawString(getHealth(target).toInt().toString(), 40f + additionalWidth - 16.8f - font.getStringWidth(getHealth(target).toInt().toString()), 25f - font.FONT_HEIGHT, Color.WHITE.rgb)
        GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        RenderUtils.drawCircle(40f + additionalWidth - 20f, 20f, 15f, 0, (getHealth(target) * 18).roundToInt().coerceAtMost(360))


    //    GL11.glColor4f(1f, 1 - hurtPercent, 1 - hurtPercent, 1f)
        RenderUtils.quickDrawHead(target.skin, 5, 4.5.toInt(), 32, 32)
        if (shadowValue.get()) {
            GL11.glTranslated(-renderX, -renderY, 0.0)
            GL11.glPushMatrix()
            ShadowUtils.shadow(shadowStrength.get(), {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(-1F + shadowX.get(), -1F + shadowY.get(), 45f + additionalWidth + shadowX.get(), 41F + shadowY.get(), barColor.rgb)
                GL11.glPopMatrix()
            }, {
                GL11.glPushMatrix()
                GL11.glTranslated(renderX, renderY, 0.0)
                if (fadeValue.get()) {
                    GL11.glTranslatef(calcTranslateX, calcTranslateY, 0F)
                    GL11.glScalef(1F - calcScaleX, 1F - calcScaleY, 1F - calcScaleX)
                }
                RenderUtils.drawRect(-1F + shadowX.get(), -1F + shadowY.get(), 45f + additionalWidth + shadowX.get(), 41F + shadowY.get(), barColor.rgb)
                GL11.glPopMatrix()
            })
            GL11.glPopMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
        }
        if(target.hurtTime >= 9) {
            gotDamaged = true
        }
        // particle engine
        if (riceParticle.get()) {
            // adding system
            if (gotDamaged) {
                for (j in 0..(generateAmountValue.get() / 3)) {
                    val parSize = RandomUtils.nextFloat(minParticleSize.get(), maxParticleSize.get())
                    val parDistX = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    val parDistY = RandomUtils.nextFloat(-particleRange.get(), particleRange.get())
                    val firstChar = RandomUtils.random(1, "${if (riceParticleCircle.get().equals("none", true)) "" else "c"}${if (riceParticleRect.get().equals("none", true)) "" else "r"}${if (riceParticleTriangle.get().equals("none", true)) "" else "t"}")
                    val drawType = ShapeType.getTypeFromName(when (firstChar) {
                        "c" -> "c_${riceParticleCircle.get().lowercase(Locale.getDefault())}"
                        "r" -> "r_${riceParticleRect.get().lowercase(Locale.getDefault())}"
                        else -> "t_${riceParticleTriangle.get().lowercase(Locale.getDefault())}"
                    }) ?: break

                    particleList.add(
                        Particle(
                            BlendUtils.blendColors(
                                floatArrayOf(0F, 1F),
                                arrayOf<Color>(Color.white, barColor),
                                if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.5F, 1.0F) else 0F),
                            parDistX, parDistY, parSize, drawType)
                    )
                }
                gotDamaged = false
            }

            // render and removing system
            val deleteQueue = mutableListOf<Particle>()

            particleList.forEach { particle ->
                if (particle.alpha > 0F)
                    particle.render(18F, 18F, riceParticleFade.get(), riceParticleSpeed.get(), riceParticleFadingSpeed.get(), riceParticleSpin.get())
                else
                    deleteQueue.add(particle)
            }

            particleList.removeAll(deleteQueue)
        }
    }

    private fun drawHanabi(target: EntityLivingBase) {
        fun getAnimationStateSmooth(target: Double, current: Double, speed: Double): Double {
            var current = current
            var speed = speed
            val larger = target > current
            if (speed < 0.0) {
                speed = 0.0
            } else if (speed > 1.0) {
                speed = 1.0
            }
            if (target == current) {
                return target
            }
            val dif = Math.max(target, current) - Math.min(target, current)
            var factor = dif * speed
            if (factor < 0.1) {
                factor = 0.1
            }
            if (larger) {
                if (current + factor > target) {
                    current = target
                } else {
                    current += factor
                }
            } else {
                if (current - factor < target) {
                    current = target
                } else {
                    current -= factor
                }
            }
            return current
        }
        var healthBarWidth = 0.0
        var healthBarWidth2 = 0.0
        var hudHeight = 0.0
        val blackcolor = Color(0, 0, 0, 180).rgb
        val blackcolor2 = Color(200, 200, 200).rgb
        val health: Float
        var hpPercentage: Double
        val hurt: Color
        val healthStr: String
        val width = (38 + Fonts.font40.getStringWidth(target.name))
            .coerceAtLeast(140)
            .toFloat()
        health = target.getHealth()
        hpPercentage = (health / target.getMaxHealth()).toDouble()
        hurt = Color.getHSBColor(310f / 360f, target.hurtTime.toFloat() / 10f, 1f)
        healthStr = (target.getHealth().toInt().toFloat() / 2.0f).toString()
        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0)
        val hpWidth = 140.0 * hpPercentage
        healthBarWidth2 = AnimationUtils.animate(hpWidth, healthBarWidth2, 0.20000000298023224)
        healthBarWidth = getAnimationStateSmooth(
            hpWidth,
            healthBarWidth,
            (14f / Minecraft.getDebugFPS()).toDouble()
        ).toFloat().toDouble()
        hudHeight =
            getAnimationStateSmooth(40.0, hudHeight, (8f / Minecraft.getDebugFPS()).toDouble())
        if (hudHeight == 0.0) {
            healthBarWidth2 = 140.0
            healthBarWidth = 140.0
        }
        RenderUtils.prepareScissorBox(
            0f,
            (40 - hudHeight).toFloat(),
            (x + 140.0f).toFloat(),
            (y + 40).toFloat()
        )
        RenderUtils.drawRect(0f, 0f, 170.0f, 40.0f, blackcolor)
        RenderUtils.drawRect(0f, 37.0f, 170f, 40f, Color(0, 0, 0, 48).rgb)
        drawPlayerHead(target.skin, 2, 2, 33, 33)
        if (easingHealth > target.health)
            RenderUtils.drawRect(
                0F,
                37.0f,
                (easingHealth / target.maxHealth) * width,
                40.0f,
                Color(255, 0, 213, 220).rgb
            )
        // Health bar
        RenderUtils.drawGradientSidewaysNormal(
            0.0, 37.0, ((target.health / target.maxHealth) * width).toDouble(),
            40.0, Color(0, 126, 255).rgb, Color(0, 210, 255).rgb
        )
        easingHealth += ((target.health - easingHealth) / 2.0F.pow(10.0F - fadeSpeed.get())) * RenderUtils.deltaTime
        Fonts.font35.drawStringWithShadow("❤", 112F, 28F, hurt.rgb)
        Fonts.font35.drawStringWithShadow(healthStr, 120F, 28F, Color.WHITE.rgb)
        Fonts.font35.drawString(
            "XYZ:" + target.posX.toInt() + " " + target.posY.toInt() + " " + target.posZ.toInt() + " | " + "Hurt:" + (target.hurtTime > 0),
            38F,
            15f,
            blackcolor2
        )
        Fonts.font40.drawString(target.getName(), 38.0f, 4.0f, blackcolor2)
        mc.textureManager.bindTexture((target as AbstractClientPlayer).locationSkin)
        Gui.drawScaledCustomSizeModalRect(3, 3, 8.0f, 8.0f, 8, 8, 32, 32, 64f, 64f)
    }

    fun getColor(color: Color) = ColorUtils.reAlpha(color, color.alpha / 255F * (1F - getFadeProgress()))
    fun getColor(color: Int) = getColor(Color(color))

    open fun updateAnim(targetHealth: Float) {
        if (noAnimValue.get())
            easingHealth = targetHealth
        else
            easingHealth += ((targetHealth - easingHealth) / 2.0F.pow(10.0F - globalAnimSpeed.get())) * RenderUtils.deltaTime
    }

    fun getFadeProgress() = animProgress

    fun getTBorder(): Border? {
        return when (modeValue.get().lowercase()) {
            "novoline" -> Border(0F, 0F, 140F, 40F)
            "novoline2" -> Border(0F, 0F, 140F, 40F)
            "novoline3" -> Border(0F, 0F, 132F, 43F)
            "astolfo" -> Border(0F, 0F, 140F, 60F)
            "liquid" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth)).coerceAtLeast(118).toFloat(), 36F)
            "fdp" -> Border(0F, 0F, 150F, 47F)
            "flux" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth))
                .coerceAtLeast(70)
                .toFloat(), 34F)
            "rise" -> {
                when (modeRise.get().lowercase()) {
                    "original" -> Border(0F, 0F, 150F, 50F)
                    "new1" -> Border(0F, 0F, 150F, 50F)
                    "new2" -> Border(0F, 0F, 150F, 45F)
                    "rise6" -> Border(0F, 0F, 150F, 50F)
                    else -> null
                }
            }
            "zamorozka" -> Border(0F, 0F, 150F, 55F)
            "arris" -> Border(0F, 0F, 120F, 40F)
            "tenacity" -> Border(0F, 0F, 120F, 40F)
            "tenacitynew" -> Border(0F, 5F, 125F, 45F)
            "chill" -> Border(0F, 0F, 120F, 48F)
            "remix" -> Border(0F, 0F, 146F, 49F)
            "rice" -> Border(0F, 0F, 135F, 55F)
            "stitch" -> Border(0F, 0F, 150F, 65F)
            "slowly" -> Border(0F, 0F, 102F, 36F)
            "exhibition" -> Border(0F, 0F, 126F, 45F)
            "exhibitionold" -> Border(2F, 1F, 122F, 40F)
            "watermelon" -> Border(0F, 0F, 120F, 48F)
            "sparklingwater" -> Border(0F, 0F, 120F, 48F)
            "bar" -> Border(3F, 22F, 115F, 42F)
            "aerolite" -> Border(0F, 0F, 140F, 40F)
            "aeroliteold" -> Border(0F, 0F, 120F, 40F)
            "aerolite2" -> Border(0F, 0F, 160F, 40F)
            "romantic" -> Border(0f,0f, 150f,28f)
            "overflow" -> Border(0f,0f, 150f,32f)
            "hanabi" -> Border(0F, 0F, 140F, 40F)
            "vape" -> Border(0F, 0F, 110F, 40F)
            else -> null
        }
    }

    private fun getColorAtIndex(i: Int): Int {
        return (when (colorModeValue.get()) {
            "Rainbow" -> ColorUtils.getRainbowOpaque(waveSecondValue.get(), saturationValue.get(), brightnessValue.get(), i * gradientDistanceValue.get())
            "Slowly" -> ColorUtils.slowlyRainbow(
                System.nanoTime(),
                i * gradientDistanceValue.get(),
                saturationValue.get(),
                brightnessValue.get()
            ).rgb
            "Fade" -> ColorUtils.fade(Color(redValue.get(), greenValue.get(), blueValue.get()), i * gradientDistanceValue.get(), 100).rgb
            else -> -1
        })
    }

}