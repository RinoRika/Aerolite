package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.flux.otc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.utils.render.Translate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

public class ModuleList {
    static Minecraft mc = Minecraft.getMinecraft();
    ModuleCategory modulecategory;
    int startX;
    int startY;
    int mouseWheel;
    int wheel=0;
    boolean slideropen;
    public float minY = -100;
    float barHeight=20;
    public float scrollAni = 0;

    Translate translate = new Translate(0F,0F);
    boolean mouseClicked,mouseClicked1;
    public ModuleList(ModuleCategory category){
           this.modulecategory=category;
    }
    public void draw(int Sx,int Sy,int mouseX,int mouseY){
        this.startX=Sx;
        this.startY=Sy;
        float moduleY=startY+35+translate.getY();
        float w=0;
        minY=280;
        GlStateManager.pushMatrix();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        doGlScissor(startX-160,startY+35,startX+300,285);
        for (Module module : LiquidBounce.moduleManager.getModuleInCategory(this.modulecategory)){
            module.getModuleTranslate().interpolate(0,moduleY,0.4);
            float modulePosY=module.getModuleTranslate().getY();
            if (module.getState()) {
                Fonts.gs35.drawStringWithShadow(module.getName(), startX - 137, modulePosY + 4, new Color(255,255,255).getRGB());
            }else{
                Fonts.gs35.drawStringWithShadow(module.getName(), startX - 137, modulePosY + 4, new Color(153, 153, 153).getRGB());
            }
            if (isHovered(startX-137, modulePosY+5,startX-137+Fonts.gs35.getStringWidth(module.getName()),modulePosY+15,mouseX,mouseY)){
                if (Mouse.isButtonDown(0)) {
                    if (!mouseClicked)
                        module.toggle();
                    mouseClicked = true;
                } else mouseClicked = false;
            }
            if (isHovered(startX-137, modulePosY+5,startX-137+Fonts.gs35.getStringWidth(module.getName()),modulePosY+15,mouseX,mouseY)){
                if (Mouse.isButtonDown(1)) {
                    if (!mouseClicked1)
                        for (Module module1 : LiquidBounce.moduleManager.getModules()){
                            if (module1.showSettings && module1!=module){
                                module1.showSettings=false;
                            }
                        }
                        LiquidBounce.INSTANCE.setModule(module);
                        Objects.requireNonNull(LiquidBounce.INSTANCE.getModule()).showSettings=!LiquidBounce.INSTANCE.getModule().showSettings;
                        LiquidBounce.INSTANCE.getModule().setLoading(true);
                    LiquidBounce.INSTANCE.getModule().setSave(true);
                    LiquidBounce.INSTANCE.getModule().setStart(true);
                    mouseClicked1 = true;
                } else mouseClicked1 = false;
            }
            moduleY+=18;
            w+=18;
        }
        slideropen=isHovered(startX-160,startY+36,startX-50,startY+320,mouseX,mouseY);
        float moduleHeight = moduleY - translate.getY();
        if (Mouse.hasWheel() && isHovered(startX-160,startY+36,startX-50,startY+320,mouseX,mouseY)) {
            if (wheel > 0 && Math.abs(mouseWheel)>0) {
                mouseWheel += 10;
            }
            if (wheel < 0 && Math.abs(mouseWheel) < (moduleHeight)-(startY - 15)-335) {
                mouseWheel -= 10;
            }
        }


        minY -= w;
        if (((moduleHeight)-(startY - 15)-335)>0) {
            float viewable = 281;
            float progress = Math.min(translate.getY() / this.minY, 1);
            float ratio = (viewable / w) * viewable;//获取x位置
            this.barHeight = Math.max(ratio, 20f);
            float position = progress * (viewable - barHeight);//获取x2位置
            RenderUtils.drawRect(startX - 52, translate.getY() + 37 + .5f, startX - 51f, this.startY + 37 + 285 - .5f, 0xff2d2d2d);
            RenderUtils.drawRect(startX - 52, this.startY + 37 + position, startX - 51, (this.startY + 37 + position + barHeight), new Color(255, 160, 0).getRGB());
        }
        translate.interpolate(0, mouseWheel, 0.4F);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.popMatrix();
    }
    
    public void setMouseWheel(int wheel){
        this.wheel = wheel;
    }
    
    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public static void doGlScissor(int x, int y, int width, int height2) {
        int scaleFactor = 1;
        int k = mc.gameSettings.guiScale;
        if (k == 0) {
            k = 1000;
        }
        while (scaleFactor < k && mc.displayWidth / (scaleFactor + 1) >= 320 && mc.displayHeight / (scaleFactor + 1) >= 240) {
            ++scaleFactor;
        }
        GL11.glScissor((x * scaleFactor), (mc.displayHeight - (y + height2) * scaleFactor), (width * scaleFactor), (height2 * scaleFactor));
    }
}
