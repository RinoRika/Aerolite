/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.modules.client.Animations;
import net.ccbluex.liquidbounce.features.module.modules.client.HUD;
import net.ccbluex.liquidbounce.features.module.modules.render.AntiBlind;
import net.ccbluex.liquidbounce.features.module.modules.render.BetterFont;
import net.ccbluex.liquidbounce.features.module.modules.render.Crosshair;
import net.ccbluex.liquidbounce.injection.access.StaticStorage;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.awt.*;

@Mixin(GuiIngame.class)
public abstract class MixinGuiInGame extends MixinGui {

    @Shadow
    protected abstract void renderHotbarItem(int index, int xPos, int yPos, float partialTicks, EntityPlayer player);

    @Shadow
    protected abstract void renderPlayerStats(ScaledResolution p_renderPlayerStats_1_);

    @Shadow
    protected abstract void renderVignette(float p_renderVignette_1_, ScaledResolution p_renderVignette_2_);

    @Shadow
    protected abstract void renderPumpkinOverlay(ScaledResolution p_renderPumpkinOverlay_1_);

    @Shadow
    protected abstract void renderPortal(float p_renderPortal_1_, ScaledResolution p_renderPortal_2_);

    @Shadow
    public abstract void renderHorseJumpBar(ScaledResolution p_renderHorseJumpBar_1_, int p_renderHorseJumpBar_2_);

    @Shadow
    public abstract void renderExpBar(ScaledResolution p_renderExpBar_1_, int p_renderExpBar_2_);

    @Shadow
    public abstract void renderSelectedItem(ScaledResolution p_renderSelectedItem_1_);

    @Shadow
    public abstract void renderDemo(ScaledResolution p_renderDemo_1_);

    @Shadow
    protected abstract void renderScoreboard(ScoreObjective p_renderScoreboard_1_, ScaledResolution p_renderScoreboard_2_);

    @Shadow
    protected abstract boolean showCrosshair();

    @Shadow
    protected abstract void renderBossHealth();

    @Shadow
    @Final
    protected static ResourceLocation widgetsTexPath;

    @Shadow
    @Final
    protected GuiSpectator spectatorGui;

    @Shadow
    protected String recordPlaying = "";

    @Shadow
    protected int recordPlayingUpFor;

    @Shadow
    protected boolean recordIsPlaying;

    @Shadow
    protected int titlesTimer;

    @Shadow
    protected String displayedTitle = "";

    @Shadow
    protected String displayedSubTitle = "";

    @Shadow
    protected int titleFadeIn;

    @Shadow
    @Final
    protected GuiNewChat persistantChatGUI;

    @Shadow
    @Final
    protected GuiOverlayDebug overlayDebug;

    @Shadow
    protected int updateCounter;

    @Shadow
    protected int titleDisplayTime;

    @Shadow
    protected int titleFadeOut;

    @Shadow
    @Final
    protected GuiPlayerTabOverlay overlayPlayerList;

    @Shadow
    @Final
    protected Minecraft mc;

    @Inject(method = "renderScoreboard", at = @At("HEAD"), cancellable = true)
    private void renderScoreboard(CallbackInfo callbackInfo) {
        if (LiquidBounce.moduleManager.getModule(HUD.class).getState())
            callbackInfo.cancel();
    }

    /**
     * @author liulihaocai
     * @reason 123
     */
    @Overwrite
    protected void renderTooltip(ScaledResolution sr, float partialTicks) {
        final HUD hud = LiquidBounce.moduleManager.getModule(HUD.class);

        float tabHope = this.mc.gameSettings.keyBindPlayerList.isKeyDown() ? 1f : 0f;
        final Animations animations = Animations.INSTANCE;
        if(animations.getTabHopePercent() != tabHope) {
            animations.setLastTabSync(System.currentTimeMillis());
            animations.setTabHopePercent(tabHope);
        }
        if(animations.getTabPercent() > 0 && tabHope == 0) {
            overlayPlayerList.renderPlayerlist(sr.getScaledWidth(), mc.theWorld.getScoreboard(), mc.theWorld.getScoreboard().getObjectiveInDisplaySlot(0));
        }

        if(Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityPlayer) {
            boolean canBetterHotbar = hud.getState() && hud.getBetterHotbarValue().get();
            Minecraft mc = Minecraft.getMinecraft();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            mc.getTextureManager().bindTexture(widgetsTexPath);
            EntityPlayer entityplayer = (EntityPlayer) mc.getRenderViewEntity();
            int i = sr.getScaledWidth() / 2;
            float f = this.zLevel;
            this.zLevel = -90.0F;
            int itemX = i - 91 + HUD.INSTANCE.getHotbarEasePos(entityplayer.inventory.currentItem * 20);
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            if(canBetterHotbar) {
                GlStateManager.disableTexture2D();
                RenderUtils.drawRect(i - 91, sr.getScaledHeight() - 22, i + 91, sr.getScaledHeight(),new Color(0,0,0, 200).getRGB());
                RenderUtils.drawRect(itemX, sr.getScaledHeight() - 21, itemX + 22, sr.getScaledHeight(),new Color(150, 150, 150, 155).getRGB());
                GlStateManager.enableTexture2D();
            } else {
                this.drawTexturedModalRect(i - 91, sr.getScaledHeight() - 22, 0, 0, 182, 22);
                this.drawTexturedModalRect(itemX - 1, sr.getScaledHeight() - 22 - 1, 0, 22, 24, 22);
            }
            this.zLevel = f;
            RenderHelper.enableGUIStandardItemLighting();

            for (int j = 0; j < 9; ++j)
            {
                int k = sr.getScaledWidth() / 2 - 90 + j * 20 + 2;
                int l = sr.getScaledHeight() - 16 - 3;
                this.renderHotbarItem(j, k, l, partialTicks, entityplayer);
            }
            //

            RenderHelper.disableStandardItemLighting();
            GlStateManager.disableRescaleNormal();
            GlStateManager.disableBlend();
        }

        LiquidBounce.eventManager.callEvent(new Render2DEvent(partialTicks, StaticStorage.scaledResolution));
    }

    /**
     * @author Stars
     * @reason title
     */
 /*   @Overwrite
    public void renderGameOverlay(float p_renderGameOverlay_1_) {
        ScaledResolution lvt_2_1_ = new ScaledResolution(this.mc);
        int lvt_3_1_ = lvt_2_1_.getScaledWidth();
        int lvt_4_1_ = lvt_2_1_.getScaledHeight();
        this.mc.entityRenderer.setupOverlayRendering();
        GlStateManager.enableBlend();
        if (Minecraft.isFancyGraphicsEnabled()) {
            this.renderVignette(this.mc.thePlayer.getBrightness(p_renderGameOverlay_1_), lvt_2_1_);
        } else {
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        }

        ItemStack lvt_5_1_ = this.mc.thePlayer.inventory.armorItemInSlot(3);
        if (this.mc.gameSettings.thirdPersonView == 0 && lvt_5_1_ != null && lvt_5_1_.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
            this.renderPumpkinOverlay(lvt_2_1_);
        }

        if (!this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            float lvt_6_1_ = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * p_renderGameOverlay_1_;
            if (lvt_6_1_ > 0.0F) {
                this.renderPortal(lvt_6_1_, lvt_2_1_);
            }
        }

        if (this.mc.playerController.isSpectator()) {
            this.spectatorGui.renderTooltip(lvt_2_1_, p_renderGameOverlay_1_);
        } else {
            this.renderTooltip(lvt_2_1_, p_renderGameOverlay_1_);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        GlStateManager.enableBlend();
        if (this.showCrosshair()) {
            GlStateManager.tryBlendFuncSeparate(775, 769, 1, 0);
            GlStateManager.enableAlpha();
            this.drawTexturedModalRect(lvt_3_1_ / 2 - 7, lvt_4_1_ / 2 - 7, 0, 0, 16, 16);
        }

        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        this.mc.mcProfiler.startSection("bossHealth");
        this.renderBossHealth();
        this.mc.mcProfiler.endSection();
        if (this.mc.playerController.shouldDrawHUD()) {
            this.renderPlayerStats(lvt_2_1_);
        }

        GlStateManager.disableBlend();
        float lvt_7_3_;
        int lvt_8_3_;
        int lvt_6_3_;
        if (this.mc.thePlayer.getSleepTimer() > 0) {
            this.mc.mcProfiler.startSection("sleep");
            GlStateManager.disableDepth();
            GlStateManager.disableAlpha();
            lvt_6_3_ = this.mc.thePlayer.getSleepTimer();
            lvt_7_3_ = (float)lvt_6_3_ / 100.0F;
            if (lvt_7_3_ > 1.0F) {
                lvt_7_3_ = 1.0F - (float)(lvt_6_3_ - 100) / 10.0F;
            }

            lvt_8_3_ = (int)(220.0F * lvt_7_3_) << 24 | 1052704;
            Gui.drawRect(0, 0, lvt_3_1_, lvt_4_1_, lvt_8_3_);
            GlStateManager.enableAlpha();
            GlStateManager.enableDepth();
            this.mc.mcProfiler.endSection();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        lvt_6_3_ = lvt_3_1_ / 2 - 91;
        if (this.mc.thePlayer.isRidingHorse()) {
            this.renderHorseJumpBar(lvt_2_1_, lvt_6_3_);
        } else if (this.mc.playerController.gameIsSurvivalOrAdventure()) {
            this.renderExpBar(lvt_2_1_, lvt_6_3_);
        }

        if (this.mc.gameSettings.heldItemTooltips && !this.mc.playerController.isSpectator()) {
            this.renderSelectedItem(lvt_2_1_);
        } else if (this.mc.thePlayer.isSpectator()) {
            this.spectatorGui.renderSelectedItem(lvt_2_1_);
        }

        if (this.mc.isDemo()) {
            this.renderDemo(lvt_2_1_);
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.overlayDebug.renderDebugInfo(lvt_2_1_);
        }

        GameFontRenderer fontRender = (GameFontRenderer) Fonts.minecraftFont;
        if(BetterFont.INSTANCE.getState()){
            fontRender = Fonts.font35;
        } else {
            fontRender = (GameFontRenderer) Fonts.minecraftFont;
        }

        int lvt_9_4_;
        if (this.recordPlayingUpFor > 0) {
            this.mc.mcProfiler.startSection("overlayMessage");
            lvt_7_3_ = (float)this.recordPlayingUpFor - p_renderGameOverlay_1_;
            lvt_8_3_ = (int)(lvt_7_3_ * 255.0F / 20.0F);
            if (lvt_8_3_ > 255) {
                lvt_8_3_ = 255;
            }

            if (lvt_8_3_ > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ - 68), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                lvt_9_4_ = 16777215;
                if (this.recordIsPlaying) {
                    lvt_9_4_ = MathHelper.hsvToRGB(lvt_7_3_ / 50.0F, 0.7F, 0.6F) & 16777215;
                }

                fontRender.drawString(this.recordPlaying, -Fonts.minecraftFont.getStringWidth(this.recordPlaying) / 2, -4, lvt_9_4_ + (lvt_8_3_ << 24 & -16777216));
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        if (this.titlesTimer > 0) {
            this.mc.mcProfiler.startSection("titleAndSubtitle");
            lvt_7_3_ = (float)this.titlesTimer - p_renderGameOverlay_1_;
            lvt_8_3_ = 255;
            if (this.titlesTimer > this.titleFadeOut + this.titleDisplayTime) {
                float lvt_9_2_ = (float)(this.titleFadeIn + this.titleDisplayTime + this.titleFadeOut) - lvt_7_3_;
                lvt_8_3_ = (int)(lvt_9_2_ * 255.0F / (float)this.titleFadeIn);
            }

            if (this.titlesTimer <= this.titleFadeOut) {
                lvt_8_3_ = (int)(lvt_7_3_ * 255.0F / (float)this.titleFadeOut);
            }

            lvt_8_3_ = MathHelper.clamp_int(lvt_8_3_, 0, 255);
            if (lvt_8_3_ > 8) {
                GlStateManager.pushMatrix();
                GlStateManager.translate((float)(lvt_3_1_ / 2), (float)(lvt_4_1_ / 2), 0.0F);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                GlStateManager.pushMatrix();
                GlStateManager.scale(3.0F, 3.0F, 3.0F);
                lvt_9_4_ = lvt_8_3_ << 24 & -16777216;
                fontRender.drawString(this.displayedTitle, (float)(-Fonts.minecraftFont.getStringWidth(this.displayedTitle) / 2), -55.0F, 16777215 | lvt_9_4_, false);
                GlStateManager.popMatrix();
                GlStateManager.pushMatrix();
                GlStateManager.scale(3.0F, 3.0F, 3.0F);
                fontRender.drawString(this.displayedSubTitle, (float)(-Fonts.minecraftFont.getStringWidth(this.displayedSubTitle) / 2), -48.0F, 16777215 | lvt_9_4_, false);
                GlStateManager.popMatrix();
                GlStateManager.disableBlend();
                GlStateManager.popMatrix();
            }

            this.mc.mcProfiler.endSection();
        }

        Scoreboard lvt_7_4_ = this.mc.theWorld.getScoreboard();
        ScoreObjective lvt_8_4_ = null;
        ScorePlayerTeam lvt_9_5_ = lvt_7_4_.getPlayersTeam(this.mc.thePlayer.getName());
        if (lvt_9_5_ != null) {
            int lvt_10_1_ = lvt_9_5_.getChatFormat().getColorIndex();
            if (lvt_10_1_ >= 0) {
                lvt_8_4_ = lvt_7_4_.getObjectiveInDisplaySlot(3 + lvt_10_1_);
            }
        }

        ScoreObjective lvt_10_2_ = lvt_8_4_ != null ? lvt_8_4_ : lvt_7_4_.getObjectiveInDisplaySlot(1);
        if (lvt_10_2_ != null) {
            this.renderScoreboard(lvt_10_2_, lvt_2_1_);
        }

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, (float)(lvt_4_1_ - 48), 0.0F);
        this.mc.mcProfiler.startSection("chat");
        this.persistantChatGUI.drawChat(this.updateCounter);
        this.mc.mcProfiler.endSection();
        GlStateManager.popMatrix();
        lvt_10_2_ = lvt_7_4_.getObjectiveInDisplaySlot(0);
        if (this.mc.gameSettings.keyBindPlayerList.isKeyDown() && (!this.mc.isIntegratedServerRunning() || this.mc.thePlayer.sendQueue.getPlayerInfoMap().size() > 1 || lvt_10_2_ != null)) {
            this.overlayPlayerList.updatePlayerList(true);
            this.overlayPlayerList.renderPlayerlist(lvt_3_1_, lvt_7_4_, lvt_10_2_);
        } else {
            this.overlayPlayerList.updatePlayerList(false);
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.disableLighting();
        GlStateManager.enableAlpha();
    } */

    @Inject(method = "renderPumpkinOverlay", at = @At("HEAD"), cancellable = true)
    private void renderPumpkinOverlay(final CallbackInfo callbackInfo) {
        final AntiBlind antiBlind = LiquidBounce.moduleManager.getModule(AntiBlind.class);

        if(antiBlind.getState() && antiBlind.getPumpkinEffectValue().get())
            callbackInfo.cancel();
    }

    @Inject(method = "showCrosshair", at = @At("HEAD"), cancellable = true)
    private void injectCrosshair(CallbackInfoReturnable<Boolean> cir) {
        final Crosshair crossHair = LiquidBounce.moduleManager.getModule(Crosshair.class);
        if (crossHair.getState())
            cir.setReturnValue(false);
    }
 }
