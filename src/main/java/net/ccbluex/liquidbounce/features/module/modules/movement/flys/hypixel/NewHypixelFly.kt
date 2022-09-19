package net.ccbluex.liquidbounce.features.module.modules.movement.flys.hypixel

import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.util.MathHelper
import net.minecraft.util.MovementInput

class NewHypixelFly : FlyMode("NewHypixel"){
    private val strafeValue = BoolValue("StrafeFly", true)

    var flag = false
    override fun onMotion(event: MotionEvent) {
        if(strafeValue.get()){
            if(mc.thePlayer.fallDistance>2.5) {
                mc.thePlayer.capabilities.allowFlying = true;
                mc.thePlayer.setSprinting(true);
                if (mc.thePlayer.moveForward != 0.0f || mc.thePlayer.moveStrafing != 0.0f) {
                    mc.thePlayer.motionX = (-MathHelper.sin(this.getDirection()) * MovementUtils.getSpeed()).toDouble();
                    mc.thePlayer.motionZ = (MathHelper.cos(this.getDirection()) * MovementUtils.getSpeed()).toDouble();
                } else {
                    mc.thePlayer.motionX = 0.0;
                    mc.thePlayer.motionZ = 0.0;
                }
                mc.thePlayer.motionY = 0.0;
            }
        }else {
            if (mc.thePlayer.motionY < -0.3 || flag) {
                flag = true
                mc.thePlayer.motionY = 0.0
                mc.thePlayer.onGround = true
            }
        }
    }
    fun getDirection(): Float {
        var yaw = mc.thePlayer.rotationYaw
        val forward = mc.thePlayer.moveForward
        val strafe = mc.thePlayer.moveStrafing
        yaw += (if (forward < 0.0f) 180 else 0).toFloat()
        if (strafe < 0.0f) {
            yaw += if (forward < 0.0f) -45.0f else if (forward == 0.0f) 90.0f else 45.0f
        }
        if (strafe > 0.0f) {
            yaw -= if (forward < 0.0f) -45.0f else if (forward == 0.0f) 90.0f else 45.0f
        }
        return yaw * 0.017453292f
    }

}