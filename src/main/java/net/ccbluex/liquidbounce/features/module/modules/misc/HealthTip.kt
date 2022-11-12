package net.ccbluex.liquidbounce.features.module.modules.misc;
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.entity.EntityLivingBase
import kotlin.math.abs


@ModuleInfo(
    name = "HealthTip",
    category = ModuleCategory.RENDER
)
class HealthTip : Module() {
    private val timer = MSTimer()
    private val healthData=HashMap<Int,Float>()
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        for(entity in mc.theWorld.loadedEntityList){
                if(entity is EntityLivingBase && EntityUtils.isSelected(entity,true)){
                    val lastHealth=healthData.getOrDefault(mc.thePlayer.entityId,mc.thePlayer.maxHealth)
                      healthData[mc.thePlayer.entityId] = mc.thePlayer.health
                      if(lastHealth==mc.thePlayer.health) continue
                      if(lastHealth>mc.thePlayer.health){
                          ClientUtils.displayChatMessage("§c扣除血量§a"+(lastHealth-mc.thePlayer.health)+"HP"+" §f| "+"§c当前血量§a"+mc.thePlayer.health+"HP")
                      }else{
                          ClientUtils.displayChatMessage("§c增加血量§a"+(abs(lastHealth-mc.thePlayer.health))+"HP"+" §f| "+"§c当前血量§a"+mc.thePlayer.health+"HP")
                }
            }
        }

            if (timer.hasTimePassed(220)) {
                if(mc.thePlayer.health<10f)
                    ClientUtils.displayChatMessage("§c[Warning]§6您当前血量小于10§cHP")
                }
                timer.reset()
    }
}

