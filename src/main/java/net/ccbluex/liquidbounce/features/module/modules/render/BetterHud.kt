package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.Minecraft
import net.minecraft.util.ResourceLocation
import java.awt.Color

@ModuleInfo(name = "BetterHud", category = ModuleCategory.RENDER)
class BetterHud : Module() {
    private val modeValue = ListValue("bar", arrayOf("Black","Lite", "Off"), "Off")
    private val fontvalue = ListValue("Info", arrayOf("Info", "Off"),"Off")
    private val girlvalue = ListValue("girl", arrayOf("Crygirl","Happygirl","Bluecat", "Liuli", "Off"),"Off")
    private val markvalue = ListValue("client", arrayOf("mark1","mark2", "Off"),"Off")
    private val offsetValue = IntegerValue("Y-Offset", 0, -50, 100)


    private var Black = ResourceLocation("cat/Packet/hud/Betterhud1.png")
    private var Lite = ResourceLocation("cat/Packet/hud/Betterhud2.png")
    private var crygirl = ResourceLocation("cat/Packet/girls/crygirl.png")
    private var happygirl = ResourceLocation("cat/Packet/girls/happygirl.png")
    private var bluecat = ResourceLocation("cat/Packet/girls/bluecat.png")
    private var liuli = ResourceLocation("cat/Packet/girls/liuli.png")
    private var watermark = ResourceLocation("cat/Packet/mark/mark1.png")
    private var watermark2 = ResourceLocation("cat/Packet/mark/mark2.png")




    @EventTarget
    fun onRender(event: Render2DEvent) {
        when (modeValue.get()) {
            "Black" -> drawhud(Black)
            "Lite" -> drawhud(Lite)
        }
        when(fontvalue.get()) {
            "Info" -> fonts()
        }
        when(girlvalue.get()) {
            "Crygirl" -> drawgirl(crygirl)
            "Happygirl" -> drawgirl(happygirl)
            "Bluecat" -> drawgirl(bluecat)
            "Liuli" -> drawgirl(liuli)
        }
        when(markvalue.get()){
            "mark1" -> drawmark(watermark)
            "mark2" -> drawmark(watermark2)
        }
    }

    private fun fonts() {
        Fonts.font40.drawString("FPS:" + Minecraft.getDebugFPS(),20F,495F + offsetValue.get(),-1)
        Fonts.icon30.drawString("k",7F,496F + offsetValue.get(), Color(11,143,180).rgb)

        Fonts.font40.drawString("Dev:" + LiquidBounce.CLIENT_DEV,143F,495F + offsetValue.get(),-1)
        Fonts.icon30.drawString("k",130F,496F + offsetValue.get(), Color(11,143,180).rgb)

        Fonts.font40.drawString("Build:" + LiquidBounce.BUILD_CODE,270F,495F + offsetValue.get(),-1)
        Fonts.icon30.drawString("k",257F,496F + offsetValue.get(), Color(11,143,180).rgb)

        Fonts.font40.drawString("ServerIP:" + ServerUtils.getRemoteIp(),780F,495F + offsetValue.get(),-1)
        Fonts.icon30.drawString("k",767F,496F + offsetValue.get(), Color(11,143,180).rgb)

    }

    fun drawhud(resource: ResourceLocation) = RenderUtils.drawImage(resource,-8,487 + offsetValue.get(),1920,45);
    fun drawgirl(resource: ResourceLocation) = RenderUtils.drawImage(resource,570,415 + offsetValue.get(),100,100);
    fun drawmark(resource: ResourceLocation) = RenderUtils.drawImage(resource,15,8 + offsetValue.get(),131,40);

}

