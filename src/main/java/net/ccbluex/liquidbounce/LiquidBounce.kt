/*
   AeroLite Hack Client - Best Liquid-bounce
   Made By Stars
 */
package net.ccbluex.liquidbounce

import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.macro.MacroManager
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.module.modules.addit.CustomTitle
import net.ccbluex.liquidbounce.features.module.modules.client.ClientSettings
import net.ccbluex.liquidbounce.features.module.modules.render.LiquidBouncePlus.ColorMixer
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.CombatManager
import net.ccbluex.liquidbounce.features.special.CoreManager
import net.ccbluex.liquidbounce.features.special.ServerSpoof
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.file.config.ConfigManager
import net.ccbluex.liquidbounce.launch.EnumLaunchFilter
import net.ccbluex.liquidbounce.launch.LaunchFilterInfo
import net.ccbluex.liquidbounce.launch.LaunchOption
import net.ccbluex.liquidbounce.launch.data.uichoser
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper
import net.ccbluex.liquidbounce.slib.hhc.IResourceLocation
import net.ccbluex.liquidbounce.ui.cape.GuiCapeManager
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.keybind.KeyBindManager
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.ui.font.FontsGC
import net.ccbluex.liquidbounce.ui.i18n.LanguageManager
import net.ccbluex.liquidbounce.ui.sound.TipSoundManager
import net.ccbluex.liquidbounce.utils.ClassUtils
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import oh.yalan.NativeClass
import org.lwjgl.opengl.Display
import javax.swing.JOptionPane

@NativeClass
object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "Aerolite"
    const val COLORED_NAME = "§b§lAerolite"
    const val BUILD_CODE = "4.0.0"                       //更完改
    const val CLIENT_REAL_VERSION = "4.0.0"                            //更完改
    const val CLIENT_CREATOR = "AeroTeam"
    const val CLIENT_WEBSITE = "Aerolite.tk"
    const val MINECRAFT_VERSION = "1.8.9"
    const val CLIENT_DEV = "Stars&Packet&Crazy&Ry4nnnnn"
    const val DEV_SAYING = "新年快乐"

    // Tasks
    var FinishChoosingScreen = false

    var isStarting = true
    var isLoadingConfig = true

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager
    lateinit var scriptManager: ScriptManager
    lateinit var tipSoundManager: TipSoundManager
    lateinit var combatManager: CombatManager
    lateinit var macroManager: MacroManager
    lateinit var configManager: ConfigManager
    lateinit var coreManager: CoreManager
    var category: ModuleCategory? = null
    var module: Module? =null

    // Some UI things
    lateinit var hud: HUD
    lateinit var mainMenu: GuiScreen
    lateinit var keyBindManager: KeyBindManager

    // Menu Background
    var background: IResourceLocation? = null

    val launchFilters = mutableListOf<EnumLaunchFilter>()
    private val dynamicLaunchOptions: Array<LaunchOption>
        get() = ClassUtils.resolvePackage(
            "${LaunchOption::class.java.`package`.name}.options",
            LaunchOption::class.java
        )
            .filter {
                val annotation = it.getDeclaredAnnotation(LaunchFilterInfo::class.java)
                if (annotation != null) {
                    return@filter annotation.filters.toMutableList() == launchFilters
                }
                false
            }
            .map {
                try {
                    it.newInstance()
                } catch (e: IllegalAccessException) {
                    ClassUtils.getObjectInstance(it) as LaunchOption
                }
            }.toTypedArray()


    /**
     * Execute if client will be started
     */

    fun initClient() {
        ClientUtils.logInfo("Loading $CLIENT_NAME Version $CLIENT_REAL_VERSION By $CLIENT_CREATOR")
        Display.setTitle("正在加载 $CLIENT_NAME $CLIENT_REAL_VERSION...")
        val startTime = System.currentTimeMillis()
        // Create file manager
        fileManager = FileManager()
        configManager = ConfigManager()
        Display.setTitle("开启文件管理器...")
        // Create event manager
        eventManager = EventManager()
        Display.setTitle("加载事件系统...")
        // Load language
        LanguageManager.switchLanguage(Minecraft.getMinecraft().gameSettings.language)
        Display.setTitle("加载语言...")
        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge)
        eventManager.registerListener(InventoryUtils)
        eventManager.registerListener(ServerSpoof)
        // Create command manager
        commandManager = CommandManager()
        fileManager.loadConfigs(fileManager.accountsConfig, fileManager.friendsConfig, fileManager.specialConfig)
        Display.setTitle("设置配置...")
        // Load client fonts
        Fonts.loadFonts()
        eventManager.registerListener(FontsGC)
        Display.setTitle("加载字体...")
        macroManager = MacroManager()
        eventManager.registerListener(macroManager)
        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        Display.setTitle("加载披风...")

        try {
            Remapper.loadSrg()
            // ScriptManager, Remapper will be lazy loaded when scripts are enabled
            Display.setTitle("加载脚本...")
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.logError("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        tipSoundManager = TipSoundManager()

        // KeyBindManager
        keyBindManager = KeyBindManager()

        // bstats.org user count display
        ClientUtils.buildMetrics()

        combatManager = CombatManager()
        eventManager.registerListener(combatManager)
        coreManager = CoreManager()
        eventManager.registerListener(coreManager)

        GuiCapeManager.load()

        mainMenu = uichoser()

        // Set HUD
        hud = HUD.createDefault()

        fileManager.loadConfigs(fileManager.hudConfig, fileManager.xrayConfig)
        Display.setTitle("加载完成!")

        ClientUtils.logInfo("$CLIENT_NAME $CLIENT_REAL_VERSION loaded in ${(System.currentTimeMillis() - startTime)}ms!")
    }

    /**
     * Execute if client ui type is selected
     */

    fun startClient() {
        dynamicLaunchOptions.forEach {
            it.start()
        }

        // Load configs
        configManager.loadLegacySupport()
        configManager.loadConfigSet()

        // Set is starting status
        isStarting = false
        isLoadingConfig = false

        // Modules
        ClientSettings.state = true
        ColorMixer.regenerateColors(true)

        ClientUtils.logInfo("$CLIENT_NAME $CLIENT_REAL_VERSION started!")
        FinishChoosingScreen = true
        if (CustomTitle.state) Display.setTitle(CustomTitle.titleValue.get())
        else ClientUtils.finishTitle()
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        if (!isStarting && !isLoadingConfig) {
            Display.setTitle("$CLIENT_NAME Shut down!")
            ClientUtils.logInfo("Closing $CLIENT_NAME $CLIENT_REAL_VERSION!")

            // Call client shutdown
            eventManager.callEvent(ClientShutdownEvent())

            // Save all available configs
            GuiCapeManager.save()
            configManager.save(true, true)
            fileManager.saveAllConfigs()

            dynamicLaunchOptions.forEach {
                it.stop()
            }
        }
    }
}