package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.client.HUD
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.CharRenderer
import net.ccbluex.liquidbounce.utils.extensions.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.*
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.entity.EntityLivingBase
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.awt.geom.FlatteningPathIterator
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.roundToInt

@ElementInfo(name = "Targets")
class Targets : Element(-46.0, -40.0, 1F, Side(Side.Horizontal.MIDDLE, Side.Vertical.MIDDLE)) {
    private val modeValue = ListValue("Mode", arrayOf("Novoline", "Hreith", "Astolfo", "Liquid", "Flux", "Rise", "Zamorozka", "Arris", "Tenacity", "Test", "Exhibition", "Chill","ZeroDay","Jello","Moon","NewAero"), "Test")
    private val modeRise = ListValue("RiseMode", arrayOf("Original", "New1", "New2"), "New2")
    private val animSpeedValue = IntegerValue("AnimSpeed", 10, 5, 20)
    private val hpAnimTypeValue = EaseUtils.getEnumEasingList("HpAnimType")
    private val hpAnimOrderValue = EaseUtils.getEnumEasingOrderList("HpAnimOrder")
    private val HreithParticle = BoolValue("HreithParticle", true)
    private val Hreithfade = BoolValue("Particle-Fade", true)
    private val HreithSpeed = FloatValue("Rise-ParticleSpeed", 0.05F, 0.01F, 0.2F)
    private val switchModeValue = ListValue("SwitchMode", arrayOf("Slide", "Zoom", "None"), "Slide")
    private val switchAnimTypeValue = EaseUtils.getEnumEasingList("SwitchAnimType")
    private val switchAnimOrderValue = EaseUtils.getEnumEasingOrderList("SwitchAnimOrder")
    private val switchAnimSpeedValue = IntegerValue("SwitchAnimSpeed", 20, 5, 40)
    private val arrisRoundedValue = BoolValue("ArrisRounded", true)
    private val riseAlpha = IntegerValue("RiseAlpha", 130, 0, 255)
    private val riseCountValue = IntegerValue("Rise-Count", 5, 1, 20)
    private val riseSizeValue = FloatValue("Rise-Size", 1f, 0.5f, 3f)
    private val riseAlphaValue = FloatValue("Rise-Alpha", 0.7f, 0.1f, 1f)
    private val riseDistanceValue = FloatValue("Rise-Distance", 1f, 0.5f, 2f)
    private val riseMoveTimeValue = IntegerValue("Rise-MoveTime", 20, 5, 40)
    private val riseFadeTimeValue = IntegerValue("Rise-FadeTime", 10, 5, 40)
    private val globalAnimSpeed = FloatValue("Global-AnimSpeed", 3F, 1F, 9F)
    private val chillFontSpeed = FloatValue("Chill-FontSpeed", 0.5F, 0.01F, 1F)
    private val fontValue = FontValue("Font", Fonts.tc40)
    private val NewAeroRainStart1 = FloatValue("RainStart1",0.5f,0.00f,1.0f)
    private val NewAeroRainStop1 = FloatValue("RainStop1",0.75f,0.00f,1.0f)
    private val NewAeroRainStart2 = FloatValue("RainStart2",0.75f,0.00f,1.0f)
    private val NewAeroRainStop2 = FloatValue("RainStop2",0.90f,0.00f,1.0f)

    private var prevTarget: EntityLivingBase? = null
    private var displayPercent = 0f
    private var lastUpdate = System.currentTimeMillis()
    private val decimalFormat = DecimalFormat("0.0")
    private val particleList = mutableListOf<Particle>()
    private var gotDamaged: Boolean = false
    var animProgress = 0F

    private var hpEaseAnimation: Animation? = null
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

    private fun getHealth(entity: EntityLivingBase?): Float {
        return entity?.health ?: 0f
    }


    private fun getColor(color: Color) = ColorUtils.reAlpha(color, color.alpha / 255F * (1F - getFadeProgress()))
    private fun getColor(color: Int) = getColor(Color(color))
    fun getFadeProgress() = animProgress

    override fun drawElement(partialTicks: Float): Border? {
        var target = LiquidBounce.combatManager.target
        val time = System.currentTimeMillis()
        val pct = (time - lastUpdate) / (switchAnimSpeedValue.get() * 50f)
        lastUpdate = System.currentTimeMillis()

        if (mc.currentScreen is GuiHudDesigner) {
            target = mc.thePlayer
        } else if (target != null) {
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

        animProgress = 0F
        animProgress = animProgress.coerceIn(0F, 1F)

        easingHP = getHealth(target)

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

        when (modeValue.get().lowercase()) {
            "novoline" -> drawNovo(prevTarget!!)
            "hreith" -> drawHreith(prevTarget!!)
            "astolfo" -> drawAstolfo(prevTarget!!)
            "liquid" -> drawLiquid(prevTarget!!)
            "flux" -> drawFlux(prevTarget!!)
            "rise" -> {
                when (modeRise.get().lowercase()) {
                    "original" -> drawRise(prevTarget!!)
                    "new1" -> drawRiseNew(prevTarget!!)
                    "new2" -> drawRiseNewNew(prevTarget!!)
                }
            }
            "zamorozka" -> drawZamorozka(prevTarget!!)
            "arris" -> drawArris(prevTarget!!)
            "test" -> drawTest(prevTarget!!)
            "tenacity" -> drawTenacity(prevTarget!!)
            "exhibition" -> drawExhi(prevTarget!!)
            "chill" -> drawChill(prevTarget!!)
            "zeroday" -> drawZeroDay(prevTarget!!)
            "jello" -> drawJello(prevTarget!!)
            "moon" -> drawMoon(prevTarget!!)
            "newaero" -> drawNewAero(prevTarget!!)
        }

        return getTBorder()
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
    }

    private fun drawNovo(target: EntityLivingBase) {
        val font = fontValue.get()
        val color = ColorUtils.healthColor(getHealth(target), target.maxHealth)
        val darkColor = ColorUtils.darker(color, 0.6F)
        val hpPos = 33F + ((getHealth(target) / target.maxHealth * 10000).roundToInt() / 100)

        RenderUtils.drawRect(0F, 0F, 140F, 40F, Color(40, 40, 40).rgb)
        font.drawString(target.name, 33, 5, Color.WHITE.rgb)
        RenderUtils.drawEntityOnScreen(20, 35, 15, target)
        RenderUtils.drawRect(hpPos, 18F, 33F + ((easingHP / target.maxHealth * 10000).roundToInt() / 100), 25F, darkColor)
        RenderUtils.drawRect(33F, 18F, hpPos, 25F, color)
        font.drawString("❤", 33, 30, Color.RED.rgb)
        font.drawString(decimalFormat.format(getHealth(target)), 43, 30, Color.WHITE.rgb)
    }

    private fun drawNewAero(target: EntityLivingBase) {
        val font = Fonts.tc45
        val hpPos = 33F + ((getHealth(target) / target.maxHealth * 10000).roundToInt() / 100)
        RenderUtils.drawGradientSidewaysH(0.0,0.0,140.0,40.0,ColorUtils.hslRainbow(1, NewAeroRainStart1.get(),NewAeroRainStop1.get(),300,1500,0.7f,1f).rgb,ColorUtils.hslRainbow(1, NewAeroRainStart2.get(),NewAeroRainStop2.get(),300,1500,0.7f,1f).rgb)
        RenderUtils.drawHead(target.skin,1,1,30,30,255f)
        RenderUtils.drawShadow(0f,0f,140f,40f)

        Fonts.tc45.drawString(target.name,35,1,Color.WHITE.rgb)
        Fonts.tc45.drawString("Health  "+target.health,35,10,Color.WHITE.rgb)
        Fonts.tc45.drawString("Armor  "+target.totalArmorValue,35,20,Color.WHITE.rgb)
        RenderUtils.drawRect(0f,38f,hpPos,40f,Color.WHITE.rgb)
        RenderUtils.drawShadow(0f,38f,hpPos,2f)
    }

    private fun drawHreith(target: EntityLivingBase) {
        val font = Fonts.tc45

        //x1 长度 y1 宽度 x 左右  y上下 *血条长度 x2线条长度 y2线条宽度
        RenderUtils.drawBorderedRect(0f, 0f, 170f, 53f, 4f, Color(0, 0, 0, 100).rgb,1)
        RenderUtils.drawBorder(0f, 0f, 170f, 53f, 4f, ColorUtils.rainbow().rgb)

        //Shadow
        RenderUtils.drawShadow(0f, 0f, 0f, 0f)

        val hurtPercent = target.hurtPercent
        val scale = if (hurtPercent == 0f) {
            1f
        } else if (hurtPercent < 0.5f) {
            1 - (0.1f * hurtPercent * 2)
        } else {
            0.9f + (0.1f * (hurtPercent - 0.5f) * 2)
        }
        val size = 37
        GL11.glPushMatrix()
        GL11.glTranslatef(5f, 5f, 0f)

        // 受伤粒子爆出
        if (HreithParticle.get()) {
            if (target.hurtTime >= 8) {
                if (!gotDamaged) {
                    for (j in 0..10)
                        particleList.add(
                            Particle(
                                BlendUtils.blendColors(
                                    floatArrayOf(0F, 1F),
                                    arrayOf<Color>(Color.white, ColorUtils.rainbow()),
                                    if (RandomUtils.nextBoolean()) RandomUtils.nextFloat(0.4F, 1.0F) else 0F
                                ),
                                RandomUtils.nextFloat(-30F, 30F),
                                RandomUtils.nextFloat(-30F, 30F),
                                RandomUtils.nextFloat(0.5F, 2.5F)
                            )
                        )

                    gotDamaged = true
                }
            } else if (gotDamaged) {
                gotDamaged = false
            }

            val deleteQueue = mutableListOf<Particle>()

            particleList.forEach { particle ->
                if (particle.alpha > 0F)
                    particle.render(5F + 15F, 5 + 15F, Hreithfade.get(), HreithSpeed.get())
                else
                    deleteQueue.add(particle)
            }

            for (p in deleteQueue)
                particleList.remove(p)
        }

        // 渲染缩放效果
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslatef(((size * 0.5f * (1 - scale)) / scale), ((size * 0.5f * (1 - scale)) / scale), 0f)

        // 渲染受伤红色
        GL11.glColor4f(50f, 1 - hurtPercent, 1 - hurtPercent, 5f)

        // 头像渲染
        RenderUtils.quickDrawHead(target.skin, 0, 0, size, size)
        GL11.glPopMatrix()

        // 字体位置
        font.drawString("Player ${target.name}", 45, 6, ColorUtils.rainbow().rgb)
        font.drawString(
            "Health ${target.health} / ${(target.maxHealth)}",
            45,
            7 + font.FONT_HEIGHT,
            ColorUtils.rainbow().rgb
        )
        font.drawString(
            "Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}",
            45,
            19 + font.FONT_HEIGHT,
            ColorUtils.rainbow().rgb
        )
        //x1 长度 y1 宽度 x 左右  y上下 *血条长度 x2线条长度 y2线条宽度 HP y1 上下
        // Render drawRoundedCornerRect的规律 记住每个数字需要带f 规律是 [ x , y ,x1 ,y1, radius , color.rgb ]
        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(160)
        RenderUtils.drawRoundedCornerRect(10f, 45f, 0f + additionalWidth, 50f, 2.5f, Color(0, 0, 0, 70).rgb)
        RenderUtils.drawRoundedCornerRect(10f, 45f, 0f + (easingHP / target.maxHealth) * additionalWidth, 50f, 2.5f, ColorUtils.rainbow().rgb)
    }

    private fun drawRiseNewNew(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(60)*1.65f
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

        // draw health
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
        font.drawString(decimalFormat.format(easingHP), stopPos + 5, 38 - font.FONT_HEIGHT / 2, Color.WHITE.rgb)


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
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.colorR, rp.colorG, rp.colorB, (alpha * 255).toInt()).rgb)
        }
    }


    private fun drawExhi(target: EntityLivingBase) {
        val font = Fonts.font35
        val minWidth = 136F.coerceAtLeast(50F + font.getStringWidth(target.name))

        RenderUtils.drawExhiRect(0F, 0F, minWidth, 45F, 1F - getFadeProgress())

        RenderUtils.drawRect(2.5F, 2.5F, 42.5F, 42.5F, getColor(Color(59, 59, 59)).rgb)
        RenderUtils.drawRect(3F, 3F, 42F, 42F, getColor(Color(19, 19, 19)).rgb)

        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        RenderUtils.drawEntityOnScreen(22, 40, 15, target)

        font.drawString(target.name, 46, 5, getColor(-1).rgb)

        val barLength = 80F * (target.health / target.maxHealth).coerceIn(0F, 1F)
        RenderUtils.drawRect(45F, 14F, 45F + 80F, 18F, getColor(BlendUtils.getHealthColor(target.health, target.maxHealth).darker()).rgb)
        RenderUtils.drawRect(45F, 14F, 45F + barLength, 18F, getColor(BlendUtils.getHealthColor(target.health, target.maxHealth)).rgb)

        for (i in 0..9)
            RenderUtils.drawRectBasedBorder(45F + i * 8F, 14F, 45F + (i + 1) * 8F, 18F, 0.5F, getColor(Color.black).rgb)

        GL11.glPushMatrix()
        GL11.glTranslatef(46F, 22F, 0F)
        GL11.glScalef(0.5f, 0.5f, 0.5f)
        Fonts.font35.drawString("HP: ${target.health.toInt()} | Dist: ${mc.thePlayer.getDistanceToEntityBox(target).toInt()}", 0, 0, getColor(-1).rgb)
        GL11.glPopMatrix()

        GlStateManager.resetColor()
        GL11.glPushMatrix()
        GL11.glColor4f(1f, 1f, 1f, 1f - getFadeProgress())
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()


        val renderItem = mc.renderItem

        var x = 45
        var y = 27

        for (index in 3 downTo 0) {
            val stack = target.inventory[index] ?: continue

            if (stack.item == null)
                continue

            renderItem.renderItemIntoGUI(stack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, stack, x, y)

            x += 18
        }

        val mainStack = target.heldItem
        if (mainStack != null && mainStack.getItem() != null) {
            renderItem.renderItemIntoGUI(mainStack, x, y)
            renderItem.renderItemOverlays(mc.fontRendererObj, mainStack, x, y)
        }

        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.enableAlpha()
        GlStateManager.disableBlend()
        GlStateManager.disableLighting()
        GlStateManager.disableCull()
        GL11.glPopMatrix()
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
    }

    private fun drawJello(target: EntityLivingBase){
        val font  = fontValue.get()

        //Rect(采用rect+啥shadow设计)
        RenderUtils.drawRect(0f,0f,150f,50f,Color(255,255,255,100).rgb)
        RenderUtils.drawShadow(0f,0f,150f,50f)
        GL11.glColor4f(1f,1f,1f,1f)
        RenderUtils.quickDrawHead(target.skin,5,5,30,30)
        RenderUtils.drawShadow(5f,5f,30f,30f)

        //HealthBar(采用其他其他targets的绘制方法)
        val barLength = 150 -7f
        RenderUtils.drawRoundedCornerRect(10f,40f,7+((easingHP / target.maxHealth)* barLength),45f,2f,Color(255,255,255,255).rgb)
        RenderUtils.drawRoundedCornerRect(10f,40f,7+((target.health / target.maxHealth) * barLength),45f,2f,Color(255,255,255,100).rgb)
        font.drawString("${target.name}",50,10,Color(255,255,255,255).rgb)

        //Info(自写)
        font.drawString("Health:"+"${target.health}", 50,20,Color(255,255,255,255).rgb)
        font.drawString("HurtTime:"+"${target.hurtTime}",50,30,Color(255,255,255,255).rgb)
    }

    private fun drawMoon(target: EntityLivingBase){
        val font = fontValue.get()
        //别说skid辣真的是自己写的
        //RECT
        RenderUtils.drawRoundedCornerRect(0f,0f,150f,50f,5f,Color(0,0,0,0).rgb)
        RenderUtils.drawBorder(0f,0f,150f,50f,4f,Color.WHITE.rgb)
        RenderUtils.quickDrawHead(target.skin, 5, 5, 40,40)
        Fonts.tc45.drawString(target.name,50,5,Color.WHITE.rgb)
        Fonts.fontJello.drawString("${target.health.toInt()} health",50,20,Color.WHITE.rgb)
        RenderUtils.drawRoundedCornerRect(50f,35f,7+((easingHP / target.maxHealth)* (150-7f)),45f,1.5f,Color.WHITE.rgb)
        RenderUtils.drawRoundedCornerRect(50f,35f,7+((target.health / target.maxHealth) * (150-7f)),45f,1.5f,Color(0,0,0,0).rgb)

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
        font.drawString("Distance ${decimalFormat.format(mc.thePlayer.getDistanceToEntityBox(target))} Hurt ${target.hurtTime}", 40, 11 + font.FONT_HEIGHT, Color.WHITE.rgb)

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
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.colorR, rp.colorG, rp.colorB, (alpha * 255).toInt()).rgb)
        }
    }

    class RiseParticle {
        val colorR = RandomUtils.nextInt(0, 255)
        val colorG = RandomUtils.nextInt(200, 255)
        val colorB = RandomUtils.nextInt(200, 255)
        val alpha = RandomUtils.nextInt(120, 200)
        val time = System.currentTimeMillis()
        val x = RandomUtils.nextInt(-50, 50)
        val y = RandomUtils.nextInt(-50, 50)
    }

    private fun drawZeroDay(target: EntityLivingBase){
        val font = fontValue.get()
        val size = 38
        RenderUtils.drawRect(0f,0f,150f,50f,Color(30,30,30,255).rgb)
        RenderUtils.drawShadow(0f,0f,150f,50f)
        RenderUtils.quickDrawHead(target.skin,0,0,size,size)
        GL11.glPopMatrix()
        font.drawString("${target.name}",48,8,Color.WHITE.rgb)
        RenderUtils.drawRect(0f, 45f, 0f + (easingHP / target.maxHealth)*79f, 50f,  ColorUtils.skyRainbow(0,100f,0f,5.0.toDouble()).rgb)
        RenderUtils.drawCircle(50f,25f,4f,Color.WHITE.rgb)
        RenderUtils.drawCircle(100f,25f,4f,Color.YELLOW.rgb)

        //judge health
        if(mc.thePlayer.health > target.health){
            font.drawString("Winning",48,18,Color.WHITE.rgb)
        }else if(mc.thePlayer.health == target.health){
            font.drawString("Same LOL",48,18,Color.WHITE.rgb)
        }else{
            font.drawString("Losing",48,18,Color.WHITE.rgb)
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
        font.drawString("${target.name}", 48, 8, Color.WHITE.rgb)
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
            RenderUtils.drawCircle(x, y, riseSizeValue.get() * 2, Color(rp.colorR, rp.colorG, rp.colorB, (alpha * 255).toInt()).rgb)
        }
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

    private fun drawArris(target: EntityLivingBase) {
        val font = fontValue.get()

        val hp = decimalFormat.format(easingHP)
        val additionalWidth = font.getStringWidth("${target.name}  ${hp} hp").coerceAtLeast(75)
        if(arrisRoundedValue.get()){
            RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)
        } else {
            RenderUtils.drawRect(0f, 0f, 45f + additionalWidth, 1f, ColorUtils.rainbow())
            RenderUtils.drawRect(0f, 1f, 45f + additionalWidth, 40f, Color(0, 0, 0, 110).rgb)
        }

        RenderUtils.quickDrawHead(target.skin, 5, 5, 30, 30)

        // info text
        font.drawString(target.name, 40, 5, Color.WHITE.rgb)
        "$hp HP".also {
            font.drawString(it, 40 + additionalWidth - font.getStringWidth(it), 5, Color.LIGHT_GRAY.rgb)
        }

        // hp bar
        val yPos = 5 + font.FONT_HEIGHT + 3f
        RenderUtils.drawRect(40f, yPos, 40 + (easingHP / target.maxHealth) * additionalWidth, yPos + 4, Color.GREEN.rgb)
        RenderUtils.drawRect(40f, yPos + 9, 40 + (target.totalArmorValue / 20F) * additionalWidth, yPos + 13, Color(77, 128, 255).rgb)
    }

    private fun drawChill(target: EntityLivingBase) {
        val numberRenderer = CharRenderer(false)
        var calcScaleX = 0F
        var calcScaleY = 0F
        var calcTranslateX = 0F
        var calcTranslateY = 0F

        fun updateData(_a: Float, _b: Float, _c: Float, _d: Float) {
            calcTranslateX = _a
            calcTranslateY = _b
            calcScaleX = _c
            calcScaleY = _d
        }

        easingHP += ((target.health - easingHP) / 2.0F.pow(10.0F - globalAnimSpeed.get())) * RenderUtils.deltaTime

        val name = target.name
        val health = target.health
        val tWidth = (45F + Fonts.font40.getStringWidth(name).coerceAtLeast(Fonts.font70.getStringWidth(decimalFormat.format(health)))).coerceAtLeast(120F)
        val playerInfo = mc.netHandler.getPlayerInfo(target.uniqueID)

        // background
        RenderUtils.drawRoundedCornerRect(0F, 0F, tWidth, 48F, 7F, ColorUtils.reAlpha(Color(0,0,0,255), 255 / 255F * (1F - animProgress)).rgb)
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
            RenderUtils.drawHead(playerInfo.locationSkin, 4, 4, 30, 30, 1F - getFadeProgress())
            Stencil.dispose()
        }

        GlStateManager.resetColor()
        GL11.glColor4f(1F, 1F, 1F, 1F)

        // name + health
        Fonts.font40.drawString(name, 38F, 6F, getColor(-1).rgb)
        numberRenderer.renderChar(health, calcTranslateX, calcTranslateY, 38F, 17F, calcScaleX, calcScaleY, false, chillFontSpeed.get(), getColor(-1).rgb)

        // health bar
        RenderUtils.drawRoundedCornerRect(4F, 38F, tWidth - 4F, 44F, 3F, Color(0,0,0,160).darker().rgb)

        Stencil.write(false)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        GL11.glEnable(GL11.GL_BLEND)
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
        RenderUtils.fastRoundedRect(4F, 38F, tWidth - 4F, 44F, 3F)
        GL11.glDisable(GL11.GL_BLEND)
        Stencil.erase(true)
        RenderUtils.customRounded(4F, 38F, 4F + (easingHP / target.maxHealth) * (tWidth - 8F), 44F, 0F, 3F, 3F, 0F, ColorUtils.reAlpha(ColorUtils.rainbow(), ColorUtils.rainbow().alpha / 255F * (1F - animProgress)).rgb)
        Stencil.dispose()
    }

    private fun drawTest(target: EntityLivingBase) {
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

    private fun drawTenacity(target: EntityLivingBase) {
        val font = fontValue.get()

        val additionalWidth = font.getStringWidth(target.name).coerceAtLeast(75)
        RenderUtils.drawRoundedCornerRect(0f, 0f, 45f + additionalWidth, 40f, 7f, Color(0, 0, 0, 110).rgb)

        // circle player avatar
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

    private class Particle(var color: Color, var distX: Float, var distY: Float, var radius: Float) {
        var alpha = 1F
        var progress = 0.0
        fun render(x: Float, y: Float, fade: Boolean, speed: Float) {
            if (progress >= 1.0) {
                progress = 1.0
                if (fade) alpha -= 0.1F
                if (alpha < 0F) alpha = 0F
            } else
                progress += speed.toDouble()

            if (alpha <= 0F) return

            var reColored = Color(color.red / 255.0F, color.green / 255.0F, color.blue / 255.0F, alpha)
            var easeOut = EaseUtils.easeOutQuart(progress).toFloat()

            RenderUtils.drawFilledCircle2(x + distX * easeOut, y + distY * easeOut, radius, reColored)
        }
    }

    private fun getTBorder(): Border? {
        return when (modeValue.get().lowercase()) {
            "novoline" -> Border(0F, 0F, 140F, 40F)
            "astolfo" -> Border(0F, 0F, 140F, 60F)
            "liquid" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth)).coerceAtLeast(118).toFloat(), 36F)
            "flux" -> Border(0F, 0F, (38 + mc.thePlayer.name.let(Fonts.font40::getStringWidth))
                .coerceAtLeast(70)
                .toFloat(), 34F)
            "hreith" -> Border(0F, 0F, 170F, 53F)
            "test" -> Border(0F, 0F, 120F, 40F)
            "rise" -> Border(0F, 0F, 150F, 50F)
            "zamorozka" -> Border(0F, 0F, 150F, 55F)
            "arris" -> Border(0F, 0F, 120F, 40F)
            "tenacity" -> Border(0F, 0F, 120F, 40F)
            "exhibition" -> Border(0F, 0F, 136F.coerceAtLeast(50F + Fonts.font35.getStringWidth("123456789")), 45F)
            else -> null
        }
    }
}
