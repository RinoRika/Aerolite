/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.C0BPacketEntityAction



@ModuleInfo(name = "SuperKnockback", category = ModuleCategory.COMBAT)
class SuperKnockback : Module() {

    private val setting = TextValue("Setting", "None")
    private val presetValue =
        ListValue("Preset", arrayOf("None", "LiquidBounce", "Packet", "InstantWtap", "LegitWtap", "FDPPacket", "FDPPacket2"), "None")
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val delayValue = IntegerValue("Delay", 1, 0, 10)
    private val rangeValue = FloatValue("Range", 4F, 3F, 7F)
    private val onlyMoveValue = BoolValue("OnlyMove", false)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val onlySprintValue = BoolValue("OnlySprint", false)
    private val setSprintingFalseValue = BoolValue("SetSprintingFalse", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val setSprintingTrueValue = BoolValue("SetSprintingTrue", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val setServerSprintingFalseValue = BoolValue("SetServerSprintingFalse", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val setServerSprintingTrueValue = BoolValue("SetServerSprintingTrue", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStartSprintValue = BoolValue("C0BPacketStartSprint", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStopSprintValue = BoolValue("C0BPacketStopSprint", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStartSprint2Value = BoolValue("C0BPacketStartSprint2", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStopSprint2Value = BoolValue("C0BPacketStopSprint2", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStartSprint3Value = BoolValue("C0BPacketStartSprint3", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStopSprint3Value = BoolValue("C0BPacketStopSprint3", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStartSprint4Value = BoolValue("C0BPacketStartSprint4", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val c0BPacketStopSprint4Value = BoolValue("C0BPacketStopSprint4", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val setSprintingTrue2Value = BoolValue("SetSprintingTrue2", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val setServerSprintingTrue2Value = BoolValue("SetServerSprintingFalse2", false).displayable { !presetValue.get().equals("FDPPacket") || !presetValue.get().equals("FDPPacket2") }
    private val debugMessageValue = BoolValue("DebugMessage", false)
    private var counts = 0


    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (mc.thePlayer == null || mc.theWorld == null) return
        if (event.targetEntity is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get() || (!MovementUtils.isMoving() && onlyMoveValue.get()) ||
                (!mc.thePlayer.onGround && onlyGroundValue.get()) || (!mc.thePlayer.isSprinting && onlySprintValue.get())
            )
                return
            counts++

            val entity = event.targetEntity

            if (presetValue.get().equals("FDPPacket")) {
                if (mc.thePlayer.isSprinting) {
                    mc.thePlayer.isSprinting = true
                }
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                mc.thePlayer.serverSprintState = true
                ClientUtils.displayChatMessage("§f§l[Debug] §a§lSUPERKB SUCCESSFUL!")
            } else if (presetValue.get().equals("FDPPacket2")) {
                if (mc.thePlayer.isSprinting) {
                    mc.thePlayer.isSprinting = true
                }
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                mc.thePlayer.serverSprintState = true
                ClientUtils.displayChatMessage("§f§l[Debug] §a§lSUPERKB SUCCESSFUL!")
            } else
                if (mc.thePlayer.getDistanceToEntity(entity) <= rangeValue.get() && counts >= delayValue.get()) {
                    counts = 0
                    if (setSprintingFalseValue.get())
                        mc.thePlayer.isSprinting = false
                    if (setSprintingTrueValue.get())
                        mc.thePlayer.isSprinting = true
                    if (setServerSprintingFalseValue.get())
                        mc.thePlayer.serverSprintState = false
                    if (setServerSprintingTrueValue.get())
                        mc.thePlayer.serverSprintState = true
                    if (c0BPacketStartSprintValue.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    if (c0BPacketStopSprintValue.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    if (c0BPacketStartSprint2Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    if (c0BPacketStopSprint2Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    if (c0BPacketStartSprint3Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    if (c0BPacketStopSprint3Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    if (c0BPacketStartSprint4Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING
                        )
                    )
                    if (c0BPacketStopSprint4Value.get()) mc.netHandler.addToSendQueue(
                        C0BPacketEntityAction(
                            mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING
                        )
                    )
                    if (setSprintingTrue2Value.get())
                        mc.thePlayer.isSprinting = true
                    if (setServerSprintingTrue2Value.get())
                        mc.thePlayer.serverSprintState = true
                    if (debugMessageValue.get())
                        ClientUtils.displayChatMessage("§f§l[Debug] §a§lSUPERKB SUCCESSFUL!")
                }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (presetValue.get()) {
            "None" -> {}
            "LiquidBounce" -> {
                setSprintingFalseValue.set(false)
                setSprintingTrueValue.set(false)
                setServerSprintingFalseValue.set(false)
                setServerSprintingTrueValue.set(false)
                c0BPacketStartSprintValue.set(false)
                c0BPacketStopSprintValue.set(true)
                c0BPacketStartSprint2Value.set(true)
                c0BPacketStopSprint2Value.set(true)
                c0BPacketStartSprint3Value.set(true)
                c0BPacketStopSprint3Value.set(false)
                c0BPacketStartSprint4Value.set(false)
                c0BPacketStopSprint4Value.set(false)
                setSprintingTrueValue.set(true)
                setServerSprintingTrueValue.set(true)
                presetValue.set("None")
                setting.set("LiquidBounce")
                ClientUtils.displayChatMessage("§c§l[SuperKnockBack] §6§lYour SuperKB Values has changed to LiquidBounce")
            }
            "Packet" -> {
                setSprintingFalseValue.set(false)
                setSprintingTrueValue.set(true)
                setServerSprintingFalseValue.set(false)
                setServerSprintingTrueValue.set(false)
                c0BPacketStartSprintValue.set(true)
                c0BPacketStopSprintValue.set(true)
                c0BPacketStartSprint2Value.set(false)
                c0BPacketStopSprint2Value.set(false)
                c0BPacketStartSprint3Value.set(false)
                c0BPacketStopSprint3Value.set(false)
                c0BPacketStartSprint4Value.set(false)
                c0BPacketStopSprint4Value.set(false)
                setSprintingTrueValue.set(false)
                setServerSprintingTrueValue.set(true)
                presetValue.set("None")
                setting.set("Packet")
                ClientUtils.displayChatMessage("§c§l[SuperKnockBack] §6§lYour SuperKB Values has changed to Packet")
            }
            "InstantWtap" -> {
                setSprintingFalseValue.set(true)
                setSprintingTrueValue.set(false)
                setServerSprintingFalseValue.set(false)
                setServerSprintingTrueValue.set(false)
                c0BPacketStartSprintValue.set(true)
                c0BPacketStopSprintValue.set(false)
                c0BPacketStartSprint2Value.set(false)
                c0BPacketStopSprint2Value.set(false)
                c0BPacketStartSprint3Value.set(false)
                c0BPacketStopSprint3Value.set(false)
                c0BPacketStartSprint4Value.set(false)
                c0BPacketStopSprint4Value.set(false)
                setSprintingTrueValue.set(false)
                setServerSprintingTrueValue.set(true)
                presetValue.set("None")
                setting.set("InstantWtap")
                ClientUtils.displayChatMessage("§c§l[SuperKnockBack] §6§lYour SuperKB Values has changed to InstantWtap")
            }
            "LegitWtap" -> {
                setSprintingFalseValue.set(true)
                setSprintingTrueValue.set(false)
                setServerSprintingFalseValue.set(false)
                setServerSprintingTrueValue.set(false)
                c0BPacketStartSprintValue.set(false)
                c0BPacketStopSprintValue.set(false)
                c0BPacketStartSprint2Value.set(false)
                c0BPacketStopSprint2Value.set(false)
                c0BPacketStartSprint3Value.set(false)
                c0BPacketStopSprint3Value.set(false)
                c0BPacketStartSprint4Value.set(false)
                c0BPacketStopSprint4Value.set(false)
                setSprintingTrueValue.set(true)
                setServerSprintingTrueValue.set(true)
                presetValue.set("None")
                setting.set("LegitWtap")
                ClientUtils.displayChatMessage("§c§l[SuperKnockBack] §6§lYour SuperKB Values has changed to LegitWtap")
            }
        }
    }
    override val tag: String
        get() = setting.get()
}