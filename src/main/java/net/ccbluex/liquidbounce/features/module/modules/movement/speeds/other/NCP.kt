package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.other

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue

class NCP : SpeedMode("NCP") {
    private val strafeboost = IntegerValue("StrafeBoost", 1, 0, 20)
    private val jumpboost = IntegerValue("JumpBoost", 1, 0, 20)
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe(0.484f + strafeboost.get().toFloat())
                    mc.timer.timerSpeed = 1.066f
                    mc.thePlayer.jumpMovementFactor = 0.02f
                }
                mc.timer.timerSpeed = 1.034f
                MovementUtils.strafe()
            }
            if (MovementUtils.isOnGround(0.2)) {
                mc.timer.timerSpeed = 1.15f
            }
            if (mc.thePlayer.fallDistance > 0.7) {
                mc.thePlayer.jumpMovementFactor = 0.02f + jumpboost.get().toFloat()
            }
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}