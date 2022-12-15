//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.skeet;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;


public class TypeScreen {
    private final Position pos;
    private final ModuleCategory type;
    private final float minScroll = 0.0F;
    public int x;
    public int y;
    public int mainx;
    public int mainy;
    public int leftAdd;
    public int rightAdd;
    public ArrayList<ModuleRender> moduleRect;
    private boolean selected;
    private float maxScroll = Float.MAX_VALUE;
    private float rawScroll;
    private float scroll;
    private Animation scrollAnimation;

    public TypeScreen(ModuleCategory category, int x) {
        this.scrollAnimation = new SmoothStepAnimation(0, 0.0, Direction.BACKWARDS);
        this.x = x;
        this.type = category;
        this.pos = new Position(0.0F, 0.0F, 0.0F, 0.0F);
        int count = 0;
        this.moduleRect = new ArrayList();
        Iterator var4 = LiquidBounce.moduleManager.getModules().iterator();

        while(var4.hasNext()) {
            Module holder = (Module)var4.next();
            if (holder.getCategory().equals(category)) {
                float posWidth = 0.0F;
                float posX = this.pos.x + (float)(count % 2 == 0 ? 0 : 170);
                float posY = this.pos.y + (float)(count % 2 == 0 ? this.leftAdd : this.rightAdd);
                Position pos = new Position(posX, posY, posWidth, 20.0F);
                ModuleRender otlM = new ModuleRender(holder, pos.x, pos.y, pos.width, 0.0F);
                pos.height = (float)otlM.height;
                if (count % 2 == 0) {
                    this.leftAdd = (int)((float)this.leftAdd + pos.height + 10.0F);
                } else {
                    this.rightAdd = (int)((float)this.rightAdd + pos.height + 10.0F);
                }

                this.moduleRect.add(otlM);
                ++count;
            }
        }

    }

    public static void doGlScissor(double mainx, double d, double e, double f) {
        Minecraft mc = Minecraft.getMinecraft();
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((int) (mainx * scaleFactor), (int) (mc.displayHeight - (d + f) * scaleFactor), (int) (e * scaleFactor), (int) (f * scaleFactor));
    }

    public void draw(int mx, int my) {
    	   this.mainx = HyperGui.mainx;
           this.mainy = HyperGui.mainy;
   		Color baseColor = new Color(3,11,23, 110);
   		Color colorr = ColorUtils.INSTANCE.interpolateColorC(baseColor, new Color(ColorUtils.INSTANCE.applyOpacity(baseColor.getRGB(), .3f)), 0.5F);
       if (this.selected) {
           RenderUtils.drawRoundedCornerRect((float)(this.mainx -95 ), (float)this.mainy+50+ this.x,(float)(this.mainx -65) + 60,this.mainy+ 68F+ this.x,2, (new Color(4,51,77)).getRGB());
       }
     //  RenderUtil.drawImage(new ResourceLocation("Boker/clickgui/RENDER.png"), (int)mainx + 5, (int)mainx + 60, 0, 0);

  //      FontLoaders.NL24.drawString(this.newcatename(this.type), (float)(this.mainx -70 ), (float)(this.mainy + 55)+ this.x,  (new Color(255, 255, 255)).getRGB());
       
    
      //  Client.INSTANCE.getFontManager().check18.drawString(l2, var10002, (float)(this.mainy + 16), this.selected ? Hud.colorValue.getValue() : (new Color(133, 135, 139)).getRGB());
    
        if (this.selected) {
            GL11.glPushMatrix();
            doGlScissor((double)this.mainx, (double)(this.mainy + 35), 500.0, 305.0);
            GL11.glEnable(3089);
            this.moduleRect.forEach((e) -> {
                e.draw(mx, my);
            });
            GL11.glDisable(3089);
            GL11.glPopMatrix();
            double scrolll = (double)this.getScroll();

            ModuleRender module;
            for(Iterator var7 = this.moduleRect.iterator(); var7.hasNext(); module.scrollY = (int) MathUtil.roundToHalf(scrolll)) {
                module = (ModuleRender)var7.next();
            }

            this.onScroll(30, mx, my);
            this.maxScroll = (float)Math.max(0, this.moduleRect.size() == 0 ? 0 : ((ModuleRender)this.moduleRect.get(this.moduleRect.size() - 1)).getMaxScrollY());
        }

    }

    public String newcatename(ModuleCategory moduleCategory) {
        return moduleCategory.getDisplayName();
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean s) {
        this.selected = s;
    }

    public void onScroll(int ms, int mx, int my) {
        this.scroll = (float)((double)this.rawScroll - this.scrollAnimation.getOutput());
        if (RenderUtil.isHovering((float) this.mainx, (float) (this.mainy + 35), 350.0F, 265.0F, mx, my)) {
            this.rawScroll += (float)Mouse.getDWheel() / 4.0F;
        }

        this.rawScroll = Math.max(Math.min(0.0F, this.rawScroll), -this.maxScroll);
        this.scrollAnimation = new SmoothStepAnimation(ms, (double)(this.rawScroll - this.scroll), Direction.BACKWARDS);
    }

    public float getScroll() {
        this.scroll = (float)((double)this.rawScroll - this.scrollAnimation.getOutput());
        return this.scroll;
    }

    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.moduleRect.forEach((s) -> {
            s.mouseClicked(mouseX, mouseY, mouseButton);
        });
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        this.moduleRect.forEach((e) -> {
            e.mouseReleased(mouseX, mouseY, state);
        });
    }

    public void keyTyped(char typedChar, int keyCode) {
        this.moduleRect.forEach((e) -> {
            e.keyTyped(typedChar, keyCode);
        });
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return RenderUtil.isHovering((float)(this.mainx -70 ), (float)(this.mainy + 54)+ this.x, (float) Fonts.NL18.getStringWidth(this.newcatename(this.type))+10, (float)(Fonts.NL18.getHeight() + 42)+ this.x, mouseX, mouseY);
    }
}
