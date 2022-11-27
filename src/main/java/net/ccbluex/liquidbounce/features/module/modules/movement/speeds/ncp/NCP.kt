package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.ncp

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.potion.Potion
import oh.yalan.NativeClass

@NativeClass
class NCP : SpeedMode("NCP") {
    private val strafeboost = IntegerValue("StrafeBoost", 1, 0, 100)
    private val jumpboost = IntegerValue("JumpBoost", 1, 0, 100)
    private val potionBoost = BoolValue("PotionBoost", false)
    private val boostCount = IntegerValue("BoostCount", 30, 1, 100).displayable { potionBoost.get() }

    private var deSt = strafeboost.get() * 0.01
    private var deJu = jumpboost.get() * 0.01
    override fun onUpdate() {
        if (!mc.thePlayer.isInWeb && !mc.thePlayer.isInLava && !mc.thePlayer.isInWater && !mc.thePlayer.isOnLadder && mc.thePlayer.ridingEntity == null) {
            if (MovementUtils.isMoving()) {
                mc.gameSettings.keyBindJump.pressed = false
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    MovementUtils.strafe(0.481f + deSt.toFloat() + getPotionAmplifier())
                    mc.timer.timerSpeed = 1.07f
                    mc.thePlayer.jumpMovementFactor = 0.02f
                }
                mc.timer.timerSpeed = 1.05f
                MovementUtils.strafe(MovementUtils.getSpeed() + 0.1f + deSt.toFloat() + getPotionAmplifier())
            }
            if (MovementUtils.isOnGround(0.2)) {
                mc.timer.timerSpeed = 1.14f
            }
            if (MovementUtils.isOnGround(0.4)) {
                mc.timer.timerSpeed = 1.05f
            }
            if (mc.thePlayer.fallDistance > 0.7) {
                mc.thePlayer.jumpMovementFactor = 0.02f + deJu.toFloat()
            }
        }
    }

    private fun getPotionAmplifier(): Float {
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            return 0.06f + (boostCount.get() * 0.01f)
        } else return 0.0f
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
        mc.thePlayer.jumpMovementFactor = 0.02f
    }
}