package net.ccbluex.liquidbounce.features.module.modules.addit

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove
import net.minecraft.network.play.server.S27PacketExplosion
import net.minecraft.network.play.server.S29PacketSoundEffect

@ModuleInfo("AntiCrash", ModuleCategory.ADDIT)
class AntiCrash : Module() {

    private val modeValue = ListValue("Mode", arrayOf("HyCraft","Sound"), "HyCraft")

    fun onPacket(event: PacketEvent) {
        val p = event.packet
        when (modeValue.get().lowercase()) {
            "hycraft" -> {
                if (p is S27PacketExplosion) {
                    if (p.strength > 100 || p.affectedBlockPositions == null) {
                        event.cancelEvent()
                        ClientUtils.displayChatMessage("[AntiCrash] Detected unexpected S27PacketExplosion,cancelled.")
                    } else {
                        ClientUtils.displayChatMessage("[AntiCrash] Detected S27PacketExplosion.")
                    }
                }
            }
            "sound" -> {
                if (p is S29PacketSoundEffect && p.volume > 200.0) {
                    event.cancelEvent()
                    ClientUtils.displayChatMessage("[AntiCrash] Detected S29PacketSoundEffect with high volume,cancelled.")
                }
            }
        }
    }
}