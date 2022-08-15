package net.ccbluex.liquidbounce.features.module.modules.addit

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue

@ModuleInfo("Mixins", ModuleCategory.ADDIT)
class Mixins : Module() {
    val hytC08Fix = BoolValue("Hyt FixC08", false)
}