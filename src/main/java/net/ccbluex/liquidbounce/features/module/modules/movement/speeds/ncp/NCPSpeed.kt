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
import oh.yalan.NativeClass

@NativeClass
class NCPSpeed : SpeedMode("NCP") {
    private val strafeboost = IntegerValue("StrafeBoost", 1, 0, 100)
    private val jumpboost = IntegerValue("JumpBoost", 1, 0, 100)
    private val potionBoost = BoolValue("PotionBoost", false)
    private val veloBoost = BoolValue("VelocityBoost", false)
    private val boostCount = IntegerValue("BoostCount", 30, 1, 100).displayable { potionBoost.get() }
    private val timer = FloatValue("NCPTimer", 1.05f, 1f, 1.3f)

    var deSt = strafeboost.get() * 0.01
    var deJu = jumpboost.get() * 0.01
    val mstimer = MSTimer()
    override fun onUpdate() {
        if (mstimer.hasTimePassed(1000L)) {
            deSt = strafeboost.get() * 0.01
            deJu = jumpboost.get() * 0.01
            mstimer.reset()
        }
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe(0.48f + deSt.toFloat() + getPotionAmplifier())
                    mc.thePlayer.jumpMovementFactor = 0.02f
                }
                MovementUtils.strafe(RandomUtils.nextFloat(MovementUtils.getSpeed() - 0.02f, MovementUtils.getSpeed() + deSt.toFloat() + getPotionAmplifier()))
            }
            if (mc.thePlayer.fallDistance > 0.7) {
                mc.thePlayer.jumpMovementFactor = 0.02f + deJu.toFloat()
            }
            mc.timer.timerSpeed = RandomUtils.nextFloat(1.0f, timer.get())
        }
    }

    override fun onPacket(event: PacketEvent) {
        if (event.packet is S12PacketEntityVelocity && veloBoost.get()) {
            deSt = deJu * 1.2
        }
    }

    private fun getPotionAmplifier(): Float {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return 0.06f + (boostCount.get() * 0.01f)
        } else return 0.0f
    }

    override fun onDisable() {
        MovementUtils.stop()
        mc.timer.timerSpeed = 1f
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}