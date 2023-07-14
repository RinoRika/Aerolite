package net.ccbluex.liquidbounce.features.module.modules.addit

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraftforge.fml.common.gameevent.TickEvent
import org.lwjgl.opengl.Display

@ModuleInfo(name = "CustomTitle", ModuleCategory.ADDIT)
object CustomTitle : Module() {
    val titleValue: TextValue = object : TextValue("Title", "Liquidbounce b73") {
        override fun onChanged(oldValue: String, newValue: String) {
            Display.setTitle(newValue)
        }
    }

    var wasRestarted = false

    override fun onEnable() {
        Display.setTitle(titleValue.get())
    }

    override fun onDisable() {
        ClientUtils.setClientTitle()
    }

    fun onTick(event: TickEvent) {
        if (!wasRestarted) {
            Display.setTitle(titleValue.get())
            wasRestarted = true
        }
    }
}