package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.utils.MinecraftInstance.mc
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacketNoEvent
import net.minecraft.block.Block
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemBucketMilk
import net.minecraft.item.ItemFood
import net.minecraft.item.ItemPotion
import net.minecraft.network.play.client.C07PacketPlayerDigging
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import net.minecraft.util.MathHelper

object PlayerUtils {
    fun randomUnicode(str: String): String {
        val stringBuilder = StringBuilder()
        for (c in str.toCharArray()) {
            if (Math.random()> 0.5 && c.code in 33..128) {
                stringBuilder.append(Character.toChars(c.code + 65248))
            } else {
                stringBuilder.append(c)
            }
        }
        return stringBuilder.toString()
    }

    fun block(x: Double, y: Double, z: Double): Block? {
        return mc.theWorld.getBlockState(BlockPos(x, y, z)).block
    }
    fun block(blockPos: BlockPos?): Block? {
        return mc.theWorld.getBlockState(blockPos).block
    }
    fun getAr(player : EntityLivingBase):Double{
        var arPercentage: Double = (player!!.totalArmorValue / player!!.maxHealth).toDouble()
        arPercentage = MathHelper.clamp_double(arPercentage, 0.0, 1.0)
        return 100 * arPercentage
    }
    fun getHp(player : EntityLivingBase):Double{
        val heal = player.health.toInt().toFloat()
        var hpPercentage: Double = (heal / player.maxHealth).toDouble()
        hpPercentage = MathHelper.clamp_double(hpPercentage, 0.0, 1.0)
        return 100 * hpPercentage
    }
    fun isUsingFood(): Boolean {
        val usingItem = mc.thePlayer.itemInUse.item
        return if (mc.thePlayer.itemInUse != null) {
            mc.thePlayer.isUsingItem && (usingItem is ItemFood || usingItem is ItemBucketMilk || usingItem is ItemPotion)
        } else false
    }
    fun sendBlocking(callEvent: Boolean, placement: Boolean) {
        if (mc.thePlayer == null) return
        if (placement) {
            val packet = C08PacketPlayerBlockPlacement(BlockPos(-1, -1, -1), 255, mc.thePlayer.heldItem, 0f, 0f, 0f)
            if (callEvent) {
                mc.netHandler.addToSendQueue(packet)
            } else {
                sendPacketNoEvent(packet)
            }
        } else {
            val packet = C08PacketPlayerBlockPlacement(mc.thePlayer.heldItem)
            if (callEvent) {
                mc.netHandler.addToSendQueue(packet)
            } else {
                sendPacketNoEvent(packet)
            }
        }
    }

    fun releaseUseItem(callEvent: Boolean) {
        if (mc.thePlayer == null) return
        val packet =
            C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN)
        if (callEvent) {
            mc.netHandler.addToSendQueue(packet)
        } else {
            sendPacketNoEvent(packet)
        }
    }
    fun isBlockUnder(): Boolean {
        if (mc.thePlayer.posY < 0) return false
        var off = 0
        while (off < mc.thePlayer.posY.toInt() + 2) {
            val bb: AxisAlignedBB = mc.thePlayer.getEntityBoundingBox()
                .offset(0.0, -off.toDouble(), 0.0)
            if (mc.theWorld.getCollidingBoundingBoxes(
                    mc.thePlayer,
                    bb
                ).isNotEmpty()
            ) {
                return true
            }
            off += 2
        }
        return false
    }
    fun getBlockRelativeToPlayer(offsetX: Double, offsetY: Double, offsetZ: Double): Block? {
        return mc.theWorld.getBlockState(
            BlockPos(
                mc.thePlayer.posX + offsetX,
                mc.thePlayer.posY + offsetY,
                mc.thePlayer.posZ + offsetZ
            )
        ).block
    }
}