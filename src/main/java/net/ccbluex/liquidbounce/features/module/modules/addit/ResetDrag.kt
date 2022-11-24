package net.ccbluex.liquidbounce.features.module.modules.addit

import net.ccbluex.liquidbounce.cn.Fonts.newdropdown.DropdownClickGui
import net.ccbluex.liquidbounce.cn.Fonts.newdropdown.MainScreen
import net.ccbluex.liquidbounce.cn.Fonts.newdropdown.utils.normal.Main
import net.ccbluex.liquidbounce.cn.Fonts.newdropdown.utils.normal.Screen
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer

@ModuleInfo("ResetDrag", ModuleCategory.ADDIT)
class ResetDrag : Module() {
    val a = MSTimer()
    override fun onEnable() {
        Main.categoryCount = 0
        DropdownClickGui.categoryPanels = null
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (a.hasTimePassed(1000L)) {
            ClientUtils.logInfo(Main.categoryCount.toString())
            a.reset()
        }
    }
}