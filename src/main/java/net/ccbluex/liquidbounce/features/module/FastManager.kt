package net.ccbluex.liquidbounce.features.module

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.modules.client.ClientSettings
import net.ccbluex.liquidbounce.features.module.modules.misc.KillInsults
import net.ccbluex.liquidbounce.features.module.modules.render.LiquidBouncePlus.ColorMixer
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.Display

object FastManager {
    var mc: Minecraft = Minecraft.getMinecraft()
    fun init() {
        mc = Minecraft.getMinecraft()
    }
    private fun chatMessage(msg:String) {
        if(mc.theWorld != null && mc.thePlayer != null) ClientUtils.displayChatMessage(msg)
    }
    fun clientAlert(msg: String) {
        if(mc.theWorld != null && mc.thePlayer != null) ClientUtils.displayAlert(msg)
    }
    fun fuckMySelf(m: Module) {
        LiquidBounce.moduleManager.unregisterModule(m)
    }
    fun fastReload() {
        chatMessage("§c§lRELOADING! This may take a few seconds and will lag the client!")
        System.gc()
        // Commands
        LiquidBounce.commandManager = CommandManager()
        LiquidBounce.commandManager.registerCommands()
        // Prevent crashing
        LiquidBounce.isStarting = true
        LiquidBounce.isLoadingConfig = true
        // Scripts
        LiquidBounce.scriptManager.disableScripts()
        LiquidBounce.scriptManager.unloadScripts()
        try {
            Remapper.loadSrg()
            LiquidBounce.scriptManager = ScriptManager()
            LiquidBounce.scriptManager.loadScripts()
            LiquidBounce.scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.logError("Failed to load scripts.", throwable)
        }
        for (module in LiquidBounce.moduleManager.modules)
            LiquidBounce.moduleManager.generateCommand(module)
        // Fonts
        Fonts.loadFonts()
        LiquidBounce.configManager.load(LiquidBounce.configManager.nowConfig, false)
        KillInsults.loadFile()
        GuiCapeManager.load()
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.accountsConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.friendsConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.xrayConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.hudConfig)
        // Modules
        ColorMixer.regenerateColors(true)
        ClientSettings.reloadMainMenu()
        LiquidBounce.isStarting = false
        LiquidBounce.isLoadingConfig = false
        // Finishing
        chatMessage("§a§lLOAD FINISHED!")
        System.gc()
    }
}