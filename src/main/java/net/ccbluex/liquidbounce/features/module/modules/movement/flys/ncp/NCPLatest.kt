package net.ccbluex.liquidbounce.features.module.modules.movement.flys.ncp

import net.ccbluex.liquidbounce.event.BlockBBEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.flys.FlyMode
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.block.BlockAir
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos

class NCPLatest : FlyMode("NCPLatest") {
    private val fakeGround = BoolValue("${valuePrefix}FakeGround", false)
    private val speed = FloatValue("${valuePrefix}Speed", 3.0f, 0.1f, 10.0f)
    private val boostTimer = FloatValue("${valuePrefix}BoostTimer", 1f, 0.1f, 2f)
    private val boostNoEditY = BoolValue("${valuePrefix}BoostNoEditY", false)
    private val boostMotionY = FloatValue("${valuePrefix}BoostMotionY", 1f, 0f, 2f).displayable { !boostNoEditY.get() }
    private val autoJump = BoolValue("${valuePrefix}AutoJump", true)
    private val timer = MSTimer()
    private var canBoost = false
    private var onBoost = false
    override fun onEnable() {
        mc.thePlayer.motionY = 0.0
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        canBoost = false
        onBoost = false
        timer.reset()
    }

    override fun onUpdate(event: UpdateEvent) {
        if (mc.theWorld.isBlockFullCube(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2, mc.thePlayer.posZ))) {
            canBoost = true
        }
        if (canBoost) {
            if (mc.thePlayer.onGround) {
                mc.thePlayer.motionX = 0.0
                mc.thePlayer.motionZ = 0.0
            }
            mc.timer.timerSpeed = 0.2f
            if(autoJump.get()) mc.thePlayer.jump()
            if (timer.hasTimePassed(1000L)) {
                onBoost = true
                canBoost = false
            }
        } else if (onBoost) {
            if (!boostNoEditY.get()) mc.thePlayer.motionY = boostMotionY.get().toDouble()
            MovementUtils.strafe(speed.get())
            mc.timer.timerSpeed = boostTimer.get()
        }
    }

    override fun onBlockBB(event: BlockBBEvent) {
        if (fakeGround.get()) {
            if (event.block is BlockAir && event.y <= fly.launchY) {
                event.boundingBox = AxisAlignedBB.fromBounds(event.x.toDouble(), event.y.toDouble(), event.z.toDouble(), event.x + 1.0, fly.launchY, event.z + 1.0)
            }
        }
    }
}