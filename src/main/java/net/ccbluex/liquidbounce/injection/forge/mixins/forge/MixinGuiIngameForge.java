package net.ccbluex.liquidbounce.injection.forge.mixins.forge;

import net.ccbluex.liquidbounce.injection.forge.mixins.gui.MixinGuiInGame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
@Mixin(GuiIngameForge.class)
public abstract class MixinGuiIngameForge extends MixinGuiInGame {
    /**
     * @author Stars
     * @reason Title
     */
    @Overwrite(remap = false)
    protected void renderTitle(int width, int height, float partialTicks) {
        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            float age = (float)this.titlesTimer - partialTicks;
            int opacity = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float f3 = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - age;
                opacity = (int)(f3 * 255.0F / (float)this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut) {
                opacity = (int)(age * 255.0F / (float)this.titleFadeOut);
            }

            opacity = MathHelper.clamp_int(opacity, 0, 255);
            if (opacity > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(width / 2), (float)(height / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(3.0F, 3.0F, 3.0F);
                int l = opacity << 24 & -16777216;
                mc.fontRendererObj.drawString(this.displayedTitle, (float)(-mc.fontRendererObj.getStringWidth(this.displayedTitle) / 2), -55.0F, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(2.8F, 2.8F, 2.8F);
                mc.fontRendererObj.drawString(this.displayedSubTitle, (float)(-mc.fontRendererObj.getStringWidth(this.displayedSubTitle) / 2), -47.0F, 16777215 | l, true);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }
    }
}
