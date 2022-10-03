package net.ccbluex.liquidbounce.features.module.modules.movement.flys.hypixel

import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.block.BlockAir
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.util.AxisAlignedBB

class HypixelLatest : FlyMode("HypixelLatest") {
    private val timer = FloatValue("Timer", 1.0f, 0.5f, 5f)

    override fun onUpdate(event: UpdateEvent) {
          mc.timer.timerSpeed = timer.get()
    }

    override fun onBlockBB(event: BlockBBEvent) {
        if (event.block is BlockAir && event.y <= mc.thePlayer.posY) {
            event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, mc.thePlayer.posY, event.z + 1.0)
        }
    }

    override fun onEnable() {
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4, mc.thePlayer.posZ)
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 4.5, mc.thePlayer.posZ)
    }

    override fun onJump(event: JumpEvent) {
        event.cancelEvent()
    }

    override fun onStep(event: StepEvent) {
        event.stepHeight = 0f
    }

    override fun onPacket(event: PacketEvent) {
        val packet = event.packet

        if (packet is C03PacketPlayer) {
            packet.onGround = false
        }
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
    }
}