/*
 * Aerolite Hacked Client
 * Copyright 2022 Aero Team
 * Powered By Stars
 */
package net.ccbluex.liquidbounce.features.module.modules.addit

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S07PacketRespawn
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraftforge.event.world.WorldEvent
import java.awt.Color
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "HypDisabler2", category = ModuleCategory.ADDIT)
class HypDisabler2 : Module() {
    private val banAlert = BoolValue("BanAlert", true)
    private val showState = BoolValue("ShowState", true)
    private val rotationDisabler = BoolValue("RotationDisabler", true)
    private val c00Disabler = BoolValue("KeepAliveDisabler", false)
    private val c0BDisabler = BoolValue("C0BDisabler", false)
    private val lessFlag = BoolValue("LessFlag", false)
    private val noC03 = BoolValue("NoC03Packet", true)
    private val strafeDisabler = BoolValue("StrafeDisabler", true)
    private val strafePacketAmount = IntegerValue("StrafePacketAmount", 70,60, 120).displayable { strafeDisabler.get() }
    private val timerDisabler = BoolValue("TimerDisabler", true)
    private val pingSpoofC0F2 = BoolValue("C0FEdit", false)
    private val pingSpoofSize = IntegerValue("PingSpoofSize", 7, 4, 10).displayable { timerDisabler.get() }
    private val pingSpoofC0F = BoolValue("PingSpoofC0F", false).displayable { timerDisabler.get() }
    private val pingSpoofListReleaseType = ListValue("PingSpoofReleaseType", arrayOf("Take", "Poll"), "Take").displayable { timerDisabler.get() }
    private val invMove = BoolValue("InvMoveDisabler", false)
    private val debug = BoolValue("Debug", true)
    private var counter = 0
    private var x = 0.0
    private var y = 0.0
    private var z = 0.0

    private val packets = LinkedBlockingQueue<Packet<INetHandlerPlayServer>>()
    private val timerCancelDelay = MSTimer()
    private val timerCancelCounter = MSTimer()
    private var timerShouldCancel = true
    private var inCage = true
    
    private var c00active = false
    private var c0bactive = false
    private var rotationactive = false
    private var lessflagactive = false
    private var noc03active = false
    private var strafeactive = false
    private var timerDisablerctive = false
    private var blinkactive = false
    private var pingspoofactive = false
    private var canBlink = true

    private fun dAlert(msg: String) {
        if(debug.get()) ClientUtils.displayChatMessage("§7[§b§lDISABLER§r§7] §e$msg")
    }

    @EventTarget // I finally got the importance of @EventTarget
    fun onWorld(event: WorldEvent) {
        counter = 0
        inCage = true
        dAlert("Event on world, reset counter.")
    }

    override fun onEnable() {
        counter = 0
        inCage = true
        x = 0.0
        y = 0.0
        z = 0.0
        packets.clear()
        timerCancelDelay.reset()
        timerCancelCounter.reset()
        dAlert("Enabled hypixel disabler,make sure you add NOTIFICATION hud!")
        c00active = false
        c0bactive = false
        rotationactive = false
        lessflagactive = false
        noc03active = false
        strafeactive = false
        timerDisablerctive = false
        blinkactive = false
        pingspoofactive = false
    }

    override fun onDisable() {
        inCage = true
        dAlert("Disabled hypixel disabler,stop using suspicious activities!")
        packets.forEach { PacketUtils.sendPacketNoEvent(it) }
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        val sr = ScaledResolution(mc)
        if (!showState.get()) return
        Fonts.font35.drawString("C00 Disabler: ${getRenderString(c00active)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f - 10f, Color.WHITE.rgb)
        Fonts.font35.drawString("C0B Disabler: ${getRenderString(c0bactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f, Color.WHITE.rgb)
        Fonts.font35.drawString("Rotation Disabler: ${getRenderString(rotationactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 10f, Color.WHITE.rgb)
        Fonts.font35.drawString("Less Flag: ${getRenderString(lessflagactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 20f, Color.WHITE.rgb)
        Fonts.font35.drawString("No C03: ${getRenderString(noc03active)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 30f, Color.WHITE.rgb)
        Fonts.font35.drawString("Strafe Disabler: ${getRenderString(strafeactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 40f, Color.WHITE.rgb)
        Fonts.font35.drawString("Timer Disabler: ${getRenderString(timerDisablerctive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 50f, Color.WHITE.rgb)
        Fonts.font35.drawString("Blink: ${getRenderString(blinkactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 60f, Color.WHITE.rgb)
        Fonts.font35.drawString("Ping Spoof: ${getRenderString(pingspoofactive)}", sr.scaledWidth / 2f - 30f, sr.scaledHeight / 10f + 70f, Color.WHITE.rgb)
    }
    
    private fun getRenderString(abc: Boolean): String {
        return if (abc) "§aActive"
        else "§cDisable"
    }

    private fun isInventory(action: Short): Boolean = action in 1..99

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        canBlink = true

        // Check Cage
        if (banAlert.get() && packet is S02PacketChat && packet.chatComponent.unformattedText.contains("Cages opened!", true)) {
            dAlert("Game started,start modifying.")
            LiquidBounce.hud.addNotification(Notification("Disabler", "STRAFE and TIMER is bannable before this disappear!", NotifyType.WARNING, 20000))
            inCage = false
        }

        if (pingSpoofC0F2.get() && packet is C0FPacketConfirmTransaction && !ServerUtils.isHypixelLobby() && invMove.get()) {
            if (packet.windowId == 0 && packet.uid < 0 && packet.uid != (-1).toShort() && !isInventory(packet.uid)) {
                if (mc.thePlayer.ticksExisted % 2 == 0) {
                    event.cancelEvent()
                    dAlert("Cancel C0F")
                }
            }
        }

        // Do Timer Disabler
        if (timerDisabler.get() && !inCage) {
            pingspoofactive = true
            if ((packet is C02PacketUseEntity || packet is C03PacketPlayer || packet is C07PacketPlayerDigging || packet is C08PacketPlayerBlockPlacement ||
                packet is C0APacketAnimation || packet is C0BPacketEntityAction || (pingSpoofC0F.get() && packet is C0FPacketConfirmTransaction && !isInventory(packet.uid))) && mc.thePlayer.ticksExisted > strafePacketAmount.get() && !ServerUtils.isHypixelLobby()) {
                if (timerShouldCancel) {
                    if (!timerCancelCounter.hasTimePassed(50L * pingSpoofSize.get())) {
                        packets.add(packet as Packet<INetHandlerPlayServer>)
                        event.cancelEvent()
                        dAlert("Do pingspoof, delay = ${50 * pingSpoofSize.get()}")
                        canBlink = false
                        dAlert("Release stacked packets, stacked ${packets.size} totally")
                    } else {
                        dAlert("Do timer disabler")
                        timerShouldCancel = false
                        while (!packets.isEmpty()) {
                            if (pingSpoofListReleaseType.get() == "Take") PacketUtils.sendPacketNoEvent(packets.take())
                            else PacketUtils.sendPacketNoEvent(packets.poll())
                            pingspoofactive = false
                        }
                        timerCancelCounter.reset()
                    }
                }
            }
        }

        // No C03 (Less Packet)
        if (noC03.get()) {
            if (packet is C03PacketPlayer && !(packet is C05PacketPlayerLook || packet is C06PacketPlayerPosLook || packet is C04PacketPlayerPosition) &&
                !packet.isMoving && !mc.thePlayer.isSwingInProgress && !mc.thePlayer.isUsingItem) {
                noc03active = true
                event.cancelEvent()
                canBlink = false
            }
        } else {
            noc03active = false
        }

        // Strafe Disabler (c03 control)
        if (strafeDisabler.get() && (mc.thePlayer.ticksExisted < strafePacketAmount.get()) && packet is C03PacketPlayer && (mc.thePlayer.ticksExisted % 15 != 0)) {
            strafeactive = true
            event.cancelEvent()
            canBlink = false
        } else {
            strafeactive = false
        }

        // Less Flag (When you drop into void)
        if(lessFlag.get() || (strafeDisabler.get() && (mc.thePlayer.ticksExisted < strafePacketAmount.get()))) {
            if (event.packet is C06PacketPlayerPosLook && mc.thePlayer.onGround && mc.thePlayer.fallDistance > 10) {
                lessflagactive = true
                if (counter > 0) {
                    if (event.packet.x == x && event.packet.y == y && event.packet.z == z) {
                        dAlert("Watchdog lagging in air!")
                        mc.netHandler.networkManager.sendPacket(C04PacketPlayerPosition(event.packet.x, event.packet.y, event.packet.z, event.packet.onGround))
                        dAlert("Try to cancel C06.")
                        event.cancelEvent()
                    }
                }
                counter += 1

                // This actually happens when player get on the vehicle
                if (event.packet is C05PacketPlayerLook && mc.thePlayer.isRiding) {
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.START_SPRINTING))
                    dAlert("Bad packets C05")
                    dAlert("Sent C0B")
                } else if (event.packet is C0CPacketInput && mc.thePlayer.isRiding) {
                    mc.netHandler.networkManager.sendPacket(event.packet)
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SNEAKING))
                    dAlert("Bad packets C0C")
                    dAlert("Sent C0B")
                    event.cancelEvent()
                }
            } else {
                lessflagactive = false
            }

            if (event.packet is S08PacketPlayerPosLook) {
                val s08 = event.packet
                x = s08.x
                y = s08.y
                z = s08.z
                dAlert("Watchdog lag")
            }

            // Reset counter when respawn
            if (event.packet is S07PacketRespawn) {
                counter = 0
                inCage = true
            }
        }

    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        // An old bypass
        val adyaw = MovementUtils.movingYaw
        rotationactive = if (rotationDisabler.get()) {
            RotationUtils.setTargetRotation(Rotation(adyaw, mc.thePlayer.rotationPitch), 10)
            true
        } else {
            false
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (timerDisabler.get()) {
            // This should take a time to do or watchdog will ignore the ping
            if (timerCancelDelay.hasTimePassed(5000)) {
                timerShouldCancel = true
                timerCancelCounter.reset()
                timerCancelDelay.reset()
            }
        }

        // Send C00 to improve bypass
        if (c00Disabler.get()) {
            if (mc.thePlayer.onGround && PlayerUtils.isBlockUnder() && mc.thePlayer.fallDistance > 10) {
                dAlert("Watchdog lagging in air!")
                mc.netHandler.addToSendQueue(C00PacketKeepAlive(RandomUtils.nextInt(0, 1000)))
                c00active = true
                dAlert("Send C00 to resolve bad packets")
            } else {
                c00active = false
            }
        }

        // Send C0B to improve bypass
        if (c0BDisabler.get()) {
            if (mc.thePlayer.ticksExisted % 180 == 90) {
                if (mc.thePlayer.onGround && mc.thePlayer.fallDistance > 10) {
                    dAlert("Watchdog lagging in air!")
                    mc.netHandler.addToSendQueue(C00PacketKeepAlive(RandomUtils.nextInt(0, 1000)))
                    mc.netHandler.addToSendQueue(C0BPacketEntityAction(mc.thePlayer, C0BPacketEntityAction.Action.STOP_SPRINTING))
                    c0bactive = true
                    dAlert("Send C00 to resolve bad packets")
                    dAlert("Send C0B to resolve bad packets")
               //     mc.timer.timerSpeed = 0.8f;
                } else {
                    if (mc.thePlayer.fallDistance < 10) {
                        if (mc.thePlayer.posY == mc.thePlayer.fallDistance.toDouble()) {
                            mc.netHandler.addToSendQueue(C03PacketPlayer(false))
              //              if (mc.thePlayer.onGround) mc.timer.timerSpeed = 0.4f;
                            if (mc.thePlayer.fallDistance == 0f) mc.netHandler.addToSendQueue(C03PacketPlayer(true))
                            dAlert("Send C03")
                        }
                    }
                } // Timer Speed is not friendly to the player who is moving
            } else {
                c0bactive= false
            }
        }
    }
}