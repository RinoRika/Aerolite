package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.launch.data.legacyui.GuiMainMenu
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.GuiMainMenuLLL
import net.ccbluex.liquidbounce.ui.fix.JelloMainMenu
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.ColorValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.GuiScreen

@ModuleInfo(name = "ClientSettings", ModuleCategory.CLIENT, defaultOn = true)
object ClientSettings : Module() {
    val mainMenuValue = ListValue("MainMenuType", arrayOf("Aerolite", "AeroliteOld", "Jello", "Minecraft"), "Aerolite")
    val shaderBackGround = BoolValue("ShaderBackground", true)
    val reload = BoolValue("Reload", false)
    val color = ColorValue("Test", 1)

    fun getMainMenuType(): GuiScreen {
        return when (mainMenuValue.get()) {
            "Aerolite" -> GuiMainMenuLLL()
            "AeroliteOld" -> GuiMainMenu()
            "Jello" -> JelloMainMenu()
            "Minecraft" -> net.minecraft.client.gui.GuiMainMenu()
            else -> GuiMainMenuLLL()
        }
    }

    fun reloadMainMenu() {
        LiquidBounce.mainMenu = getMainMenuType()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (reload.get()) {
            reloadMainMenu()
            ClientUtils.displayChatMessage("Success!")
            reload.set(false)
        }
    }
}