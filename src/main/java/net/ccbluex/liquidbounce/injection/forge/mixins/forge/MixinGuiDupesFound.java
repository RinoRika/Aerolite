package net.ccbluex.liquidbounce.injection.forge.mixins.forge;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.GuiDupesFound;
import net.minecraftforge.fml.common.ModContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

@Mixin(GuiDupesFound.class)
public class MixinGuiDupesFound extends GuiScreen {
    /**
     * @author
     * @reason
     */
    @Overwrite
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "发现mod冲突 请删除你mod中的[Inputfix(中文输入)],[BetterFps(更好的帧率)", this.width / 2, this.height / 2, 16777215);
    }
}
