package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.hypixel

import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class HypixelHop2 : SpeedMode("HypixelHop2") {
    // 构式 来自 blockman3063
    override fun onUpdate() {
        if (mc.thePlayer.onGround) {
            MovementUtils.strafe(0.48f)
        }
        if (MovementUtils.isOnGround(0.42)) {
            MovementUtils.strafe(0.45f)
        }
    }
}