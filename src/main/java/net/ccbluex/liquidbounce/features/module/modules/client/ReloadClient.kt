package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.features.module.FastManager
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "ReloadClient", category = ModuleCategory.CLIENT)
class ReloadClient : Module() {
    override fun onEnable() {
        this.state = false
        FastManager.fastReload()
    }
}