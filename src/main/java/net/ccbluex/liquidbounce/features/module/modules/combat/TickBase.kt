package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.*
import net.minecraft.entity.EntityLivingBase

@ModuleInfo(name = "TickBase", category = ModuleCategory.COMBAT)
object TickBase : Module() {

    private var ticks = 0

    val ticksAmount = IntegerValue("BoostTicks", 10, 3, 20)
    val boostAmount = FloatValue("BoostTimer", 10f, 1f, 50f)
    val chargeAmount = FloatValue("ChargeTimer", 0.11f, 0.05f, 1f)

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase && ticks == 0) {
            ticks = ticksAmount.get()
        }
    }

    override fun onEnable() {
        mc.timer.timerSpeed = 1f
    }

    override fun onDisable() {
        mc.timer.timerSpeed = 1f
    }


    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (ticks == ticksAmount.get()) {
            mc.timer.timerSpeed = chargeAmount.get()
            ticks --
        } else if (ticks > 1) {
            mc.timer.timerSpeed = boostAmount.get()
            ticks --
        } else if (ticks == 1) {
            mc.timer.timerSpeed = 1f
            ticks --
        }
    }


}