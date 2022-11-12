package net.ccbluex.liquidbounce.features.module.modules.render.button

//import net.ccbluex.liquidbounce.utils.render.skided.RenderUtils5
import net.ccbluex.liquidbounce.features.module.modules.client.button.AbstractButtonRenderer
import net.ccbluex.liquidbounce.utils.RenderUtil
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiButton
import java.awt.Color

class XiaoChiBounceRenderer(button: GuiButton) : AbstractButtonRenderer(button) {
    var cs: Int = 0
    var alpha = 0
    override fun render(mouseX: Int, mouseY: Int, mc: Minecraft) {
        button.hovered =
            mouseX >= button.xPosition && mouseY >= button.yPosition && mouseX < button.xPosition + button.width && mouseY < button.yPosition + button.height
        updatefade()
        if (button.hovered) {
            if (this.cs >= 4) this.cs = 4
            this.cs++
        } else {
            if (this.cs <= 0) this.cs = 0
            this.cs--
        }
        if (button.enabled) {
            RenderUtil.drawGradientSideways(
                (button.xPosition + this.cs).toDouble(),
                (button.yPosition + button.height - 1).toDouble(),
                (button.xPosition + button.width - button.width / 2).toDouble(),
                (button.yPosition + button.height).toDouble(),
                Color(255, 120, 255, 255).rgb,
                Color(120, 120, 255, 255).rgb
            )
            RenderUtil.drawGradientSideways(
                (button.xPosition + button.width / 2).toDouble(),
                (button.yPosition + button.height - 1).toDouble(),
                (button.xPosition + button.width - this.cs).toDouble(),
                (button.yPosition + button.height).toDouble(),
                Color(120, 120, 255, 255).rgb,
                Color(255, 120, 255, 255).rgb
            )
        } else {
            RenderUtil.drawGradientSideways(
                button.xPosition.toDouble(),
                (button.yPosition + button.height - 1).toDouble(),
                (button.xPosition + button.width - button.width / 2).toDouble(),
                (button.yPosition + button.height).toDouble(),
                Color(255, 120, 60, 255).rgb,
                Color(255, 0, 0, 255).rgb
            )
            RenderUtil.drawGradientSideways(
                (button.xPosition + button.width / 2).toDouble(),
                (button.yPosition + button.height - 1).toDouble(),
                (button.xPosition + button.width).toDouble(),
                (button.yPosition + button.height).toDouble(),
                Color(255, 0, 0, 255).rgb,
                Color(255, 120, 60, 255).rgb
            )
        }
    }

    private fun updatefade() {
        if (button.enabled) if (button.hovered) {
            this.alpha += 25
            if (this.alpha >= 210) this.alpha = 210
        } else {
            this.alpha -= 25
            if (this.alpha <= 120) this.alpha = 120
        }
    }
}

