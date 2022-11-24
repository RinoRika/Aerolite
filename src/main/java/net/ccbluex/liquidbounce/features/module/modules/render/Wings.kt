package net.ccbluex.liquidbounce.features.module.modules.render

import me.stars.utils.WingUtils
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render3DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "Wings", category = ModuleCategory.RENDER)
class Wings : Module() {
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        val renderWings = WingUtils()
        renderWings.renderWings(event.partialTicks)
    }
}