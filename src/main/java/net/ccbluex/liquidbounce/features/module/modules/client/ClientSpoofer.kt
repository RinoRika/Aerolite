package net.ccbluex.liquidbounce.features.module.modules.client

import io.netty.buffer.Unpooled
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.C17PacketCustomPayload

@ModuleInfo("ClientSpoofer", ModuleCategory.CLIENT)
class ClientSpoofer : Module() {
    private val clientMode = ListValue("Client", arrayOf("Forge", "Lunar", "LabyMod", "PVPLounge", "CB", "Geyser"), "Forge")

    override fun onEnable() {
        ClientUtils.displayChatMessage("[ClientSpoofer] Rejoin the server to active!")
    }

    fun onPacket(event: PacketEvent) {
        val p = event.packet
        if (p is C17PacketCustomPayload) {
            when (clientMode.get().lowercase()) {
                "forge" -> {
                    p.data = createPacketBuffer("FML", true)
                }
                "lunar" -> {
                    p.data = PacketBuffer(Unpooled.buffer()).writeString("LunarClient;1.8.9;VAUYSDF7AS63DSJK1");
                }
                "labymod" -> {
                    p.data = createPacketBuffer("LMC", true)
                }
                "pvplounge" -> {
                    p.data = createPacketBuffer("PLC18", false)
                }
                "cb" -> {
                    p.data = createPacketBuffer("CB", true)
                }
                "geyser" -> {
                    p.data = createPacketBuffer("Geyser", false)
                }
            }
        }
    }

    private fun createPacketBuffer(data: String, string: Boolean): PacketBuffer? {
        return if (string) PacketBuffer(Unpooled.buffer()).writeString(data) else PacketBuffer(
            Unpooled.wrappedBuffer(
                data.toByteArray()
            )
        )
    }

    override val tag: String
        get() = clientMode.get()
}