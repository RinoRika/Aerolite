package net.ccbluex.liquidbounce.features.module.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Vec3;

public class Circle {
    private static Minecraft mc = Minecraft.getMinecraft();
    private Vec3 vector;
    private int tick, prevTick;

    public Circle(Vec3 vector) {
        this.vector = vector;
        this.tick = 20;
        this.prevTick = tick;
    }

    public double getAnimation(float pt) {
        return (this.prevTick + (this.tick - this.prevTick) * pt) / 20F;
    }

    public boolean update() {
        prevTick = tick;
        return tick-- <= 0;
    }

    public Vec3 pos() {
        return new Vec3(vector.xCoord - mc.getRenderManager().renderPosX, vector.yCoord - mc.getRenderManager().renderPosY, vector.zCoord - mc.getRenderManager().renderPosZ);
    }
}
