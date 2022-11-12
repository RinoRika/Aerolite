package net.ccbluex.liquidbounce.features.module.modules.misc

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock

@ModuleInfo(name = "MurderDetector", category = ModuleCategory.MISC)
class MurderDetector : Module() {
    override fun onEnable() {
        killerData.clear()
    }

    class KillerData {
        var playerName = ""
    }

    private var itemIds = intArrayOf(288, 396, 412, 398, 75, 50)
    private var itemTypes = arrayOf(
        Items.fishing_rod,
        Items.diamond_hoe,
        Items.golden_hoe,
        Items.iron_hoe,
        Items.stone_hoe,
        Items.wooden_hoe,
        Items.stone_sword,
        Items.diamond_sword,
        Items.golden_sword,
        ItemBlock.getItemFromBlock(Blocks.sponge),
        Items.iron_sword,
        Items.wooden_sword,
        Items.diamond_axe,
        Items.golden_axe,
        Items.iron_axe,
        Items.stone_axe,
        Items.diamond_pickaxe,
        Items.wooden_axe,
        Items.golden_pickaxe,
        Items.iron_pickaxe,
        Items.stone_pickaxe,
        Items.wooden_pickaxe,
        Items.stone_shovel,
        Items.diamond_shovel,
        Items.golden_shovel,
        Items.iron_shovel,
        Items.wooden_shovel
    )
    private var killerData = HashMap<EntityPlayer, KillerData?>()

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        for (entity in mc.theWorld.loadedEntityList) {
            if (entity is EntityLivingBase) {
                if (entity is EntityPlayer) {
                    if (entity.inventory.getCurrentItem() != null) {
                        if (killerData[entity] == null) {
                            if (isWeapon(entity.inventory.getCurrentItem().item)) {
                                LiquidBounce.hud.addNotification(
                                    Notification(
                                        this.name,
                                        "${entity.name} is Murderer!",
                                        NotifyType.WARNING,
                                        4000,
                                        500
                                    )
                                )
                            }
                            if (killerData[entity] == null) killerData[entity] =
                                KillerData()
                        }
                    } else {
                        if (!isWeapon(entity.inventory.getCurrentItem().item)) {
                            killerData.remove(entity)
                        }
                    }
                }
            }
        }
    }

    private fun isWeapon(item: Item): Boolean {
        for (id in itemIds) {
            val itemId = Item.getItemById(id)
            //ClientUtils.INSTANCE.displayChatMessage(itemId+":"+item);
            if (item === itemId) {
                return true
            }
        }
        for (id in itemTypes) {
            if (item === id) {
                return true
            }
        }
        return false
    }
}