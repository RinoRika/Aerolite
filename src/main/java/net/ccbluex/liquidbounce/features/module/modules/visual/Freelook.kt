package net.ccbluex.liquidbounce.features.module.modules.visual

import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.MathHelper
import org.lwjgl.input.Keyboard

@ModuleInfo("Freelook", ModuleCategory.VISUAL)
class Freelook : Module() {
    private var invertPitch = BoolValue("Invert Pitch", false)

    private var previousPerspective = 0
    var originalYaw = 0f
    var originalPitch = 0f
    var lastYaw= 0f
    var lastPitch = 0f

    override fun onEnable() {
        previousPerspective = mc.gameSettings.thirdPersonView
        originalYaw = mc.thePlayer.rotationYaw.also { lastYaw = it }
        originalPitch = mc.thePlayer.rotationPitch.also { lastPitch = it }
        if (invertPitch.get()) lastPitch *= -1f
    }

    override fun onDisable() {
        mc.thePlayer.rotationYaw = originalYaw
        mc.thePlayer.rotationPitch = originalPitch
        mc.gameSettings.thirdPersonView = previousPerspective
    }

    fun onRender2D(event: Render2DEvent) {
        if (this.keyBind == Keyboard.KEY_NONE || !Keyboard.isKeyDown(this.keyBind)) {
            this.state = !this.state
            return
        }
        mc.mouseHelper.mouseXYChange()
        val f: Float = mc.gameSettings.mouseSensitivity * 0.6f + 0.2f
        val f1 = (f * f * f * 1.5).toFloat()
        lastYaw += (mc.mouseHelper.deltaX * f1) as Float
        lastPitch -= (mc.mouseHelper.deltaY * f1) as Float

        lastPitch = MathHelper.clamp_float(lastPitch, -90f, 90f)
        mc.gameSettings.thirdPersonView = 1
    }

    fun onPacket(event: PacketEvent) {
        if (event.getPacket() is S08PacketPlayerPosLook && event.isServerSide()) {
            val wrapper = event.getPacket() as S08PacketPlayerPosLook
            originalYaw = wrapper.getYaw()
            originalPitch = wrapper.getPitch()
        }
    }
}