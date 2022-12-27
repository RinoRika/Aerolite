package net.ccbluex.liquidbounce.features.module.modules.addit

import io.netty.util.internal.ThreadLocalRandom
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.PacketUtils
import net.minecraft.network.play.client.C00PacketKeepAlive
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S00PacketKeepAlive
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

@ModuleInfo(name = "HypDisabler3", category = ModuleCategory.ADDIT)
class HypDisabler3 : Module() {
    var coo = -1
    override fun onDisable() {
        while (!map2de.isEmpty()) {
            val p = map2de.poll()
            mc.netHandler.networkManager.sendPacket(p)
        }
        map2.forEach { (key: C00PacketKeepAlive?, _: Long?) ->
            mc.netHandler.networkManager.sendPacket(key)
            map2.remove(key)
        }
    }

    @EventTarget
    fun onWorld(e: WorldEvent) {
        map2 = LinkedHashMap()
        map2de.clear()
        coo = -1
        switcher.set(false)
    }

    @EventTarget
    fun onMotion(e: MotionEvent) {
        if (e.eventState === EventState.PRE) {
            if (!map2de.isEmpty() && mc.thePlayer.ticksExisted % ThreadLocalRandom.current().nextInt(4, 6) == 0) {
                val p = map2de.poll()
                mc.netHandler.networkManager.sendPacket(p)
            }
        }
    }

    @EventTarget
    fun onTick(e: TickEvent) {
        map2.entries.stream()
            .filter { entry: Map.Entry<C00PacketKeepAlive, Long> -> entry.value <= System.currentTimeMillis() }
            .forEachOrdered { entry: Map.Entry<C00PacketKeepAlive, Long> ->
                mc.netHandler.networkManager.sendPacket(entry.key)
                map2.remove(entry.key)
            }
    }

    fun C0F_Send(e: PacketEvent) {
        if (e.packet is C0FPacketConfirmTransaction) {
            val packet = e.packet
            if (packet.windowId != 0) choose = packet.windowId
            if (packet.windowId == 0 && mc.thePlayer != null && mc.theWorld != null) {
                if (switcher.get()) {
                    if (packet.uid < 0) {
                        if (ThreadLocalRandom.current().nextBoolean()) {
                            e.cancelEvent()
                            PacketUtils.sendPacketNoEvent(C0FPacketConfirmTransaction(choose, abs(packet.uid.toInt()).toShort(), false))
                        }
                        map2de.offer(packet)
                    } else {
                        mc.thePlayer.sendQueue.addToSendQueue(packet)
                    }
                    e.cancelEvent()
                }
                if (abs(packet.getUid().toInt()) - abs(coo) == 1) switcher.set(true)
                coo = packet.getUid().toInt()
            }
        }
    }

    fun S00_RECE(e: PacketEvent) {
        if (e.packet is S00PacketKeepAlive) {
            map2[C00PacketKeepAlive(e.packet.func_149134_c())] =
                System.currentTimeMillis() + 210L + ThreadLocalRandom.current().nextInt(8, 10)
            e.cancelEvent()
        }
    }

    fun PAUSE(e: PacketEvent) {
        if (e.packet is S08PacketPlayerPosLook) {
            if (!map2de.isEmpty()) {
                while (!map2de.isEmpty()) {
                    val p = map2de.poll()
                    mc.netHandler.networkManager.sendPacket(p)
                }
            }
        }
    }

    @EventTarget
    fun onPacket(e: PacketEvent) {
        if (e.packet is C0FPacketConfirmTransaction) {
            C0F_Send(e)
        }
        if (e.packet is S00PacketKeepAlive) {
            S00_RECE(e)
        } else if (e.packet is S08PacketPlayerPosLook) {
            PAUSE(e)
        }
    }

    override val tag: String
        get() = "" + map2de.size

    companion object {
        var map2de = LinkedList<C0FPacketConfirmTransaction>()
        var map2 = LinkedHashMap<C00PacketKeepAlive, Long>()
        var switcher = AtomicBoolean()
        var choose = 2
    }
}