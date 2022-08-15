package net.ccbluex.liquidbounce.injection.forge.mixins.network;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.modules.addit.Mixins;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.util.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.io.IOException;

@Mixin(C08PacketPlayerBlockPlacement.class)
public class MixinC08PacketPlayerBlockPlacement {
    @Shadow
    private BlockPos position;
    @Shadow
    private int placedBlockDirection;
    @Shadow
    public ItemStack stack;
    @Shadow
    private float facingX;
    @Shadow
    private float facingY;
    @Shadow
    private float facingZ;

    /**
     * @author Stars
     * @reason Fix Hyt C08 Ban
     */
    @Overwrite
    public void readPacketData(PacketBuffer p_readPacketData_1_) throws IOException {
        this.position = p_readPacketData_1_.readBlockPos();
        this.placedBlockDirection = p_readPacketData_1_.readUnsignedByte();
        this.stack = p_readPacketData_1_.readItemStackFromBuffer();
        final Mixins mixins = LiquidBounce.moduleManager.getModule(Mixins.class);
        if (mixins.getState() && mixins.getHytC08Fix().get()) {
            this.facingX = p_readPacketData_1_.readFloat();
            this.facingY = p_readPacketData_1_.readFloat();
            this.facingZ = p_readPacketData_1_.readFloat();
        } else {
            this.facingX = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
            this.facingY = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
            this.facingZ = (float)p_readPacketData_1_.readUnsignedByte() / 16.0F;
        }
    }

    /**
     * @author AiYi
     * @reason 修复hyt右键防砍
     */
    @Overwrite
    public void writePacketData(PacketBuffer p_writePacketData_1_) {
        p_writePacketData_1_.writeBlockPos(this.position);
        p_writePacketData_1_.writeByte(this.placedBlockDirection);
        p_writePacketData_1_.writeItemStackToBuffer(this.stack);
        final Mixins mixins = LiquidBounce.moduleManager.getModule(Mixins.class);
        if (mixins.getState() && mixins.getHytC08Fix().get()) {
            p_writePacketData_1_.writeByte((int) this.facingX);
            p_writePacketData_1_.writeByte((int) this.facingY);
            p_writePacketData_1_.writeByte((int) this.facingZ);
        } else {
            p_writePacketData_1_.writeByte((int)(this.facingX * 16.0F));
            p_writePacketData_1_.writeByte((int)(this.facingY * 16.0F));
            p_writePacketData_1_.writeByte((int)(this.facingZ * 16.0F));
        }
    }
}
