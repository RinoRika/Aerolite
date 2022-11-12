package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura;
import net.ccbluex.liquidbounce.slib.Colors;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.awt.*;

@ModuleInfo(name = "TargetHUD", category = ModuleCategory.RENDER)
public class NewTargetHUD
        extends Module {
    public final ListValue modeValue = new ListValue("Mode", new String[] { "Astro", "AimWhere" }, "Astro");
    KillAura killAura = (KillAura) LiquidBounce.moduleManager.getModule(KillAura.class);

    @EventTarget
    public void onRender2D(Render2DEvent e) {
        EntityLivingBase entityLivingBase = this.killAura.getTarget();
        if (entityLivingBase instanceof EntityLivingBase &&
                entityLivingBase != null && !((Entity)entityLivingBase).isDead && entityLivingBase instanceof net.minecraft.entity.player.EntityPlayer && mc.thePlayer.getDistanceToEntity((Entity)entityLivingBase) < 8.0F) {
            double hpPercentage = (entityLivingBase.getHealth() / entityLivingBase.getMaxHealth());
            ScaledResolution scaledRes = new ScaledResolution(mc);
            float scaledWidth = scaledRes.getScaledWidth();
            float scaledHeight = scaledRes.getScaledHeight();
            if (hpPercentage > 1.0D) {
                hpPercentage = 1.0D;
            } else if (hpPercentage < 0.0D) {
                hpPercentage = 0.0D;
            }
            RenderUtils.drawBorderedRect((scaledWidth / 2.0F - 200.0F), (scaledHeight / 2.0F - 42.0F), (scaledWidth / 2.0F - 200.0F + 40.0F), (scaledHeight / 2.0F - 2.0F), 1.0F, Colors.getColor(0, 0), Colors.getColor(0, 0));
            RenderUtils.drawRect(scaledWidth / 2.0F - 200.0F, scaledHeight / 2.0F - 42.0F, scaledWidth / 2.0F - 200.0F + 40.0F + ((mc.fontRendererObj.getStringWidth(entityLivingBase.getName()) > 105) ? (mc.fontRendererObj.getStringWidth(entityLivingBase.getName()) - 10) : 105), scaledHeight / 2.0F - 2.0F, (new Color(34, 34, 34, 150)).getRGB());
            mc.fontRendererObj.drawStringWithShadow(entityLivingBase.getName(), scaledWidth / 2.0F - 196.0F + 40.0F, scaledHeight / 2.0F - 36.0F, -1);
            RenderUtils.drawRect((scaledWidth / 2.0F - 196.0F + 40.0F), (scaledHeight / 2.0F - 26.0F), (float) ((scaledWidth / 2.0F - 196.0F + 40.0F) + 87.5D), (scaledHeight / 2.0F - 14.0F), (new Color(55, 55, 55)).getRGB());
            RenderUtils.drawRect((scaledWidth / 2.0F - 196.0F + 40.0F), (scaledHeight / 2.0F - 26.0F), (float) ((scaledWidth / 2.0F - 196.0F + 40.0F) + hpPercentage * 1.25D * 70.0D), (scaledHeight / 2.0F - 14.0F), Colors.getHealthColor(entityLivingBase).getRGB());
            mc.fontRendererObj.drawStringWithShadow(String.format("%.1f", new Object[] { Float.valueOf(entityLivingBase.getHealth()) }), scaledWidth / 2.0F - 196.0F + 40.0F + 36.0F, scaledHeight / 2.0F - 23.0F, Colors.getHealthColor(entityLivingBase).getRGB());
            mc.fontRendererObj.drawStringWithShadow("Distance: \u00A77" + (int)mc.thePlayer.getDistanceToEntity((Entity)entityLivingBase) + "m", scaledWidth / 2.0F - 196.0F + 40.0F, scaledHeight / 2.0F - 12.0F, -1);
        }
    }
}