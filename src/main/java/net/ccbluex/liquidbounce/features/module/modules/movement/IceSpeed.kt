/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getMaterial
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.block.material.Material
import net.minecraft.init.Blocks
import net.minecraft.util.BlockPos

@ModuleInfo(name = "IceSpeedPlus", category = ModuleCategory.MOVEMENT)
class IceSpeed : Module() {
    private val modeValue = ListValue("Mode", arrayOf("NCP", "AAC3", "Spartan", "AAC4", "AAC5", "Reflex"), "NCP")
    private val slipperBoostValue = FloatValue("SlipperBoost", 0.0F, 0.0F, 5.0F)
    private val motionBoostValue = FloatValue("MotionBoost", 0.0F, 0.0F, 3.0F)
    private val timerBoostValue = FloatValue("TimerBoost", 0.0F, 0.0F, 1.0F)
    override fun onEnable() {
        if (modeValue.get().equals("NCP", ignoreCase = true)) {
            Blocks.ice.slipperiness = 0.39f + slipperBoostValue.get()
            Blocks.packed_ice.slipperiness = 0.39f + slipperBoostValue.get()
        }
        super.onEnable()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val mode = modeValue.get()
        if (mode.equals("NCP", ignoreCase = true)) {
            Blocks.ice.slipperiness = 0.39f + slipperBoostValue.get()
            Blocks.packed_ice.slipperiness = 0.39f + slipperBoostValue.get()
        } else {
            Blocks.ice.slipperiness = 0.98f + slipperBoostValue.get()
            Blocks.packed_ice.slipperiness = 0.98f + slipperBoostValue.get()
        }
        if (mc.thePlayer.onGround && !mc.thePlayer.isOnLadder && !mc.thePlayer.isSneaking && mc.thePlayer.isSprinting && mc.thePlayer.movementInput.moveForward > 0.0) {
            if (mode.equals("AAC3", ignoreCase = true)) {
                val material = getMaterial(mc.thePlayer.position.down())
                if (material === Material.ice || material === Material.packedIce) {
                    mc.thePlayer.motionX *= 1.342 + motionBoostValue.get()
                    mc.thePlayer.motionZ *= 1.342 + motionBoostValue.get()
                    Blocks.ice.slipperiness = 0.6f + slipperBoostValue.get()
                    Blocks.packed_ice.slipperiness = 0.6f + slipperBoostValue.get()
                }
            }
            if (mode.equals("AAC4", ignoreCase = true)) {
                val material = getMaterial(mc.thePlayer.position.down())
                if (material === Material.ice || material === Material.packedIce) {
                    mc.timer.timerSpeed = 1.08F + timerBoostValue.get()
                }
            }
            if (mode.equals("AAC5", ignoreCase = true)) {
                val material = getMaterial(mc.thePlayer.position.down())
                if (material === Material.ice || material === Material.packedIce) {
                    mc.timer.timerSpeed = 1.049F + timerBoostValue.get()
                }
            }
            if (mode.equals("Reflex", ignoreCase = true)) {
                val material = getMaterial(mc.thePlayer.position.down())
                if (material === Material.ice || material === Material.packedIce) {
                    mc.timer.timerSpeed = 1.02F + timerBoostValue.get()
                    Blocks.ice.slipperiness = 1.0f + slipperBoostValue.get()
                    Blocks.packed_ice.slipperiness = 1.0f + slipperBoostValue.get()
                } // 盲目下判断的人终究是失败者(一时生气写的awa)
            }
            if (mode.equals("Spartan", ignoreCase = true)) {
                val material = getMaterial(mc.thePlayer.position.down())
                if (material === Material.ice || material === Material.packedIce) {
                    val upBlock = getBlock(BlockPos(mc.thePlayer.posX, mc.thePlayer.posY + 2.0, mc.thePlayer.posZ))
                    if (upBlock !is BlockAir) {
                        mc.thePlayer.motionX *= 1.342 + motionBoostValue.get()
                        mc.thePlayer.motionZ *= 1.342 + motionBoostValue.get()
                    } else {
                        mc.thePlayer.motionX *= 1.18 + motionBoostValue.get()
                        mc.thePlayer.motionZ *= 1.18 + motionBoostValue.get()
                    }
                    Blocks.ice.slipperiness = 0.6f + slipperBoostValue.get()
                    Blocks.packed_ice.slipperiness = 0.6f + slipperBoostValue.get()
                }
            }
        }
    }

    override fun onDisable() {
        Blocks.ice.slipperiness = 0.98f
        Blocks.packed_ice.slipperiness = 0.98f
        mc.timer.timerSpeed = 1.0F
        super.onDisable()
    }
}