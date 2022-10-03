package net.ccbluex.liquidbounce.features.module.modules.movement.flys.hypixel

import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import kotlin.math.cos
import kotlin.math.sin

class HypixelDash : FlyMode("HypixelDash") {



    private val timer = MSTimer()

    override fun onEnable() {
        ClientUtils.displayChatMessage("§8[§c§lHypixe-Dash§8] §cPlease stand by and don't move,or you might be kicked out")
        timer.reset()
    }

    override fun onUpdate(event: UpdateEvent) {
        mc.thePlayer.onGround = false
        if (timer.hasTimePassed(1000)) {
            val yaw = Math.toRadians(mc.thePlayer.rotationYaw.toDouble())
            mc.thePlayer.setPosition(mc.thePlayer.posX + (-sin(yaw) * 6), mc.thePlayer.posY - 1.5, mc.thePlayer.posZ + (cos(yaw) * 6))
            timer.reset()
        }
        mc.thePlayer.jumpMovementFactor = 0.00f
    }
}
