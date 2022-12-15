/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.gui;

import net.ccbluex.liquidbounce.utils.ServerUtils;
import net.ccbluex.liquidbounce.utils.extensions.RendererExtensionKt;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiConnecting.class)
public abstract class MixinGuiConnecting extends GuiScreen {

    @Inject(method = "connect", at = @At("HEAD"))
    private void headConnect(final String ip, final int port, CallbackInfo callbackInfo) {
        ServerUtils.serverData = new ServerData("", ip + ":" + port, false);
    }

    /**
     * @author CCBlueX
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    //    mc.getTextureManager().bindTexture(new ResourceLocation("main/game.png"));
    //    Gui.drawModalRectWithCustomSizedTexture(0, 0, 0f, 0f, width, height, width, height);

        drawDefaultBackground();
        RenderUtils.drawLoadingCircle(this.width / 2, this.height / 4 + 70);

        String ip = "Unknown";

        final ServerData serverData = mc.getCurrentServerData();
        if(serverData != null)
            ip = serverData.serverIP;

        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, "Trying to connection to:", this.width / 2, this.height / 4 + 110, 0xFFFFFF, true);
        RendererExtensionKt.drawCenteredString(mc.fontRendererObj, ip, this.width / 2, this.height / 4 + 120, 0x5281FB, true);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}