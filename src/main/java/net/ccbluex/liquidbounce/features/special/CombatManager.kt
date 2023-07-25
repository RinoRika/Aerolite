package net.ccbluex.liquidbounce.features.special

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.network.play.server.S02PacketChat




class CombatManager : Listenable, MinecraftInstance() {
    private val lastAttackTimer = MSTimer()
    private val shitTimer = MSTimer()

    var inCombat = false
        private set
    private var killCounts: Long = 0
    private var win: Long = 0
    private var totalPlayed: Long = 0
    var target: EntityLivingBase? = null
        private set
    val attackedEntityList = mutableListOf<EntityLivingBase>()
    val focusedPlayerList = mutableListOf<EntityPlayer>()

    private val hackerWords = arrayOf("hack", "cheat", "bhop", "fly", "nokb", "antikb")

  @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (mc.thePlayer == null) return
        MovementUtils.updateBlocksPerSecond()

        inCombat = false

        if (!lastAttackTimer.hasTimePassed(1000)) {
            inCombat = true
            return
        }

        if (target != null) {
            if (mc.thePlayer.getDistanceToEntity(target) > 7 || !inCombat || target!!.isDead) {
                target = null
            } else {
                inCombat = true
            }
        }

        // bypass java.util.ConcurrentModificationException
        attackedEntityList.map { it }.forEach {
            if (it.isDead) {
                killCounts += 1
                LiquidBounce.eventManager.callEvent(EntityKilledEvent(it))
                attackedEntityList.remove(it)
            }
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        val target = event.targetEntity

        if (target is EntityLivingBase && EntityUtils.isSelected(target, true)) {
            this.target = target
            if (!attackedEntityList.contains(target)) {
                attackedEntityList.add(target)
            }
        }
        lastAttackTimer.reset()
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        inCombat = false
        target = null
        attackedEntityList.clear()
        focusedPlayerList.clear()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if(packet is S02PacketChat) {
            val raw = packet.chatComponent.unformattedText
            val found = hackerWords.filter { raw.contains(it, true) }
            if(raw.contains(mc.session.username, true) && found.isNotEmpty()) {
                LiquidBounce.hud.addNotification(Notification("Someone call you a hacker!", found.joinToString(", "), NotifyType.WARNING))
            }
        }
    }

    fun getKillCounts(): Long {
    return killCounts
}
    fun getTotalPlayed(): Long {
        return totalPlayed
    }

    fun getWin(): Long {
        return win
    }


    fun totalplayed(event: PacketEvent) {
        val packet = event.packet
        if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText
            if (text.contains("Cages opened! FIGHT!", true)) {
                totalPlayed += 1
                return
            }
        }
    }

    fun win(event: PacketEvent) {
        val packet = event.packet
        if (packet is S02PacketChat) {
            val text = packet.chatComponent.unformattedText
            if (text.contains("You won! Want to play again? Click here!", true)) {
                win += 1
                return
            }
        }
    }

    fun getNearByEntity(radius: Float): EntityLivingBase? {
        return try {
            mc.theWorld.loadedEntityList
                .filter { mc.thePlayer.getDistanceToEntity(it) < radius && EntityUtils.isSelected(it, true) }
                .sortedBy { it.getDistanceToEntity(mc.thePlayer) }[0] as EntityLivingBase?
        } catch (e: Exception) {
            null
        }
    }

    fun isFocusEntity(entity: EntityPlayer): Boolean {
        if (focusedPlayerList.isEmpty()) {
            return true // no need 2 focus
        }

        return focusedPlayerList.contains(entity)
    }

    override fun handleEvents() = true
}