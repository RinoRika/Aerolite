//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite.settings;

import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite.Downward;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite.AeroGui;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite.ModuleRender;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.aerolite.RenderUtil;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;


public class StringsSetting extends Downward<ListValue> {
    public int mainx;
    public int mainy;
    public int y;
    private double length = 3.0;
	boolean previousmouse = true;
    private double anim = 5.0;

    public StringsSetting(ListValue s, float x, float y, int width, int height, ModuleRender moduleRender) {
        super(s, x, y, width, height, moduleRender);
    }

    public void draw(int mouseX, int mouseY) {
    	  this.mainx = AeroGui.mainx;
          this.mainy = AeroGui.mainy;
        this.y = (int)(this.pos.y + (float)this.getScrollY());
        Fonts.NL18.drawString(((ListValue)this.setting).getName(), (float)(this.mainx + 17) + this.pos.x, (float)(this.mainy + 38 + this.y),new Color(130,140,150).getRGB());
        RenderUtil.drawRect((float)(this.mainx + 91) + this.pos.x, (float)(this.mainy + 35 + this.y),(float)(this.mainx + 91) + this.pos.x+ 70.0F, (float)(this.mainy + 35 + this.y)+ 12.0F,  new Color(5,23,37).getRGB());
        Fonts.NL16.drawString(((ListValue)this.setting).get().toString(), (float)(this.mainx + 93) + this.pos.x, (float)(this.mainy + 38 + this.y), new Color(130,140,150).getRGB());
        double val = (double)Minecraft.getDebugFPS() / 8.3;
        if (((ListValue)this.setting).openList && this.length > -3.0) {
            this.length -= 3.0 / val;
        } else if (!((ListValue)this.setting).openList && this.length < 3.0) {
            this.length += 3.0 / val;
        }

        if (((ListValue)this.setting).openList && this.anim < 8.0) {
            this.anim += 3.0 / val;
        } else if (!((ListValue)this.setting).openList && this.anim > 5.0) {
            this.anim -= 3.0 / val;
        }

        if (((ListValue)this.setting).openList) {
            GL11.glTranslatef(0.0F, 0.0F, 2.0F);
            RenderUtil.drawRect((float)(this.mainx + 91) + this.pos.x, (float)(this.mainy + 35 + 12 + this.y),(float)(this.mainx + 91) + this.pos.x+ 70.0F,(float)(this.mainy + 35 + 12 + this.y)+ (float)((ListValue)this.setting).getModes().size() * 12.0F, new Color(5,23,37).getRGB());
            List<String> var5 = ((ListValue)this.setting).getModes();
            int var6 = var5.size();

            for (String option : var5) {
                if (option.equals(this.setting.get())) {
                    RenderUtil.drawRect((float) (this.mainx + 91 + 69) + this.pos.x, (float) (this.mainy + 38 + 11 + this.y + ((ListValue) this.setting).getModeListNumber(String.valueOf(option)) * 12), (float) (this.mainx + 91 + 69) + this.pos.x + 1.0F, (float) (this.mainy + 38 + 11 + this.y + ((ListValue) this.setting).getModeListNumber(String.valueOf(option)) * 12) + 8.0F, new Color(5, 166, 238).getRGB());
                }

                Fonts.NL16.drawString(option, (float) (this.mainx + 93) + this.pos.x, (float) (this.mainy + 38 + 12 + this.y + ((ListValue) this.setting).getModeListNumber(String.valueOf(option)) * 12), new Color(130, 140, 150).getRGB());
                GlStateManager.scale(1.0F, 1.0F, 1.0F);
            }

            GL11.glTranslatef(0.0F, 0.0F, -2.0F);
        }

    }
    
    

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && RenderUtil.isHovering((float)(this.mainx + 91) + this.pos.x, (float)(this.mainy + 35 + this.y), 70.0F, 12.0F, mouseX, mouseY)) {
            ((ListValue)this.setting).openList = !((ListValue)this.setting).openList;
        }

        if (mouseButton == 0 && ((ListValue)this.setting).openList && (float)mouseX >= (float)(this.mainx + 91) + this.pos.x && (float)mouseX <= (float)(this.mainx + 91) + this.pos.x + 70.0F) {
            for(int i = 0; i < ((ListValue)this.setting).getModes().size(); ++i) {
                int v = this.mainy + 38 + 12 + this.y + i * 12;
                if (mouseY >= v && mouseY <= v + 12) {
                    ((ListValue)this.setting).set(((ListValue)this.setting).getModeGet(i));
                }
            }
        }

    }
	public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
		if (mouseX >= f && mouseX <= g && mouseY >= y && mouseY <= y2) {
			return true;
		}

		return false;
	}

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }
}
