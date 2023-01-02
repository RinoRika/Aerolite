package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.AnimationUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.util.ResourceLocation;

@ModuleInfo(name = "Pendant", category = ModuleCategory.RENDER)
public class Pendant extends Module {

    public final ListValue Fubukistyle = new ListValue("Fubukistyle", new String[] { "GIF", "Static"}, "GIF");
    public static BoolValue Taco = new BoolValue("Taco", false);
    public static BoolValue Fubuki = new BoolValue("Fubuki", false);
    public final FloatValue positionY = new FloatValue("PositionY", 5.0F, 130.0F, 1000.0F);
    public final FloatValue positionX = new FloatValue("PositionX", 5.0F, 130.0F, 1000.0F);
    public final FloatValue size = new FloatValue("size", 50.0F, 10.0F, 1000.0F);
    float posX = 0.0F;

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (Pendant.Taco.getValue()) {
            this.Taco();
        }

        if (Pendant.Fubuki.getValue()) {
            this.Fubuki();
        }

    }

    public void Fubuki() {
        if (this.Fubukistyle.get().contains("GIF")) {
            int state = Pendant.mc.thePlayer.ticksExisted % 16 + 1;

            RenderUtils.drawImage3(new ResourceLocation("aerolite/fubuki/" + state + ".png"), (float) ((Float) this.positionX.getValue()).intValue(), (float) (RenderUtils.height() - this.positionY.getValue().intValue()), (float) this.size.getValue().intValue(), (float) ((Float) this.size.getValue()).intValue());
        }

        if (this.Fubukistyle.get().contains("Static")) {
            RenderUtils.drawImage3(new ResourceLocation("aerolite/fubuki/Static.png"), (float) ((Float) this.positionX.getValue()).intValue(), (float) (RenderUtils.height() - this.positionY.getValue().intValue()), 77.0F, 250.0F);
        }

    }

    public void Taco() {
        if (this.posX < (float) RenderUtils.width()) {
            this.posX = (float) ((double) this.posX + AnimationUtils.delta * 0.1D);
        } else {
            this.posX = 0.0F;
        }

        int state = Pendant.mc.thePlayer.ticksExisted % 12 + 1;

        RenderUtils.drawImage3(new ResourceLocation("aerolite/taco/" + state + ".png"), this.posX, (float) (RenderUtils.height() - 80), 42.0F, 27.0F);
    }
}

