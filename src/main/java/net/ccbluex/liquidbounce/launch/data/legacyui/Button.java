package net.ccbluex.liquidbounce.launch.data.legacyui;

import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.GuiMainMenuLLL;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;

import java.awt.*;
public class Button {
    public GuiMainMenuLLL parent;
    public String icon;
    public String text;
    public Executor action;
    public int buttonID;
    public float x;
    public float y;
    public float textOffset;
    public float yAnimation = 0.0F;

    public Button(GuiMainMenuLLL parent, int id, String icon, String text, Executor action) {
        this.parent = parent;
        this.buttonID = id;
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.textOffset = 0.0F;
    }

    public Button(GuiMainMenuLLL parent, int id, String icon, String text, Executor action, float yOffset) {
        this.parent = parent;
        this.buttonID = id;
        this.icon = icon;
        this.text = text;
        this.action = action;
        this.textOffset = yOffset;
    }

    public void draw(float x, float y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        RenderUtils.drawRoundedCornerRect(x-30F,y-30F,x+30f,y+30f,15f,new Color(0,0,0,40).getRGB());
    //    BlurUtils.INSTANCE.draw(x-40F,y-40F,80f,80f,20f);
        this.yAnimation = RenderUtils.smoothAnimation(this.yAnimation, RenderUtils.isHovering(mouseX, mouseY, this.x-30f, this.y-30f, this.x + 30.0F, this.y + 30.0F) ? 4.0F : 0.0F, 50.0F, 0.3F);
        Fonts.mainmenu.drawString(this.icon, x - (float) Fonts.mainmenu.getStringWidth(this.icon) / 2.0F, y-6f+(this.yAnimation*-1f), Color.WHITE.getRGB(),false);
        if(this.yAnimation>=0.11) {
            Fonts.gs15.drawString(this.text, x - (float) Fonts.gs15.getStringWidth(this.text) / 2.0F, y + 12f +(this.yAnimation*-1f), new Color(255,255,255, ((((this.yAnimation/4.0f)) * 254.0f * 1f)<=255.0f) ? (int)(((this.yAnimation/4.0f)) * 254.0f + 1f) : 25).getRGB());
        }//RenderUtils.drawGradientRect(x, y + 40.0F - this.yAnimation * 3.0F, x + 50.0F, y + 40.0F, 3453695, 2016719615);
   //     RenderUtils.fastRoundedRect(x-31F,y-31F,x+31f,y+31f,15f, ColorUtils.INSTANCE.rainbow().getRGB());
        RenderUtils.drawRoundedCornerRect(x-30F,y-30F,x+30f,y+30f,15f,new Color(255,255,255,50).getRGB());
    }

    public void mouseClick(int mouseX, int mouseY, int mouseButton) {
        if (RenderUtils.isHovering(mouseX, mouseY, this.x-30f, this.y-30f, this.x + 30.0F, this.y + 30.0F) && this.action != null && mouseButton == 0) {
            this.action.execute();
        }

    }

    public interface Executor {
        void execute();
    }
}