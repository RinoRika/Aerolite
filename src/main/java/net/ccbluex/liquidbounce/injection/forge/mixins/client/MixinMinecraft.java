/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/UnlegitMC/FDPClient/
 */
package net.ccbluex.liquidbounce.injection.forge.mixins.client;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.*;
import net.ccbluex.liquidbounce.features.module.modules.client.HUD;
import net.ccbluex.liquidbounce.features.module.modules.client.Modules;
import net.ccbluex.liquidbounce.features.module.modules.client.Rotations;
import net.ccbluex.liquidbounce.features.module.modules.combat.AutoClicker;
import net.ccbluex.liquidbounce.features.module.modules.world.FastPlace;
import net.ccbluex.liquidbounce.injection.access.StaticStorage;
import net.ccbluex.liquidbounce.injection.forge.mixins.access.MixinMinecraftForgeClient;
import net.ccbluex.liquidbounce.utils.CPSCounter;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.RotationUtils;
import net.ccbluex.liquidbounce.utils.render.ImageUtils;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.main.GameConfiguration;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Util;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.AccessDeniedException;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft {

    @Shadow
    public GuiScreen currentScreen;

    @Shadow
    public boolean skipRenderWorld;

    @Shadow
    private int leftClickCounter;

    @Shadow
    public MovingObjectPosition objectMouseOver;

    @Shadow
    public WorldClient theWorld;

    @Shadow
    public EntityPlayerSP thePlayer;

    @Shadow
    public EffectRenderer effectRenderer;

    @Shadow
    public PlayerControllerMP playerController;

    @Shadow
    public int rightClickDelayTimer;

    @Shadow
    public GameSettings gameSettings;

    @Shadow
    public int displayWidth;

    @Shadow
    public int displayHeight;

    @Shadow
    private Entity renderViewEntity;

    @Shadow
    @Final
    public File mcDataDir;

    @Shadow private boolean fullscreen;

    @Shadow private CrashReport crashReporter;

    @Shadow public boolean renderChunksMany;

    /**
     * @author XiGuaGeGe
     * @reason s
     */

    @Overwrite
    public int getLimitFramerate() {
        return this.gameSettings.limitFramerate;
    }

    @Inject(method = "run", at = @At("HEAD"))
    private void init(CallbackInfo callbackInfo) {
        if(displayWidth < 1067)
            displayWidth = 1067;

        if(displayHeight < 622)
            displayHeight = 622;
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;checkGLError(Ljava/lang/String;)V", ordinal = 2, shift = At.Shift.AFTER))
    private void startGame(CallbackInfo callbackInfo) throws AccessDeniedException {
        LiquidBounce.INSTANCE.initClient();
    }


    @Inject(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V", shift = At.Shift.AFTER))
    private void createDisplay(CallbackInfo callbackInfo) {
        ClientUtils.INSTANCE.setTitle();
    }

    @Inject(method = "displayGuiScreen", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;currentScreen:Lnet/minecraft/client/gui/GuiScreen;", shift = At.Shift.AFTER))
    private void displayGuiScreen(CallbackInfo callbackInfo) {
        if(currentScreen instanceof net.minecraft.client.gui.GuiMainMenu || (currentScreen != null && currentScreen.getClass().getName().startsWith("net.labymod") && currentScreen.getClass().getSimpleName().equals("ModGuiMainMenu"))) {
            currentScreen = LiquidBounce.mainMenu;

            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            currentScreen.setWorldAndResolution(Minecraft.getMinecraft(), scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
            skipRenderWorld = false;
        }

        LiquidBounce.eventManager.callEvent(new ScreenEvent(currentScreen));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Inject(
            method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V",
            at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;theWorld:Lnet/minecraft/client/multiplayer/WorldClient;", opcode = Opcodes.PUTFIELD, shift = At.Shift.AFTER)
    )
    private void clearRenderCache(CallbackInfo ci) {
        //noinspection ResultOfMethodCallIgnored
        MinecraftForgeClient.getRenderPass(); // Ensure class is loaded, strange accessor issue
        MixinMinecraftForgeClient.getRegionCache().invalidateAll();
        MixinMinecraftForgeClient.getRegionCache().cleanUp();
    }

    private long lastFrame = getTime();

    @Inject(method = "runGameLoop", at = @At("HEAD"))
    private void runGameLoop(final CallbackInfo callbackInfo) {
        final long currentTime = getTime();
        final int deltaTime = (int) (currentTime - lastFrame);
        lastFrame = currentTime;

        RenderUtils.deltaTime = deltaTime;
    }

    @Inject(method = "runTick", at = @At("HEAD"))
    private void runTick(final CallbackInfo callbackInfo) {
        StaticStorage.scaledResolution = new ScaledResolution((Minecraft)(Object) this);
    }

    public long getTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    @Inject(method = "runTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;joinPlayerCounter:I", shift = At.Shift.BEFORE))
    private void onTick(final CallbackInfo callbackInfo) {
        LiquidBounce.eventManager.callEvent(new TickEvent());
    }

    @Inject(method = "dispatchKeypresses", at = @At(value = "HEAD"))
    private void onKey(CallbackInfo callbackInfo) {
        if(Keyboard.getEventKeyState() && (currentScreen == null || (Modules.INSTANCE.getToggleIgnoreScreenValue().get() && this.currentScreen instanceof GuiContainer)))
            LiquidBounce.eventManager.callEvent(new KeyEvent(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()));
    }


    @Inject(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MovingObjectPosition;getBlockPos()Lnet/minecraft/util/BlockPos;"))
    private void onClickBlock(CallbackInfo callbackInfo) {
        if (this.leftClickCounter == 0 && theWorld.getBlockState(objectMouseOver.getBlockPos()).getBlock().getMaterial() != Material.air) {
            LiquidBounce.eventManager.callEvent(new ClickBlockEvent(objectMouseOver.getBlockPos(), this.objectMouseOver.sideHit));
        }
    }

    @Inject(method = "shutdown", at = @At("HEAD"))
    private void shutdown(CallbackInfo callbackInfo) {
        LiquidBounce.INSTANCE.stopClient();
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    private void clickMouse(CallbackInfo callbackInfo) {
        CPSCounter.registerClick(CPSCounter.MouseButton.LEFT);
        if(LiquidBounce.moduleManager.getModule(AutoClicker.class).getState())
            leftClickCounter = 0; // fix hit delay lol
    }

    @Inject(method = "middleClickMouse", at = @At("HEAD"))
    private void middleClickMouse(CallbackInfo ci) {
        CPSCounter.registerClick(CPSCounter.MouseButton.MIDDLE);
    }

    @Inject(method = "rightClickMouse", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;rightClickDelayTimer:I", shift = At.Shift.AFTER))
    private void rightClickMouse(final CallbackInfo callbackInfo) {
        CPSCounter.registerClick(CPSCounter.MouseButton.RIGHT);

        final FastPlace fastPlace = LiquidBounce.moduleManager.getModule(FastPlace.class);

        if (fastPlace.getState())
            rightClickDelayTimer = fastPlace.getSpeedValue().get();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void loadWorld(WorldClient p_loadWorld_1_, String p_loadWorld_2_, final CallbackInfo callbackInfo) {
        LiquidBounce.eventManager.callEvent(new WorldEvent(p_loadWorld_1_));
    }

    // 2k20 code lmao
    @Inject(method = "getRenderViewEntity", at = @At("HEAD"))
    public void getRenderViewEntity(CallbackInfoReturnable<Entity> cir) {
        if (RotationUtils.targetRotation != null && thePlayer != null) {
            final Rotations rotations = LiquidBounce.moduleManager.getModule(Rotations.class);
            final float yaw = RotationUtils.targetRotation.getYaw();
            if (rotations.getHeadValue().get()) {
                thePlayer.rotationYawHead = yaw;
            }
            if (rotations.getBodyValue().get()) {
                thePlayer.renderYawOffset = yaw;
            }
        }
    }



    /**
     * @author CCBlueX
     * @reason
     */
    @Overwrite
    private void sendClickBlockToController(boolean leftClick) {
        if(!leftClick)
            this.leftClickCounter = 0;

        if (this.leftClickCounter <= 0 && !this.thePlayer.isUsingItem()) {
            if(leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                BlockPos blockPos = this.objectMouseOver.getBlockPos();

                if(this.leftClickCounter == 0)
                    LiquidBounce.eventManager.callEvent(new ClickBlockEvent(blockPos, this.objectMouseOver.sideHit));


                if(this.theWorld.getBlockState(blockPos).getBlock().getMaterial() != Material.air && this.playerController.onPlayerDamageBlock(blockPos, this.objectMouseOver.sideHit)) {
                    this.effectRenderer.addBlockHitEffects(blockPos, this.objectMouseOver.sideHit);
                    this.thePlayer.swingItem();
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    @Inject(method = "displayCrashReport", at = @At("HEAD"))
    private void displayCrashReport(CrashReport crashReport, CallbackInfo ci) {
        try {
            File file = new File("./", "CrashReport.txt");
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
                file.createNewFile();
            }
            FileWriter fileWritter = new FileWriter(file.getName(), true);
            fileWritter.write("######################### CRASH REPORT #########################\r\n\r\n" +
                    "客户端出现问题，现在需要强制关闭" +
                    "\r\n\r\n" +
                    " | 如果你需要解决这个问题，请先查阅崩溃日志（此文件）\r\n" +
                    " | 如果你无法修复,请把这份报告交给群管理员或制作者.\r\n" +
                    " | 千万别只发截图，不然把你妈杀了\r\n" +
                    " | 在发送之前，请检查自己的客户端/mod/电脑 问题，不然发给我们也修不好\r\n" +
                    "   - 信息:\r\n" +
                    "   |   版本: " + LiquidBounce.CLIENT_REAL_VERSION + "\r\n" +
                    "   |   时间: " + System.currentTimeMillis() + "\r\n" +
                    "   |   电脑系统: " + Util.getOSType() + "\r\n" +
                    "\r\n######################### CRASH REPORT #########################\r\n\r\n" +
                 /*   "*************** 错误原因 *************** \r\nException: " + crashReport.getCrashCause() +
                    "\r\n\r\n\r\n*************** 错误信息 ***************\r\n" + crashReport.getCauseStackTraceOrString() + */
                    "\r\n\r\n\r\n*************** 完整报告 ***************\r\n" + crashReport.getCompleteReport());
            fileWritter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File file1 = new File("./", "CrashReport.txt");
        ;
        String s = file1.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", s});
                return;
            } catch (IOException ioexception1) {
                ioexception1.printStackTrace();
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s2 = String.format("cmd.exe /C tree C:");
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", new Object[]{s});

            try {
                Runtime.getRuntime().exec(s1);
                Runtime.getRuntime().exec(s2);
                return;
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }

        boolean flag = false;

        try {
            Class<?> oclass = Class.forName("java.awt.Desktop");
            Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object) null, new Object[0]);
            oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{file1.toURI()});
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            flag = true;
        }

        if (flag) {
            Sys.openURL("file://" + s);
        }
    }

    @Inject(method = "setWindowIcon", at = @At("HEAD"), cancellable = true)
    private void setWindowIcon(CallbackInfo callbackInfo) throws IOException {
        try {
            if (Util.getOSType() != Util.EnumOS.OSX) {
                BufferedImage image = ImageIO.read(this.getClass().getResourceAsStream("/assets/minecraft/aerolite/misc/icon.png"));
                Display.setIcon(new ByteBuffer[]{ImageUtils.readImageToBuffer(ImageUtils.resizeImage(image, 16, 16)),
                        ImageUtils.readImageToBuffer(image)});
                callbackInfo.cancel();
            }
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Redirect(method="loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at=@At(value="INVOKE", target="Lnet/minecraft/client/LoadingScreenRenderer;resetProgressAndMessage(Ljava/lang/String;)V"))
    public void loadWorld(LoadingScreenRenderer loadingScreenRenderer, String string) {
    }

    @Redirect(method="loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at=@At(value="INVOKE", target="Lnet/minecraft/client/LoadingScreenRenderer;displayLoadingString(Ljava/lang/String;)V"))
    public void loadWorld1(LoadingScreenRenderer loadingScreenRenderer, String string) {
    }

    @Redirect(method="loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at=@At(value="INVOKE", target="Ljava/lang/System;gc()V", remap=false))
    public void loadWorld2() {
    }

    @Inject(method="toggleFullscreen()V", at=@At(value="INVOKE", target="Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", shift=At.Shift.AFTER, remap=false), require=1, allow=1)
    private void toggleFullscreen(CallbackInfo callbackInfo) {
        if (!this.fullscreen) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }
}
