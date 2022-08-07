package net.ccbluex.liquidbounce.features.module.modules.movement.flys.other

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.ccbluex.liquidbounce.utils.extensions.drawCenteredString
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0BPacketEntityAction
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.client.settings.GameSettings
import java.awt.Color

class VulcanFly2 : FlyMode("Vulcan2") {

    private val timerValue = FloatValue("${valuePrefix}Timer", 2f, 1f, 3f)

    private var ticks = 0
    private var modifyTicks = 0
    private var stage = FlyStage.WAITING
    private var flags = 0
    private var groundX = 0.0
    private var groundY = 0.0
    private var groundZ = 0.0
    private var timer = MSTimer()

    override fun onEnable() {
        timer.reset()
        ticks = 0
        modifyTicks = 0
        flags = 0
        mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY*2).toDouble() / 2, mc.thePlayer.posZ)
        stage = FlyStage.WAITING
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        timer.reset()
    }

    override fun onUpdate(event: UpdateEvent) {
        ticks++
        modifyTicks++
        mc.gameSettings.keyBindJump.pressed = false
        mc.gameSettings.keyBindSneak.pressed = false
        when(stage) {
            FlyStage.FLYING,FlyStage.WAITING -> {
                if(stage == FlyStage.FLYING) {
                    mc.timer.timerSpeed = timerValue.get()
                }else{
                    mc.timer.timerSpeed = 1.0f
                }
                if(ticks == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindJump) && modifyTicks>=6 && mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(0.0, 0.5, 0.0)).isEmpty()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+0.5, mc.thePlayer.posZ)
                    modifyTicks = 0
                }
                if(!MovementUtils.isMoving() && ticks == 1 && (GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) || GameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && modifyTicks>=5) {
                    var playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                    mc.thePlayer.setPosition(mc.thePlayer.posX + 0.05 * -Math.sin(playerYaw)
                        , mc.thePlayer.posY
                        , mc.thePlayer.posZ + 0.05 * Math.cos(playerYaw))
                }
                if(ticks == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && modifyTicks>=6 && mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(0.0, -0.5, 0.0)).isEmpty()) {
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY-0.5, mc.thePlayer.posZ)
                    modifyTicks = 0
                }else if(ticks == 2 && GameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && !mc.theWorld.getCollisionBoxes(mc.thePlayer.entityBoundingBox.offset(0.0, -0.5, 0.0)).isEmpty()) {
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX+0.05,mc.thePlayer.posY,mc.thePlayer.posZ,true))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY,mc.thePlayer.posZ,true))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+0.42,mc.thePlayer.posZ,true))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+0.7532,mc.thePlayer.posZ,true))
                    PacketUtils.sendPacketNoEvent(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,mc.thePlayer.posY+1.0,mc.thePlayer.posZ,true))
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+1.0, mc.thePlayer.posZ)
                    stage = FlyStage.WAIT_APPLY
                    modifyTicks = 0
                    groundY = mc.thePlayer.posY - 1.0
                    groundX = mc.thePlayer.posX
                    groundZ = mc.thePlayer.posZ
                }
                mc.thePlayer.onGround = true
                mc.thePlayer.motionY = 0.0
            }
            FlyStage.WAIT_APPLY -> {
                mc.timer.timerSpeed = 1.0f
                MovementUtils.stop()
                mc.thePlayer.jumpMovementFactor = 0.0f
                if (modifyTicks >= 10) {
                    var playerYaw = mc.thePlayer.rotationYaw * Math.PI / 180
                    if (!(modifyTicks % 2 == 0)) {
                        mc.thePlayer.setPosition(mc.thePlayer.posX + 0.1 * -Math.sin(playerYaw)
                            , mc.thePlayer.posY
                            , mc.thePlayer.posZ + 0.1 * Math.cos(playerYaw))
                    }else{
                        mc.thePlayer.setPosition(mc.thePlayer.posX - 0.1 * -Math.sin(playerYaw)
                            , mc.thePlayer.posY
                            , mc.thePlayer.posZ - 0.1 * Math.cos(playerYaw))
                        if (modifyTicks >= 16 && ticks == 2) {
                            modifyTicks = 16
                            mc.thePlayer.setPosition(mc.thePlayer.posX
                                , mc.thePlayer.posY + 0.5
                                , mc.thePlayer.posZ)
                        }
                    }
                }
            }
        }
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if(packet is C03PacketPlayer) {
            if(ticks > 2) {
                ticks = 0
                packet.y += 0.5
            }
            packet.onGround = true
        } else if(packet is S08PacketPlayerPosLook) {
            if (stage == FlyStage.WAITING) {
                flags++
                if (flags >= 2) {
                    flags = 0
                    stage = FlyStage.FLYING
                }
            }
            if (stage == FlyStage.WAIT_APPLY) {
                if(Math.sqrt((packet.x - groundX) * (packet.x - groundX)
                            + (packet.z - groundZ) * (packet.z - groundZ)) < 1.4 && packet.y >= (groundY - 0.5)) {
                    stage = FlyStage.FINISHING
                    return
                }
            }
            event.cancelEvent()
        } else if(packet is C0BPacketEntityAction) {
            event.cancelEvent()
        }
    }

    enum class FlyStage {
        WAITING,
        FLYING,
        WAIT_APPLY,
        FINISHING
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val scaledRes = ScaledResolution(mc)
        val preWidth = if (stage == FlyStage.WAITING) 1 else if (stage == FlyStage.FLYING) 2 else if (stage == FlyStage.WAIT_APPLY) 3 else if (stage == FlyStage.FINISHING) 4 else 0
        val width = preWidth.toFloat() / 5f * 60f
        Fonts.font40.drawCenteredString(
            bowStatus,
            scaledRes.scaledWidth / 2f,
            scaledRes.scaledHeight / 2f + 14f,
            -1,
            true
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 31f,
            scaledRes.scaledHeight / 2f + 25f,
            scaledRes.scaledWidth / 2f + 31f,
            scaledRes.scaledHeight / 2f + 29f,
            -0x60000000
        )
        RenderUtils.drawRect(
            scaledRes.scaledWidth / 2f - 30f,
            scaledRes.scaledHeight / 2f + 26f,
            scaledRes.scaledWidth / 2f - 30f + width,
            scaledRes.scaledHeight / 2f + 28f,
            statusColor
        )
    }

    private val bowStatus: String
        get() = when (stage) {
            FlyStage.WAITING -> "Waiting..."
            FlyStage.FLYING -> "Boosting!"
            FlyStage.WAIT_APPLY -> "Landing..."
            FlyStage.FINISHING -> "Finished!"
        }
    private val statusColor: Color
        get() {
            return when (stage) {
                FlyStage.WAITING -> Color(21, 21, 21)
                FlyStage.FLYING -> Color.red
                FlyStage.WAIT_APPLY -> Color.yellow
                FlyStage.FINISHING -> Color.green
            }
        }
}