/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.player

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.potion.Potion

@ModuleInfo(name = "Regen", category = ModuleCategory.PLAYER)
class Regen : Module() {

    private val modeValue = ListValue("Mode", arrayOf("Vanilla", "OldSpartan", "NewSpartan", "AAC4NoFire", "Matrix", "MatrixTimer", "Ghostly", "Mineland"), "Vanilla")
    private val healthValue = IntegerValue("Health", 18, 0, 20)
    private val foodValue = IntegerValue("Food", 18, 0, 20)
    private val speedValue = IntegerValue("Speed", 100, 1, 100)
    private val noAirValue = BoolValue("NoAir", false)
    private val potionEffectValue = BoolValue("PotionEffect", false)
    private val sendPacketDelay = IntegerValue("SendPacketDelay", 3000, 1000, 5000).displayable { modeValue.equals("Mineland")}

    private var resetTimer = false
    private var timer = MSTimer()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (resetTimer) {
            mc.timer.timerSpeed = 1F
        }
        resetTimer = false

        if ((!noAirValue.get() || mc.thePlayer.onGround) && !mc.thePlayer.capabilities.isCreativeMode &&
            mc.thePlayer.foodStats.foodLevel > foodValue.get() && mc.thePlayer.isEntityAlive && mc.thePlayer.health < healthValue.get()) {
            if (potionEffectValue.get() && !mc.thePlayer.isPotionActive(Potion.regeneration)) {
                return
            }

            when (modeValue.get().lowercase()) {
                "vanilla" -> {
                    repeat(speedValue.get()) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }
                }

                "mineland" -> {
                    if (timer.hasTimePassed(sendPacketDelay.get().toLong())) {
                        if (MovementUtils.isMoving()) PacketUtils.sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.onGround))
                        else PacketUtils.sendPacketNoEvent(C03PacketPlayer(mc.thePlayer.onGround))
                        timer.reset()
                    }
                }

                "ghostly" -> {
                    if (mc.thePlayer.ticksExisted % 10 === 0) {
                        val lllIllIlI: Double = mc.thePlayer.posX
                        val lllIllIIl: Double = mc.thePlayer.posY + 1.0E-9
                        val lllIllIII: Double = mc.thePlayer.posZ
                        mc.thePlayer.sendQueue.addToSendQueue(C06PacketPlayerPosLook(lllIllIlI, lllIllIIl, lllIllIII, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true))
                        }
                    }

                "matrix" -> {
                    PacketUtils.sendPacketNoEvent(C08PacketPlayerBlockPlacement(mc.thePlayer.position.down(256), 256, null, 0.0f, 0.0f, 0.0f))
                }

                "matrixtimer" -> {
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true))
                    mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                }

                "aac4nofire" -> {
                    if (mc.thePlayer.isBurning && mc.thePlayer.ticksExisted % 10 == 0) {
                        repeat(35) {
                            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                        }
                    }
                }

                "newspartan" -> {
                    if (mc.thePlayer.ticksExisted % 5 == 0) {
                        resetTimer = true
                        mc.timer.timerSpeed = 0.98F
                        repeat(10) {
                            mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                        }
                    } else {
                        if (MovementUtils.isMoving()) mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }
                }

                "oldspartan" -> {
                    if (MovementUtils.isMoving() || !mc.thePlayer.onGround) {
                        return
                    }

                    repeat(9) {
                        mc.netHandler.addToSendQueue(C03PacketPlayer(mc.thePlayer.onGround))
                    }

                    mc.timer.timerSpeed = 0.45F
                    resetTimer = true
                }
            }
        }
    }
    override val tag: String
        get() = modeValue.get()
}
