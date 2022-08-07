/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.misc.FallingPlayer
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.C02PacketUseEntity
import net.minecraft.network.play.client.C03PacketPlayer
import net.minecraft.network.play.client.C0APacketAnimation
import net.minecraft.network.play.client.C0FPacketConfirmTransaction
import net.minecraft.network.play.server.S12PacketEntityVelocity
import net.minecraft.util.BlockPos
import net.minecraft.util.MathHelper
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@ModuleInfo(name = "Velocity", category = ModuleCategory.COMBAT)
class Velocity : Module() {

    /**
     * OPTIONS
     */
    private val horizontalValue = FloatValue("Horizontal", 0F, -2F, 2F)
    private val verticalValue = FloatValue("Vertical", 0F, -2F, 2F)
    private val velocityTickValue = IntegerValue("VelocityTick", 1, 0, 10).displayable { modeValue.equals("Tick") || modeValue.equals("OldSpartan")}
    private val modeValue = ListValue("Mode", arrayOf(
        "Simple", "Tick", "Cancel",
        "Redesky1", "Redesky2", "Redesky3", "Redesky4",
        "MatrixReduce", "MatrixSimple", "MatrixGround",
        "AGC",
        "Reverse", "SmoothReverse",
        "Jump", "Legit",
        "Phase", "PacketPhase", "Glitch", "Spoof",
        "HytPacket", "Vulcan"), "Simple")

    // Reverse
    private val reverseStrengthValue = FloatValue("ReverseStrength", 1F, 0.1F, 1F).displayable { modeValue.equals("Reverse") }
    private val reverse2StrengthValue = FloatValue("SmoothReverseStrength", 0.05F, 0.02F, 0.1F).displayable { modeValue.equals("SmoothReverse") }

    private val phaseHeightValue = FloatValue("PhaseHeight", 0.5F, 0F, 1F)
        .displayable { modeValue.contains("Phase") }
    private val phaseOnlyGround = BoolValue("PhaseOnlyGround", true)
        .displayable { modeValue.contains("Phase") }

    // legit
    private val legitStrafeValue = BoolValue("LegitStrafe", false).displayable { modeValue.equals("Legit") }
    private val legitFaceValue = BoolValue("LegitFace", true).displayable { modeValue.equals("Legit") }

    private val rspAlwaysValue = BoolValue("RedeskyAlwaysReduce", true)
        .displayable { modeValue.contains("RedeSky") }
    private val rspDengerValue = BoolValue("RedeskyOnlyDanger", false)
        .displayable { modeValue.contains("RedeSky") }

    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val onlyCombatValue = BoolValue("OnlyCombat", false)
    // private val onlyHitVelocityValue = BoolValue("OnlyHitVelocity",false)
    private val noFireValue = BoolValue("noFire", false)

    private val overrideDirectionValue = ListValue("OverrideDirection", arrayOf("None", "Hard", "Offset"), "None")
    private val overrideDirectionYawValue = FloatValue("OverrideDirectionYaw", 0F, -180F, 180F)
        .displayable { !overrideDirectionValue.equals("None") }

    /**
     * VALUES
     */
    private var velocityTimer = MSTimer()
    private var velocityCalcTimer = MSTimer()
    private var velocityInput = false
    private var velocityTick = 0

    // SmoothReverse
    private var reverseHurt = false

    // Legit
    private var pos: BlockPos? = null

    private var redeCount = 24

    private var isMatrixOnGround = false

    private var countingTicks = false
    private var cancelTicks = 0
    private var row = 0

    override val tag: String
        get() = "${modeValue.get()} ${horizontalValue.get()}% ${verticalValue.get()}%"

    override fun onEnable() {
        countingTicks = false
        cancelTicks = 0
        if (modeValue.equals("AGC")) {
            ClientUtils.displayChatMessage("[Velocity] DON'T enable fly/speed/longjump when velocity enabled!!!!")
        }
    }
    override fun onDisable() {
        mc.thePlayer?.speedInAir = 0.02F
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (countingTicks) cancelTicks++
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(velocityInput) {
            velocityTick++
        }else velocityTick = 0

        if (redeCount <24) redeCount++
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb) {
            return
        }

        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }
        // if(onlyHitVelocityValue.get() && mc.thePlayer.motionY<0.05) return；
        if (noFireValue.get() && mc.thePlayer.isBurning) return

        when (modeValue.get().lowercase()) {
            "tick" -> {
                if(velocityTick > velocityTickValue.get()) {
                    if(mc.thePlayer.motionY > 0) mc.thePlayer.motionY = 0.0
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    mc.thePlayer.jumpMovementFactor = -0.00001f
                    velocityInput = false
                }
                if(mc.thePlayer.onGround && velocityTick > 1) {
                    velocityInput = false
                }
            }

            "jump" -> if (mc.thePlayer.hurtTime > 0 && mc.thePlayer.onGround) {
                mc.thePlayer.motionY = 0.42
            }

            "reverse" -> {
                if (!velocityInput) {
                    return
                }

                if (!mc.thePlayer.onGround) {
                    MovementUtils.strafe(MovementUtils.getSpeed() * reverseStrengthValue.get())
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                }
            }

            "smoothreverse" -> {
                if (!velocityInput) {
                    mc.thePlayer.speedInAir = 0.02F
                    return
                }

                if (mc.thePlayer.hurtTime > 0) {
                    reverseHurt = true
                }

                if (!mc.thePlayer.onGround) {
                    if (reverseHurt) {
                        mc.thePlayer.speedInAir = reverse2StrengthValue.get()
                    }
                } else if (velocityTimer.hasTimePassed(80L)) {
                    velocityInput = false
                    reverseHurt = false
                }
            }
            "matrixreduce" -> {
                if (mc.thePlayer.hurtTime > 0) {
                    if (mc.thePlayer.onGround) {
                        if (mc.thePlayer.hurtTime <= 6) {
                            mc.thePlayer.motionX *= 0.70
                            mc.thePlayer.motionZ *= 0.70
                        }
                        if (mc.thePlayer.hurtTime <= 5) {
                            mc.thePlayer.motionX *= 0.80
                            mc.thePlayer.motionZ *= 0.80
                        }
                    } else if (mc.thePlayer.hurtTime <= 10) {
                        mc.thePlayer.motionX *= 0.60
                        mc.thePlayer.motionZ *= 0.60
                    }
                }
            }

            "matrixground" -> {
                isMatrixOnGround = mc.thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown
                if (isMatrixOnGround) mc.thePlayer.onGround = false
            }

            "glitch" -> {
                mc.thePlayer.noClip = velocityInput

                if (mc.thePlayer.hurtTime == 7) {
                    mc.thePlayer.motionY = 0.4
                }

                velocityInput = false
            }

        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }

        val packet = event.packet
        if (modeValue.equals("Vulcan") && mc.thePlayer.hurtTime > 0 && packet is C0FPacketConfirmTransaction) {
            event.cancelEvent()
        }
        if (packet is S12PacketEntityVelocity) {
            if (mc.thePlayer == null || (mc.theWorld?.getEntityByID(packet.entityID) ?: return) != mc.thePlayer) {
                return
            }
            // if(onlyHitVelocityValue.get() && packet.getMotionY()<400.0) return
            if (noFireValue.get() && mc.thePlayer.isBurning) return
            velocityTimer.reset()
            velocityTick = 0

            if(!overrideDirectionValue.equals("None")) {
                val yaw = Math.toRadians(if(overrideDirectionValue.get() == "Hard") {
                    overrideDirectionYawValue.get()
                } else {
                    mc.thePlayer.rotationYaw + overrideDirectionYawValue.get() + 90
                }.toDouble())
                val dist = sqrt((packet.motionX * packet.motionX + packet.motionZ * packet.motionZ).toDouble())
                val x = cos(yaw) * dist
                val z = sin(yaw) * dist
                packet.motionX = x.toInt()
                packet.motionZ = z.toInt()
            }

            when (modeValue.get().lowercase()) {
                "tick" -> {
                    velocityInput = true
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F) {
                        event.cancelEvent()
                    }

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
                "agc" -> {
                    row++
                    if (MovementUtils.isMoving()) {
                        if (row <= 2) event.setCancelled(true) else {
                            countingTicks = !LiquidBounce.moduleManager[Speed::class.java]!!.state
                            cancelTicks = 0
                            row = 0
                        }
                    } else
                        event.setCancelled(true)
                }
                "simple" -> {
                    //velocityInput = true
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()

                    if (horizontal == 0F && vertical == 0F) {
                        event.cancelEvent()
                    }

                    packet.motionX = (packet.getMotionX() * horizontal).toInt()
                    packet.motionY = (packet.getMotionY() * vertical).toInt()
                    packet.motionZ = (packet.getMotionZ() * horizontal).toInt()
                }
                "hytpacket" -> {
                    val horizontal = horizontalValue.get()
                    val vertical = verticalValue.get()
                    if (horizontal == 0F && vertical == 0F) {
                        event.cancelEvent()
                    }
                    if (mc.thePlayer.hurtTime == 9) {
                        packet.motionX = mc.thePlayer.motionX.toInt()
                        packet.motionY = mc.thePlayer.motionY.toInt()
                        packet.motionZ = mc.thePlayer.motionZ.toInt()
                    }
                    packet.motionX *= (horizontal / 100.0).toInt()
                    packet.motionY *= (vertical / 100.0).toInt()
                    packet.motionZ *= (horizontal / 100.0).toInt()
                }
                "vulcan" -> {
                    val  horizontal: Double = horizontalValue.get().toDouble()
                    val  vertical: Double = verticalValue.get().toDouble()

                    if (horizontal == 0.0 && vertical == 0.0) {
                        event.cancelEvent()
                        return
                    }
                }

                "cancel" -> {
                    event.cancelEvent()
                }
                "matrixsimple" -> {
                    packet.motionX = (packet.getMotionX() * 0.36).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.36).toInt()
                    if (mc.thePlayer.onGround) {
                        packet.motionX = (packet.getMotionX() * 0.9).toInt()
                        packet.motionZ = (packet.getMotionZ() * 0.9).toInt()
                    }
                }

                "matrixground" -> {
                    packet.motionX = (packet.getMotionX() * 0.36).toInt()
                    packet.motionZ = (packet.getMotionZ() * 0.36).toInt()
                    if (isMatrixOnGround) {
                        packet.motionY = (-628.7).toInt()
                        packet.motionX = (packet.getMotionX() * 0.6).toInt()
                        packet.motionZ = (packet.getMotionZ() * 0.6).toInt()
                    }
                }

                "reverse", "smoothreverse", "aaczero" -> velocityInput = true

                "phase" -> {
                    if (!mc.thePlayer.onGround && phaseOnlyGround.get()) {
                        return
                    }

                    velocityInput = true
                    mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY - phaseHeightValue.get(), mc.thePlayer.posZ)
                    event.cancelEvent()
                    packet.motionX = 0
                    packet.motionY = 0
                    packet.motionZ = 0
                }

                "spoof" -> {
                    event.cancelEvent()
                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX + packet.motionX / 8000.0, mc.thePlayer.posY + packet.motionY / 8000.0, mc.thePlayer.posZ + packet.motionZ / 8000.0, false))
                }

                "packetphase" -> {
                    if (!mc.thePlayer.onGround && phaseOnlyGround.get()) {
                        return
                    }

//                    chat("MOTX=${packet.motionX}, MOTZ=${packet.motionZ}")
                    if (packet.motionX <500 && packet.motionY <500) {
                        return
                    }

                    mc.netHandler.addToSendQueue(C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - phaseHeightValue.get(), mc.thePlayer.posZ, false))
                    event.cancelEvent()
                    packet.motionX = 0
                    packet.motionY = 0
                    packet.motionZ = 0
                }

                "glitch" -> {
                    if (!mc.thePlayer.onGround) {
                        return
                    }

                    velocityInput = true
                    event.cancelEvent()
                }

                "legit" -> {
                    pos = BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ)
                }

                "redesky2" -> {
                    if (packet.getMotionX() == 0 && packet.getMotionZ() == 0) { // ignore horizonal velocity
                        return
                    }

                    val target = LiquidBounce.combatManager.getNearByEntity(LiquidBounce.moduleManager[KillAura::class.java]!!.rangeValue.get() + 1) ?: return
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    packet.motionX = 0
                    packet.motionZ = 0
                    for (i in 0..redeCount) {
                        mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK))
                        mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                    }
                    if (redeCount> 12) redeCount -= 5
                }

                "redesky3" -> {
                    if (packet.getMotionX() == 0 && packet.getMotionZ() == 0) { // ignore horizonal velocity
                        return
                    }

                    val target = LiquidBounce.combatManager.getNearByEntity(LiquidBounce.moduleManager[KillAura::class.java]!!.rangeValue.get() + 1) ?: return
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    packet.motionX = 0
                    packet.motionZ = 0
                    for (i in 0..redeCount) {
                    //    mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK))
                        mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                    }
                    if (!mc.thePlayer.onGround) {
                        packet.motionY = (0.1 * mc.thePlayer.motionY).toInt()
                        mc.thePlayer.motionY = mc.thePlayer.motionY - 0.01
                    }
                    if (redeCount> 12) redeCount -= 5
                    if (!mc.thePlayer.onGround && mc.thePlayer.fallDistance > 1) event.cancelEvent()
                }

                "redeskyclean" -> {
                    if (packet.getMotionX() == 0 && packet.getMotionZ() == 0) { // ignore horizonal velocity
                        return
                    }
                    mc.thePlayer.motionX = 0.0
                    mc.thePlayer.motionZ = 0.0
                    packet.motionX = 0
                    packet.motionZ = 0
                    if (!mc.thePlayer.onGround) {
                        packet.motionY = (0.1 * mc.thePlayer.motionY).toInt()
                        mc.thePlayer.motionY = mc.thePlayer.motionY - 0.01
                    }
                }

                "redesky1" -> {
                    if (packet.getMotionX() == 0 && packet.getMotionZ() == 0) { // ignore horizonal velocity
                        return
                    }

                    if (rspDengerValue.get()) {
                        val pos = FallingPlayer(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, packet.motionX / 8000.0, packet.motionY / 8000.0, packet.motionZ / 8000.0, 0f, 0f, 0f, 0f).findCollision(60)
                        if (pos != null && pos.y> (mc.thePlayer.posY - 7)) {
                            return
                        }
                    }

                    val target = LiquidBounce.combatManager.getNearByEntity(LiquidBounce.moduleManager[KillAura::class.java]!!.rangeValue.get()) ?: return
                    if (rspAlwaysValue.get()) {
                        mc.thePlayer.motionX = 0.0
                        mc.thePlayer.motionZ = 0.0
                        // mc.thePlayer.motionY=(packet.motionY/8000f)*1.0
                        packet.motionX = 0
                        packet.motionZ = 0
                        // event.cancelEvent() better stuff
                    }

                    if (velocityCalcTimer.hasTimePassed(500)) {
                        if (!rspAlwaysValue.get()) {
                            mc.thePlayer.motionX = 0.0
                            mc.thePlayer.motionZ = 0.0
                            // mc.thePlayer.motionY=(packet.motionY/8000f)*1.0
                            packet.motionX = 0
                            packet.motionZ = 0
                        }
                        val count = if (!velocityCalcTimer.hasTimePassed(800)) {
                            8
                        } else if (!velocityCalcTimer.hasTimePassed(1200)) {
                            12
                        } else {
                            25
                        }
                        for (i in 0..count) {
                            mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK))
                            mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                        }
                        velocityCalcTimer.reset()
                    } else {
                        packet.motionX = (packet.motionX * 0.6).toInt()
                        packet.motionZ = (packet.motionZ * 0.6).toInt()
                        for (i in 0..4) {
                            mc.thePlayer.sendQueue.addToSendQueue(C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK))
                            mc.thePlayer.sendQueue.addToSendQueue(C0APacketAnimation())
                        }
                    }
                }
            }
        }
    }

    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }

        when (modeValue.get().lowercase()) {
            "agc" -> {
                if (cancelTicks >= 3 && countingTicks) {
                    mc.thePlayer.setVelocity(0.0, 0.0, 0.0)
                    countingTicks = false
                }
            }
            "legit" -> {
                if (pos == null || mc.thePlayer.hurtTime <= 0) {
                    return
                }

                val rot = RotationUtils.getRotations(pos!!.x.toDouble(), pos!!.y.toDouble(), pos!!.z.toDouble())
                if (legitFaceValue.get()) {
                    RotationUtils.setTargetRotation(rot)
                }
                val yaw = rot.yaw
                if (legitStrafeValue.get()) {
                    val speed = MovementUtils.getSpeed()
                    val yaw1 = Math.toRadians(yaw.toDouble())
                    mc.thePlayer.motionX = -sin(yaw1) * speed
                    mc.thePlayer.motionZ = cos(yaw1) * speed
                } else {
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = MathHelper.sqrt_float(f)

                        if (f < 1.0F) {
                            f = 1.0F
                        }

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = MathHelper.sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((yaw * Math.PI / 180F).toFloat())

                        mc.thePlayer.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer.motionZ += forward * yawCos + strafe * yawSin
                    }
                }
            }
        }
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (mc.thePlayer.isInWater || mc.thePlayer.isInLava || mc.thePlayer.isInWeb || (onlyGroundValue.get() && !mc.thePlayer.onGround)) {
            return
        }

        if ((onlyGroundValue.get() && !mc.thePlayer.onGround) || (onlyCombatValue.get() && !LiquidBounce.combatManager.inCombat)) {
            return
        }

    }
}