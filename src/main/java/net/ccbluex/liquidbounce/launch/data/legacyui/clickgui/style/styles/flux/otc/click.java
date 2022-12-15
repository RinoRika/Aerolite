package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles.flux.otc;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.render.Translate;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class click extends GuiScreen {
    int startX;
    int startY;
    public Minecraft mc = Minecraft.getMinecraft();
    private ModuleCategory modulecategory=ModuleCategory.COMBAT;
    boolean dragged;
    int mouseX2,mouseY2;
    boolean mouseClicked;
    private Translate translate = new Translate(0F,0F);
    public final Opacity smooth = new Opacity(0);
    public ArrayList<ModuleList> moduleLists=new ArrayList<>();
    public EmptyInputBox emptyInputBox=null;
    public boolean loading=true ;
    public int mouseWheel;
    boolean close;
    public float minY = -100;
    float barHeight=20;
    public float lastPercent;
    public float percent;
    public float percent2;
    public float outro;
    public float lastOutro;
    public float lastPercent2;
    public float animationPosition;
    public float ani=10;
    public Translate Trani = new Translate(0F,0F);
    @Override
    public void initGui() {
        loading=true;
        float width = RenderUtils.width;
        float height = RenderUtils.height;
        startX = (int) (width / 2 - (150 / 2f));
        startY = (int)((height/2)-150);
        moduleLists.clear();
        for (ModuleCategory moduleCategory : ModuleCategory.values()){
            moduleLists.add(new ModuleList(moduleCategory));
            loadClickGui();
        }
        loadCategory();
        super.initGui();
    }

    public static float smoothTrans(double current, double last) {
        return (float) (current + (last - current) / (Minecraft.getDebugFPS() / 10));
    }
    HashMap<Module, Integer> hashMap=new HashMap<Module, Integer>();

    private String getCategoryIcon(ModuleCategory category) {
        switch(category) {
            case COMBAT:
                return "a";
            case MOVEMENT:
                return "b";
            case RENDER:
                return "c";
            case PLAYER:
                return "d";
            case WORLD:
                return "e";
            case MISC:
                return "g";
            case EXPLOIT:
                return "f";
            default:
                return "m";
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float p_drawScreen_3_) {
        try {
            if (dragged) {
                startX = mouseX2 + mouseX;
                startY = mouseY2 + mouseY;
            }
            if (isHovered(startX - 160, startY - 24, startX + 285, startY + 36, mouseX, mouseY) && Mouse.isButtonDown(0)) {
                dragged = true;
                mouseX2 = (int) (startX - mouseX);
                mouseY2 = (int) (startY - mouseY);
            } else {
                dragged = false;
            }
            float w = 0;
            minY = 280;
            RenderUtils.drawRoundRect(startX - 160, startY - 24, startX + 285, startY + 320, new Color(255, 160, 0).getRGB());
            RenderUtils.drawRoundRect(startX - 160, startY - 20, startX + 285, startY + 320, new Color(35, 34, 41).getRGB());
            int modulecategorX = startX - 40;
            Fonts.icon40.drawStringWithShadow("q", startX - 140, startY - 6, -1);
            Fonts.gs40.drawStringWithShadow("Aerolite", startX - 105, startY - 1, -1);
            for (int i = 0; i <= 8; i++) {
                if (ModuleCategory.values()[i] == modulecategory) {
                    Fonts.gs35.drawStringWithShadow(ModuleCategory.values()[i].getDisplayName(), modulecategorX, startY + 4, new Color(255, 255, 255).getRGB());
                    Fonts.icon35.drawStringWithShadow(getCategoryIcon(ModuleCategory.values()[i]), modulecategorX - 10, startY + 5, new Color(255, 255, 255).getRGB());
                } else {
                    Fonts.gs35.drawStringWithShadow(ModuleCategory.values()[i].getDisplayName(), modulecategorX, startY + 4, new Color(153, 153, 153).getRGB());
                    Fonts.icon35.drawStringWithShadow(getCategoryIcon(ModuleCategory.values()[i]), modulecategorX - 10, startY + 5, new Color(153, 153, 153).getRGB());
                }
                if (isHovered(modulecategorX, startY + 4, modulecategorX + Fonts.gs35.getStringWidth(ModuleCategory.values()[i].getDisplayName()), startY + 13, mouseX, mouseY)) {
                    if (Mouse.isButtonDown(0)) {
                        if (!mouseClicked)
                            modulecategory = ModuleCategory.values()[i];
                        mouseClicked = true;
                    } else mouseClicked = false;
                }
                modulecategorX += 25 + Fonts.gs35.getStringWidth(ModuleCategory.values()[i].getDisplayName());
            }
            RenderUtils.drawRect(startX - 160, startY + 35, startX + 285, startY + 36, new Color(53, 53, 59).getRGB());
            RenderUtils.drawRect(startX - 50, startY + 36, startX - 49, startY + 320, new Color(53, 53, 59).getRGB());
            int wheel = Mouse.getDWheel();
            for (ModuleList moduleList : moduleLists) {
                if (moduleList.modulecategory == modulecategory) {
                    moduleList.draw(startX, startY, mouseX, mouseY);
                    moduleList.setMouseWheel(wheel);
                }
            }
            GlStateManager.pushMatrix();
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtils.doGlScissor(startX - 160, startY + 35, startX + 300, 285);
            if (LiquidBounce.INSTANCE.getModule() != null && LiquidBounce.INSTANCE.getModule().getLoading()) {
                loadWheel();
                LiquidBounce.INSTANCE.getModule().setLoading(false);
            }
            if (LiquidBounce.INSTANCE.getModule() != null && LiquidBounce.INSTANCE.getModule().getSave() && !LiquidBounce.INSTANCE.getModule().getLoading()) {
                SaveMouseWheel();
                LiquidBounce.INSTANCE.getModule().setSave(false);
            }
            if (LiquidBounce.INSTANCE.getModule() != null && LiquidBounce.INSTANCE.getModule().getStart()) {

                LiquidBounce.INSTANCE.getModule().setStart(false);
            }
            if (LiquidBounce.INSTANCE.getModule() != null && !LiquidBounce.INSTANCE.getModule().getStart()) {
                //   this.animationPosition = 75;
            }
            if (LiquidBounce.INSTANCE.getModule() != null) {
                Module module = LiquidBounce.INSTANCE.getModule();
                float valueY = startY + 55 + translate.getY();
                Fonts.gs40.drawStringWithShadow(module.getName(), startX - 41, valueY - 15, -1);
                for (Value value : module.getValues()) {
                    value.getValueTranslate().interpolate(0, valueY, 0.1);
                    float valuePosY = value.getValueTranslate().getY();
                    if (value instanceof BoolValue) {
                        RenderUtils.drawBorderedRect(startX - 40, valuePosY, startX - 32, valuePosY + 8, 1.5f, new Color(185, 184, 190).getRGB(), new Color(185, 184, 190).getRGB());
                        Fonts.gs35.drawStringWithShadow(value.getName(), startX - 26, valuePosY + 1, new Color(185, 184, 190).getRGB());
                        if (((BoolValue) value).get()) {
                            Fonts.icon35.drawString("v", startX - 40, valuePosY + 3, new Color(57, 57, 59).getRGB());
                        }
                        if (isHovered(startX - 40, valuePosY, startX - 31, valuePosY + 8, mouseX, mouseY)) {
                            if (Mouse.isButtonDown(0)) {
                                if (!mouseClicked)
                                    ((BoolValue) value).toggle();
                                mouseClicked = true;
                            } else mouseClicked = false;
                        }
                        w += 15;
                        valueY += 15;
                    }
                    if (value instanceof TextValue) {
                        if (loading) {
                            ((TextValue) value).setEmptyInputBox(new EmptyInputBox(4, mc.fontRendererObj, startX - 42, 150, startX + 300, 8, new Color(145, 145, 145), Fonts.gs35));
                            loading = false;
                        }
                        emptyInputBox = Objects.requireNonNull(((TextValue) value).getEmptyInputBox());
                        emptyInputBox.xPosition = startX - 42;
                        emptyInputBox.yPosition = (int) (valuePosY + 20);
                        Fonts.gs35.drawStringWithShadow(value.getName(), startX - 41, valuePosY + 6, new Color(185, 184, 190).getRGB());
                        RenderUtils.drawRoundRect(startX - 42, valuePosY + 14, startX + 266, valuePosY + 31, new Color(58, 58, 58).getRGB());
                        RenderUtils.drawRoundRect(startX - 41, valuePosY + 15, startX + 265, valuePosY + 30, new Color(22, 22, 22).getRGB());
                        if (!emptyInputBox.isFocused()) {
                            emptyInputBox.setText(((TextValue) value).get());
                        } else {
                            value.set(emptyInputBox.getText());
                        }
                        emptyInputBox.drawTextBox();
                        w += 35;
                        valueY += 35;
                    }
                    if (value instanceof ListValue) {
                        Fonts.gs35.drawStringWithShadow(value.getName(), startX - 41, valuePosY + 7, new Color(185, 184, 190).getRGB());
                        RenderUtils.drawRoundRect(startX - 42, valuePosY + 14, startX + 266, valuePosY + 31, new Color(49, 49, 49).getRGB());
                        RenderUtils.drawRoundRect(startX - 41, valuePosY + 15, startX + 265, valuePosY + 30, new Color(22, 22, 22).getRGB());
                        Fonts.gs35.drawStringWithShadow(((ListValue) value).get(), startX - 38, valuePosY + 20, new Color(200, 200, 200).getRGB());
                        drawAndRotateArrow(startX + 255 - 5.0f, valuePosY + 21 - 0.5f, 6.0f, ((ListValue) value).openList);
                        if (isHovered(startX - 41, valuePosY + 15, startX + 265, valuePosY + 30, mouseX, mouseY)) {
                            if (Mouse.isButtonDown(1)) {
                                if (!mouseClicked)
                                    ((ListValue) value).openList = !((ListValue) value).openList;

                                mouseClicked = true;
                            } else mouseClicked = false;
                        }

                        if (((ListValue) value).openList) {
                            if (((ListValue) value).getOpen()) {
                                ((ListValue) value).setAnim(mouseWheel);
                                ((ListValue) value).setOpen(false);
                            }
                            float valueBoxHeight = valuePosY + 32;
                            for (String values : ((ListValue) value).getValues()) {
                                if (values != ((ListValue) value).get()) {
                                    RenderUtils.drawRect(startX - 42, valueBoxHeight, startX + 265, valueBoxHeight + 20, new Color(22, 22, 22).getRGB());
                                    Fonts.gs35.drawStringWithShadow(values, startX - 38, valueBoxHeight + 8, new Color(200, 200, 200).getRGB());
                                    if (isHovered(startX - 42, valueBoxHeight, startX + 265, valueBoxHeight + 20, mouseX, mouseY)) {
                                        RenderUtils.drawRect(startX - 42, valueBoxHeight, startX + 265, valueBoxHeight + 20, new Color(255, 255, 255, 100).getRGB());
                                    }
                                    if (isHovered(startX - 42, valueBoxHeight, startX + 265, valueBoxHeight + 20, mouseX, mouseY)) {
                                        if (Mouse.isButtonDown(0)) {
                                            if (!mouseClicked)
                                                value.set(values);
                                            mouseWheel = ((ListValue) value).getAnim();
                                            ((ListValue) value).setOpen(true);
                                            ((ListValue) value).openList = false;
                                            mouseClicked = true;
                                        } else mouseClicked = false;
                                    }
                                    w += 20;
                                    valueY += 20;
                                    valueBoxHeight += 20;
                                }
                            }
                        }
                        w += 35;
                        valueY += 35;
                    }
                    if (value instanceof IntegerValue) {
                        float posX = startX - 50;
                        final double max = Math.max(0.0, (mouseX - (posX + 8)) / 300.0);
                        IntegerValue optionInt = (IntegerValue) value;
                        Fonts.gs35.drawString(optionInt.getName() + ":", startX - 42, valuePosY, new Color(160, 160, 160).getRGB());
                        optionInt.getTranslate().interpolate((300F * (optionInt.get() > optionInt.getMaximum() ? optionInt.getMaximum() : optionInt.get() < optionInt.getMinimum() ? 0 : optionInt.get() - optionInt.getMinimum()) / (optionInt.getMaximum() - optionInt.getMinimum()) + 8), 0, 0.1);
                        RenderUtils.drawRoundedRect(startX - 42, valuePosY + 11, 307, 7, 2f, (new Color(45, 45, 45)).getRGB(), .5f, (new Color(45, 45, 45)).getRGB());
                        RenderUtils.drawRoundedRect(posX + 9, valuePosY + 11, (optionInt.getTranslate().getX() - 2), 7, 2f, (new Color(255, 255, 255)).getRGB(), .5F, (new Color(255, 255, 255)).getRGB());
                        RenderUtils.drawRoundedRect((posX + optionInt.getTranslate().getX() + 1), (valuePosY + 8F), 6, 13F, 3f, new Color(255, 255, 255).getRGB(), .5f, new Color(255, 255, 255).getRGB());

                        Fonts.gs35.drawString(optionInt.get().toString(), startX - 40 + Fonts.gs35.getStringWidth(optionInt.getName() + ":"), valuePosY, new Color(255, 255, 255).getRGB());
                        if (this.isHovered(posX + 8, valuePosY + 9, posX + 308, valuePosY + 16, mouseX, mouseY) && Mouse.isButtonDown(0))
                            optionInt.set(Math.toIntExact(Math.round(optionInt.getMinimum() + (optionInt.getMaximum() - optionInt.getMinimum()) * Math.min(max, 1.0))));
                        valueY += 25;
                        w += 25;
                    }
                    if (value instanceof FloatValue) {
                        float posX = startX - 50;
                        final double max = Math.max(0.0, (mouseX - (posX + 8)) / 305.0);
                        FloatValue optionInt = (FloatValue) value;
                        Fonts.gs35.drawString(optionInt.getName() + ":", startX - 42, valuePosY, new Color(160, 160, 160).getRGB());
                        optionInt.getTranslate().interpolate((305F * (optionInt.get() > optionInt.getMaximum() ? optionInt.getMaximum() : optionInt.get() < optionInt.getMinimum() ? 0 : optionInt.get() - optionInt.getMinimum()) / (optionInt.getMaximum() - optionInt.getMinimum()) + 8), 0, 0.1);
                        RenderUtils.drawRoundedRect(startX - 42, valuePosY + 11, 307, 7, 2f, (new Color(45, 45, 45)).getRGB(), .5f, (new Color(45, 45, 45)).getRGB());
                        RenderUtils.drawRoundedRect(posX + 9, valuePosY + 11, (optionInt.getTranslate().getX() - 2), 7, 2f, (new Color(255, 255, 255)).getRGB(), .5F, (new Color(255, 255, 255)).getRGB());
                        RenderUtils.drawRoundedRect((posX + optionInt.getTranslate().getX() + 1), (valuePosY + 8F), 6, 13F, 3f, new Color(255, 255, 255).getRGB(), .5f, new Color(255, 255, 255).getRGB());
                        Fonts.gs35.drawString(optionInt.get().toString(), startX - 40 + Fonts.gs35.getStringWidth(optionInt.getName() + ":"), valuePosY, new Color(255, 255, 255).getRGB());
                        if (this.isHovered(posX + 8, valuePosY + 9, posX + 313, valuePosY + 16, mouseX, mouseY) && Mouse.isButtonDown(0))
                            optionInt.set(Math.round((optionInt.getMinimum() + (optionInt.getMaximum() - optionInt.getMinimum()) * Math.min(max, 1.0)) * 100.0) / 100.0);
                        valueY += 25;
                        w += 25;
                    }
                }
                ;
                float moduleHeight = valueY - translate.getY();
                if (Mouse.hasWheel() && isHovered(startX - 50, startY + 36, startX + 285, startY + 320, mouseX, mouseY)) {
                    if (wheel > 0 && mouseWheel < 0) {
                        mouseWheel += 40;
                    }
                    if (wheel < 0 && Math.abs(mouseWheel) < (moduleHeight) - (startY - 15) - 335) {
                        mouseWheel -= 40;
                    }
                }

                minY -= w;
                if (((moduleHeight) - (startY - 15) - 335) > 0) {
                    float viewable = 281;
                    float progress = Math.min(translate.getY() / this.minY, 1);
                    float ratio = (viewable / w) * viewable;//获取x位置
                    this.barHeight = Math.max(ratio, 20f);
                    float position = progress * (viewable - barHeight);//获取x2位置
                    RenderUtils.drawRect(startX + 284, translate.getY() + 37 + .5f, startX + 285, this.startY + 37 + 285 - .5f, 0xff2d2d2d);
                    RenderUtils.drawRect(startX + 284, this.startY + 37 + position, startX + 285, (this.startY + 37 + position + barHeight), new Color(255, 160, 0).getRGB());
                }
                translate.interpolate(0, mouseWheel, 0.8F);
                hashMap.put(LiquidBounce.INSTANCE.getModule(), mouseWheel);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.popMatrix();
            RenderUtils.drawCircleWithTexture(startX + 240, startY + 8, 0, 360, 8, RenderUtils.getResourceLocation(), Colors.WHITE.c);
            Fonts.gs35.drawStringWithShadow("Stars", startX + 250, startY + 3, -1);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    GuiScreen GuiScreen;
    public static void drawAndRotateArrow(float x, float y, float size, boolean rotate) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0f);
        GL11.glEnable((int)3042);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        GL11.glLineWidth(1.0f);
        GL11.glDisable(3553);
        GL11.glBegin(4);

        if (rotate) {
            GL11.glVertex2f(size, (size / 2.0f));
            GL11.glVertex2f((size / 2.0f), 0.0f);
            GL11.glVertex2f(0.0f, (size / 2.0f));
        } else {
            GL11.glVertex2f(0.0f, 0.0f);
            GL11.glVertex2f((size / 2.0f), (size / 2.0f));
            GL11.glVertex2f(size, 0.0f);
        }

        GL11.glEnd();
        GL11.glDisable(2848);
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void drawGradientSideways(float left, float top, float right, float bottom, int startColor, int endColor) {
        float f = (float) (startColor >> 24 & 255) / 255.0F;
        float f1 = (float) (startColor >> 16 & 255) / 255.0F;
        float f2 = (float) (startColor >> 8 & 255) / 255.0F;
        float f3 = (float) (startColor & 255) / 255.0F;
        float f4 = (float) (endColor >> 24 & 255) / 255.0F;
        float f5 = (float) (endColor >> 16 & 255) / 255.0F;
        float f6 = (float) (endColor >> 8 & 255) / 255.0F;
        float f7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, this.zLevel).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(left, top, this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, this.zLevel).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(right, bottom, this.zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(GuiScreen);
                return;
        }
        if (emptyInputBox!=null) {
            if (emptyInputBox.isFocused()) {
                emptyInputBox.textboxKeyTyped(typedChar, keyCode);
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (emptyInputBox!=null) {
            emptyInputBox.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    public void SaveConfig() {
        File file = new File(LiquidBounce.fileManager.getDir() + "/fluxotc.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            for (ModuleList menu : moduleLists) {
                printWriter.print(menu.modulecategory.getDisplayName() + ":" + menu.startX + ":" + menu.startY +":"+menu.mouseWheel + "\n");
            }
            printWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SaveCategory() {
        String name="";
        File file = new File(LiquidBounce.fileManager.getDir() + "/Categorygui.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            if (LiquidBounce.INSTANCE.getModule()!=null){
                name=":"+LiquidBounce.INSTANCE.getModule().getName();
            }else{
                name="";
            }
            printWriter.print(modulecategory.getDisplayName()+":"+startX+":"+startY+name+"\n");
            printWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadCategory() {
        File file = new File(LiquidBounce.fileManager.getDir() + "/Categorygui.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String len;
            String name1 = "";
            while ((len = bufferedReader.readLine()) != null) {
                String str = len;
                String name = str.toString().split(":")[0];
                if (str.toString().split(":").length > 3) {
                    name1 = str.toString().split(":")[3];
                }
                for (ModuleCategory modulecategory : ModuleCategory.values()) {
                    if (Objects.equals(modulecategory.getDisplayName(), name)) {
                        this.modulecategory = modulecategory;
                    }
                }
                for (Module module : LiquidBounce.moduleManager.getModules()) {
                    if (Objects.equals(module.getName(), name1)) {
                        LiquidBounce.INSTANCE.setModule(module);
                    }
                }
                this.startX = Integer.parseInt(str.toString().split(":")[1]);
                this.startY = Integer.parseInt(str.toString().split(":")[2]);
            }
            bufferedReader.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SaveMouseWheel() {
        File file = new File(LiquidBounce.fileManager.getDir() + "/Wheelgui.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            PrintWriter printWriter = new PrintWriter(file);
            for (Map.Entry<Module,Integer> set : hashMap.entrySet()) {
                printWriter.print(set.getKey().getName() +":"+set.getValue() + "\n");
            }
            printWriter.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void loadClickGui(){
        File file = new File(LiquidBounce.fileManager.getDir() + "/fluxotc.txt");
        try {
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String len;
            while ((len=bufferedReader.readLine())!=null) {
                String str = len;
                String moduleCatrgory=str.toString().split(":")[0];
                for (ModuleList menu : moduleLists) {
                    if (moduleCatrgory.equals(menu.modulecategory.getDisplayName())) {
                        int newx = Integer.parseInt(str.toString().split(":")[1]);
                        int newy = Integer.parseInt(str.toString().split(":")[2]);
                        int newwheel = Integer.parseInt(str.toString().split(":")[3]);
                        menu.startX = newx;
                        menu.startY = newy;
                        menu.mouseWheel = newwheel;
                    }
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
        SaveConfig();
        SaveCategory();
        SaveMouseWheel();
        this.smooth.setOpacity(0.0F);
    }

    public void loadWheel(){
        File file = new File(LiquidBounce.fileManager.getDir() + "/Wheelgui.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String len;
            String all = null;
            while ((len=bufferedReader.readLine())!=null) {
                String str = len;
                all+=str;
                if (LiquidBounce.INSTANCE.getModule()!=null) {
                    String name=str.toString().split(":")[0];
                    if (!name.equals(LiquidBounce.INSTANCE.getModule().getName())) {
                        continue;
                    }
                    if (name.equals(LiquidBounce.INSTANCE.getModule().getName())){
                        mouseWheel = Integer.parseInt(str.toString().split(":")[1]);
                        break;
                    }
                }
            }
            if (all!=null) {
                if (!all.contains(LiquidBounce.INSTANCE.getModule().getName())) {
                    mouseWheel=0;
                }
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        percent = smoothTrans(this.percent, lastPercent);
        percent2 = smoothTrans(this.percent2, lastPercent2);
        if (!this.close) {
            if (this.percent > 0.981D) {
                GlStateManager.translate((RenderUtils.width / 2), (RenderUtils.height / 2), 0.0F);
                GlStateManager.scale(this.percent, this.percent, 0.0F);
            } else {
                this.percent2 = smoothTrans(this.percent2, this.lastPercent2);
                GlStateManager.translate((RenderUtils.width / 2), (RenderUtils.height / 2), 0.0F);
                GlStateManager.scale(this.percent2, this.percent2, 0.0F);
            }
        } else {
            GlStateManager.translate((RenderUtils.width / 2), (RenderUtils.height / 2), 0.0F);
            GlStateManager.scale(this.percent, this.percent, 0.0F);
        }
        GlStateManager.translate((-RenderUtils.width / 2), (-RenderUtils.height / 2), 0.0F);

        if (this.percent <= 1.5D && this.close) {
            this.percent = smoothTrans(this.percent, 12.0D);
        }

        if (this.percent >= 1.4D && this.close) {
            this.mc.currentScreen = null;
            this.mc.mouseHelper.grabMouseCursor();
            this.mc.inGameHasFocus = true;
        }
    }
}
