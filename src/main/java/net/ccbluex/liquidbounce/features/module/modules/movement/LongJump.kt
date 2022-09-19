package net.ccbluex.liquidbounce.features.module.modules.movement

import codes.som.anthony.koffee.modifiers.bridge
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.ClientUtils.displayChatMessage
import net.ccbluex.liquidbounce.utils.MovementUtils.getSpeed
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.PacketUtils.sendPacketNoEvent
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.ItemEnderPearl
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C03PacketPlayer.*
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement
import net.minecraft.network.play.client.C09PacketHeldItemChange
import net.minecraft.network.play.server.S08PacketPlayerPosLook
import net.minecraft.util.BlockPos
import net.minecraft.util.EnumFacing
import java.util.*

@ModuleInfo(
    name = "LongJump",
    category = ModuleCategory.MOVEMENT
)
class LongJump : Module() {
    private val modeValue = ListValue(
        "Mode",
        arrayOf(
            "NCP",
            "Damage",
            "AACv1",
            "AACv2",
            "AACv3",
            "AACv4",
            "Mineplex",
            "Mineplex2",
            "Mineplex3",
            "RedeskyMaki",
            "Redesky",
            "InfiniteRedesky",
            "MatrixFlag",
            "VerusDmg",
            "Pearl"
        ),
        "NCP"
    )
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val ncpBoostValue =
        FloatValue("NCPBoost", 4.25f, 1f, 10f)
    private val matrixBoostValue =
        FloatValue("MatrixFlag-Boost", 1.95f, 0f, 3f)
    private val matrixHeightValue =
        FloatValue("MatrixFlag-Height", 5f, 0f, 10f)
    private val matrixSilentValue =
        BoolValue("MatrixFlag-Silent", true)
    private val matrixBypassModeValue = ListValue(
        "MatrixFlag-BypassMode",
        arrayOf("Motion", "Clip", "None"),
        "EqualMotion")
    private val matrixDebugValue =
        BoolValue("MatrixFlag-Debug", true)
    private val redeskyTimerBoostValue =
        BoolValue("Redesky-TimerBoost", false)
    private val redeskyGlideAfterTicksValue =
        BoolValue("Redesky-GlideAfterTicks", false)
    private val redeskyTickValue =
        IntegerValue("Redesky-Ticks", 21, 1, 25)
    private val redeskyYMultiplier =
        FloatValue("Redesky-YMultiplier", 0.77f, 0.1f, 1f)
    private val redeskyXZMultiplier =
        FloatValue("Redesky-XZMultiplier", 0.9f, 0.1f, 1f)
    private val redeskyTimerBoostStartValue = FloatValue(
        "Redesky-TimerBoostStart",
        1.85f,
        0.05f,
        10f)
    private val redeskyTimerBoostEndValue = FloatValue(
        "Redesky-TimerBoostEnd",
        1.0f,
        0.05f,
        10f)
    private val redeskyTimerBoostSlowDownSpeedValue = IntegerValue(
        "Redesky-TimerBoost-SlowDownSpeed",
        2,
        1,
        10)
    private val verusDmgModeValue = ListValue(
        "VerusDmg-DamageMode",
        arrayOf("Instant", "InstantC06", "Jump"),
        "None")
    private val verusBoostValue =
        FloatValue("VerusDmg-Boost", 4.25f, 0f, 10f)
    private val verusHeightValue =
        FloatValue("VerusDmg-Height", 0.42f, 0f, 10f)
    private val verusTimerValue =
        FloatValue("VerusDmg-Timer", 1f, 0.05f, 10f)
    private val pearlBoostValue =
        FloatValue("Pearl-Boost", 4.25f, 0f, 10f)
    private val pearlHeightValue =
        FloatValue("Pearl-Height", 0.42f, 0f, 10f)
    private val pearlTimerValue =
        FloatValue("Pearl-Timer", 1f, 0.05f, 10f)
    private val damageBoostValue =
        FloatValue("Damage-Boost", 4.25f, 0f, 10f)
    private val damageHeightValue =
        FloatValue("Damage-Height", 0.42f, 0f, 10f)
    private val damageTimerValue =
        FloatValue("Damage-Timer", 1f, 0.05f, 10f)
    private val damageNoMoveValue =
        BoolValue("Damage-NoMove", false)
    private val damageARValue =
        BoolValue("Damage-AutoReset", false)
    private var jumped = false
    private var canBoost = false
    private var teleported = false
    private var canMineplexBoost = false
    private var ticks = 0
    private var currentTimer = 1f
    private var verusDmged = false
    private var hpxDamage = false
    private var damaged = false
    private var verusJumpTimes = 0
    private var pearlState = 0
    private var lastMotX = 0.0
    private var lastMotY = 0.0
    private var lastMotZ = 0.0
    private var flagged = false
    private var hasFell = false
    private val dmgTimer = MSTimer()
    private val posLookInstance: PosLookInstance = PosLookInstance()
    private fun debug(message: String) {
        if (matrixDebugValue.get()) displayChatMessage(message)
    }

    override fun onEnable() {
        if (mc.thePlayer == null) return
        if (modeValue.get().equals("redesky", ignoreCase = true) && redeskyTimerBoostValue.get()) currentTimer =
            redeskyTimerBoostStartValue.get()
        ticks = 0
        verusDmged = false
        hpxDamage = false
        damaged = false
        flagged = false
        hasFell = false
        pearlState = 0
        verusJumpTimes = 0
        dmgTimer.reset()
        posLookInstance.reset()
        val x = mc.thePlayer.posX
        val y = mc.thePlayer.posY
        val z = mc.thePlayer.posZ
        if (modeValue.get().equals("verusdmg", ignoreCase = true)) {
            if (verusDmgModeValue.get().equals("Instant", ignoreCase = true)) {
                if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(
                        mc.thePlayer,
                        mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                    ).isEmpty()
                ) {
                    sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, y + 4, mc.thePlayer.posZ, false))
                    sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, false))
                    sendPacketNoEvent(C04PacketPlayerPosition(mc.thePlayer.posX, y, mc.thePlayer.posZ, true))
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.motionX = mc.thePlayer.motionZ
                }
            } else if (verusDmgModeValue.get().equals("InstantC06", ignoreCase = true)) {
                if (mc.thePlayer.onGround && mc.theWorld.getCollidingBoundingBoxes(
                        mc.thePlayer,
                        mc.thePlayer.entityBoundingBox.offset(0.0, 4.0, 0.0).expand(0.0, 0.0, 0.0)
                    ).isEmpty()
                ) {
                    sendPacketNoEvent(
                        C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            y + 4,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            false
                        )
                    )
                    sendPacketNoEvent(
                        C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            y,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            false
                        )
                    )
                    sendPacketNoEvent(
                        C06PacketPlayerPosLook(
                            mc.thePlayer.posX,
                            y,
                            mc.thePlayer.posZ,
                            mc.thePlayer.rotationYaw,
                            mc.thePlayer.rotationPitch,
                            true
                        )
                    )
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.motionX = mc.thePlayer.motionZ
                }
            } else if (verusDmgModeValue.get().equals("Jump", ignoreCase = true)) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    verusJumpTimes = 1
                }
            }
        }
        if (modeValue.get().equals("matrixflag", ignoreCase = true)) {
            if (matrixBypassModeValue.get().equals("none", ignoreCase = true)) {
                debug("no less flag enabled.")
                hasFell = true
                return
            }
            if (mc.thePlayer.onGround) {
                if (matrixBypassModeValue.get().equals("clip", ignoreCase = true)) {
                    mc.thePlayer.setPosition(x, y + 0.01, z)
                    debug("clipped")
                }
                if (matrixBypassModeValue.get().equals("motion", ignoreCase = true)) mc.thePlayer.jump()
            } else if (mc.thePlayer.fallDistance > 0f) {
                hasFell = true
                debug("falling detected")
            }
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (modeValue.get().equals("matrixflag", ignoreCase = true)) {
            if (hasFell) {
                if (!flagged && !matrixSilentValue.get()) {
                    strafe(matrixBoostValue.get())
                    mc.thePlayer.motionY = matrixHeightValue.get().toDouble()
                    debug("triggering")
                }
            } else {
                if (matrixBypassModeValue.get().equals("motion", ignoreCase = true)) {
                    mc.thePlayer.motionX *= 0.2
                    mc.thePlayer.motionZ *= 0.2
                    if (mc.thePlayer.fallDistance > 0) {
                        hasFell = true
                        debug("activated")
                    }
                }
                if (matrixBypassModeValue.get().equals("clip", ignoreCase = true) && mc.thePlayer.motionY < 0f) {
                    hasFell = true
                    debug("activated")
                }
            }
            return
        }
        if (modeValue.get().equals("verusdmg", ignoreCase = true)) {
            if (mc.thePlayer.hurtTime > 0 && !verusDmged) {
                verusDmged = true
                strafe(verusBoostValue.get())
                mc.thePlayer.motionY = verusHeightValue.get().toDouble()
            }
            if (verusDmgModeValue.get().equals("Jump", ignoreCase = true) && verusJumpTimes < 5) {
                if (mc.thePlayer.onGround) {
                    mc.thePlayer.jump()
                    verusJumpTimes += 1
                }
                return
            }
            if (verusDmged) mc.timer.timerSpeed = verusTimerValue.get() else {
                mc.thePlayer.movementInput.moveForward = 0f
                mc.thePlayer.movementInput.moveStrafe = 0f
                if (!verusDmgModeValue.get().equals("Jump", ignoreCase = true)) mc.thePlayer.motionY = 0.0
            }
            return
        }
        if (modeValue.get().equals("damage", ignoreCase = true)) {
            if (mc.thePlayer.hurtTime > 0 && !damaged) {
                damaged = true
                strafe(damageBoostValue.get())
                mc.thePlayer.motionY = damageHeightValue.get().toDouble()
            }
            if (damaged) {
                mc.timer.timerSpeed = damageTimerValue.get()
                if (damageARValue.get() && mc.thePlayer.hurtTime <= 0) damaged = false
            }
            return
        }
        if (modeValue.get().equals("pearl", ignoreCase = true)) {
            val enderPearlSlot = pearlSlot
            if (pearlState == 0) {
                if (enderPearlSlot == -1) {
                    LiquidBounce.hud.addNotification(
                        Notification("Longjump",
                            "You don't have any ender pearl!",
                            NotifyType.ERROR
                        )
                    )
                    pearlState = -1
                    state = false
                    return
                }
                if (mc.thePlayer.inventory.currentItem != enderPearlSlot) {
                    mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(enderPearlSlot))
                }
                mc.thePlayer.sendQueue.addToSendQueue(
                    C05PacketPlayerLook(
                        mc.thePlayer.rotationYaw,
                        90f,
                        mc.thePlayer.onGround
                    )
                )
                mc.thePlayer.sendQueue.addToSendQueue(
                    C08PacketPlayerBlockPlacement(
                        BlockPos(-1, -1, -1),
                        255,
                        mc.thePlayer.inventoryContainer.getSlot(enderPearlSlot + 36).stack,
                        0f,
                        0f,
                        0f
                    )
                )
                if (enderPearlSlot != mc.thePlayer.inventory.currentItem) {
                    mc.thePlayer.sendQueue.addToSendQueue(C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem))
                }
                pearlState = 1
            }
            if (pearlState == 1 && mc.thePlayer.hurtTime > 0) {
                pearlState = 2
                strafe(pearlBoostValue.get())
                mc.thePlayer.motionY = pearlHeightValue.get().toDouble()
            }
            if (pearlState == 2) mc.timer.timerSpeed = pearlTimerValue.get()
            return
        }
        if (jumped) {
            val mode = modeValue.get()
            if (mc.thePlayer.onGround || mc.thePlayer.capabilities.isFlying) {
                jumped = false
                canMineplexBoost = false
                if (mode.equals("NCP", ignoreCase = true)) {
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                }
                return
            }
            when (mode.lowercase(Locale.getDefault())) {
                "ncp" -> {
                    strafe(getSpeed() * if (canBoost) ncpBoostValue.get() else 1f)
                    canBoost = false
                }
                "aacv1" -> {
                    mc.thePlayer.motionY += 0.05999
                    strafe(getSpeed() * 1.08f)
                }
                "aacv2", "mineplex3" -> {
                    mc.thePlayer.jumpMovementFactor = 0.09f
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999
                    mc.thePlayer.jumpMovementFactor = 0.08f
                    strafe()
                }
                "aacv3" -> {
                    val player = mc.thePlayer
                    if (player.fallDistance > 0.5f && !teleported) {
                        val value = 3.0
                        val horizontalFacing = player.horizontalFacing
                        var x = 0.0
                        var z = 0.0
                        when (horizontalFacing) {
                            EnumFacing.NORTH -> z = -value
                            EnumFacing.EAST -> x = +value
                            EnumFacing.SOUTH -> z = +value
                            EnumFacing.WEST -> x = -value
                        }
                        player.setPosition(player.posX + x, player.posY, player.posZ + z)
                        teleported = true
                    }
                }
                "mineplex" -> {
                    mc.thePlayer.motionY += 0.0132099999999999999999999999999
                    mc.thePlayer.jumpMovementFactor = 0.08f
                    strafe()
                }
                "mineplex2" -> {
                    if (!canMineplexBoost)
                    mc.thePlayer.jumpMovementFactor = 0.1f
                    if (mc.thePlayer.fallDistance > 1.5f) {
                        mc.thePlayer.jumpMovementFactor = 0f
                        mc.thePlayer.motionY = -10.0
                    }
                    strafe()
                }
                "aacv4" -> {
                    mc.thePlayer.jumpMovementFactor = 0.05837456f
                    mc.timer.timerSpeed = 0.5f
                }
                "redeskymaki" -> {
                    mc.thePlayer.jumpMovementFactor = 0.15f
                    mc.thePlayer.motionY += 0.05
                }
                "redesky" -> {
                    if (redeskyTimerBoostValue.get()) {
                        mc.timer.timerSpeed = currentTimer
                    }
                    if (ticks < redeskyTickValue.get()) {
                        mc.thePlayer.jump()
                        mc.thePlayer.motionY *= redeskyYMultiplier.get().toDouble()
                        mc.thePlayer.motionX *= redeskyXZMultiplier.get().toDouble()
                        mc.thePlayer.motionZ *= redeskyXZMultiplier.get().toDouble()
                    } else {
                        if (redeskyGlideAfterTicksValue.get()) {
                            mc.thePlayer.motionY += 0.03
                        }
                        if (redeskyTimerBoostValue.get() && currentTimer > redeskyTimerBoostEndValue.get()) {
                            currentTimer = Math.max(
                                0.08f,
                                currentTimer - 0.05f * redeskyTimerBoostSlowDownSpeedValue.get()
                            ) // zero-timer protection
                        }
                    }
                    ticks++
                }
                "infiniteredesky" -> {
                    if (mc.thePlayer.fallDistance > 0.6f) mc.thePlayer.motionY += 0.02
                    strafe(Math.min(0.85, Math.max(0.25, getSpeed() * 1.05878)).toFloat())
                }
            }
        }
        if (autoJumpValue.get() && mc.thePlayer.onGround && isMoving()) {
            jumped = true
            mc.thePlayer.jump()
        }
    }

    @EventTarget
    fun onMove(event: MoveEvent) {
        val mode = modeValue.get()
        if (mode.equals("mineplex3", ignoreCase = true)) {
            if (mc.thePlayer.fallDistance != 0f) mc.thePlayer.motionY += 0.037
        } else if (mode.equals("ncp", ignoreCase = true) && !isMoving() && jumped) {
            mc.thePlayer.motionX = 0.0
            mc.thePlayer.motionZ = 0.0
            event.zeroXZ()
        }
        if (mode.equals("damage", ignoreCase = true) && damageNoMoveValue.get() && !damaged || mode.equals(
                "verusdmg",
                ignoreCase = true
            ) && !verusDmged
        ) event.zeroXZ()
        if (mode.equals("pearl", ignoreCase = true) && pearlState != 2) event.cancelEvent()
        if (matrixSilentValue.get() && hasFell && !flagged) event.cancelEvent()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val mode = modeValue.get()
        if (event.packet is C03PacketPlayer) {
            val packetPlayer = event.packet
            if (mode.equals("verusdmg", ignoreCase = true) && verusDmgModeValue.get()
                    .equals("Jump", ignoreCase = true)
            ) {
                packetPlayer.onGround = false
            }
            if (mode.equals("matrixflag", ignoreCase = true)) {
                if (event.packet is C06PacketPlayerPosLook && posLookInstance.equalFlag(event.packet)) {
                    posLookInstance.reset()
                    mc.thePlayer.motionX = lastMotX
                    mc.thePlayer.motionY = lastMotY
                    mc.thePlayer.motionZ = lastMotZ
                    debug("should be launched by now")
                } else if (matrixSilentValue.get()) {
                    if (hasFell && !flagged) {
                        if (packetPlayer.isMoving) {
                            debug("modifying packet: rotate false, onGround false, moving enabled, x, y, z set to expected speed")
                            packetPlayer.onGround = false
                            val data: DoubleArray? = MovementUtils.getXZDist(
                                matrixBoostValue.get(),
                                if (packetPlayer.rotating) packetPlayer.yaw else mc.thePlayer.rotationYaw
                            )
                            lastMotX = data!![0]
                            lastMotZ = data[1]
                            lastMotY = matrixHeightValue.get().toDouble()
                            packetPlayer.x += lastMotX
                            packetPlayer.y += lastMotY
                            packetPlayer.z += lastMotZ
                        }
                    }
                }
            }
        }
        if (event.packet is S08PacketPlayerPosLook && mode.equals("matrixflag", ignoreCase = true) && hasFell) {
            debug("flag check started")
            flagged = true
            posLookInstance.set(event.packet)
            if (!matrixSilentValue.get()) {
                debug("data saved")
                lastMotX = mc.thePlayer.motionX
                lastMotY = mc.thePlayer.motionY
                lastMotZ = mc.thePlayer.motionZ
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onJump(event: JumpEvent) {
        jumped = true
        canBoost = true
        teleported = false
        if (state) {
            when (modeValue.get().lowercase(Locale.getDefault())) {
                "mineplex" -> event.motion = event.motion * 4.08f
                "mineplex2" -> if (mc.thePlayer.isCollidedHorizontally) {
                    event.motion = 2.31f
                    canMineplexBoost = true
                    mc.thePlayer.onGround = false
                }
                "aacv4" -> event.motion = event.motion * 1.0799f
            }
        }
    }

    private val pearlSlot: Int
        private get() {
            for (i in 36..44) {
                val stack = mc.thePlayer.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item is ItemEnderPearl) {
                    return i - 36
                }
            }
            return -1
        }

    override fun onDisable() {
        mc.timer.timerSpeed = 1.0f
        mc.thePlayer.speedInAir = 0.02f
    }

    override val tag: String
        get() = modeValue.get()
}