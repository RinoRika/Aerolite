package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.cn.Fonts.newdropdown.DropdownClickGui
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.ClickGui
import net.ccbluex.liquidbounce.launch.data.uichoser
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.gui.inventory.GuiEditSign
import net.minecraft.client.settings.GameSettings
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.play.server.S2DPacketOpenWindow
import net.minecraft.network.play.server.S2EPacketCloseWindow
import java.util.*
import java.util.function.Consumer

@ModuleInfo(name = "InvMove", category = ModuleCategory.MOVEMENT)
class InvMove : Module() {
    val mode = ListValue(
        "Mode", arrayOf(
            "Vanilla",
            "Spoof",
            "Delay"
        ), "Spoof")
    private val noMoveClicksValue = BoolValue("NoMoveClicks", false)
    private val keepOpen = BoolValue("KeepOpen", true)
    private val sneak = BoolValue("Sneak", false)
    private var keys = listOf(
        mc.gameSettings.keyBindForward,
        mc.gameSettings.keyBindBack,
        mc.gameSettings.keyBindLeft,
        mc.gameSettings.keyBindRight,
        mc.gameSettings.keyBindJump,
        mc.gameSettings.keyBindSprint)



    private fun updateStates() {
        try {
            if (System.getProperty("NoInvCrash").contains("true")) {
                return
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (sneak.get()) {
            keys = listOf(
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint,
                mc.gameSettings.keyBindSneak
            )
        } else {
            keys = listOf(
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint)
        }
        if (mc.currentScreen != uichoser() && mc.currentScreen != null) {
            try {
                keys.forEach(Consumer { k: KeyBinding -> k.pressed = GameSettings.isKeyDown(k) })
            } catch (e: Exception) {
                e.printStackTrace()
                ClientUtils.logError("InvMove occurred a serious error.", e)
            }
        }
    }
    private val delayTimer = MSTimer()
    @EventTarget
    fun onMotion(event: MotionEvent) {
        when (mode.get().lowercase(Locale.getDefault())) {
            "spoof", "vanilla" -> if (event.eventState === EventState.PRE && mc.currentScreen is GuiContainer) {
                updateStates()
            }
            "delay" -> if (event.eventState === EventState.PRE && mc.currentScreen is GuiContainer) {
                if (delayTimer.hasTimePassed(100)) {
                    updateStates()
                    delayTimer.reset()
                }
            }
        }
    }

    @EventTarget
    fun onScreen(event: ScreenEvent) {
        updateStates()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
    //    if (keepOpen.get() && (!mc.gameSettings.keyBindInventory.isKeyDown || !mc.gameSettings.keyBindInventory.isPressed || !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)
    //                && (event.packet is S2EPacketCloseWindow || event.packet is C0DPacketCloseWindow))) event.cancelEvent()
        if (mode.get().equals("spoof", ignoreCase = true)) {
            if (event.packet is S2DPacketOpenWindow) {
                event.cancelEvent()
            }
            if (event.packet is S2EPacketCloseWindow) {
                event.cancelEvent()
            }
        }
    }

    @EventTarget
    fun onClick(event: ClickWindowEvent) {
        if (noMoveClicksValue.get() && MovementUtils.isMoving())
            event.cancelEvent()
    }

    override val tag: String
        get() = mode.get()
}