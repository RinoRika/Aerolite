package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.launch.data.legacyui.Button;
import net.ccbluex.liquidbounce.launch.uninfo.GuiUpdateLog;
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.ParticleUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.GuiModList;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GuiMainMenuLLL extends GuiScreen {
    public ArrayList butt = new ArrayList();
    public static float scale = 1f;
    private float currentX;
    private float currentY;
    net.ccbluex.liquidbounce.launch.data.legacyui.Button updatelog;
//    private final ResourceLocation bigLogo = new ResourceLocation("aerolite/main/m.png");

    @Override
    public void initGui() {
   /*     this.butt.clear();
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 0, "G", "Single Player", () -> {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 1, "H", "Multi Player", () -> {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 2, "I", "Alt Manager", () -> {
            this.mc.displayGuiScreen(new GuiAltManager(this));
        }));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 3, "J", "Mods", () -> {
            this.mc.displayGuiScreen(new GuiModList(this));
        }, 0.5F));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 4, "K", "Options", () -> {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 5, "L", "Languages", () -> {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }));
        this.butt.add(new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 6, "M", "Quit", () -> {
            this.mc.shutdown();
        }));
        updatelog = new net.ccbluex.liquidbounce.launch.data.legacyui.Button(this, 7, "E", "Update Log", () -> {
            this.mc.displayGuiScreen(new GuiUpdateLog());
        }); */
        super.initGui();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
        mc.getTextureManager().bindTexture(new ResourceLocation("aerolite/main/game.png"));
        Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, width, height, width, height);
    //    GlStateManager.pushMatrix();
        if (useParallax) {
            this.moveMouseEffect(mouseX, mouseY, 7.0F);
        }

        Fonts.font70.drawCenteredString("Aerolite", (float) this.width / 2.0F, (float) this.height / 2.0F - 70.0F, ColorUtils.INSTANCE.rainbow().getRGB(),true);
        Fonts.font35.drawCenteredString("You are using " + LiquidBounce.CLIENT_REAL_VERSION + " version!", (float) this.width / 2.0F, (float) this.height / 2.0F + 70.0F, new Color(255, 255, 255, 255).getRGB());

        ParticleUtils.drawParticles(mouseX,mouseY);
        RenderUtils.fastRoundedRect((float) this.width / 2.0F - 82.0F * ((float) this.butt.size() / 2.0F) - 3f, (float) this.height / 2.0F - 102.0F - 3f, (float) this.width / 2.0F + 82.0F * ((float) this.butt.size() / 2.0F) + 3f, (float) this.height / 2.0F + 105.0F, 10, ColorUtils.INSTANCE.rainbow().getRGB());
        if (RenderUtils.isHovering(mouseX, mouseY, (float) this.width / 2.0F - 80.0F * ((float) this.butt.size() / 2.0F) - 3f, (float) this.height / 2.0F - 100.0F - 3f, (float) this.width / 2.0F + 80.0F * ((float) this.butt.size() / 2.0F) + 3f, (float) this.height / 2.0F + 103.0F))
            RenderUtils.drawRoundedCornerRect((float) this.width / 2.0F - 80.0F * ((float) this.butt.size() / 2.0F) - 3f, (float) this.height / 2.0F - 100.0F - 3f, (float) this.width / 2.0F + 80.0F * ((float) this.butt.size() / 2.0F) + 3f, (float) this.height / 2.0F + 103.0F, 10, new Color(0, 0, 0, 150).getRGB());
        else RenderUtils.drawRoundedCornerRect((float) this.width / 2.0F - 80.0F * ((float) this.butt.size() / 2.0F) - 3f, (float) this.height / 2.0F - 100.0F - 3f, (float) this.width / 2.0F + 80.0F * ((float) this.butt.size() / 2.0F) + 3f, (float) this.height / 2.0F + 103.0F, 10, new Color(0, 0, 0, 70).getRGB());
     //   RenderUtils.drawShadow((float) this.width / 2.0F - 82.0F * ((float) this.butt.size() / 2.0F) - 3f, (float) this.height / 2.0F - 102.0F - 3f, (float) this.width / 2.0F + 82.0F * ((float) this.butt.size() / 2.0F) + 3f, (float) this.height / 2.0F + 105.0F);

        float startX = (float) this.width / 2.0F - 64.5F * ((float) this.butt.size() / 2.0F);

        for (Iterator var9 = this.butt.iterator(); var9.hasNext(); startX += 75.0F) {
            net.ccbluex.liquidbounce.launch.data.legacyui.Button button = (net.ccbluex.liquidbounce.launch.data.legacyui.Button) var9.next();
            button.draw(startX, (float) this.height / 2.0F + 20.0F, mouseX, mouseY);
        }

        updatelog.draw(30f, 15f, mouseX, mouseY);
        Fonts.font32.drawCenteredString("Made with <3 by " + LiquidBounce.CLIENT_DEV, (float)this.width / 2.0f, (float)this.height - 12f, Color.WHITE.getRGB());

        renderSwitchButton();
    //    GlStateManager.popMatrix();

        } catch (Exception e) {
            ClientUtils.INSTANCE.logError("Error while loading main menu.", e);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        Iterator var4 = this.butt.iterator();

        while (var4.hasNext()) {
            net.ccbluex.liquidbounce.launch.data.legacyui.Button button = (Button)var4.next();
            button.mouseClick(mouseX, mouseY, mouseButton);
        }

        super.mouseClicked(mouseX, mouseY, mouseButton);
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
      //  Intrinsics.checkNotNullExpressionValue(var10005, "Color.white");
        RenderUtils.drawRoundedCornerRect(var10000, var10001, var10002, var10003, 5.0F, var10005.getRGB());
    }

}