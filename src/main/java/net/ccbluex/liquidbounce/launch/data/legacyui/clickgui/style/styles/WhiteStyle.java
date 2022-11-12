/*
 * Liangyu Like Your CODE MUAAA .
 *
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.gui.GuiYesNoCallback
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.input.Mouse
 */
package net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.styles;

import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.Opacity;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.RenderUtils;
import net.ccbluex.liquidbounce.launch.data.legacyui.clickgui.style.Style;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.ui.font.GameFontRenderer;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.*;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

import java.awt.*;

public class WhiteStyle extends GuiScreen implements GuiYesNoCallback {
    private ModuleCategory currentModuleType = ModuleCategory.COMBAT;
    private Module currentModule = LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).size() != 0 ? LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).get(0) : null;
    private float startX = 100.0f;
    private float startY = 85.0f;
    private int moduleStart = 0;
    private int valueStart = 0;
    private boolean previousMouse = true;
    private boolean mouse;
    private Opacity opacity = new Opacity(0);
    private float moveX = 0.0f;
    private float moveY = 0.0f;
    private GameFontRenderer LogoFont = Fonts.poppinsBold20;
    private int animationHeight = 0;
    public Style style = new SlowlyStyle();

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (this.isHovered(this.startX - 40.0f, this.startY, this.startX + 280.0f, this.startY + 25.0f, mouseX, mouseY) && Mouse.isButtonDown((int)0)) {
            if (this.moveX == 0.0f && this.moveY == 0.0f) {
                this.moveX = (float)mouseX - this.startX;
                this.moveY = (float)mouseY - this.startY;
            } else {
                this.startX = (float)mouseX - this.moveX;
                this.startY = (float)mouseY - this.moveY;
            }
            this.previousMouse = true;
        } else if (this.moveX != 0.0f || this.moveY != 0.0f) {
            this.moveX = 0.0f;
            this.moveY = 0.0f;
        }
        int opacityX = 255;
        this.opacity.interpolate(opacityX);
        RenderUtils.drawRoundedRect2(this.startX - 40.0f, this.startY, this.startX + 280.0f, this.startY + 330.0f, 8f, new Color(49, 52, 57, (int)this.opacity.getOpacity()).getRGB());
        RenderUtils.drawRoundedRect2(this.startX + 170.0f, this.startY, this.startX + 300.0f, this.startY + 330.0f, 8f, new Color(64, 68, 75, (int)this.opacity.getOpacity()).getRGB());
        this.LogoFont.drawString("AeroLite", this.startX - 30.0f, this.startY + 10.0f, ColorUtils.INSTANCE.rainbow().getRGB());
        Fonts.poppins16.drawString(String.valueOf(LiquidBounce.CLIENT_REAL_VERSION), this.startX + 5.0f, this.startY + 25.0f, new Color(255, 255, 255, (int)this.opacity.getOpacity()).getRGB());
        for (int i = 0; i < ModuleCategory.values().length; ++i) {
            ModuleCategory[] iterator2 = ModuleCategory.values();
            if (iterator2[i] == this.currentModuleType) {
                int finishHeight = i * 30;
                RenderUtils.drawRoundedRect2(this.startX - 40.0f, this.startY + 50.0f + (float)this.animationHeight, this.startX + 55.0f, this.startY + 75.0f + (float)this.animationHeight, 7.0f, new Color(66, 134, 245).getRGB());
                if (this.animationHeight < finishHeight) {
                    this.animationHeight = finishHeight - this.animationHeight < 30 ? this.animationHeight + 5 : this.animationHeight + 10;
                } else if (this.animationHeight > finishHeight) {
                    this.animationHeight = this.animationHeight - finishHeight < 30 ? this.animationHeight - 5 : this.animationHeight - 10;
                }
                if (this.animationHeight == finishHeight) {
                    this.animationHeight = this.animationHeight;
                    Fonts.font35.drawString(iterator2[i].getDisplayName(), this.startX - 8.0f, this.startY + 60.0f + (float)(i * 30), new Color(255, 255, 255, (int)this.opacity.getOpacity()).getRGB());
                } else {
                    this.animationHeight = this.animationHeight;
                    RenderUtils.drawRoundRect(this.startX - 20.0f, this.startY + 50.0f + (float)(i * 30), this.startX + 60.0f, this.startY + 75.0f + (float)(i * 30), new Color(255, 255, 255, 0).getRGB());
                    Fonts.font35.drawString(iterator2[i].getDisplayName(), this.startX - 8.0f, this.startY + 60.0f + (float)(i * 30), new Color(196, 196, 196, (int)this.opacity.getOpacity()).getRGB());
                }
            } else {
                RenderUtils.drawRoundRect(this.startX - 20.0f, this.startY + 50.0f + (float)(i * 30), this.startX + 60.0f, this.startY + 75.0f + (float)(i * 30), new Color(255, 255, 255, 0).getRGB());
                Fonts.font35.drawString(iterator2[i].getDisplayName(), this.startX - 8.0f, this.startY + 60.0f + (float)(i * 30), new Color(196, 196, 196, (int)this.opacity.getOpacity()).getRGB());
            }
            try {
                if (!this.isCategoryHovered(this.startX - 40.0f, this.startY + 50.0f + (float)(i * 30), this.startX + 60.0f, this.startY + 75.0f + (float)(i * 40), mouseX, mouseY) || !Mouse.isButtonDown((int)0)) continue;
                this.currentModuleType = iterator2[i];
                this.currentModule = LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).size() != 0 ? LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).get(0) : null;
                this.moduleStart = 0;
                continue;
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
        int m = Mouse.getDWheel();
        if (this.isCategoryHovered(this.startX + 68.0f, this.startY, this.startX + 169.0f, this.startY + 245.0f, mouseX, mouseY)) {
            if (m < 0 && this.moduleStart < LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).size() - 1) {
                ++this.moduleStart;
            }
            if (m > 0 && this.moduleStart > 0) {
                --this.moduleStart;
            }
        }
        if (this.isCategoryHovered(this.startX + 170.0f, this.startY, this.startX + 300.0f, this.startY + 245.0f, mouseX, mouseY)) {
            if (m < 0 && this.valueStart < this.currentModule.getValues().size() - 1) {
                ++this.valueStart;
            }
            if (m > 0 && this.valueStart > 0) {
                --this.valueStart;
            }
        }
        Fonts.font35.drawString(this.currentModule == null ? this.currentModuleType.getDisplayName() : this.currentModuleType.getDisplayName() + " - " + this.currentModule.getName(), this.startX + 70.0f, this.startY + 10.0f, new Color(255, 255, 255).getRGB());
        if (this.currentModule != null) {
            float mY = this.startY + 30.0f;
            for (int i = 0; i < LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).size(); ++i) {
                Module module = LiquidBounce.moduleManager.getModuleInCategory(this.currentModuleType).get(i);
                if (mY > this.startY + 220.0f) break;
                if (i < this.moduleStart) continue;
                RenderUtils.drawRoundRect(this.startX + 75.0f, mY, this.startX + 185.0f, mY + 2.0f, new Color(246, 246, 246, 0).getRGB());
                if (this.isSettingsButtonHovered(this.startX + 70.0f, mY + 2.0f, this.startX + 165.0f, mY + 20.0f, mouseX, mouseY)) {
                    if (module.getState()) {
                        RenderUtils.drawBorderRect(this.startX + 70.0f, mY + 2.0f, this.startX + 165.0f, mY + 20.0f, 3.0f, new Color(74, 78, 85).getRGB());
                        Fonts.font35.drawString(module.getName(), this.startX + 80.0f, mY + 8.0f, new Color(255, 255, 255, (int)this.opacity.getOpacity()).getRGB(), false);
                    } else {
                        RenderUtils.drawBorderRect(this.startX + 70.0f, mY + 2.0f, this.startX + 165.0f, mY + 20.0f, 3.0f, new Color(60, 64, 70).getRGB());
                        Fonts.font35.drawString(module.getName(), this.startX + 80.0f, mY + 8.0f, new Color(107, 107, 107, (int)this.opacity.getOpacity()).getRGB(), false);
                    }
                } else if (module.getState()) {
                    RenderUtils.drawBorderRect(this.startX + 70.0f, mY + 2.0f, this.startX + 165.0f, mY + 20.0f, 3.0f, new Color(64, 68, 75).getRGB());
                    Fonts.font35.drawString(module.getName(), this.startX + 80.0f, mY + 8.0f, new Color(255, 255, 255, (int)this.opacity.getOpacity()).getRGB(), false);
                } else {
                    RenderUtils.drawBorderRect(this.startX + 70.0f, mY + 2.0f, this.startX + 165.0f, mY + 20.0f, 3.0f, new Color(55, 59, 66).getRGB());
                    Fonts.font35.drawString(module.getName(), this.startX + 80.0f, mY + 8.0f, new Color(107, 107, 107, (int)this.opacity.getOpacity()).getRGB(), false);
                }
                if (this.isSettingsButtonHovered(this.startX + 75.0f, mY, this.startX + 100.0f + (float)Fonts.font35.getStringWidth(module.getName()), mY + 8.0f + (float)Fonts.font35.FONT_HEIGHT, mouseX, mouseY)) {
                    if (!this.previousMouse && Mouse.isButtonDown((int)0)) {
                        module.setState(!module.getState());
                        this.previousMouse = true;
                    }
                    if (!this.previousMouse && Mouse.isButtonDown((int)1)) {
                        this.previousMouse = true;
                    }
                }
                if (!Mouse.isButtonDown((int)0)) {
                    this.previousMouse = false;
                }
                if (this.isSettingsButtonHovered(this.startX + 70.0f, mY, this.startX + 165.0f, mY + 8.0f + (float)Fonts.font35.FONT_HEIGHT, mouseX, mouseY) && Mouse.isButtonDown((int)1)) {
                    this.currentModule = module;
                    this.valueStart = 0;
                }
                mY += 20.0f;
            }
            mY = this.startY + 30.0f;
            GameFontRenderer font = Fonts.font35;
            for (int i = 0; i < this.currentModule.getValues().size() && !(mY > this.startY + 220.0f); ++i) {
                double val;
                double valRel;
                double perc;
                double valAbs;
                double inc;
                double max;
                double render;
                if (i < this.valueStart) continue;
                Value<?> value = this.currentModule.getValues().get(i);
                if(!value.getDisplayable())
                    continue;
                if (value instanceof TextValue) {
                    float x = this.startX + 190.0f;
                    TextValue textValue = (TextValue)value;
                    font.drawString(textValue.getName() + ": " + (String)textValue.get(), this.startX + 180.0f, mY, new Color(255, 255, 255).getRGB());
                    mY += 20.0f;
                }
                if (value instanceof FontValue) {
                    FontValue fontValue = (FontValue)value;
                    font.drawString(fontValue.getName() + " : " + fontValue.get(), this.startX + 180.0f, mY, new Color(255, 255, 255).getRGB());
                    mY += 20.0f;
                }
                if (value instanceof BoolValue) {
                    BoolValue boolValue = (BoolValue)value;
                    float x = this.startX + 190.0f;
                    font.drawString(boolValue.getName(), x - 10.0f, mY, new Color(255, 255, 255).getRGB());
                    if (((Boolean)boolValue.get()).booleanValue()) {
                        RenderUtils.drawRoundedRect2(x + 80.0f, mY, x + 100.0f, mY + 10.0f, 4.0f, new Color(66, 134, 245).getRGB());
                        RenderUtils.circle(x + 96.0f, mY + 5.0f, 4.0f, new Color(255, 255, 255).getRGB());
                    } else {
                        RenderUtils.drawRoundedRect2(x + 80.0f, mY, x + 100.0f, mY + 10.0f, 4.0f, new Color(114, 118, 125).getRGB());
                        RenderUtils.circle(x + 84.0f, mY + 5.0f, 4.0f, new Color(164, 168, 175).getRGB());
                    }
                    if (this.isCheckBoxHovered(x + 80.0f, mY, x + 100.0f, mY + 9.0f, mouseX, mouseY)) {
                        if (!this.previousMouse && Mouse.isButtonDown((int)0)) {
                            this.mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
                            this.previousMouse = true;
                            this.mouse = true;
                        }
                        if (this.mouse) {
                            boolValue.set((Boolean)boolValue.get() == false);
                            this.mouse = false;
                        }
                    }
                    if (!Mouse.isButtonDown((int)0)) {
                        this.previousMouse = false;
                    }
                    mY += 25.0f;
                }
                if (value instanceof ListValue) {
                    ListValue listValue = (ListValue)value;
                    float x = this.startX + 190.0f;
                    font.drawString(listValue.getName(), x - 10.0f, mY - 1.0f, new Color(255, 255, 255).getRGB());
                    RenderUtils.drawRoundedRect2(x - 10.0f, mY + 10.0f, x + 75.0f, mY + 26.0f, 3.0f, new Color(86, 154, 255, (int)this.opacity.getOpacity()).getRGB());
                    Fonts.font35.drawString((String)listValue.get(), x + 30.0f - (float)font.getStringWidth((String)listValue.get()) / 2.0f, mY + 15.0f, -1);
                    if (this.isStringHovered(x - 10.0f, mY + 10.0f, x + 75.0f, mY + 26.0f, mouseX, mouseY)) {
                        if (Mouse.isButtonDown(0)) {
                            this.mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
              //              if (listValue.getValues().length <= listValue.getModeListNumber((String)listValue.get()) + 1) {
              //                  listValue.set(listValue.getValues()[0]);
              //              } else {
              //                  listValue.set(listValue.getValues()[listValue.getModeListNumber((String)listValue.get()) + 1]);
              //              }
                            int nowNumber = listValue.getModeListNumber(listValue.getName());
                            listValue.openList = true;
                            listValue.set(listValue.getValues()[nowNumber + 1]);
              //              this.previousMouse = true;
                        }
              //          if (!Mouse.isButtonDown(0)) {
              //              this.previousMouse = false;
              //          }
                        for (final String valueOfList : listValue.getValues()) {
                            if (listValue.openList) {{
                                listValue.set(valueOfList + 1);
                            }

                                GlStateManager.resetColor();
                            }
                        }
                    }
                    mY += 35.0f;
                }
                if (value instanceof IntegerValue) {
                    IntegerValue integerValue = (IntegerValue)value;
                    float x = this.startX + 190.0f;
                    render = 68.0f * (float)((Integer)integerValue.get() - integerValue.getMinimum()) / (float)(integerValue.getMaximum() - integerValue.getMinimum());
                    RenderUtils.drawRect(x - 11.0f, mY + 7.0f, (float)((double)x + 70.0), mY + 8.0f, new Color(213, 213, 213, (int)this.opacity.getOpacity()).getRGB());
                    RenderUtils.drawRect(x - 11.0f, mY + 7.0f, (float)((double)x + render + 0.5), mY + 8.0f, new Color(88, 182, 255, (int)this.opacity.getOpacity()).getRGB());
                    RenderUtils.circle((float)((double)x + render + 2.0), mY + 7.0f, 2.0f, new Color(0, 144, 255).getRGB());
                    font.drawString(integerValue.getName() + ": " + integerValue.get(), this.startX + 180.0f, mY - 5.0f, new Color(255, 255, 255).getRGB());
                    if (!Mouse.isButtonDown((int)0)) {
                        this.previousMouse = false;
                    }
                    if (this.isButtonHovered(x, mY - 4.0f, x + 100.0f, mY + 9.0f, mouseX, mouseY) && Mouse.isButtonDown((int)0)) {
                        if (!this.previousMouse && Mouse.isButtonDown((int)0)) {
                            render = integerValue.getMinimum();
                            max = integerValue.getMaximum();
                            inc = 1.0;
                            valAbs = (double)mouseX - ((double)x + 1.0);
                            perc = valAbs / 68.0;
                            perc = Math.min(Math.max(0.0, perc), 1.0);
                            valRel = (max - render) * perc;
                            val = render + valRel;
                            val = (double)Math.round(val * (1.0 / inc)) / (1.0 / inc);
                            integerValue.set((int) val);
                        }
                        if (!Mouse.isButtonDown((int)0)) {
                            this.previousMouse = false;
                        }
                    }
                    mY += 25.0f;
                }
                if (!(value instanceof FloatValue)) continue;
                FloatValue floatValue = (FloatValue)value;
                float x = this.startX + 190.0f;
                render = 68.0f * (((Float)floatValue.get()).floatValue() - floatValue.getMinimum()) / (floatValue.getMaximum() - floatValue.getMinimum());
                RenderUtils.drawRect(x - 11.0f, mY + 7.0f, (float)((double)x + 70.0), mY + 8.0f, new Color(213, 213, 213, (int)this.opacity.getOpacity()).getRGB());
                RenderUtils.drawRect(x - 11.0f, mY + 7.0f, (float)((double)x + render + 0.5), mY + 8.0f, new Color(88, 182, 255, (int)this.opacity.getOpacity()).getRGB());
                RenderUtils.circle((float)((double)x + render + 2.0), mY + 7.0f, 2.0f, new Color(0, 144, 255).getRGB());
                font.drawString(floatValue.getName() + ": " + floatValue.get(), this.startX + 180.0f, mY - 5.0f, new Color(255, 255, 255).getRGB());
                if (!Mouse.isButtonDown((int)0)) {
                    this.previousMouse = false;
                }
                if (this.isButtonHovered(x, mY - 4.0f, x + 100.0f, mY + 9.0f, mouseX, mouseY) && Mouse.isButtonDown((int)0)) {
                    if (!this.previousMouse && Mouse.isButtonDown((int)0)) {
                        render = floatValue.getMinimum();
                        max = floatValue.getMaximum();
                        inc = 0.1;
                        valAbs = (double)mouseX - ((double)x + 1.0);
                        perc = valAbs / 68.0;
                        perc = Math.min(Math.max(0.0, perc), 1.0);
                        valRel = (max - render) * perc;
                        val = render + valRel;
                        val = (double)Math.round(val * (1.0 / inc)) / (1.0 / inc);
                        floatValue.set((float) val);
                    }
                    if (!Mouse.isButtonDown((int)0)) {
                        this.previousMouse = false;
                    }
                }
                mY += 15.0f;
            }
            if (mY > this.startY + 220.0f) {
                return;
            }
            float x = this.startX + 190.0f;
            font.drawString("Hide", x - 10.0f, mY, new Color(255, 255, 255).getRGB());
            if (!this.currentModule.getArray()) {
                RenderUtils.drawRoundedRect2(x + 80.0f, mY, x + 100.0f, mY + 10.0f, 4.0f, new Color(66, 134, 245).getRGB());
                RenderUtils.circle(x + 95.0f, mY + 5.0f, 4.0f, new Color(255, 255, 255).getRGB());
            } else {
                RenderUtils.drawRoundedRect2(x + 80.0f, mY, x + 100.0f, mY + 10.0f, 4.0f, new Color(114, 118, 125).getRGB());
                RenderUtils.circle(x + 85.0f, mY + 5.0f, 4.0f, new Color(164, 168, 175).getRGB());
            }
            if (this.isCheckBoxHovered(x + 80.0f, mY, x + 100.0f, mY + 9.0f, mouseX, mouseY)) {
                if (!this.previousMouse && Mouse.isButtonDown((int)0)) {
                    this.mc.thePlayer.playSound("random.click", 1.0f, 1.0f);
                    this.previousMouse = true;
                    this.mouse = true;
                }
                if (this.mouse) {
                    this.currentModule.setArray(!this.currentModule.getArray());
                    this.mouse = false;
                }
            }
            if (!Mouse.isButtonDown((int)0)) {
                this.previousMouse = false;
            }
            mY += 25.0f;
        }
    }

    public boolean isStringHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= f && (float)mouseX <= g && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public boolean isSettingsButtonHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x2 && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public boolean isButtonHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= f && (float)mouseX <= g && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public boolean isCheckBoxHovered(float f, float y, float g, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= f && (float)mouseX <= g && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public boolean isCategoryHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x2 && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public boolean isHovered(float x, float y, float x2, float y2, int mouseX, int mouseY) {
        return (float)mouseX >= x && (float)mouseX <= x2 && (float)mouseY >= y && (float)mouseY <= y2;
    }

    public void onGuiClosed() {
        this.opacity.setOpacity(0.0f);
    }
}

