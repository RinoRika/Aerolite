package net.ccbluex.liquidbounce.launch.data.legacyui;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.launch.ui.GuiUpdateLog;
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GuiMainMenuOld extends GuiScreen {
    ScaledResolution sr;
    public static float scale = 1f;
//    private final ResourceLocation bigLogo = new ResourceLocation("aerolite/main/m.png");

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        mc.getTextureManager().bindTexture(new ResourceLocation("aerolite/main/game.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, width, height, width, height);
        GlStateManager.pushMatrix();
        GlStateManager.scale(2, 2, 2);
        ColorUtils.INSTANCE.astolfoRainbow(100, 5, 107);
        GlStateManager.popMatrix();
        int rectHeight = 20;
        if (useParallax) {
            this.moveMouseEffect(mouseX, mouseY, 7.0F);
        }

        String object = "Single Player";
        int top = height / 2 - 35;
        boolean isOnSingle  = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + rectHeight;
        boolean isOnMulit   = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + rectHeight + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + rectHeight + 2 + rectHeight;
        boolean isOnAlt     = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 2) + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 2) + 2 + rectHeight;
        boolean isOnSetting = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 3) + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 3) + 2 + rectHeight;
        boolean isUpdating  = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 4) + 7 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 4) + 7 + rectHeight;
        boolean isOuing     = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 5) + 15 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 5) + 15 + rectHeight;
        int unHoverRectcolor = new Color(0, 0, 0, 150).getRGB();
        int HoverRectcolor = new Color(101, 101, 101, 150).getRGB();
        int strColor = new Color(200, 200, 200, 255).getRGB();
        int strColor2 = new Color(200,200,200,255).getRGB();
        renderSwitchButton();
 //       RenderUtils.drawImage2(this.bigLogo, (float)this.width / 2.0F - 50.0F, (float)this.height / 2.0F - 90.0F - 130, 100, 100);
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight,isOnSingle ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor);
        top += rectHeight + 2;
        object = "Multi Player";
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight, isOnMulit ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor);
        top += rectHeight + 2;
        object = "Alt Manager";
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight, isOnAlt ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor);
        top += rectHeight + 2;
        object = "Settings";
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight, isOnSetting ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor);

        top += rectHeight + 2;
        object = "Update Log";
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight, isUpdating ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor2);

        top += rectHeight + 2;
        object = "Shutdown";
        RenderUtils.drawRect(width / 2 - 50, top, width / 2 + 50, top + rectHeight, isOuing ? HoverRectcolor : unHoverRectcolor);
        Fonts.font35.drawString(object, width / 2 - mc.fontRendererObj.getStringWidth(object) / 2, top + 6, strColor2);


        //Dev saying
        Fonts.font35.drawStringWithShadow("Dev:", width / 100,height / 100, new Color(200,200,200,200).getRGB());
        Fonts.font35.drawStringWithShadow(LiquidBounce.CLIENT_DEV, width / 100 + 25,height / 100, new Color(5,255,5,200).getRGB());

        Fonts.font35.drawStringWithShadow("Version:",width / 100,height / 100 + 10, new Color(200,200,200,200).getRGB());
        Fonts.font35.drawStringWithShadow(LiquidBounce.CLIENT_REAL_VERSION, width / 100 + 40,height / 100 + 10, new Color(5,255,5,200).getRGB());



        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5, 0.5, 0.5);
        GlStateManager.popMatrix();
        List<String> strs = new CopyOnWriteArrayList<>();
        strs.clear();
        strs.add("");
        strs.add("");
        strs.add("");
        strs.add("");
        strs.add(" ");
        strs.add(" ");
        strs.add(" ");
        strs.add(" ");
        strs.add(" ");
        strs.add(" ");
        strs.add(" ");
        strs.sort((o1, o2) -> mc.fontRendererObj.getStringWidth(o2) - mc.fontRendererObj.getStringWidth(o1));
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.7,0.7,0.7);
        mc.fontRendererObj.drawStringWithShadow(""  + "", (3) * 2, (3) * 2, -1);
        int count = 5;
        for (String str : strs) {
            mc.fontRendererObj.drawStringWithShadow(str, (3) * 2, (3 + count) * 2, -1);
            count += 5;
        }
        GlStateManager.popMatrix();

    }
    public final void moveMouseEffect(int mouseX, int mouseY, float strength) {
        int mX = mouseX - this.width / 2;
        int mY = mouseY - this.height / 2;
        float xDelta = (float)mX / (float)(this.width / 2);
        float yDelta = (float)mY / (float)(this.height / 2);
        GL11.glTranslatef(xDelta * strength, yDelta * strength, 0.0F);
    }
    private float sliderX;
    private static boolean useParallax = true;

    public final void renderSwitchButton() {
        this.sliderX += useParallax ? 2.0F : -2.0F;
        if (this.sliderX > 12.0F) {
            this.sliderX = 12.0F;
        } else if (this.sliderX < 0.0F) {
            this.sliderX = 0.0F;
        }

        Fonts.font35.drawStringWithShadow("Animations", 28.0F, (float)this.height - 25.0F, -1);
        RenderUtils.drawRoundedCornerRect(4.0F, (float)this.height - 24.0F, 22.0F, (float)this.height - 18.0F, 3.0F, useParallax ? (new Color(0, 111, 255, 255)).getRGB() : (new Color(140, 140, 140, 255)).getRGB());
        float var10000 = 2.0F + this.sliderX;
        float var10001 = (float)this.height - 26.0F;
        float var10002 = 12.0F + this.sliderX;
        float var10003 = (float)this.height - 16.0F;
        Color var10005 = Color.white;
        Intrinsics.checkNotNullExpressionValue(var10005, "Color.white");
        RenderUtils.drawRoundedCornerRect(var10000, var10001, var10002, var10003, 5.0F, var10005.getRGB());
    }
    public final boolean isMouseHover(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX < x2 && (float)mouseY >= y && (float)mouseY < y2;
    }
    public static final class Companion {
        public final boolean getUseParallax() {
            return GuiMainMenuOld.useParallax;
        }

        public final void setUseParallax(boolean var1) {
            GuiMainMenuOld.useParallax = var1;
        }

        private Companion() {
        }

        // $FF: synthetic method
        public Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }





    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        int rectHeight = 20;
        if (this.isMouseHover(2.0F, (float)this.height - 22.0F, 28.0F, (float)this.height - 12.0F, mouseX, mouseY)) {
            useParallax = !useParallax;
        }
        boolean isOnSingle  = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + rectHeight;
        boolean isOnMulit   = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + rectHeight + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + rectHeight + 2 + rectHeight;
        boolean isOnAlt     = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 2) + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 2) + 2 + rectHeight;
        boolean isOnSetting = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 3) + 2 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 3) + 2 + rectHeight;
        boolean isUpdating  = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 4) + 7 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 4) + 7 + rectHeight;
        boolean isOuing     = mouseX > width / 2 - 50 && mouseY > height / 2 - 35 + (rectHeight * 5) + 15 && mouseX < width / 2 + 50 && mouseY < height / 2 - 35 + (rectHeight * 5) + 15 + rectHeight;
        if (mouseButton == 0) {
            if (isOnSingle) {
                mc.displayGuiScreen(new GuiSelectWorld(this));
            } else if (isOnMulit) {
                mc.displayGuiScreen(new GuiMultiplayer(this));
            } else if (isOnAlt) {
                mc.displayGuiScreen(new GuiAltManager(this));
            } else if (isOnSetting) {
                mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
            }
            else if (isOuing){
                mc.shutdown();
            }
            else if (isUpdating){
                mc.displayGuiScreen(new GuiUpdateLog());
            }
        }
    }
}