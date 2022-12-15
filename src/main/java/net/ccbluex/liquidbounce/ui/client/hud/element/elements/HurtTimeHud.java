package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.VisualBase;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.util.MathHelper;

import java.awt.*;

@ElementInfo(name = "HurtTimeHud")
public class HurtTimeHud extends Element {
    public final ListValue shadowValue = new ListValue("Shadow", new String[]{"None", "Basic", "Thick"}, "None");
    public FloatValue indx = new FloatValue("noting",0.0000001f,0,0.000004f);

    public float animWidth;
    public float animatedCircleEnd;
    float Width = 41.0f;
    float Height = 42.0f;
    int x = 0;
    int y = 0;
    public Float x2 = indx.get();

    @Override
    public Border drawElement(float partialTicks) {
        double hurttimePercentage = MathHelper.clamp_double(mc.thePlayer.hurtTime, 0.0, 0.6);
        hurttimePercentage = MathHelper.clamp_double(hurttimePercentage, 0.0, 1.0);

        final double newWidth = 51 * hurttimePercentage;
        animWidth = (float) animate(newWidth, this.animWidth, 0.0429999852180481);

        VisualBase.drawSmoothRect(this.x, this.y, this.x + 40.0f, this.y + 43.0f, new Color(0,0,0,100).getRGB());
        //drawShadow
        switch (shadowValue.get()) {
            case "Basic":
                RenderUtils.drawShadow(this.x-0.5f, this.y-0.5f, this.x + 40.0f+1f, this.y + 43.0f);
                break;
            case "Thick":
                RenderUtils.drawShadow(this.x-0.5f, this.y-0.5f, this.x + 40.0f+1f, this.y + 43.0f);
                RenderUtils.drawShadow(this.x-0.5f, this.y-0.5f, this.x + 40.0f+1f, this.y + 43.0f);
                break;
        }

        Fonts.font35.drawCenteredString("HURT", x + 20, y + 14 - 10, -1);
        VisualBase.drawCircle(x + 15 + 5, (double)y + 23.5, 11.5, -5.0f, 360.0f, Color.DARK_GRAY.darker().getRGB(), 5.5f);
        float coef = animWidth / 100.0f;
        double scoef = mc.thePlayer.hurtTime;
        this.animatedCircleEnd = coef * 360.0f;
        VisualBase.drawCircle(x + 15 + 5, (double)y + 23.5, 11.5, -5.0f, this.animatedCircleEnd * 2 + x2, new Color(255,255,255).getRGB(), 5.5f);
        Fonts.font30.drawCenteredString(Math.round(scoef) + "s", x + 20, y + 22, -1);
        return new Border(0,0,Width,Height);
    }

}