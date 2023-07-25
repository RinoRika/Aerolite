package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.potion.Potion



class NCPSpeed : SpeedMode("NCP") {
    private val strafeBoost = FloatValue("StrafeBoost", 1.0f, 0.0f, 10.0f)
    private val jumpBoost = FloatValue("JumpBoost", 1.0f, 0.0f, 100.0f)
    private val potionBoost = BoolValue("PotionBoost", false)
    private val veloBoost = BoolValue("VelocityBoost", false)
    private val boostCount = IntegerValue("BoostCount", 30, 1, 100).displayable { potionBoost.get() }
    private val timer = FloatValue("NCPTimer", 1.05f, 1f, 1.3f)

    private var strafeAdd = strafeBoost.get() * 0.01f
    private var jumpAdd = jumpBoost.get() * 0.01f
    private val msTimer = MSTimer()
    override fun onUpdate() {
        if (msTimer.hasTimePassed(1000L)) {
            strafeAdd = strafeBoost.get() * 0.01f
            jumpAdd = jumpBoost.get() * 0.01f
            msTimer.reset()
        }
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false

                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe(0.48f + strafeAdd + getPotionAmplifier())
                    mc.thePlayer.jumpMovementFactor = 0.02f
                }
                MovementUtils.strafe(RandomUtils.nextFloat(MovementUtils.getSpeed() - 0.04f, MovementUtils.getSpeed() + strafeAdd + getPotionAmplifier()))
            }
            if (mc.thePlayer.fallDistance > 0.6) {
                mc.thePlayer.jumpMovementFactor = 0.02f + jumpAdd
            }
            mc.timer.timerSpeed = RandomUtils.nextFloat(0.95f, timer.get())
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S12PacketEntityVelocity && veloBoost.get() && event.packet.entityID == mc.thePlayer.entityId) {
            strafeAdd *= 1.1f
        }
    }

    private fun getPotionAmplifier(): Float {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return 0.04f + (boostCount.get() * 0.01f)
        } else return 0.0f
    }

    override fun onDisable() {
        MovementUtils.stop()
        mc.timer.timerSpeed = 1f
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}