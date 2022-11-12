package net.ccbluex.liquidbounce.features.special

import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.network.play.server.S02PacketChat

class CoreManager : Listenable, MinecraftInstance() {
    var kills = 0
    var finalKills = 0

    fun getPlayerName(): String? {
        return if (mc.thePlayer == null) null else mc.thePlayer.name
    }

    fun getDead(): Boolean? {
        return if (mc.thePlayer == null) null else mc.thePlayer.isDead
    }

    fun getFinalKill(e: PacketEvent, lang: Lang): Int {
        val p = e.getPacket()
        if (p is S02PacketChat && p.chatComponent.unformattedText != null) {
            val text = p.chatComponent.unformattedText
            when (lang) {
                Lang.CHINESE -> {
                    if (text.contains("最终击杀") && text.contains(mc.session.username, true))
                        finalKills++
                }
                Lang.ENGLISH -> {
                    if (text.contains("final", true) && text.contains("kill", true) && text.contains(mc.session.username, true))
                        finalKills++
                }
                Lang.SPANISH -> {
                    if (text.contains("final", true) && text.contains(mc.session.username, true))
                        finalKills++
                }
            }
        }
        return finalKills
    }

    fun getKill(e: PacketEvent): Int {
        val p = e.getPacket()
        if (p is S02PacketChat && p.chatComponent.unformattedText != null) {
            val text = p.chatComponent.unformattedText
            if (text.contains(mc.session.username, true) && (
                        text.contains("杀") || text.contains("被") || text.contains("扔") || text.contains("死") ||
                                text.contains("Kill", true) || text.contains("Slain", true) || text.contains("Eliminate", true)))
                kills++
        }
        return kills
    }

    override fun handleEvents() = true
}

enum class Lang {
    CHINESE,
    ENGLISH,
    SPANISH
}