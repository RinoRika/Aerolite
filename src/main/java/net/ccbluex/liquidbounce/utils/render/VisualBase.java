package net.ccbluex.liquidbounce.utils.render;

import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static net.ccbluex.liquidbounce.utils.render.GuiRenderUtils.setColor;
import static net.ccbluex.liquidbounce.utils.render.RenderUtils.drawRect;
import static net.ccbluex.liquidbounce.utils.render.RenderUtils.glColor;
import static net.minecraft.client.renderer.GlStateManager.*;
import static org.lwjgl.opengl.GL11.*;

public class VisualBase extends MinecraftInstance {
    protected static float zLevel;
    static RenderManager renderManager;
    public static double ticks = 0;
    public static long lastFrame = 0;

    public static void drawGradientRect(double d, double e, double e2, double g, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer bufferbuilder = tessellator.getWorldRenderer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)e2, (double)e, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)d, (double)e, (double)zLevel).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)d, (double)g, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)e2, (double)g, (double)zLevel).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
    public static void drawNewRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer vertexbuffer = tessellator.getWorldRenderer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.color(f, f1, f2, f3);
        vertexbuffer.begin(7, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(left, bottom, 0.0).endVertex();
        vertexbuffer.pos(right, bottom, 0.0).endVertex();
        vertexbuffer.pos(right, top, 0.0).endVertex();
        vertexbuffer.pos(left, top, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawSmoothRect(double left, double top, double right, double bottom, int color) {
        GlStateManager.resetColor();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        drawRect(left, top, right, bottom, color);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        drawRect(left * 2.0f - 1.0f, top * 2.0f, left * 2.0f, bottom * 2.0f - 1.0f, color);
        drawRect(left * 2.0f, top * 2.0f - 1.0f, right * 2.0f, top * 2.0f, color);
        drawRect(right * 2.0f, top * 2.0f, right * 2.0f + 1.0f, bottom * 2.0f - 1.0f, color);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glScalef(2.0f, 2.0f, 2.0f);
    }
    public static void drawCircle(float x, float y, float start, float end, float radius, boolean filled, Color color) {
        float sin;
        float cos;
        float i;
        GlStateManager.color(0, 0, 0, 0);

        float endOffset;
        if (start > end) {
            endOffset = end;
            end = start;
            start = endOffset;
        }

        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        setColor(color.getRGB());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glLineWidth(2);
        GL11.glBegin(GL11.GL_LINE_STRIP);
        for (i = end; i >= start; i -= 4) {
            cos = (float) (Math.cos(i * Math.PI / 180) * radius * 1);
            sin = (float) (Math.sin(i * Math.PI / 180) * radius * 1);
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);

        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(filled ? GL11.GL_TRIANGLE_FAN : GL11.GL_LINE_STRIP);
        for (i = end; i >= start; i -= 4) {
            cos = (float) Math.cos(i * Math.PI / 180) * radius;
            sin = (float) Math.sin(i * Math.PI / 180) * radius;
            GL11.glVertex2f(x + cos, y + sin);
        }
        GL11.glEnd();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }
    public static void drawCircle(double x, double y, double radius, float startAngle, float endAngle, int color, float lineWidth) {
        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GL11.glEnable((int)2848);
        GL11.glLineWidth((float)lineWidth);
        GL11.glBegin((int)3);
        for (int i = (int)((double)startAngle / 360.0 * 100.0); i <= (int)((double)endAngle / 360.0 * 100.0); ++i) {
            double angle = Math.PI * 2 * (double)i / 100.0 + Math.toRadians(180.0);
            if (color == 1337) {
                Render.color(astolfoColors(i * 5, 1));
            } else {
                Render.color(color);
            }
            GL11.glVertex2d((double)(x + Math.sin(angle) * radius), (double)(y + Math.cos(angle) * radius));
        }
        GL11.glEnd();
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GL11.glDisable((int)2848);
        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }
    public static int astolfoColors(int yOffset, int yTotal) {
        float hue;
        float speed = 2900.0f;
        for (hue = (float)(System.currentTimeMillis() % (long)((int)speed)) + (float)((yTotal - yOffset) * 9); hue > speed; hue -= speed) {
        }
        hue /= speed;
        if ((double)(hue /= speed) > 0.5) {
            hue = 0.5f - (hue - 0.5f);
        }
        return Color.HSBtoRGB(hue += 0.5f, 0.5f, 1.0f);
    }
    public static void drawGradientSideways(double left, double top, double right, double bottom, int col1, int col2) {
        float f = (col1 >> 24 & 255) / 255.0f;
        float f1 = (col1 >> 16 & 255) / 255.0f;
        float f2 = (col1 >> 8 & 255) / 255.0f;
        float f3 = (col1 & 255) / 255.0f;
        float f4 = (col2 >> 24 & 255) / 255.0f;
        float f5 = (col2 >> 16 & 255) / 255.0f;
        float f6 = (col2 >> 8 & 255) / 255.0f;
        float f7 = (col2 & 255) / 255.0f;
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f1, f2, f3, f);
        GL11.glVertex2d(left, top);
        GL11.glVertex2d(left, bottom);
        GL11.glColor4f(f, f6, f7, f4);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
    }
    public static void drawCircle(Entity entity, double rad, int color, boolean shade) {
        ticks += .004 * (System.currentTimeMillis() - lastFrame);

        lastFrame = System.currentTimeMillis();

        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glEnable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glHint(3154, 4354);
        GL11.glHint(3155, 4354);
        GL11.glHint(3153, 4354);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        if (shade) GL11.glShadeModel(GL11.GL_SMOOTH);
        GlStateManager.disableCull();

        Color c = new Color(color);

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX)
                * Minecraft.getMinecraft().timer.renderPartialTicks - renderManager.renderPosX;
        double y = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY)
                * Minecraft.getMinecraft().timer.renderPartialTicks - renderManager.renderPosY) + Math.sin(ticks) + 1;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ)
                * Minecraft.getMinecraft().timer.renderPartialTicks - renderManager.renderPosZ;

        GL11.glBegin(GL11.GL_TRIANGLE_STRIP);

        double TAU = Math.PI * 2.D;
        for (float i = 0; i < TAU; i += TAU / 64.F) {

            double vecX = x + rad * Math.cos(i);
            double vecZ = z + rad * Math.sin(i);

            if (shade) {
                GL11.glColor4f(c.getRed() / 255.F,
                        c.getGreen() / 255.F,
                        c.getBlue() / 255.F,
                        0.05F
                );

                GL11.glVertex3d(vecX, y - Math.sin(ticks + 1) / 2.7f, vecZ);
            }

            GL11.glColor4f(c.getRed() / 255.F,
                    c.getGreen() / 255.F,
                    c.getBlue() / 255.F,
                    0.56F
            );

            GL11.glVertex3d(vecX, y, vecZ);
        }

        GL11.glColor4f(1, 1, 1, 1);
        GL11.glEnd();
        if (shade) GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glDepthMask(true);
        GL11.glEnable(2929);
        GlStateManager.enableCull();
        GL11.glDisable(2848);
        GL11.glEnable(2832);
        GL11.glEnable(3553);
        GL11.glPopMatrix();

        pushAttribAndMatrix();
        GL11.glPushMatrix();
        mc.entityRenderer.disableLightmap();
        GL11.glDisable(3553);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glDisable(2929);
        GL11.glEnable(2848);
        GL11.glDepthMask(false);
        GL11.glPushMatrix();
        GL11.glLineWidth(2);
        glColor(new Color(color));
        GL11.glBegin(1);
        for (int i = 0; i <= 90; ++i) {
            glColor(new Color(color));
            GL11.glVertex3d(x + rad * Math.cos((double) i * (Math.PI * 2) / 45), y, z + rad * Math.sin((double) i * (Math.PI * 2) / 45));
        }
        GL11.glEnd();
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDisable(2848);
        GL11.glEnable(2929);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        mc.entityRenderer.enableLightmap();
        GL11.glPopMatrix();
        popAttribAndMatrix();
    }
    public static void pushAttribAndMatrix() {
        pushAttrib();
        pushMatrix();
    }
    public static void popAttribAndMatrix() {
        popAttrib();
        popMatrix();
    }
}
