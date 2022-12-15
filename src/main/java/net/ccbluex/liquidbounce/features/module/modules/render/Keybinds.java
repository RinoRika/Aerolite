package net.ccbluex.liquidbounce.features.module.modules.render;

import java.awt.Color;
import java.util.ArrayList;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.cn.Fonts.Module.fonts.impl.Fonts;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.modules.client.HUD;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.VisualBase;
import net.ccbluex.liquidbounce.value.FloatValue;
import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "Keybinds", category = ModuleCategory.RENDER)
public class Keybinds extends Module {
    public final FloatValue IndicatorX = new FloatValue("PositionX", 360, 0, 500);
    public final FloatValue IndicatorY = new FloatValue("PositionY", 150, 0, 170);

    @EventTarget
    public void biba(Render2DEvent event) {

        ScaledResolution sr = new ScaledResolution(mc);
        final float scaledWidth = sr.getScaledWidth();
        final float scaledHeight = sr.getScaledHeight();

        float x = IndicatorX.get();
        float y = IndicatorY.get();

        VisualBase.drawGradientRect(x, y, x + 95, y + 10, new Color(11, 11, 11, 255).getRGB(),
                new Color(11, 11, 11, 255).getRGB());
        VisualBase.drawGradientSideways(x, y, x + 95, y + 1, ColorUtils.INSTANCE.rainbow().getRGB(),
                new Color(255, 255, 255, 220).getRGB());
        Fonts.SF.SF_14.SF_14.drawString("binds", x + 40, y + 4, -1, true);
        double offsetY = y + 3;

        for (final Module f : LiquidBounce.moduleManager.getModules()) {
            if (f.getState() && f.getKeyBind() != 0) {
                Fonts.SF.SF_15.SF_15.drawString(f.getName().toLowerCase(), x + 2, offsetY + 10, -1, true);
                Fonts.SF.SF_15.SF_15.drawString("toggled",
                        x + 92 - Fonts.SF.SF_14.SF_14.stringWidth("toggled"), offsetY + 10, -1, true);
                offsetY += 8;
            }
        }

    }
}

