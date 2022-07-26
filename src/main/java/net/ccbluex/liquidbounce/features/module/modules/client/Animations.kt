package net.ccbluex.liquidbounce.features.module.modules.client

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.render.EaseUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue


@ModuleInfo(name = "Animations", category = ModuleCategory.CLIENT, canEnable = false)
object Animations : Module() {
    val blockingModeValue = ListValue(
        "BlockingMode", arrayOf(
            "Akrien", "Avatar", "ETB", "Exhibition", "Push", "Reverse",
            "Shield", "SigmaNew", "SigmaOld", "Slide", "SlideDown", "HSlide", "Swong", "VisionFX",
            "Swank", "Jello", "Rotate", "Liquid",
            "Test", "Tap1", "Tap2", "Stab", "Slide2", "Continuity", "Poke", "Zoom", "Normal", "Remix", "Rotate360", "SmoothFloat",
            "None"
        ),
        "SlideDown"
    )
    val invModeValue = ListValue("InvMode", arrayOf("None", "Slide", "Zoom"), "Slide")
    val invEaseModeValue = EaseUtils.getEnumEasingList("InvEase")
    val invEaseOrderModeValue = EaseUtils.getEnumEasingOrderList("InvEaseOrder")
    val invTimeValue = IntegerValue("InvTime", 500, 0, 1000).displayable { !invModeValue.equals("None") }
    private val tabShowPlayerSkinValue = BoolValue("TabShowPlayerSkin", false)
    val tabModeValue = ListValue("TabMode", arrayOf("None", "UpSlide", "DownSlide", "Zoom"), "Zoom")
    val tabEaseModeValue = EaseUtils.getEnumEasingList("TabEase")
    val tabEaseOrderModeValue = EaseUtils.getEnumEasingOrderList("TabEaseOrder")
    val tabTimeValue = IntegerValue("TabTime", 500, 0, 1000).displayable { !tabModeValue.equals("None") }
    val translateXValue = FloatValue("TranslateX", 0.0f, 0.0f, 1.5f)
    val translateYValue = FloatValue("TranslateY", 0.0f, 0.0f, 0.5f)
    val translateZValue = FloatValue("TranslateZ", 0.0f, 0.0f, -2.0f)
    val itemPosXValue = FloatValue("ItemPosX", 0.56F, -1.0F, 1.0F)
    val itemPosYValue = FloatValue("ItemPosY", -0.52F, -1.0F, 1.0F)
    val itemPosZValue = FloatValue("ItemPosZ", -0.71999997F, -1.0F, 1.0F)
    val itemScaleValue = FloatValue("ItemScale", 0.4f, 0.0f, 2.0f)
    val swingAnimValue = BoolValue("SwingAnim", false)
    val swingSpeedValue = FloatValue("SwingSpeed", 1f, 0.5f, 5.0f)
    val anythingBlockValue = BoolValue("AnythingBlock", false)

    // transform rotation
    val transformFirstPersonRotate =
        ListValue("RotateMode", arrayOf("RotateY", "RotateXY", "Custom", "None"), "RotateY")

    val RotateItems = BoolValue("RotateItems", false)
    val SpeedRotate = FloatValue(
        "RotateSpeed",
        1f,
        0f,
        10f,
    ).displayable { RotateItems.get() }
    // custom item rotate
    val customRotate1 = FloatValue(
        "RotateXAxis",
        0f,
        -180f,
        180f,
    ).displayable { RotateItems.get() && transformFirstPersonRotate.get().equals("custom", ignoreCase = true) }
    val customRotate2 = FloatValue(
        "RotateYAxis",
        0f,
        -180f,
        180f,
    ).displayable { RotateItems.get() && transformFirstPersonRotate.get().equals("custom", ignoreCase = true) }
    val customRotate3 = FloatValue(
        "RotateZAxis",
        0f,
        -180f,
        180f,
    ).displayable { RotateItems.get() && transformFirstPersonRotate.get().equals("custom", ignoreCase = true) }
    val viewBobbingYawValue = FloatValue("ViewBobbingYaw", 0.1f, 0f, 0.5f)

    var flagRenderTabOverlay = false
        get() = field && tabShowPlayerSkinValue.get()

    var tabPercent = 0f
    var tabHopePercent = 0f
    var lastTabSync = 0L

    @EventTarget
    fun onUpdate() {
        mc.thePlayer.cameraYaw = viewBobbingYawValue.get()
        mc.thePlayer.prevCameraYaw = viewBobbingYawValue.get()
    }
}
