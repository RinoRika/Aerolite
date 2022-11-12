package net.ccbluex.liquidbounce.injection.forge.mixins.client;

import net.ccbluex.liquidbounce.utils.misc.RandomUtils;
import net.minecraft.crash.CrashReport;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(CrashReport.class)
public class MixinCrashReport {

    /**
     * @author Stars
     * @reason Fun
     */
    @Overwrite
    private static String getWittyComment() {
        String[] astring = new String[]{"谁引爆了炸弹?", "他似乎出了点问题...", "你是一个一个一个什么啊", "你tm劈我端是吧!", "好我再给你一次机会啊", "哼哼哼啊啊啊啊啊啊啊啊啊(你看到这个时的心情)",
                "全体目光向我看齐,我宣布个事,我崩端啦!", "这里的水很深,你把握不住", "很好啊,很好啊(赞赏)", "24岁，是黑客", "我启动这个客户端的时候,我已经发现114514个问题了"};

        try {
            return astring[RandomUtils.INSTANCE.nextInt(1, astring.length)];
        } catch (Throwable var2) {
            return "Failed to get witty comment:(";
        }
    }
}
