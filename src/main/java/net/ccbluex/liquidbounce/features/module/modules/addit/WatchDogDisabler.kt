package net.ccbluex.liquidbounce.features.module.modules.addit

import io.netty.buffer.Unpooled
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.features.module.modules.movement.NewTargetStrafe
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.render.Stencil
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.init.Items
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.util.ResourceLocation
import net.minecraft.util.Vec3
import net.minecraft.network.Packet
import net.minecraft.network.PacketBuffer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.S02PacketChat
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import org.lwjgl.opengl.GL11
import scala.reflect.internal.Trees.New
import java.awt.Color
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.IOException
import java.util.*
import kotlin.math.round
import kotlin.math.sqrt

@ModuleInfo(name = "HypDisabler3", category = ModuleCategory.ADDIT)
class WatchDogDisabler : Module(){
    val modeValue = ListValue("Mode",
        arrayOf(
            "WatchDogMovement"
        ), "WatchDogMovement")

    // watchdog
    private val waitingDisplayMode = ListValue("Waiting-Display", arrayOf("Top", "Middle", "Notification", "Chat", "Amongus", "None"), "Top")
    val renderServer = BoolValue("Render-ServerSide", false)
    private val autoAlert = BoolValue("BanAlert", false)
    private val rotModify = BoolValue("RotationModifier", false)
    private val tifality90 = BoolValue("Tifality", false)
    private val noMoveKeepRot = BoolValue("NoMoveKeepRot", true)
    private val noC03s = BoolValue("NoC03s", true)
    private val testFeature = BoolValue("PingSpoof", false)
    private val testDelay = IntegerValue("Delay", 400, 0, 1000)
    private val checkValid = BoolValue("InvValidate", false)


    // debug
    private val debugValue = BoolValue("Debug", false)

    // sus
  //  private val susImage = ResourceLocation("liquidbounce+/sus.png")
    private var rotatingSpeed = 0F

    private val psfStartSendMode = ListValue("PingSpoof-StartSendMode", arrayOf("All", "First"), "All")
    private val psfSendMode = ListValue("PingSpoof-SendMode", arrayOf("All", "First"), "All")
    private val psfWorldDelay = IntegerValue("PingSpoof-WorldDelay", 15000, 0, 30000)


    // variables
    private val keepAlives = arrayListOf<C00PacketKeepAlive>()
    private val transactions = arrayListOf<C0FPacketConfirmTransaction>()
    private val packetQueue = LinkedList<C0FPacketConfirmTransaction>()
    private val anotherQueue = LinkedList<C00PacketKeepAlive>()
    private val playerQueue = LinkedList<C03PacketPlayer>()

    private val packetBus = hashMapOf<Long, Packet<INetHandlerPlayServer>>()
    private val queueBus = LinkedList<Packet<INetHandlerPlayServer>>()

    private val posLookInstance = PosLookInstance()

    private val msTimer = MSTimer()
    private val wdTimer = MSTimer()
    private val benTimer = MSTimer()

    private var alrSendY = false
    private var alrSprint = false

    private var expectedSetback = false

    private var sendDelay = 0
    private var shouldActive = false
    private var benHittingLean = false

    private var transCount = 0
    private var counter = 0
    private var randDelay = 250

    var shouldModifyRotation = false

    private var verusLastY = 0.0
    private var lastTick = 0

    private var s08count = 0
    private var ticking = 0
    private var lastYaw = 0F

    private var lastUid = 0

    private var initPos: Vec3? = null

    val canModifyRotation: Boolean
        get() = (state && modeValue.get().equals("watchdog", true) && shouldModifyRotation)

    val canRenderInto3D: Boolean
        get() = (state && modeValue.get().equals("watchdog", true) && renderServer.get() && shouldModifyRotation)

    fun isMoving(): Boolean = (mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F || mc.thePlayer.movementInput.sneak || mc.thePlayer.movementInput.jump))

    fun debug(s: String, force: Boolean = false) {
        if (debugValue.get() || force)
            ClientUtils.displayChatMessage("§7[§3§lDisabler§7]§f $s")
    }

    fun isInventory(action: Short): Boolean = action > 0 && action < 100

    override val tag: String
        get() = modeValue.get()

    override fun onEnable() {
        keepAlives.clear()
        transactions.clear()
        packetQueue.clear()
        anotherQueue.clear()
        playerQueue.clear()
        packetBus.clear()
        queueBus.clear()

        s08count = 0

        msTimer.reset()
        wdTimer.reset()
        benTimer.reset()
        expectedSetback = false
        shouldActive = false
        alrSendY = false
        alrSprint = false
        transCount = 0
        lastTick = 0
        ticking = 0

        lastUid = 0
        posLookInstance.reset()

        shouldModifyRotation = false
        benHittingLean = false

        rotatingSpeed = 0F
    }
    override fun onDisable() {
        keepAlives.forEach {
            PacketUtils.sendPacketNoEvent(it)
        }
        transactions.forEach {
            PacketUtils.sendPacketNoEvent(it)
        }

        keepAlives.clear()
        transactions.clear()
        packetQueue.clear()
        anotherQueue.clear()
        packetBus.clear()

        if (modeValue.get().equals("watchdog", true)) {
            anotherQueue.forEach { PacketUtils.sendPacketNoEvent(it) }
            packetQueue.forEach { PacketUtils.sendPacketNoEvent(it) }
        }

        queueBus.clear()

        msTimer.reset()

        mc.thePlayer.motionY = 0.0
        MovementUtils.strafe(0F)
        mc.timer.timerSpeed = 1F

        shouldModifyRotation = false
    }

    @EventTarget
    fun onWorld(event: WorldEvent) {
        transactions.clear()
        keepAlives.clear()
        packetQueue.clear()
        anotherQueue.clear()
        playerQueue.clear()
        packetBus.clear()
        queueBus.clear()

        s08count = 0

        msTimer.reset()
        wdTimer.reset()
        benTimer.reset()
        expectedSetback = false
        shouldActive = false
        alrSendY = false
        alrSprint = false
        benHittingLean = false
        transCount = 0
        counter = 0
        lastTick = 0
        ticking = 0
        lastUid = 0
        posLookInstance.reset()

        rotatingSpeed = 0F
    }

    @EventTarget
    fun onRender2D(event: Render2DEvent) {
        if (!shouldActive)
        {
            val sc = ScaledResolution(mc)
            val strength = (msTimer.hasTimeLeft(psfWorldDelay.get().toLong()).toFloat() / psfWorldDelay.get().toFloat()).coerceIn(0F, 1F)

            if ((modeValue.get().equals("watchdog", true) && testFeature.get()) && !ServerUtils.isHypixelLobby() && !mc.isSingleplayer()) {
                when (waitingDisplayMode.get().toLowerCase()) {
                    "top" -> {
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, 61.5F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, 62.5F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F - 0.5F, 62F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F + 0.5F, 62F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, 62F, Color(220, 220, 60).rgb, false)
                    }
                    "middle" -> {
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, sc.scaledHeight / 2F + 14.5F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, sc.scaledHeight / 2F + 15.5F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F - 0.5F, sc.scaledHeight / 2F + 15F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F + 0.5F, sc.scaledHeight / 2F + 15F, Color(0, 0, 0).rgb, false)
                        Fonts.minecraftFont.drawString("Please wait...", sc.scaledWidth / 2F - Fonts.minecraftFont.getStringWidth("Please wait...") / 2F, sc.scaledHeight / 2F + 15F, Color(220, 220, 60).rgb, false)
                    }
                    "amongus" -> {
                        rotatingSpeed += 0.5F * RenderUtils.deltaTime * 0.05F
                        rotatingSpeed %= sc.scaledWidth + 100
                        GL11.glPushMatrix()
                        GL11.glTranslatef(sc.scaledWidth + 25F - rotatingSpeed, sc.scaledHeight / 2F, 0F)
                        GL11.glPushMatrix()
                        GL11.glRotatef(rotatingSpeed, 0F, 0F, 1F)
             //           RenderUtils.drawImage(susImage, -25, -30, 50, 60)
                        GL11.glPopMatrix()
                        GL11.glPopMatrix()
                    }
                }
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet

        when (modeValue.get().toLowerCase()) {
            "watchdog" -> {
                if (mc.isSingleplayer()) return

                if (autoAlert.get() && packet is S02PacketChat && packet.getChatComponent().getUnformattedText().contains("Cages opened!", true))
                    LiquidBounce.hud.addNotification(Notification("Speed is bannable until this notification disappears.","",NotifyType.WARNING,800,500))

                if (testFeature.get() && !ServerUtils.isHypixelLobby()) {
                    if (packet is C0FPacketConfirmTransaction && (!checkValid.get() || !isInventory(packet.uid))) {
                        event.cancelEvent()
                        packetQueue.add(packet)

                        debug("c0f, ${packet.uid} ID, ${packet.windowId} wID")

                        if (!shouldActive) {
                            shouldActive = true
                            debug("activated")
                            when (waitingDisplayMode.get().toLowerCase()) {
                                "notification" -> LiquidBounce.hud.addNotification(Notification("Activated Disabler.", "",NotifyType.SUCCESS,800,500))
                                "chat" -> debug("Activated Disabler.", true)
                            }
                        }
                    }
                    if (packet is C00PacketKeepAlive) {
                        event.cancelEvent()
                        anotherQueue.add(packet)

                        wdTimer.reset()
                        debug("c00, ${packet.key}")
                    }
                    if (packet is C03PacketPlayer || packet is C0BPacketEntityAction || packet is C08PacketPlayerBlockPlacement || packet is C0APacketAnimation) {
                        if (!shouldActive)
                            event.cancelEvent()
                    }
                    if (packet is S08PacketPlayerPosLook && !shouldActive) {
                        if (alrSendY) {
                            //mc.netHandler.addToSendQueue(C06PacketPlayerPosLook(packet.getX(), packet.getY(), packet.getZ(), packet.getYaw(), packet.getPitch(), false))
                            event.cancelEvent()
                            debug("no s08")
                        } else {
                            alrSendY = true
                            debug("first s08, ignore")
                        }
                    }
                }

                if (noC03s.get() && packet is C03PacketPlayer)
                    if (packet !is C03PacketPlayer.C04PacketPlayerPosition && packet !is C03PacketPlayer.C05PacketPlayerLook && packet !is C03PacketPlayer.C06PacketPlayerPosLook)
                        event.cancelEvent()
            }
        }
    }
    fun flush(check: Boolean) {
        if ((if (check) psfSendMode.get() else psfStartSendMode.get()).equals("all", true))
            while (queueBus.size > 0) {
                PacketUtils.sendPacketNoEvent(queueBus.poll())
            }
        else
            PacketUtils.sendPacketNoEvent(queueBus.poll())
    }

    @EventTarget(priority = 2)
    fun onMotion(event: MotionEvent) {
        val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java)!! as KillAura
        val speed = LiquidBounce.moduleManager.getModule(Speed::class.java)!! as Speed
        val fly = LiquidBounce.moduleManager.getModule(Fly::class.java)!! as Fly
        val targetStrafe = LiquidBounce.moduleManager.getModule(NewTargetStrafe::class.java)!! as NewTargetStrafe

        if (event.eventState == EventState.PRE)
            shouldModifyRotation = false

        if (modeValue.get().equals("watchdog", true)) {
            if (event.eventState == EventState.PRE) {
                if ((speed.state || fly.state) && rotModify.get()) {
                    shouldModifyRotation = true
                    if (MovementUtils.isMoving()) {
                        val cYaw = if (targetStrafe.canStrafe) MovementUtils.getPredictionYaw(event.x, event.z) - 90F
                        else MovementUtils.getRawDirection(event.yaw)

                        lastYaw = cYaw
                        event.yaw = cYaw
                        if (tifality90.get()) event.pitch = 90F
                        RotationUtils.setTargetRotation(Rotation(cYaw, if (tifality90.get()) 90F else event.pitch))
                    } else if (noMoveKeepRot.get()) {
                        event.yaw = lastYaw
                        if (tifality90.get()) event.pitch = 90F
                        RotationUtils.setTargetRotation(Rotation(lastYaw, if (tifality90.get()) 90F else event.pitch))
                    }
                }
                if (mc.isSingleplayer()) return
                if (testFeature.get() && !ServerUtils.isHypixelLobby()) {
                    if (shouldActive && wdTimer.hasTimePassed(testDelay.get().toLong())) {
                        while (!anotherQueue.isEmpty()) {
                            PacketUtils.sendPacketNoEvent(anotherQueue.poll())
                            debug("c00, ${anotherQueue.size}")
                        }
                        while (!packetQueue.isEmpty()) {
                            PacketUtils.sendPacketNoEvent(packetQueue.poll())
                            debug("c0f, ${packetQueue.size}")
                        }
                    }
                }
            }
        }

    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().toLowerCase()) {

        }
    }
}