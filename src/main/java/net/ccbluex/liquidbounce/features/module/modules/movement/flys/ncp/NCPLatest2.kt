package net.ccbluex.liquidbounce.features.module.modules.movement.flys.ncp

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.event.StrafeEvent
import net.ccbluex.liquidbounce.event.TeleportEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.PlayerUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook

class NCPLatest2 : FlyMode("NCPLatest2") {
    private val modeValue = ListValue("Mode", arrayOf("Normal", "Clip"), "Normal")

    private var moveSpeed = 0.0
    private var started = false
    private var notUnder = false
    private var clipped = false
    private var teleport = false

    override fun onTeleport(event: TeleportEvent) {
        if (teleport) {
            event.cancelEvent()
            teleport = false
            ClientUtils.displayAlert("Teleported")
        }
    }

    override fun onStrafe(event: StrafeEvent) {
        if(!modeValue.equals("Clip")) return
        val bb = mc.thePlayer.entityBoundingBox.offset(0.0, 1.0, 0.0)
        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() || started) {
            when (mc.thePlayer.onGround) {
                true -> if (notUnder) {
                    if (clipped) {
                        started = true
                        MovementUtils.strafe(10f)
                        mc.thePlayer.motionY = 0.42
                        notUnder = false
                    }
                }
                false -> if (started) MovementUtils.strafe(9.6f)
            }
        } else {
            notUnder = true
            if (clipped) return
            clipped = true
            PacketUtils.sendPacketNoEvent(
                C06PacketPlayerPosLook(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch,
                    false
                )
            )
            PacketUtils.sendPacketNoEvent(
                C06PacketPlayerPosLook(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY - 0.1,
                    mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch,
                    false
                )
            )
            PacketUtils.sendPacketNoEvent(
                C06PacketPlayerPosLook(
                    mc.thePlayer.posX,
                    mc.thePlayer.posY,
                    mc.thePlayer.posZ,
                    mc.thePlayer.rotationYaw,
                    mc.thePlayer.rotationPitch,
                    false
                )
            )
            teleport = true
        }

        MovementUtils.strafe()

        mc.timer.timerSpeed = 0.4f
    }

    override fun onMotion(event: MotionEvent) {
        if (!modeValue.equals("Normal")) return

        val bb = mc.thePlayer.entityBoundingBox.offset(0.0, 1.0, 0.0)

        if (started) {
            mc.thePlayer.motionY += 0.025
            MovementUtils.strafe(moveSpeed.toFloat() * 0.935f)
            if (mc.thePlayer.motionY < -0.5 && !PlayerUtils.isBlockUnder()) {
                LiquidBounce.moduleManager.getModule(Fly::class.java)!!.state = false
            }
        }

        if (mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, bb).isEmpty() && !started) {
            started = true
            mc.thePlayer.jump()
            moveSpeed = 9.0
            MovementUtils.strafe(moveSpeed.toFloat())
        }
    }

    override fun onDisable() {
        MovementUtils.stop()
    }

    override fun onEnable() {
        ClientUtils.displayAlert("Start the fly under the block and walk forward")
        moveSpeed = 0.0
        notUnder = false
        started = false
        clipped = false
        teleport = false
    }
}