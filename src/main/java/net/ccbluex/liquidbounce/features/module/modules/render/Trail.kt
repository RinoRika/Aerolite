package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.util.EnumParticleTypes
import oh.yalan.NativeClass
import java.util.*

@ModuleInfo(name = "Trail", category = ModuleCategory.RENDER)
class Trail : Module() {
    private val mode = ListValue(
        "Mode",
        arrayOf(
            "Flame",
            "Cloud",
            "Fireworksspark",
            "Reddust",
            "LargeSmoke",
            "NormalSmoke",
            "HugeExplode",
            "LargeExplode",
            "NormalExplode",
            "Heart",
            "Lava",
            "Special"
        ),
        "Flame"
    )
    private val delay = IntegerValue("Delay", 0, 0, 3000)

    private val timer = MSTimer()

    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (timer.hasTimePassed(delay.get().toLong())) {
            when (mode.get().lowercase(Locale.getDefault())) {
                "flame" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.FLAME.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "cloud" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.CLOUD.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "fireworksspark" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.FIREWORKS_SPARK.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "reddust" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.REDSTONE.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "largesmoke" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.SMOKE_LARGE.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "normalsmoke" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.SMOKE_NORMAL.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "heart" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.HEART.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "hugeexplode" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.EXPLOSION_HUGE.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "normalexplode" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.EXPLOSION_NORMAL.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "largeexplode" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.EXPLOSION_LARGE.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "lava" -> mc.effectRenderer.spawnEffectParticle(
                    EnumParticleTypes.LAVA.particleID,
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    0.0,
                    0.0,
                    0.0
                )
                "special" -> {
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX + 0.5,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX + 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX + 1.5,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX - 0.5,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX - 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX - 1.5,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 0.5,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 1,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 1.5,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - 0.5,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - 1,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.FLAME.particleID,
                        mc.thePlayer.posX,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - 1.5,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.DRIP_LAVA.particleID,
                        mc.thePlayer.posX - 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 1,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.DRIP_LAVA.particleID,
                        mc.thePlayer.posX + 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ + 1,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.DRIP_LAVA.particleID,
                        mc.thePlayer.posX - 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - 1,
                        0.0,
                        0.0,
                        0.0
                    )
                    mc.effectRenderer.spawnEffectParticle(
                        EnumParticleTypes.DRIP_LAVA.particleID,
                        mc.thePlayer.posX + 1,
                        mc.thePlayer.posY,
                        mc.thePlayer.posZ - 1,
                        0.0,
                        0.0,
                        0.0
                    )
                }
            }
            timer.reset()
        }
    }
}