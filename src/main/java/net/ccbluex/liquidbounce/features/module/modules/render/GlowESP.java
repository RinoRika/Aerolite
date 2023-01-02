package net.ccbluex.liquidbounce.features.module.modules.render;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.event.RenderEntityEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.features.module.utils.ShaderShell;
import net.ccbluex.liquidbounce.utils.EntityUtils;
import net.ccbluex.liquidbounce.utils.render.ColorUtils;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "GlowESP", category = ModuleCategory.RENDER)
public class GlowESP extends Module {
    private final BoolValue rainbow = new BoolValue("Rainbow", false);
    private final IntegerValue redValue = new IntegerValue("Red", 255, 0, 255);
    private final IntegerValue greenValue = new IntegerValue("Green", 255, 0, 255);
    private final IntegerValue blueValue = new IntegerValue("Blue", 255, 0, 255);
    private static Framebuffer buffer;
    public static boolean ignoreGlint;
    public Color color;

    @EventTarget
    public void onRender2D(Render2DEvent event) {
        if (mc.thePlayer.getHealth() > 0) {
            setupFramebuffer();
            ScaledResolution res = ((Render2DEvent) event).getScaledResolution();
            ShaderShell.CHAMS_SHADER.attach();
            if (rainbow.get()) color = ColorUtils.INSTANCE.rainbow();
            else color = new Color(redValue.get(), greenValue.get(), blueValue.get());
            setRGB(color.getRed(), color.getGreen(), color.getBlue());
            glDrawFramebuffer(buffer.framebufferTexture, res.getScaledWidth(), res.getScaledHeight());
            ShaderShell.CHAMS_SHADER.detach();
            buffer.framebufferClear();
            mc.getFramebuffer().bindFramebuffer(false);
        }
    }

    @EventTarget
    public void onRenderEntity(RenderEntityEvent event) {
        if (event.getEntity() != mc.thePlayer
                && !EntityUtils.INSTANCE.isFriend(event.getEntity().getName())
                && mc.thePlayer.getHealth() > 0) {
            setupFramebuffer();
            buffer.bindFramebuffer(false);
            ignoreGlint = true;
            ignoreGlint = false;
            mc.getFramebuffer().bindFramebuffer(false);
        }
    }

    public static void setupFramebuffer() {
        if (buffer == null)
            buffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
        if (mc.displayWidth != buffer.framebufferWidth || mc.displayHeight != buffer.framebufferHeight)
            buffer.createBindFramebuffer(mc.displayWidth, mc.displayHeight);
        if (ShaderShell.CHAMS_SHADER == null) {
            ShaderShell.CHAMS_SHADER = new ShaderShell("#version 130\r\n" + "uniform sampler2D t0;\r\n"
                    + "uniform float glow_size = 2;\r\n" + "uniform vec3 glow_colour = vec3(1, 1, 1);\r\n"
                    + "uniform float glow_intensity = 1;\r\n" + "uniform float glow_threshold = 0;\r\n" + "\r\n"
                    + "void main() {\r\n" + "    vec2 tex_coord = gl_TexCoord[0].xy;\r\n"
                    + "    vec4 pixel = texture(t0, tex_coord);\r\n" + "    if (pixel.a <= glow_threshold) {\r\n"
                    + "        ivec2 size = textureSize(t0, 0);\r\n" + "	\r\n"
                    + "        float uv_x = tex_coord.x * size.x;\r\n"
                    + "        float uv_y = tex_coord.y * size.y;\r\n" + "\r\n" + "        float sum = 0.0;\r\n"
                    + "        for (int n = 0; n < 9; ++n) {\r\n"
                    + "            uv_y = (tex_coord.y * size.y) + (glow_size * float(n - 4.5));\r\n"
                    + "            float h_sum = 0.0;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x - (4.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x - (3.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x - (2.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x - glow_size, uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x, uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x + glow_size, uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x + (2.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x + (3.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            h_sum += texelFetch(t0, ivec2(uv_x + (4.0 * glow_size), uv_y), 0).a;\r\n"
                    + "            sum += h_sum / 9.0;\r\n" + "        }\r\n" + "\r\n"
                    + "        gl_FragColor = vec4(glow_colour, (sum / 9.0) * glow_intensity);\r\n" + "    }\r\n"
                    + "}");
        }
    }

    public static void glDrawFramebuffer(final int framebufferTexture, final int width, final int height) {
        int current = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
        glBindTexture(GL_TEXTURE_2D, framebufferTexture);
        glDisable(GL_ALPHA_TEST);
        boolean blend = GL11.glIsEnabled(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_BLEND);
        // Make sure blend is enabled
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 1);
            glVertex2f(0, 0);

            glTexCoord2f(0, 0);
            glVertex2f(0, height);

            glTexCoord2f(1, 0);
            glVertex2f(width, height);

            glTexCoord2f(1, 1);
            glVertex2f(width, 0);
        }
        glEnd();
        if (blend) {
            GL11.glEnable(GL11.GL_BLEND);
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }
        glEnable(GL_ALPHA_TEST);
        glBindTexture(GL_TEXTURE_2D, current);
    }

    public static void setRGB(int r, int g, int b) {
        ShaderShell.CHAMS_SHADER.set3F("glow_colour", r / 255f, g / 255f, b / 255f);
    }
}
