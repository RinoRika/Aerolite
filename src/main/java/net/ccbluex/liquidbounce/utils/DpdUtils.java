package net.ccbluex.liquidbounce.utils;

import io.netty.util.concurrent.GenericFutureListener;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Listenable;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

public class DpdUtils implements Listenable {

    @Override
    public boolean handleEvents() {
        return true;
    }

    public static float maxMemorySize = 0,usedMemorySize = 0;

    @EventTarget
    public void onUpdate(UpdateEvent event) {
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
        maxMemorySize = (float) memoryUsage.getMax()/(1024*1024);
        usedMemorySize = (float) memoryUsage.getUsed()/(1024*1024);
    }

    public static float getMemory() {
        return maxMemorySize/usedMemorySize;
    }

    public static void sendPacketSilent(Packet<?> packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet, null, new GenericFutureListener[0]);
    }
    public static void sendPacketUnlogged(Packet<?> packet) {
        Minecraft.getMinecraft().getNetHandler().getNetworkManager().sendPacket(packet);
    }
}
