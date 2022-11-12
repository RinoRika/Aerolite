/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.FloatValue2;

@ModuleInfo(name = "ItemPhysics", category = ModuleCategory.RENDER)
public class ItemPhysics extends Module
{
    public final FloatValue2 itemWeight = new FloatValue2("Weight", 0.5F, 0F, 1F, "x");


    @Override
    public String getTag() {
        return itemWeight.get().toString();
    }
}
